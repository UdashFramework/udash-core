package io.udash.web.guide.views.frontend

import io.udash._
import io.udash.css.CssView
import io.udash.web.commons.components.CodeBlock
import io.udash.web.commons.views.{ClickableImageFactory, ImageFactoryPrefixSet}
import io.udash.web.guide._
import io.udash.web.guide.styles.partials.GuideStyles
import scalatags.JsDom

case object FrontendPropertiesViewFactory extends StaticViewFactory[FrontendPropertiesState.type](() => new FrontendPropertiesView)

class FrontendPropertiesView extends FinalView with CssView {

  import JsDom.all._
  import io.udash.web.guide.Context._

  override def getTemplate: Modifier = div(
    h2("Property - the Udash Data Model"),
    p(
      "Udash provides the powerful Properties mechanism for a data model management. ",
      "The Properties system wraps your data model, in order to enable ",
      "convenient value change listening. Take a look at the example below:"
    ),
    CodeBlock(
      """val username = Property.blank[String]
        |
        |// Register value change listener
        |username.listen((name: String) =>
        |  println(s"Username changed to: $name")
        |)
        |
        |username.set("Udash")""".stripMargin
    )(GuideStyles),
    p("That was the simple example. Now it is time for something more complex:"),
    CodeBlock(
      """case class NumbersInRange(minimum: Int, maximum: Int, numbers: Seq[Int])
        |object NumbersInRange extends HasModelPropertyCreator[NumbersInRange]
        |
        |val numbers = ModelProperty(
        |  NumbersInRange(0, 42, Seq.empty)
        |)
        |
        |val s: SeqProperty[Int] = numbers.subSeq(_.numbers)
        |s.set(Seq(3,7,20,32))
        |s.replace(idx = 1, amount = 2, values = Seq(8,9,10):_*)
        |""".stripMargin
    )(GuideStyles),
    p("As you can see, you can create a Property based on case class or Seq. This will be discussed later. "),
    h4("Initialization"),
    p(
      "Each property should be initialized with some meaningful value. You can put an initial value directly into ",
      "the property constructor as in the example above, but it is also possible to use a blank value. The blank constructor looks ",
      "for an implicit instance of the type class ", i("Blank[T]"), " and uses this value to initialize the property. ",
      "The blank values are defined for some basic types like ", i("String"), ", ", i("Int"), ", ", i("Option"), " or collections. "
    ),
    CodeBlock(
      """case class NumbersInRange(minimum: Int, maximum: Int, numbers: Seq[Int])
        |object NumbersInRange extends HasModelPropertyCreator[NumbersInRange] {
        |  implicit val blank: Blank[NumbersInRange] =
        |    Blank.Simple(NumbersInRange(0, 42, Seq.empty))
        |}
        |
        |val numbers = ModelProperty.blank[NumbersInRange]""".stripMargin
    )(GuideStyles),
    h3("Types of Properties"),
    p(
      i("ReadableProperty"), " is the simplest version of the data model representation. It allows you to get wrapped value ",
      "or register a listener. ", i("Property"), " extends ", i("ReadableProperty"),
      " API with value changing methods. You can create ", i("Property"), " containing any type you want. ",
      "Remember: there is no guarantee that the ", i("get"), " method called twice will return the same object."
    ),
    p(
      i("(Readable)ModelProperty"), " contains other properties. The structure of the property can be described with a trait, ",
      "a class or a case class. In case of traits all abstract vals and defs (without parameters) are considered as subproperties. ",
      "In case of classes all elements of the primary constructor are considered as subproperties. ",
    ),
    p(
      "If you want to use your type as a model template you have to create ", i("ModelPropertyCreator"), " for this type. ",
      "You can do it by creating companion object extending ", i("HasModelPropertyCreator"),
      " or explicitly using ", i("ModelPropertyCreator.materialize"), " method in the companion object of your type. ",
      "You can access any subproperty with ", i("subModel"), ", ", i("subProp"), " and ", i("subSeq"), " methods."
    ),
    p("Take a look at these two equivalent examples of model creation:"),
    CodeBlock(
      """import io.udash._
        |
        |trait Person {
        |  def name: String
        |  def birthYear: Int
        |  def friends: Seq[Person] // it'll be SeqProperty
        |}
        |object Person extends HasModelPropertyCreator[Person]
        |
        |val person = ModelProperty(new Person {
        |  def name: String = "John"
        |  def birthYear: Int = 1987
        |  def friends: Seq[Person] = Seq.empty
        |})
        |person.subProp(_.birthYear).set(2001)""".stripMargin
    )(GuideStyles),
    CodeBlock(
      """import io.udash._
        |
        |case class Person(
        |  name: String
        |  birthYear: Int
        |  friends: Seq[Person] // it'll be SeqProperty
        |)
        |object Person {
        |  implicit val modelPropertyCreator: ModelPropertyCreator[Person] =
        |    ModelPropertyCreator.materialize[Person]
        |}
        |
        |val person = ModelProperty(Person("John", 1987, Seq.empty))
        |person.subProp(_.birthYear).set(2001)""".stripMargin
    )(GuideStyles),
    p(
      i("SeqProperty"), " represents a sequence of properties. It supports partial updates of the value with methods like: ",
      i("append"), ", ", i("replace"), " or ", i("remove"), ". Method ", i("listenStructure"),
      " registers callback which will be called in case any element is added or removed from this property. ",
      "You can also access a sequence of properties contained in the ", i("SeqProperty"), " with ",
      i("elemProperties"), " method."
    ),
    p(
      "Elements of ", i("SeqProperty"), " can be any of properties type. This is expressed with ", i("CastableProperty"),
      " type, which can be casted to Model or SeqProperty with compile-time checked methods: ", i("asModel"), " and ",
      i("asSeq"), ". Take a look at the following example: "
    ),
    CodeBlock(
      """import io.udash._
        |
        |val people = SeqProperty[Person](Seq(Person("John", 1987, Seq.empty)))
        |people.foreach { p =>
        |  // it works because there is a ModelPropertyCreator for Person
        |  val person = p.asModel
        |  person.subProp(_.name).get
        |}""".stripMargin
    )(GuideStyles),
    p(
      "The standard import of Udash utils provides ", i("SeqProperty"), " alias with a single generic type describing type of ",
      "data contained in the elements. It assumes that in ", i("SeqProperty"), " elements type is ",
      i("CastableProperty"), " and ", i("ReadableCastableProperty"), " for ", i("ReadableSeqProperty"), ". You can import ",
      i("io.udash.seq.SeqProperty"), " and provide the second generic argument to specify element type."
    ),
    p(
      i("SeqProperty"), " may be based on: ",
      i("scala.collection.Seq"), ", ",
      i("scala.collection.immutable.Seq"), ", ",
      i("scala.collection.immutable.List"), ", ",
      i("scala.collection.immutable.Vector"), " and ",
      i("scala.collection.mutable.Seq"), ". ",
      "Note that due to, ", i("SeqProperty"), "'s mutable nature, you may incur a performance overhead when calling ", i("subSeq"),
      " on fields of type more specific than ", i("scala.collection.Seq"),
      ". ", "This is due to potential conversion of the underlying data structure."
    ),

    h3("Properties hierarchy"),
    p("In more complex models we can look at properties as a hierarchy. For example:"),
    CodeBlock(
      """import io.udash._
        |
        |class User(val id: Int, val name: String)
        |object User extends HasModelPropertyCreator[User]
        |
        |case class Comment(author: User, content: String, responses: Seq[Comment])
        |object Comment extends HasModelPropertyCreator[Comment]
        |
        |val comment = ModelProperty(
        |  Comment(new User(1, "Udash"), "Hello, World!", Seq.empty)
        |)
        |comment.subProp(_.author.name).set("John") //set author name
        |// print responses
        |val responses = comment.subSeq(_.responses)
        |responses.elemProperties.foreach((r: CastableProperty[Comment]) =>
        |  println(r.asModel.subProp(_.content))
        |)""".stripMargin
    )(GuideStyles),
    p("The ", i("comment"), " property might be illustrated like:"),
    ClickableImageFactory(ImageFactoryPrefixSet.Frontend, "propertyhierarchy.png", "Properties hierarchy example.", GuideStyles.imgBig, GuideStyles.frame),
    p(
      "We can say that the ", i("comment"), " property is a parent of ", i("author"), ", ", i("content"), " and ", i("responses"), " properties, ",
      "while ", i("author"), " is the parent of ", i("id"), " and ", i("name"), "."
    ),
    h3("Property value change listeners"),
    p("On any property you can register a value change listener. The value change listeners are aware of the properties hierarchy. "),
    ul(GuideStyles.defaultList)(
      li(i("Property"), " - fires the listeners when you change its value."),
      li(i("ModelProperty"), " - fires the listeners when you change a value of any subproperty."),
      li(i("SeqProperty"), " - fires the listeners when you change a value of any element or the structure of the sequence.")
    ),
    p("Take a look at the following example:"),
    CodeBlock(
      """import io.udash._
        |
        |val comment = ModelProperty(
        |  Comment(new User(1, "Udash"), "Hello, World!", Seq.empty)
        |)
        |comment.subProp(_.author.name).listen(_ => println("A"))
        |comment.subModel(_.author).listen(_ => println("B"))
        |comment.listen(_ => println("C"))
        |comment.subProp(_.content).listen(_ => println("D"))
        |comment.subProp(_.author.name).set("Name")    // prints A, B and C
        |comment.subProp(_.content).set("Content")     // prints D and C""".stripMargin
    )(GuideStyles),
    p("As you may notice, when you change a nested property, all its ancestors will be treated as changed."),
    p(
      "SeqProperty has the ", i("listenStructure"), " method which allows you to listen on adding or removing elements ",
      "in this property, yet it will not fire on change inside children of a property. For example:"
    ),
    CodeBlock(
      """val ints = SeqProperty.blank[Int]
        |ints.listen(_ => println("listen"))
        |ints.listenStructure(_ => println("listenStructure"))
        |
        |ints.insert(0, Seq(1, 2, 3))           // fires both listeners
        |ints.elemProperties.head.set(5)        // prints only "listen"""".stripMargin
    )(GuideStyles),
    h3("Properties transformation"),
    p("You can also change the type of a property. Let's assume the ", i("User"), " model looks like below:"),
    CodeBlock(
      """case class UserId(asInt: Int)
        |
        |class User(val id: UserId, val name: String)
        |object User extends HasModelPropertyCreator[User]
        |
        |val user = ModelProperty(
        |  new User(UserId(0), "test")
        |)""".stripMargin
    )(GuideStyles),
    p("Now, if you want to obtain the user id property as Int, you can use the ", i("transform"), " method:"),
    CodeBlock(
      """val userId: Property[Int] = user.subProp(_.id)
        |  .transform(_.asInt, (i: Int) => UserId(i))
        |val name: ReadableProperty[String] = user.transform(_.name)""".stripMargin
    )(GuideStyles),
    p(
      "Remember that ", i("userId"), " is not an independent property. All operations will be synchronized between the both ",
      "original and new property. You do not need to pass the second argument to the ", i("transform"), " method, then ",
      "you will receive ", i("ReadableProperty"), " as a result."
    ),
    p(
      "It is possible to transform ", i("SeqProperty[A]"), " to ", i("SeqProperty[B]"), " and ",
      i("Property[A]"), " to ", i("SeqProperty[B]"), ". For example:"
    ),
    CodeBlock(
      """val csv = Property[String]("1,2,3,4,5")
        |val ints: ReadableSeqProperty[Int] =
        |  csv.transformToSeq(_.split(",").map(_.toInt).toSeq)
        |val floats: ReadableSeqProperty[Float] =
        |  ints.transform((i: Int) => i + 0.5f)""".stripMargin
    )(GuideStyles),
    h4("Properties combining"),
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
    h4("SeqProperty filtering"),
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
    h4("SeqProperty zip/zipAll"),
    p("It is possible to zip elements from two ", i("SeqProperties"), ". You have to pass a combiner, so you can combine the elements as you want."),
    CodeBlock(
      """val numbers = SeqProperty[Int](1, 2, 3)
        |val strings = SeqProperty[String]("A", "B", "C", "D")
        |val z = numbers.zip(strings)((_, _))
        |//z.get == Seq((1,"A"), (2,"B"), (3,"C"))
        |val all = numbers.zipAll(strings)((_, _), Property(-1), Property("empty"))
        |//all.get == Seq((1,"A"), (2,"B"), (3,"C"), (-1, "D"))
        |
        |numbers.append(7)
        |numbers.append(8)
        |//z.get == Seq((1,"A"), (2,"B"), (3,"C"), (7,"D"))
        |//all.get == Seq((1,"A"), (2,"B"), (3,"C"), (7,"D"), (8,"empty"))""".stripMargin
    )(GuideStyles),
    h4("SeqProperty zipWithIndex"),
    p("It is also very easy to create sequence of elements zipped with index."),
    CodeBlock(
      """val strings = SeqProperty[String]("A", "B", "C", "D")
        |val withIdx = strings.zipWithIndex
        |// withIdx.get == Seq(("A",0), ("B",1), ("C",2), ("D",3))
        |
        |strings.append("Another")
        |// withIdx.get == Seq(("A",0), ("B",1), ("C",2), ("D",3), ("Another",4))
        |
        |strings.prepend("First")
        |// withIdx.get == Seq(("First",0), ("A",1), ("B",2), ("C",3), ("D",4), ("Another",5))
        |
        |strings.clear()
        |// withIdx.get == Seq()""".stripMargin
    )(GuideStyles),
    h4("Ensuring readonly access"),
    p(
      "When you expose a property and you want to ensure that the exposed reference enables only the read access ",
      "use the ", i("_.readable"), " method which does not allow to modify the property by type casting. "
    ),
    CodeBlock(
      """val p: Property[Int] = Property(0)
        |val ro: ReadableProperty[Int] = p.readable""".stripMargin
    )(GuideStyles),
    h3("Immutable properties"),
    p(
      "GUI components may take numerous arguments defining their behaviour as the properties. ",
      "Sometimes you do not need to change these options after component creation and transformation ",
      "of arguments to  properties is an unnecessary overhead. The static value can be wrapped ",
      "into the immutable property, which takes advantage of the value immutability ",
      "and improves the application performance without reducing the API flexibility. ",
      "The ", i("import io.udash._"), " provides three extension methods: ",
      i("_.toProperty"), ", ", i("_.toModelProperty"), " and ", i("_.toSeqProperty"), "."
    ),
    CodeBlock(
      """def component(
        |  i: ReadableProperty[Int],
        |  model: ReadableModelProperty[ComplexModelClass]
        |) = ???
        |
        |val number: Int = ???
        |val complex: ComplexModelClass = ???
        |component(number.toProperty, complex.toModelProperty)""".stripMargin
    )(GuideStyles),
    h2("What's next?"),
    p(
      "Take a look at ", a(href := FrontendBindingsState.url)("Template Data Binding"),
      " chapter to read about the data model view bindings in Udash applications."
    )
  )
}
