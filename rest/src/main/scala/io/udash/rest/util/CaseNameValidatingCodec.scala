package io.udash.rest
package util

import com.avsystem.commons.misc.Opt
import com.avsystem.commons.serialization.GenCodec.ReadFailure
import com.avsystem.commons.serialization.{GenCodec, Input, InputWrapper, ObjectInput}

/**
 * Builds a `GenCodec[T]` for a single case `T` of a `@flatten` (flat) sealed hierarchy rooted at `R`. It
 * delegates reading/writing to the root codec but, on read, additionally rejects objects whose
 * discriminator field (`caseFieldName`) does not equal the expected `caseName` - see
 * [[CaseNameValidatingInput]].
 */
object CaseNameValidatingCodec {
  // `caseName` is a by-name parameter to avoid recursive access problem
  def apply[T, R >: T](rootCodec: GenCodec[R], caseFieldName: String, caseName: => String): GenCodec[T] =
    GenCodec.create(
      input => rootCodec.read(new CaseNameValidatingInput(input, caseFieldName, caseName)).asInstanceOf[T],
      (output, value) => rootCodec.write(output, value),
    )
}

/**
 * Input that rejects objects with unexpected discriminator field value.
 */
final class CaseNameValidatingInput(
  protected val wrapped: Input,
  caseFieldName: String,
  expectedCaseName: String,
) extends InputWrapper {

  override def readObject(): ObjectInput = {
    val oi = super.readObject()
    oi.peekField(caseFieldName) match {
      case Opt(fi) =>
        val actualCaseName = fi.readSimple().readString()
        if (actualCaseName != expectedCaseName) {
          throw new ReadFailure(s"Expected $caseFieldName to be equal to $expectedCaseName but got $actualCaseName")
        }
        oi
      case Opt.Empty =>
        // This means that either:
        // * the discriminator field is completely missing - this will be validated by the sealed hierarchy codec
        // * wrapped Input doesn't support peeking - in Udash REST this won't happen as it always uses JsonStringInput
        oi
    }
  }
}
