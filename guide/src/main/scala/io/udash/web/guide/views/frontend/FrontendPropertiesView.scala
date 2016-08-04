package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.views.{ClickableImageFactory, ImageFactoryPrefixSet}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import org.scalajs.dom

import scalatags.JsDom

case object FrontendPropertiesViewPresenter extends DefaultViewPresenterFactory[FrontendPropertiesState.type](() => new FrontendPropertiesView)

class FrontendPropertiesView extends FinalView {
  import io.udash.web.guide.Context._

  import JsDom.all._
  import scalacss.ScalatagsCss._

  override def getTemplate: Modifier = div(
    h2("Property - the Udash Data Model"),
    p(
      "Udash provides the powerful Properties mechanism for a data model management. ",
      "The Properties system wraps your data model, in order to enable ",
      "convenient value change listening and validation. Take a look at the example below:"
    ),
    CodeBlock(
      """val username = Property[String]
        |
        |// Register value change listener
        |username.listen((name: String) =>
        |  println(s"Username changed to: $name")
        |)
        |
        |username.set("Udash")
        |
        |println(s"Starting validation of ${username.get}.")
        |username.isValid onComplete {
        |  case Success(Valid) =>
        |    println("It is valid, because there is no validator on this property...")
        |  case Success(Invalid(errors)) =>
        |    println("...but it might be invalid, if only we had added any.")
        |  case Failure(ex) =>
        |    println("Validation process went wrong...")
        |}""".stripMargin
    )(GuideStyles),
    p("That was the simple example. Now it is time for something more complex:"),
    CodeBlock(
      """case class NumbersInRange(minimum: Int, maximum: Int, numbers: Seq[Int])
        |
        |val numbers = ModelProperty(
        |  NumbersInRange(0, 42, Seq.empty)
        |)
        |
        |val s: SeqProperty[Int] = numbers.subSeq(_.numbers)
        |s.set(Seq(3,7,20,32))
        |s.replace(idx = 1, amount = 2, values = Seq(8,9,10))
        |""".stripMargin
    )(GuideStyles),
    p(
      "As you can see, you can create a Property based on case class or Seq. This will be discussed later. "
    ),
    h3("Types of Properties"),
    ClickableImageFactory(ImageFactoryPrefixSet.Frontend, "property.png", "Properties in the Udash", GuideStyles.get.imgBig, GuideStyles.get.frame),
    p("This might look quite complicated, but understanding the structure is not so hard. Let's go through it step by step."),
    p(
      i("ReadableProperty"), " is the simplest version of the data model representation. You have seen its whole API ",
      "except of the ", i("transform"), " method - this one will be described later. You can create a property ",
      "with any immutable type as a parameter. ", i("Property"), " extends ", i("ReadableProperty"), " API with value changing ",
      "methods."
    ),
    p(
      i("ModelProperty"), " contains other properties. The structure of the property can be described with a trait or an immutable case class. ",
      "When you create it, you can access any child property with ", i("subModel"), ", ", i("subProp"), " and ", i("subSeq"), " methods."
    ),
    CodeBlock(
      """case class NumbersInRange(minimum: Int, maximum: Int, numbers: Seq[Int])
        |val numbers = ModelProperty[NumbersInRange]
        |number.subProp(_.minimum).set(3)
        |
        |trait Person {
        |  def name: String
        |  def birthYear: Int
        |}
        |val person = ModelProperty[Person]
        |person.subProp(_.birthYear).set(2001)""".stripMargin
    )(GuideStyles),
    p(
      "If ", i("CastableProperty"), " is based on a trait or case class like NumbersInRange above, you can call an ", i("asModel"), " method ",
      "to cast it to ", i("ModelProperty"), ". Do not worry, this cast cannot fail at runtime, because it is checked ",
      "at compile time. ", i("ModelProperty"), " provides methods extracting a part of a model as ", i("Property"), " ",
      i("subModel"), ", ", i("subProp"), " and ", i("subSeq"), ". Usage is validated at compile time like the ",
      i("asModel"), " method."
    ),
    CodeBlock(
      """case class NumbersInRange(minimum: Int, maximum: Int, numbers: Seq[Int])
        |val base: CastableProperty[NumbersInRange] = ???
        |val numbers: ModelProperty[NumbersInRange] = base.asModel
        |//val error = base.asSeq""".stripMargin
    )(GuideStyles),
    p(
      i("SeqProperty"), " represents a sequence of properties.In addition to basic collection operations, it provides a lot of interesting methods like:"
    ),
    ul(GuideStyles.get.defaultList)(
      li(i("elemProperties"), " - gives access to mutable properties representing elements of the sequence"),
      li(i("listenStructure"), " - registers callback which will be called in case any element is added or removed from this property"),
      li(i("insert"), " - adds provided elements into sequence"),
      li(i("filter"), " - creates ", i("ReadableSeqProperty"), " containing matching elements, which will be synchronised with original property"),
      li(i("reversed"), " - creates ", i("SeqProperty"), " containing elements in reversed order, it will be synchronised with original property")
    ),
    p(
      i("SeqProperty"), " always contains ", i("Property"), " elements, but when you call the ",
      i("filter"), " method, it returns ", i("ReadableSeqProperty"), " witch contains ", i("ReadableProperty"), " elements."
    ),
    h3("Properties hierarchy"),
    p("In more complex models we can look at properties as a hierarchy. For example:"),
    CodeBlock(
      """trait User {
        |  def id: Int
        |  def name: String
        |}
        |
        |case class Comment(author: User, content: String, responses: Seq[Comment])
        |
        |val comment = ModelProperty[Comment]
        |comment.subProp(_.author.name).set("John") //set author name
        |// print responses
        |val responses = comment.subSeq(_.responses)
        |responses.elemProperties.foreach((r: CastableProperty[Comment]) =>
        |  println(r.asModel.subProp(_.content))
        |)""".stripMargin
    )(GuideStyles),
    p("The ", i("comment"), " property might be illustrated like:"),
    ClickableImageFactory(ImageFactoryPrefixSet.Frontend, "propertyhierarchy.png", "Properties hierarchy example.", GuideStyles.get.imgBig, GuideStyles.get.frame),
    p(
      "We can say that the ", i("comment"), " property is a parent of ", i("author"), ", ", i("content"), " and ", i("responses"), " properties, ",
      "while ", i("author"), " is the parent of ", i("id"), " and ", i("name"), " "
    ),
    h3("Properties validation"),
    p("The Property provides two means of data model validation: "),
    ul(GuideStyles.get.defaultList)(
      li(i("addValidator"), " - adds a new validator to a property"),
      li(i("isValid"), " - returns Future containing the validation result")
    ),
    p("Every validator must extend ", i("Validator[T]"), ", where T is a data model type. For example:"),
    CodeBlock(
      """object UserNameValidator extends Validator[String] {
        |  def apply(name: String)
        |           (implicit ec: ExecutionContext): Future[ValidationResult] =
        |    Future {
        |      if (name.length >= 3) Valid
        |      else Invalid(Seq("User name must contain at least 3 characters!"))
        |    }
        |}
        |
        |val comment = ModelProperty[Comment]
        |val name = comment.subProp(_.author.name)
        |name.addValidator(UserNameValidator)
        |name.set("A")
        |name.isValid                         // returns Future(Invalid)
        |comment.subProp(_.author).isValid    // returns Future(Invalid)
        |comment.isValid                      // returns Future(Invalid)
        |name.set("Abcde")
        |name.isValid                         // returns Future(Valid)
        |comment.subProp(_.author).isValid    // returns Future(Valid)
        |comment.isValid                      // returns Future(Valid)""".stripMargin
    )(GuideStyles),
    p("You can also pass an anonymous function to the ", i("addValidator"), " method:"),
    CodeBlock(
      """val comment = ModelProperty[Comment]
        |val name = comment.subProp(_.author.name)
        |name.addValidator((name: String) =>
        |  if (name.length >= 3) Valid
        |  else Invalid(Seq("User name must contain at least 3 characters!"))
        |)
        |name.set("A")
        |name.isValid                         // returns Future(Invalid)""".stripMargin
    )(GuideStyles),
    p("As you can see, properties validity is considered in the context of whole hierarchy. A property is valid when:"),
    ul(GuideStyles.get.defaultList)(
      li(i("Property"), " - every added validator accepts a value"),
      li(i("ModelProperty"), " - every added validator accepts the value and all subproperties are valid"),
      li(i("SeqProperty"), " - every added validator accepts the value and all added properties are valid")
    ),
    p("On value change all parent properties are automatically revalidated."),
    h3("Property value change listeners"),
    p("Similarly to validation, value changes are considered in the context of properties hierarchy. For example:"),
    CodeBlock(
      """val comment = ModelProperty[Comment]
        |comment.subProp(_.author.name).listen(_ => println("A"))
        |comment.subModel(_.author).listen(_ => println("B"))
        |comment.listen(_ => println("C"))
        |comment.subProp(_.content).listen(_ => println("D"))
        |comment.subProp(_.author.name).set("Name")    // prints A, B and C
        |comment.subProp(_.content).set("Content")     // prints C and D""".stripMargin
    )(GuideStyles),
    p("As you may notice, when you change a nested property, all its ancestors will be treated as changed."),
    p(
      "SeqProperty has the ", i("listenStructure"), " method witch allows you to listen on adding or removing elements ",
      "in this property, yet it will not fire on change inside children of a property. For example:"
    ),
    CodeBlock(
      """val ints = SeqProperty[Int]
        |ints.listen(_ => println("listen"))
        |ints.listenStructure(_ => println("listenStructure"))
        |
        |ints.insert(0, Seq(1, 2, 3))           // fires both listeners
        |ints.elemProperties.head.set(5)        // prints only "listen"""".stripMargin
    )(GuideStyles),
    h3("Properties transformation"),
    p("You can also change the type of a property. Let's assume the User model looks like below:"),
    CodeBlock(
      """case class UserId(asInt: Int)
        |
        |trait User {
        |  def id: UserId
        |  def name: String
        |}
        |
        |val user = ModelProperty[User]""".stripMargin
    )(GuideStyles),
    p("Now, if you want to obtain the user id property as Int, you can use the ", i("transform"), " method:"),
    CodeBlock(
      """val userId: Property[Int] = user.subProp(_.id)
        |  .transform(_.asInt, (i: Int) => UserId(i))
        |val name: ReadableProperty[String] = user.transform(_.name)""".stripMargin
    )(GuideStyles),
    p(
      "Remember that ", i("userId"), " is not a new property. All operations will be synchronized between the both ",
      "original and new property. "
    ),
    p(
      "It is possible to transform ", i("SeqProperty[A]"), " to ", i("SeqProperty[B]"), " and ",
      i("Property[A]"), " to ", i("SeqProperty[B]"), ". For example:"
    ),
    CodeBlock(
      """val csv = Property[String]("1,2,3,4,5")
        |val ints: ReadableSeqProperty[Int] =
        |  csv.transform(_.split(",").map(_.toInt).toSeq)
        |val floats: ReadableSeqProperty[Float] =
        |  ints.transform((i: Int) => i + 0.5f)""".stripMargin
    )(GuideStyles),
    h3("Properties combining"),
    p("You can combine two properties into a new one synchronised with both of them:"),
    CodeBlock(
      """val x = Property(5)
        |val y = Property(7)
        |val sum = x.combine(y)(_ + _)
        |println(sum.get) // prints: 12""".stripMargin
    )(GuideStyles),
    p(i("SeqProperty"), " has specialized version of this method which combines every element of seq with the provided one."),
    CodeBlock(
      """def isOdd(i: Int) = i % 2 == 1
        |val s = SeqProperty(1, 2, 3, 4, 5)
        |val odds = Property(true)
        |val filtered = s
        |  .combine(odds)((i: Int, odds: Boolean) => (i, isOdd(i) == odds))
        |  .filter((pair: (Int, Boolean)) => pair._2)
        |  .transform((pair: (Int, Boolean)) => pair._1)
        |
        |println(s.get, odds.get) // prints: Seq(1, 2, 3, 4, 5), true
        |println(filtered.get)    // prints: Seq(1, 3, 5)
        |
        |odds.set(false)
        |println(s.get, odds.get) // prints: Seq(1, 2, 3, 4, 5), false
        |println(filtered.get)    // prints: Seq(2, 4)
        |
        |s.append(6)
        |println(s.get, odds.get) // prints: Seq(1, 2, 3, 4, 5, 5), false
        |println(filtered.get)    // prints: Seq(2, 4, 6)""".stripMargin
    )(GuideStyles),
    h3("SeqProperty filtering"),
    p(
      "You can filter SeqProperty if you need, however you will not be able to modify the filtered property. ",
      "A filtered property is synchronised with the original one."
    ),
    CodeBlock(
      """val numbers = SeqProperty[Int](1, 2, 3)
        |val evens = numbers.filter(_ % 2 == 0) // evens.get == Seq(2)
        |numbers.append(4, 5, 6) // evens.get == Seq(2, 4, 6)
        |//evens.append(4, 5, 6) <- ERROR: evens is only the readable property""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at ", a(href := FrontendBindingsState.url)("Template Data Binding"),
      " chapter to read about the data model view bindings in Udash applications."
    )
  )
}