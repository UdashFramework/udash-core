package io.udash.bindings

import io.udash._
import io.udash.testing.AsyncUdashFrontendTest

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

class ValidationTagsBindingTest extends AsyncUdashFrontendTest with Bindings { bindings: Bindings =>
  import scalatags.JsDom.all._

  "bindValidation" should {
    "render init view on validation start" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] = {
          val result = Promise[ValidationResult]
          result.future
        }
      })

      val template = div(
        valid(p)(
          _ => b("done").render,
          _ => i("Validating...").render,
          _ => b("error").render
        )
      ).render

      retrying(template.textContent should be("Validating..."))
    }

    "render result" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] = Future.successful(Valid)
      })

      val template = div(
        span(),
        valid(p) {
          case Valid => b("done").render
          case Invalid(_) => b("invalid").render
        },
        span()
      ).render

      for {
        _ <- retrying(template.textContent should be("done"))
        _ <- retrying(template.childNodes(0).textContent should be(""))
        _ <- retrying(template.childNodes(1).textContent should be("done"))
        r <- retrying(template.childNodes(2).textContent should be(""))
      } yield r
    }

    "render error if Future failed" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] = Future.failed(new NullPointerException)
      })

      val template = div(
        valid(p)(
          _ => b("done").render,
          _ => i("Validating...").render,
          _ => b("error").render
        )
      ).render

      retrying(template.textContent should be("error"))
    }

    "not swap position" in {
      val p = Property[Int](5)
      val p2 = Property[Int](3)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] = Future.failed(new NullPointerException)
      })

      val template = div(
        "1",
        valid(p)(
          _ => b("done").render,
          _ => i("Validating...").render,
          _ => b("error").render
        ),
        span("2"),
        valid(p)(
          _ => b("done").render,
          _ => i("Validating...").render,
          _ => b("Error").render
        ),
        div("3")
      ).render

      for {
        _ <- retrying(template.textContent should be("1error2Error3"))
        _ <- Future(p.set(-8))
        _ <- retrying(template.textContent should be("1error2Error3"))
        _ <- Future(p.set(2))
        _ <- retrying(template.textContent should be("1error2Error3"))
        _ <- Future(p.set(-5))
        r <- retrying(template.textContent should be("1error2Error3"))
      } yield r
    }

    "stop updates after `kill` call" in {
      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] =
          Future.successful(Valid)
      })

      val binding = valid(p)(
        _ => b("done", p.get).render
      )
      val template = div(binding).render

      for {
        _ <- retrying(template.textContent should be("done5"))
        _ <- Future(p.set(7))
        _ <- retrying(template.textContent should be("done7"))
        _ <- Future(binding.kill())
        _ <- Future(p.set(12))
        r <- retrying(template.textContent should be("done7"))
      } yield r
    }

    "clean nested bindings" in {

      val p = Property[Int](5)
      p.addValidator(new Validator[Int] {
        override def apply(element: Int): Future[ValidationResult] =
          Future.successful(Valid)
      })

      var nestedIdGen = 0
      val nestedCalls = mutable.Set.empty[Int]

      val binding = validWithNested(p)(
        (_, nested) => {
          val nestedId = nestedIdGen
          nestedIdGen += 1
          b("done", nested(produce(p) { v => nestedCalls += nestedId; span(v).render })).render
        }
      )
      val template = div(binding).render

      for {
        _ <- retrying {
          template.textContent should be("done5")
          nestedCalls should contain(0)
        }
        _ <- Future {
          nestedCalls.clear()
          p.set(7)
        }
        _ <- retrying {
          template.textContent should be("done7")
          nestedCalls should contain(1)
        }
        _ <- Future {
          nestedCalls.clear()
          p.set(12)
        }
        _ <- retrying {
          template.textContent should be("done12")
          nestedCalls shouldNot contain(0)
          nestedCalls should contain(2)
        }
        _ <- Future {
          nestedCalls.clear()
          p.set(7)
        }
        _ <- retrying {
          template.textContent should be("done7")
          nestedCalls shouldNot contain(0)
          nestedCalls shouldNot contain(1)
          nestedCalls should contain(3)
        }
        _ <- Future {
          nestedCalls.clear()
          p.set(12)
        }
        _ <- retrying {
          template.textContent should be("done12")
          nestedCalls shouldNot contain(0)
          nestedCalls shouldNot contain(1)
          nestedCalls shouldNot contain(2)
          nestedCalls should contain(4)
        }
        _ <- Future {
          binding.kill()
          nestedCalls.clear()
          p.set(15)
        }
        r <- retrying {
          template.textContent should be("done12")
          nestedCalls.isEmpty should be(true)
        }
      } yield r
    }
  }
}
