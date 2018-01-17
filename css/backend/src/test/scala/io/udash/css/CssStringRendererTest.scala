package io.udash.css

import io.udash.testing.UdashSharedTest

import scala.io.Source

class CssStringRendererTest extends UdashSharedTest {
  val stylesheets = Seq(StylesheetExample, SecondStylesheetExample)
  val testDir = "target/CssFileRendererTest"

  "CssStringRenderer" should {
    "render stylesheets to string" in {
      implicit val r = scalacss.internal.StringRenderer.defaultPretty
      val renderer = new CssStringRenderer(stylesheets)
      renderer.render().trim should be(
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
          |  25.0% {
          |    height: 150px;
          |    width: 30px;
          |  }
          |
          |  50.0% {
          |    height: 150px;
          |    width: 30px;
          |  }
          |
          |  75.0% {
          |    height: 100px;
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
          |@media not handheld and (orientation:landscape) and (max-width:840px) {
          |  .io-udash-css-StylesheetExample-test1 {
          |    width: 600px;
          |  }
          |}
          |
          |@media not handheld and (orientation:landscape) and (max-width:740px) {
          |  .io-udash-css-StylesheetExample-test1 {
          |    width: 500px;
          |  }
          |}
          |
          |@media not handheld and (orientation:landscape) and (max-width:640px) {
          |  .io-udash-css-StylesheetExample-test1 {
          |    width: 400px;
          |  }
          |}
          |
          |@media not handheld and (orientation:landscape) and (max-width:540px) {
          |  .io-udash-css-StylesheetExample-test1 {
          |    width: 300px;
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
          |
          |.io-udash-css-SecondStylesheetExample-test {
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
