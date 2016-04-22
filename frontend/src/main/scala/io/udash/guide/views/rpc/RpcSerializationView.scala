package io.udash.guide.views.rpc

import io.udash._
import io.udash.guide.{Context, _}
import io.udash.guide.components.CodeBlock
import io.udash.guide.styles.partials.GuideStyles
import io.udash.guide.views.References
import io.udash.guide.views.rpc.demos.GenCodecsDemoComponent
import org.scalajs.dom

import scalatags.JsDom
import scalacss.ScalatagsCss._


case object RpcSerializationViewPresenter extends DefaultViewPresenterFactory[RpcSerializationState.type](() => new RpcSerializationView)

class RpcSerializationView extends View {
  import Context._

  import JsDom.all._

  override def getTemplate: dom.Element = div(
    h2("Serialization"),
    p(
      "Everything you send via RPC has to be serializable. Udash internally uses the ",
      a(href := References.avScalaCommonsGitHub)("AVSystem Scala Commons"), " library which supports serialization",
      " of basic Scala types out of the box and provides utilities for the custom data types serialization. "
    ),
    h3("GenCodecs"),
    p(
      "Udash RPC uses the ", i("GenCodec"), " mechanism from the ",
      a(href := References.avScalaCommonsGitHub)("AVSystem Scala Commons"), " library for the data serialization. It provides ",
      "serialization for a basic Scala types like: int, float, string, etc. It also brings automatic generation of serializers ",
      "for the types which are one of: "
    ),
    ul(GuideStyles.defaultList)(
      li("singleton type, e.g. an ", i("object")),
      li("case class whose every field type has its own ", i("GenCodec")),
      li(
        "class or trait whose companion object has a pair of case-class-like ", i("apply"), " and ", i("unapply"),
        " methods and every parameter type of ", i("apply"), " method has its own ", i("GenCodec")
      ),
      li(
        "sealed hierarchy in which every non-abstract subclass either has its own ", i("GenCodec"),
        " or it can be automatically materialized with the same mechanism "
      )
    ),
    p("Let's assume that we have got a RPC interface like below: "),
    CodeBlock(
      """object GenCodecServerRPC {
        |  case class DemoCaseClass(i: Int, s: String)
        |
        |  class DemoClass(val i: Int, val s: String) {
        |    var _v: Int = 5
        |  }
        |
        |  sealed trait Fruit
        |  case object Apple extends Fruit
        |  case object Orange extends Fruit
        |  case object Banana extends Fruit
        |}
        |
        |@RPC
        |trait GenCodecServerRPC {
        |  import GenCodecServerRPC._
        |
        |  def sendInt(el: Int): Future[Int]
        |  def sendString(el: String): Future[String]
        |  def sendSeq(el: Seq[String]): Future[Seq[String]]
        |  def sendMap(el: Map[String, Int]): Future[Map[String, Int]]
        |  def sendCaseClass(el: DemoCaseClass): Future[DemoCaseClass]
        |  def sendClass(el: DemoClass): Future[DemoClass]
        |  def sendSealedTrait(el: Fruit): Future[Fruit]
        |}""".stripMargin
    )(),
    p("The backend implementation is not important here. Let's assume that we use these methods as follows:"),
    CodeBlock(
      """val demoRpc: GenCodecServerRPC = ???
        |demoRpc.sendInt(Random.nextInt())
        |demoRpc.sendString(Random.nextString(10))
        |demoRpc.sendSeq(Seq(Random.nextString(5), Random.nextString(5)))
        |demoRpc.sendMap(Map(Random.nextString(5) -> Random.nextInt(), Random.nextString(5) -> Random.nextInt()))
        |demoRpc.sendCaseClass(DemoCaseClass(Random.nextInt(), Random.nextString(5)))
        |demoRpc.sendClass(new DemoClass(Random.nextInt(), Random.nextString(5)))
        |demoRpc.sendSealedTrait(Seq(Apple, Orange, Banana)(Random.nextInt(3)))""".stripMargin
    )(),
    p("Compilation of this code rises three errors:"),
    ul(GuideStyles.defaultList)(
      li("No ", i("GenCodec"), " found for ", i("GenCodecServerRPC.Fruit")),
      li("No ", i("GenCodec"), " found for ", i("GenCodecServerRPC.DemoCaseClass")),
      li("No ", i("GenCodec"), " found for ", i("GenCodecServerRPC.DemoClass"))
    ),
    p(
      "For custom data types it is required to create implicit value (in companion objects of these types) containing ", i("GenCodec"), ". ",
      "For sealed trait hierarchy or case calsses it is simple, add following lines in ", i("GenCodecServerRPC"), " object:"
    ),
    CodeBlock(
      """import com.avsystem.commons.serialization.GenCodec
        |object Fruit {
        |  implicit val fruitGenCodec = GenCodec.auto[Fruit]
        |}
        |object DemoCaseClass {
        |  implicit val DemoCaseClassCodec = GenCodec.auto[DemoCaseClass]
        |}""".stripMargin
    )(),
    p(
      "There is ", i("DemoClass"), " left. For the classic classes you have to implement ", i("GenCodec"), " manually. ",
      "Method ", i("write"), " gets two arguments. ", i("Output"), " allows you to write the basic Scala types and data structures, ",
      "you should use it to create representation of object provided as second argument. Method ", i("read"), " should convert ",
      "provided ", i("Input"), ", which allows you to read the basic Scala types and data structures, to object. Take a look at the example below. "
    ),
    CodeBlock(
      """object DemoClass {
        | import com.avsystem.commons.serialization.{Input, Output}
        |  implicit val DemoClassCodec = new GenCodec[DemoClass] {
        |    override def write(output: Output, value: DemoClass): Unit = {
        |      val values = output.writeList()
        |      values.writeElement().writeInt(value.i)
        |      values.writeElement().writeString(value.s)
        |      values.writeElement().writeInt(value._v)
        |      values.finish()
        |    }
        |
        |    override def read(input: Input): DemoClass = {
        |      val list = input.readList().get
        |      val i = list.nextElement().readInt().get
        |      val s = list.nextElement().readString().get
        |      val _v = list.nextElement().readInt().get
        |      val demo = new DemoClass(i, s)
        |      demo._v = _v
        |      demo
        |    }
        |  }
        |}""".stripMargin
    )(),
    p("Now this code should compile fine. Below you can test this exemple. "),
    new GenCodecsDemoComponent,
    h3("Default JSON serialization"),
    p(
      "The ", i("GenCodec"), "s mechanism provides abstraction layer over the serialization to the raw string, which is sent via websocket. ",
      "By default the Udash framework uses the ", i("GenCodec"), "s representation of data to serialize it to the JSON."
    ),
    p("Usually you do not need to worry about serialization from ",  i("GenCodec"), " to the raw string, unless you want to change serialization mechanism."),
    h3("Custom serialization"),
    p(
      "It is possible to change the serialization from JSON to anything else. Instead of using ",
      i("DefaultUdashRPCFramework"), " you can implement your custom ", i("UdashRPCFramework"), ". "
    ),
    CodeBlock(
      """object CustomUdashRPCFramework extends UdashRPCFramework {
        |  override type RawValue = ???
        |  def inputSerialization(value: RawValue): Input = ???
        |  def outputSerialization(valueConsumer: RawValue => Unit): Output = ???
        |  def stringToRaw(string: String): RawValue = ???
        |  def rawToString(raw: RawValue): String = ???
        |}""".stripMargin
    )(),
    p(
      "You have to implement five elements:",
      ul(GuideStyles.defaultList)(
        li(b("RawValue"), " - ", i("GenCodec"), " needs to be converted to this type and then this type needs to be converted into the string. Usually it is some kind of AST."),
        li(b("inputSerialization"), " - method which converts ", i("RawValue"), " into the ", i("Input"), " object."),
        li(b("outputSerialization"), " - method which returns the ", i("Output"), " object, which creates a ", i("RawValue"), " as the result."),
        li(b("stringToRaw"), " - converts a string into the ", i("RawValue"), "."),
        li(b("rawToString"), " - converts a ", i("RawValue"), " into the string.")
      )
    ),
    p(
      "In the Udash RPC repository you can find implementation based on the ",
      a(href := References.upickleHomepage)("uPickle"),
      " library."
    ),
    h2("What's next?"),
    p(
      "Now you know more about Udash RPC interfaces. You might also want to take a look at ",
      a(href := RpcClientServerState.url)("Client ➔ Server"), " or ", a(href := RpcClientServerState.url)("Server ➔ Client"), " communication."
    )
  ).render

  override def renderChild(view: View): Unit = ()
}