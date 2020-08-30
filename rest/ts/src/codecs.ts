import {BodyCodec, JsonCodec, PlainAndJsonCodec, ResponseReader, RestBody, RestResponse} from "./raw";

export const Undefined: JsonCodec<undefined> & BodyCodec<undefined> = {
    writeJson(value: undefined): any {
        return null
    },
    readJson(json: any): undefined {
        return undefined
    },
    writeBody(value: undefined): RestBody {
        return null
    },
    readBody(body: RestBody): undefined {
        return undefined
    }
}

export const Void: ResponseReader<void> = {
    readResponse(resp: RestResponse): void {
    }
}

export const Never: PlainAndJsonCodec<never> = {
    writePlain(value: never): string {
        throw Error("never")
    },
    readPlain(plain: string): never {
        throw Error("never")
    },
    writeJson(value: never): any {
        throw Error("never")
    },
    readJson(json: any): never {
        throw Error("never")
    },
}

export const Boolean: PlainAndJsonCodec<boolean> = {
    writePlain(value: boolean): string {
        return value ? "true" : "false"
    },
    readPlain(plain: string): boolean {
        return plain === "true"
    },
    writeJson(value: boolean): any {
        return value
    },
    readJson(json: any): boolean {
        return json as boolean
    }
}

export const String: PlainAndJsonCodec<string> = {
    writePlain(value: string): string {
        return value
    },
    readPlain(plain: string): string {
        return plain
    },
    writeJson(value: string): any {
        return value
    },
    readJson(json: any): string {
        return json as string
    },
}

export const Float: PlainAndJsonCodec<number> = {
    writePlain(value: number): string {
        return value.toString()
    },
    readPlain(plain: string): number {
        return parseFloat(plain)
    },
    writeJson(value: number): any {
        return value
    },
    readJson(json: any): number {
        return json as number
    }
}

export const Integer: PlainAndJsonCodec<number> = {
    writePlain(value: number): string {
        return value.toString()
    },
    readPlain(plain: string): number {
        return parseInt(plain)
    },
    writeJson(value: number): any {
        return value
    },
    readJson(json: any): number {
        return json as number
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

//TODO: use better JSON parser/serializer that can handle big numbers
export function bodyFromJson<T>(jsonCodec: JsonCodec<T>): BodyCodec<T> {
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

export function responseFromBody<T>(bodyCodec: BodyCodec<T>): ResponseReader<T> {
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

export function record<T>(
    constructor: { new(...fields: any): T },
    fieldCodecs: [string, JsonCodec<any>][] //TODO: lazy! recursively defined records
): JsonCodec<T> {
    type RawDict = { [key: string]: any }

    //TODO: honor raw names
    return {
        writeJson<T>(value: T): any {
            let rawValue = value as RawDict
            let result = {} as RawDict
            fieldCodecs.forEach(function ([fname, codec]) {
                //TODO: honor @transientDefault
                result[fname] = codec.writeJson(rawValue[fname])
            })
            return result
        },
        readJson(json: any): T {
            let dict = json as RawDict
            let fieldValues = fieldCodecs.map(function ([fname, codec]) {
                //TODO: validate if each field actually exists, use default values
                return codec.readJson(dict[fname])
            })
            return new constructor(...fieldValues);
        }
    }
}
