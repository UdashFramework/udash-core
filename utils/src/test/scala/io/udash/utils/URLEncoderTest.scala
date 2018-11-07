package io.udash
package utils

import io.udash.testing.UdashSharedTest

class URLEncoderTest extends UdashSharedTest {

  "URLEncoder" should {
    "encode and decode data in the same way for both JVM and JS" in {
      val data = "a b »~!@#$%^&*()_+=-`{}[]:\";'<>?/.,♦"

      URLEncoder.encode(data, spaceAsPlus = true) should
        be("a+b+%C2%BB%7E%21%40%23%24%25%5E%26*%28%29_%2B%3D-%60%7B%7D%5B%5D%3A%22%3B%27%3C%3E%3F%2F.%2C%E2%99%A6")
      URLEncoder.decode(URLEncoder.encode(data, spaceAsPlus = true), plusAsSpace = true) should be(data)

      URLEncoder.encode(data, spaceAsPlus = false) should
        be("a%20b%20%C2%BB%7E%21%40%23%24%25%5E%26*%28%29_%2B%3D-%60%7B%7D%5B%5D%3A%22%3B%27%3C%3E%3F%2F.%2C%E2%99%A6")
      URLEncoder.decode(URLEncoder.encode(data, spaceAsPlus = false), plusAsSpace = false) should be(data)
    }
  }

}
