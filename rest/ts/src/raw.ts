export function urlEncode(str: string, spaceAsPlus: boolean): string {
    const result = encodeURIComponent(str)
        .replace("!", "%21")
        .replace("'", "%27")
        .replace("(", "%28")
        .replace(")", "%29")
        .replace("~", "%7E")
    return spaceAsPlus ? result.replace("%20", "+") : result
}

export function encodeQuery(params: [string, (string | undefined)][]): string {
    return params
        .filter(([k, v]) => typeof v != 'undefined')
        .map(([k, v]) => urlEncode(k, true) + '=' + urlEncode(v as string, true))
        .join("&")
}

export function encodePath(segments: string[]): string {
    return segments
        .map(v => urlEncode(v as string, false))
        .join("/")
}

export type RestMethod = "GET" | "HEAD" | "PUT" | "POST" | "PATCH" | "DELETE" | "TRACE" | "CONNECT" | "OPTIONS"

export interface RestParameters {
    readonly path: string[],
    readonly query: [string, string | undefined][],
    readonly header: [string, string | undefined][],
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
