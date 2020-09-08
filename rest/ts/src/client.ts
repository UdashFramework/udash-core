import {encodeQuery, HandleRequest, RestBody, urlEncode} from "./raw"

export function handleUsingFetch(baseUrl: string): HandleRequest {
    return request => {
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/"
        }

        const joinedPath = request.parameters.path.map(s => urlEncode(s, false)).join("/")
        let queryString = encodeQuery(request.parameters.query)
        if(queryString.length > 0) {
            queryString = "?" + queryString
        }
        const fullUrl = baseUrl + joinedPath + queryString

        let body: BodyInit | null
        let allHeaders: [string, any][] = request.parameters.header
        if (request.body === null) {
            body = null
        } else {
            body = request.body.content
            allHeaders = [['Content-Type', request.body.contentType], ...allHeaders]
        }

        return fetch(fullUrl, {
            method: request.method,
            headers: allHeaders,
            body: body
        }).then(response => {
            const contentType = response.headers.get('Content-Type')

            let respHeaders: [string, string][] = []
            response.headers.forEach((value, key) => respHeaders.push([key, value]))

            let respBody: Promise<RestBody>
            if (contentType == null) {
                respBody = Promise.resolve(null)
            } else if (contentType.includes("charset")) {
                respBody = response.text().then(text => {
                    return {
                        content: text,
                        contentType: contentType
                    }
                })
            } else {
                respBody = response.arrayBuffer().then(buf => {
                    return {
                        content: buf,
                        contentType: contentType
                    }
                })
            }

            return respBody.then(body => {
                return {
                    status: response.status,
                    headers: respHeaders,
                    body: body
                }
            })
        })
    }
}
