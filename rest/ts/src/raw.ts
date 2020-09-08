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
    return params.map(kv => urlEncode(kv[0], true) + '=' + urlEncode(kv[1], true)).join("&")
}

export type RestMethod = "GET" | "HEAD" | "PUT" | "POST" | "PATCH" | "DELETE" | "TRACE" | "CONNECT" | "OPTIONS"

export interface RestParameters {
    // TODO: use immutable arrays?
    // TODO: no cookie parameters from browser, right?
    readonly path: string[],
    readonly query: [string, string][],
    readonly header: [string, string][],
}

export function newParameters(
    prefixParameters: RestParameters | undefined,
    path: (string | undefined)[],
    query: [string, string | undefined][],
    header: [string, string | undefined][],
): RestParameters {
    const result: RestParameters = {path: [], query: [], header: []}
    if (prefixParameters) {
        // must make a copy of each array
        result.path.push(...prefixParameters.path)
        result.query.push(...prefixParameters.query)
        result.header.push(...prefixParameters.header)
    }
    result.path.push(...path.filter(v => typeof v != 'undefined') as string[])
    result.query.push(...query.filter(([n, v]) => typeof v != 'undefined') as [string, string][])
    result.header.push(...header.filter(([n, v]) => typeof v != 'undefined') as [string, string][])
    return result
}

export interface RestContentBody {
    readonly content: string | ArrayBuffer,
    readonly contentType: string
}

export type RestBody = RestContentBody | null

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
