package io.udash.rpc

import io.udash.rpc.serialization.URLEncoder
import io.udash.testing.UdashSharedTest

class URLEncoderTest extends UdashSharedTest {

  "URLEncoder" should {
    "encode and decode data in the same way for both JVM and JS" in {
      val data = "a b »~!@#$%^&*()_+=-`{}[]:\";'<>?/.,♦"
      URLEncoder.encode(data) should be("a%20b%20%C2%BB~!%40%23%24%25%5E%26*()_%2B%3D-%60%7B%7D%5B%5D%3A%22%3B'%3C%3E%3F%2F.%2C%E2%99%A6")
      URLEncoder.decode(URLEncoder.encode(data)) should be(data)
    }
  }

}
