# Udash Core [![Build Status](https://travis-ci.org/UdashFramework/udash-core.svg?branch=master)](https://travis-ci.org/UdashFramework/udash-core) [![Join the chat at https://gitter.im/UdashFramework/udash-core](https://badges.gitter.im/UdashFramework/udash-core.svg)](https://gitter.im/UdashFramework/udash-core?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) [<img align="right" height="50px" src="http://www.avsystem.com/avsystem_logo.png">](http://www.avsystem.com/)

[Udash](http://udash.io/) is a Scala.js framework for building beautiful and maintainable web applications.

# Combined forces of Scala & JavaScript

### Type safe (HTML, CSS & JS)
In cooperation with Scalatags and ScalaCSS libraries, Udash provides a type safe layer over HTML, CSS and JS with powerful data binding into DOM templates.

### Compiled to JS
Scala is compiled to highly efficient JavaScript with no need to maintain js. It is also easy to use it with good, old JavaScript libraries like Twitter Bootstrap or jQuery.

### Shared Scala code
Udash brings out of the box the RPC system with a shared data model and interfaces between frontend and backend, which boosts development and keeps code bases consistent.

# What's more?

### Routing
Udash serves a frontend routing mechanism. Just define matching from URL to view.

### Asynchronous
The whole framework is asynchronous by default – implementing reactive websites is much easier.

### IDE support
With any IDE supporting the Scala language. No extra plugin needed.

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

### Hello World example

```scala
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.all._
import io.udash._

val name = Property("World")

div(
  TextInput(name), br,
  produce(name)(name => h3(s"Hello, $name!").render)
).render
```

### Properties example

```scala
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import org.scalajs.dom._
import scalatags.JsDom.all._
import io.udash._

def isOdd(n: Int): Boolean =
  n % 2 == 1

def renderer(n: ReadableProperty[Int]): Element =
  span(s"${n.get}, ").render

val input = Property("")
val numbers = SeqProperty[Int](Seq.empty)
val odds = numbers.filter(isOdd)
val evens = numbers.filter(n => !isOdd(n))

div(
  TextInput(input)(
    onkeyup := ((ev: KeyboardEvent) => 
      if (ev.keyCode == ext.KeyCode.Enter) {
        val n: Try[Int] = Try(input.get.toInt)
        if (n.isSuccess) {
          numbers.append(n.get)
          input.set("")
        }
      })
  ), br,
  "Numbers: ", repeat(numbers)(renderer), br,
  "Evens: ", repeat(evens)(renderer), br,
  "Odds: ", repeat(odds)(renderer)
).render
```

### Form validation example

```scala
import scalajs.concurrent.JSExecutionContext.Implicits.queue
import scalatags.JsDom.all._
import io.udash._

val emailRegex = "([\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,})".r

val email = Property("example@mail.com")
email.addValidator(new Validator[String] {
  def apply(element: String)(implicit ec: ExecutionContext) = Future {
    element match {
      case emailRegex(text) => println("valid"); Valid
      case _ => println("invalid"); Invalid(Seq("It's not an email!"))
    }
  }
})

div(
  TextInput(email), br,
  "Valid: ", bindValidation(email,
    _ => span("Wait...").render,
    {
      case Valid => span("Yes").render
      case Invalid(_) => span("No").render
    },
    _ => span("ERROR").render
  )
).render
```

Find more examples in the [Udash Demos](https://github.com/UdashFramework/udash-demos) repository.
