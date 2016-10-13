# Udash Core [![Build Status](https://travis-ci.org/UdashFramework/udash-core.svg?branch=master)](https://travis-ci.org/UdashFramework/udash-core) [![Join the chat at https://gitter.im/UdashFramework/udash-core](https://badges.gitter.im/UdashFramework/udash-core.svg)](https://gitter.im/UdashFramework/udash-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [<img align="right" height="50px" src="https://encrypted-tbn2.gstatic.com/images?q=tbn:ANd9GcSoiMy6rnzARUEdR0OjHmPGxTeiAMLBFlUYwIB9baWYWmuUwTbo">](http://www.avsystem.com/)

[Udash](http://udash.io/) is a Scala.js framework for building beautiful and maintainable web applications.

# Combined forces of Scala & JavaScript

### Type safe (HTML, CSS & JS)
In cooperation with Scalatags and ScalaCSS libraries, Udash provides a type safe layer over HTML, CSS and JS with powerful data binding into DOM templates.

### Compiled to JS
Scala is compiled to highly efficient JavaScript with no need to maintain js. It is also easy to use it with good, old JavaScript libraries like Twitter Bootstrap or jQuery.

### Shared Scala code
Udash brings out of the box the RPC system with a shared data model and interfaces between frontend and backend, which boosts development and keeps code bases consistent.

# Why Udash?

### Reactive Data Bindings
Automatically synchronise user interface with your data model.

### Type-safe RPC & REST
A client↔server communication based on typed interfaces. Bidirectional RPC via WebSockets out of the box.

### User Interface Components
Twitter Bootstrap components enriched by Udash features.

### Routing
Udash serves a frontend routing mechanism. Just define matching from URL to view.

### i18n
Translations served by the backend or compiled into JavaScript.

### Generator 
Generate a customized application, compile and try it out in 5 minutes.

### Backend independent
Udash provides a complete support for your web application and the communication with the server but does not influence your backend implementation.

### Open Source
The whole framework code is available on GitHub under Apache v2 license.

### IDE support
With any IDE supporting the Scala language. No extra plugin needed.

# Try it on ScalaFiddle

* [Hello World](https://scalafiddle.io/sf/z8zY6cP/0)
* [Properties](https://scalafiddle.io/sf/OZe6XBJ/2)
* [Validation](https://scalafiddle.io/sf/Yiz0JO2/0)
* [i18n](https://scalafiddle.io/sf/ll4AVYz/0)
* [UI Components](https://scalafiddle.io/sf/13Wn0gZ/0)

Find more examples in the [Udash Demos](https://github.com/UdashFramework/udash-demos) repository.

# Quick start guide

A good starting point is a generation of a project base with the Udash project generator. You can download it from [here](https://github.com/UdashFramework/udash-generator/releases). The generator provides a command line interface which will collect some information about the project and prepare the project base for you.

Follow the below steps:

1. Download the generator zip package and unzip it.
2. Run it using the *run.sh* or *run.bat* script.
3. Provide required data and start the project generation.
4. Switch to a project directory in the command line. 
5. Open the SBT interpreter using the sbt command.
6. Compile the project and run the Jetty server, if you selected the standard project version and asked the generator to create the Jetty launcher.
7. If you selected only the frontend project, you can find static files in target/UdashStatic.

Read more in the [Udash Developer's Guide](http://guide.udash.io/).

# Udash RPC

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
