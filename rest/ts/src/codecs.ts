import {encodeQuery, RestBody, RestResponse} from "./raw";

export type PlainWriter<T> = (value: T) => string
export type JsonWriter<T> = (value: T) => any
export type JsonReader<T> = (json: any) => T
export type BodyWriter<T> = (value: T) => RestBody
export type BodyReader<T> = (json: RestBody) => T
export type ResponseReader<T> = (resp: RestResponse) => T

export type Dictionary<K extends string | number, V> = {
    [K0 in K]: V
}

type RawDict = { [key: string]: any }

export function mapUndefined<A, B>(f: (v: A) => B, value: A | undefined): B | undefined {
    if (typeof value == 'undefined') return undefined
    else return f(value)
}

export function mapValues<K extends string | number, V, V0>(
    dict: Dictionary<K, V>,
    fun: (value: V) => V0,
    copy: boolean = true
) {
    const result = (copy ? Object.assign({}, dict) : dict) as RawDict
    for (const [key, value] of Object.entries(dict)) {
        result[key] = fun(value as V)
    }
    return result as Dictionary<K, V0>
}

export function bodyToJson(body: RestBody): any {
    //TODO: what if charset is missing and content is not string?
    if (body != null && body.contentType.startsWith('application/json')) {
        return JSON.parse(body.content as string)
    } else {
        //TODO: better error?
        throw Error(`expected body with application/json media type`)
    }
}

export function jsonToBody(json: any): RestBody {
    return {
        content: JSON.stringify(json),
        contentType: "application/json; charset=utf-8"
    }
}

export function formToBody(...params: [string, string | undefined][]): RestBody {
    return {
        content: encodeQuery(params.filter(([n, v]) => typeof v !== 'undefined') as [string, string][]),
        contentType: "application/x-www-form-urlencoded; charset=utf-8"
    }
}

export function successfulResponseToBody(resp: RestResponse): RestBody {
    if (resp.status >= 200 && resp.status < 300) {
        return resp.body
    } else {
        //TODO: better error?
        throw Error(`HTTP error: ${resp.status}`)
    }
}

//TODO: default values and transient default
export interface FieldInfo<T> {
    readonly rawName?: string,
    reader?: JsonReader<T>
    writer?: JsonWriter<T>
}

export type FieldInfos = { [name: string]: FieldInfo<any> }

export function jsonToRecord<T>(managedFields: FieldInfos): JsonReader<T> {
    return json => {
        // no need to copy when reading, we can just mutate the incoming object
        const result = json as RawDict
        for (const [name, {rawName, reader}] of Object.entries(managedFields)) {
            if (rawName) {
                result[name] = result[rawName]
                delete result[rawName]
            }
            if (reader) {
                result[name] = reader(result[name])
            }
        }
        return result as T
    }
}

export function recordToJson<T>(managedFields: { [name: string]: FieldInfo<any> }): JsonWriter<T> {
    return value => {
        // shallow copy
        const result = Object.assign({}, value) as RawDict
        for (const [name, {rawName, writer}] of Object.entries(managedFields)) {
            if (writer) {
                result[name] = writer(result[name])
            }
            if (rawName) {
                result[rawName] = result[name]
                delete result[name]
            }
        }
        return result
    }
}

export interface CaseInfo<T> {
    readonly rawName?: string,
    reader?: JsonReader<T>
    writer?: JsonWriter<T>
}

export type CaseInfos = { [name: string]: CaseInfo<any> }

export function nestedJsonToUnion<T>(managedCases: CaseInfos): JsonReader<T> {
    return json => {
        const result = json as RawDict
        for (const [cname, {rawName, reader}] of Object.entries(managedCases)) {
            if (rawName && result[rawName]) {
                result[cname] = result[rawName]
                delete result[rawName]
            }
            if (reader && result[cname]) {
                result[cname] = reader(result[cname])
            }
        }
        return result as T
    }
}

export function unionToNestedJson<T>(managedCases: { [name: string]: CaseInfo<any> }): JsonWriter<T> {
    return value => {
        let result = value as RawDict
        for (const [cname, {rawName, writer}] of Object.entries(managedCases)) {
            if (result[cname]) {
                result = Object.assign({}, result) as RawDict
                if (writer) {
                    result[cname] = writer(result[cname])
                }
                if (rawName) {
                    result[rawName] = result[cname]
                    delete result[cname]
                }
            }
        }
        return result
    }
}

export function flatJsonToUnion<T>(discriminator: string, managedCases: { [name: string]: CaseInfo<any> }): JsonReader<T> {
    return json => {
        let result = json as RawDict
        for (const [cname, {rawName, reader}] of Object.entries(managedCases)) {
            if (rawName && result[discriminator] === rawName) {
                result[discriminator] = cname
            }
            if (reader && result[discriminator] === cname) {
                result = reader(result)
            }
        }
        return result as T
    }
}

export function unionToFlatJson<T>(discriminator: string, managedCases: { [name: string]: CaseInfo<any> }): JsonWriter<T> {
    return value => {
        let result = value as RawDict
        for (const [cname, {rawName, writer}] of Object.entries(managedCases)) {
            if (result[discriminator] === cname) {
                if (writer) {
                    //TODO: this will blow up if case writer removes discriminator
                    result = writer(value)
                }
                if (rawName) {
                    // only copy when there was no codec to do it
                    if (!writer) {
                        result = Object.assign({}, result) as RawDict
                    }
                    result[discriminator] = rawName
                }
            }
        }
        return result
    }
}
