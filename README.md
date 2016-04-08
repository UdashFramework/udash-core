# Udash RPC [![Build Status](https://travis-ci.org/UdashFramework/udash-rpc.svg?branch=master)](https://travis-ci.org/UdashFramework/udash-rpc) [![Join the chat at https://gitter.im/UdashFramework/udash-rpc](https://badges.gitter.im/UdashFramework/udash-rpc.svg)](https://gitter.im/UdashFramework/udash-rpc?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [<img align="right" height="50px" src="http://www.avsystem.com/avsystem_logo.png">](http://www.avsystem.com/)

The RPC client-server communication system for the Udash framework.

The [Udash](http://udash.io/) project tries to make frontend applications as type safe as possible. Thanks to the ScalaJS cross-compilation system, it is possible to share the code between the client and server applications. Udash RPC uses this feature to share:
* RPC interfaces with typed arguments and returned value.
* Data models, which can be used in RPC communication.
* Model validators, which can be used both in frontend and backend.

Udash RPC also provides a server for client communication that works out of the box. You only have to create the RPC interface and implement it - that is all, you do not have to worry about connection handling.

Read more in the [Udash Developer's Guide](http://guide.udash.io/#/rpc).

### Ping-pong example

The implementation is really simple. In the server RPC interface, add the *ping* method and implement this method in server code. Then you can call it from the client code.

```scala
import io.udash.rpc._

/** Shared RPC interface. */
@RPC
trait PingPongServerRPC {
  def ping(id: Int): Future[Int]
}

/** Server-side implementation. */
class PingPongEndpoint extends PingPongServerRPC {
  override def ping(id: Int): Future[Int] = {
    TimeUnit.SECONDS.sleep(1)
    Future.successful(id)
  }
}

/** Client-side call. */
serverRpc.ping(123) onComplete {
  case Success(response) => println(s"Pong($response)")
  case Failure(ex) => println(s"PongError($ex)")
}
```

Find more examples in the [Udash Demos](https://github.com/UdashFramework/udash-demos) repository.
