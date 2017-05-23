package io.udash.css

import io.udash.testing.UdashSharedTest

import scala.io.Source

class CssFileRendererTest extends UdashSharedTest {
  val stylesheets = Seq(StylesheetExample, SecondStylesheetExample)
  val testDir = "target/CssFileRendererTest"

  "CssFileRenderer" should {
    "render stylesheets to file" in {
      implicit val r = scalacss.internal.StringRenderer.defaultPretty
      val renderer = new CssFileRenderer(testDir, stylesheets, createMain = true)
      renderer.render()

      val mainCssLines = Source.fromFile(s"$testDir/main.css").getLines().toSeq
      mainCssLines.size should be(2)
      mainCssLines(0) should be("@import \"io.udash.css.StylesheetExample$.css\";")
      mainCssLines(1) should be("@import \"io.udash.css.SecondStylesheetExample$.css\";")

      val firstCss = Source.fromFile(s"$testDir/io.udash.css.StylesheetExample$$.css").getLines().mkString(System.lineSeparator())
      firstCss.trim should be(
        """.io-udash-css-StylesheetExample-common {
          |  background-color: red;
          |}
          |
          |@font-face {
          |  font-family: myFont;
          |  src: url(font.woff);
          |  font-stretch: expanded;
          |  font-style: italic;
          |  unicode-range: U+0-5;
          |}
          |
          |.io-udash-css-StylesheetExample-kf1 {
          |  height: 100px;
          |  width: 30px;
          |}
          |
          |@keyframes io-udash-css-StylesheetExample-animation {
          |  0.0% {
          |    height: 100px;
          |    width: 30px;
          |  }
          |
          |  20.0% {
          |    height: 150px;
          |    width: 30px;
          |  }
          |
          |  100.0% {
          |    height: 200px;
          |    width: 60px;
          |  }
          |
          |}
          |
          |.io-udash-css-StylesheetExample-test1 {
          |  background-color: red;
          |  margin: 12px auto;
          |  text-align: left;
          |  cursor: pointer;
          |}
          |
          |.io-udash-css-StylesheetExample-test1:hover {
          |  cursor: -moz-zoom-in;
          |  cursor: -webkit-zoom-in;
          |  cursor: -o-zoom-in;
          |  cursor: zoom-in;
          |}
          |
          |@media not handheld and (orientation:landscape) and (max-width:640px) {
          |  .io-udash-css-StylesheetExample-test1 {
          |    width: 400px;
          |  }
          |}
          |
          |.io-udash-css-StylesheetExample-test2 {
          |  background-color: red;
          |  margin: 12px auto;
          |  font-family: myFont;
          |}
          |
          |.io-udash-css-StylesheetExample-test2:hover {
          |  cursor: -moz-zoom-in;
          |  cursor: -webkit-zoom-in;
          |  cursor: -o-zoom-in;
          |  cursor: zoom-in;
          |}
          |
          |.io-udash-css-StylesheetExample-test2 ul,.io-udash-css-StylesheetExample-test2 li {
          |  margin: 50px;
          |}
          |
          |.extraStyle {
          |  background-color: red;
          |  margin: 24px auto;
          |}
          |
          |.StylesheetExample-indent-0 {
          |  padding-left: 0;
          |}
          |
          |.StylesheetExample-indent-1 {
          |  padding-left: 2ex;
          |}
          |
          |.StylesheetExample-indent-2 {
          |  padding-left: 4ex;
          |}
          |
          |.StylesheetExample-indent-3 {
          |  padding-left: 6ex;
          |}
          """.stripMargin.trim)

      val secondCss = Source.fromFile(s"$testDir/io.udash.css.SecondStylesheetExample$$.css").getLines().mkString(System.lineSeparator())
      secondCss.trim should be(
        """.io-udash-css-SecondStylesheetExample-test {
          |  margin: 12px auto;
          |  text-align: left;
          |  cursor: pointer;
          |}
          |
          |.io-udash-css-SecondStylesheetExample-test:hover {
          |  cursor: -moz-zoom-in;
          |  cursor: -webkit-zoom-in;
          |  cursor: -o-zoom-in;
          |  cursor: zoom-in;
          |}
          |
          |@media not handheld and (orientation:landscape) and (max-width:640px) {
          |  .io-udash-css-SecondStylesheetExample-test {
          |    width: 400px;
          |  }
          |}
          """.stripMargin.trim)
    }
  }
}
