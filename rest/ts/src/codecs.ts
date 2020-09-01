import {RestBody, RestResponse} from "./raw";

export interface PlainCodec<T> {
    writePlain(value: T): string

    readPlain(plain: string): T
}

export interface JsonCodec<T> {
    writeJson(value: T): any

    readJson(json: any): T
}

export interface BodyCodec<T> {
    writeBody(value: T): RestBody

    readBody(body: RestBody): T
}

export interface ResponseReader<T> {
    readResponse(resp: RestResponse): T
}


export const Void: ResponseReader<void> = {
    readResponse(resp: RestResponse): void {
    }
}

export const Boolean: PlainCodec<boolean> = {
    writePlain(value: boolean): string {
        return value ? "true" : "false"
    },
    readPlain(plain: string): boolean {
        return plain === "true"
    },
}

export const Float: PlainCodec<number> = {
    writePlain(value: number): string {
        return value.toString()
    },
    readPlain(plain: string): number {
        return parseFloat(plain)
    },
}

export const Integer: PlainCodec<number> = {
    writePlain(value: number): string {
        return value.toString()
    },
    readPlain(plain: string): number {
        return parseInt(plain)
    },
}

export const Timestamp: PlainCodec<Date> & JsonCodec<Date> = {
    writePlain(value: Date): string {
        return value.toISOString();
    },
    readPlain(plain: string): Date {
        return new Date(plain);
    },
    writeJson(value: Date): any {
        return value.toISOString();
    },
    readJson(json: any): Date {
        return new Date(json as string);
    }
}

export function array<T>(elemCodec: JsonCodec<T>): JsonCodec<T[]> {
    return {
        writeJson(value: T[]): any {
            return value.map(elemCodec.writeJson)
        },
        readJson(json: any): T[] {
            return (json as any[]).map(elemCodec.readJson)
        }
    }
}

export function nullable<T>(elemCodec: JsonCodec<T>): JsonCodec<T | null> {
    return {
        writeJson(value: T | null): any {
            if (value === null) {
                return null
            } else {
                return elemCodec.writeJson(value)
            }
        },
        readJson(json: any): T | null {
            if (json === null) {
                return null
            } else {
                return elemCodec.readJson(json)
            }
        }
    }
}

export function identity<T>(): JsonCodec<T> {
    return {
        writeJson(value: T): any {
            return value
        },
        readJson(json: any): T {
            return json as T;
        }
    }
}

//TODO: use better JSON parser/serializer that can handle big numbers
export function bodyFromJson<T>(jsonCodec: JsonCodec<T> = identity<T>()): BodyCodec<T> {
    return {
        writeBody(value: T): RestBody {
            return {
                content: JSON.stringify(jsonCodec.writeJson(value)),
                contentType: "application/json; charset=utf-8"
            }
        },
        readBody(body: RestBody): T {
            //TODO: what if charset is missing and content is not string?
            if (body != null && body.contentType.startsWith('application/json')) {
                return jsonCodec.readJson(JSON.parse(body.content as string))
            } else {
                //TODO: better error?
                throw Error(`expected body with application/json media type`)
            }
        }
    }
}

export function responseFromBody<T>(bodyCodec: BodyCodec<T> = bodyFromJson<T>()): ResponseReader<T> {
    return {
        readResponse(resp: RestResponse): T {
            if (resp.status >= 200 && resp.status < 300) {
                return bodyCodec.readBody(resp.body)
            } else {
                //TODO: better error?
                throw Error(`HTTP error: ${resp.status}`)
            }
        }
    }
}

type RawDict = { [key: string]: any }

//TODO: default values and transient default
export interface FieldInfo {
    readonly rawName?: string,
    codec?: () => JsonCodec<any> // function because it must be lazy for recursively defined records
}

export function record<T>(managedFields: { [name: string]: FieldInfo }): JsonCodec<T> {
    return {
        writeJson<T>(value: T): any {
            // shallow copy
            const result = Object.assign({}, value) as RawDict
            for (const [name, {rawName, codec}] of Object.entries(managedFields)) {
                if (codec) {
                    result[name] = codec().writeJson(result[name])
                }
                if (rawName) {
                    result[rawName] = result[name]
                    delete result[name]
                }
            }
            return result
        },
        readJson(json: any): T {
            // no need to copy when reading, we can just mutate the incoming object
            const result = json as RawDict
            for (const [name, {rawName, codec}] of Object.entries(managedFields)) {
                if (rawName) {
                    result[name] = result[rawName]
                    delete result[rawName]
                }
                if (codec) {
                    result[name] = codec().readJson(result[name])
                }
            }
            return result as T
        }
    }
}

export interface CaseInfo {
    readonly rawName?: string
    codec?: () => JsonCodec<any>
}

export function nestedUnion<T>(managedCases: { [name: string]: CaseInfo }): JsonCodec<T> {
    return {
        writeJson(value: T): any {
            let result = value as RawDict
            for (const [cname, info] of Object.entries(managedCases)) {
                if (result[cname]) {
                    result = Object.assign({}, result) as RawDict
                    if (info.codec) {
                        result[cname] = info.codec().writeJson(result[cname])
                    }
                    if (info.rawName) {
                        result[info.rawName] = result[cname]
                        delete result[cname]
                    }
                }
            }
            return result
        },
        readJson(json: any): T {
            const result = json as RawDict
            for (const [cname, info] of Object.entries(managedCases)) {
                if (info.rawName && result[info.rawName]) {
                    result[cname] = result[info.rawName]
                    delete result[info.rawName]
                }
                if (info.codec && result[cname]) {
                    result[cname] = info.codec().readJson(result[cname])
                }
            }
            return result as T
        }
    }
}

export function flatUnion<T>(discriminator: string, managedCases: { [name: string]: CaseInfo }): JsonCodec<T> {
    return {
        writeJson(value: T): any {
            let result = value as RawDict
            for (const [cname, info] of Object.entries(managedCases)) {
                if (result[discriminator] === cname) {
                    if (info.codec) {
                        //TODO: this will blow up if case codec removes discriminator
                        result = info.codec().writeJson(value)
                    }
                    if (info.rawName) {
                        // only copy when there was no codec to do it
                        if (!info.codec) {
                            result = Object.assign({}, result) as RawDict
                        }
                        result[discriminator] = info.rawName
                    }
                }
            }
            return result
        },
        readJson(json: any): T {
            let result = json as RawDict
            for (const [cname, info] of Object.entries(managedCases)) {
                if (info.rawName && result[discriminator] === info.rawName) {
                    result[discriminator] = cname
                }
                if (info.codec && result[discriminator] === cname) {
                    result = info.codec().readJson(result)
                }
            }
            return result as T
        }
    }
}
