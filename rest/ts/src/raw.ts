export function urlEncode(str: string, spaceAsPlus: boolean): string {
    const result = encodeURIComponent(str)
        .replace("!", "%21")
        .replace("'", "%27")
        .replace("(", "%28")
        .replace(")", "%29")
        .replace("~", "%7E")
    return spaceAsPlus ? result.replace("%20", "+") : result
}

export function encodeQuery(params: [string, string][]): string {
    let queryString = params.map(kv =>
        urlEncode(kv[0], true) + '=' + urlEncode(kv[1], true)).join("&")
    if (queryString.length > 0) {
        queryString = "?" + queryString
    }
    return queryString
}

export type RestMethod = "GET" | "HEAD" | "PUT" | "POST" | "PATCH" | "DELETE" | "TRACE" | "CONNECT" | "OPTIONS"

export interface RestParameters {
    // TODO: use immutable arrays?
    // TODO: no cookie parameters from browser, right?
    readonly path: string[],
    readonly query: [string, string][],
    readonly header: [string, string][],
}

export function newParameters(prefixParameters?: RestParameters): RestParameters {
    if (!prefixParameters) {
        return {path: [], query: [], header: []}
    } else {
        // make a copy of every array so that they can be safely appended with more values
        return {
            path: [...prefixParameters.path],
            query: [...prefixParameters.query],
            header: [...prefixParameters.header]
        }
    }
}

export interface RestContentBody {
    readonly content: string | ArrayBuffer,
    readonly contentType: string
}

export type RestBody = RestContentBody | null

export function mkJsonBody(json: any): RestBody {
    return {
        content: JSON.stringify(json),
        contentType: "application/json; charset=utf-8"
    }
}

export function mkFormBody(...params: [string, string][]): RestBody {
    return {
        content: encodeQuery(params),
        contentType: "application/x-www-form-urlencoded; charset=utf-8"
    }
}


export interface RestRequest {
    readonly method: RestMethod,
    readonly parameters: RestParameters,
    readonly body: RestBody
}

export interface RestResponse {
    readonly status: number,
    readonly headers: [string, string][],
    readonly body: RestBody,
}

export type HandleRequest = (request: RestRequest) => Promise<RestResponse>
