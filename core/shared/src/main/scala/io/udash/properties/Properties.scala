package io.udash.properties

trait Properties {
  val Property                             = single.Property
  val ModelProperty                        = model.ModelProperty
  val SeqProperty                          = seq.SeqProperty
  val CallbackSequencer                    = io.udash.properties.CallbackSequencer

  type CastableReadableProperty[A]         = single.CastableReadableProperty[A]
  type CastableProperty[A]                 = single.CastableProperty[A]
  type ReadableProperty[A]                 = single.ReadableProperty[A]
  type Property[A]                         = single.Property[A]
  type ReadableModelProperty[A]            = model.ReadableModelProperty[A]
  type ModelProperty[A]                    = model.ModelProperty[A]
  type ReadableSeqProperty[A]              = seq.ReadableSeqProperty[A, _ <: ReadableProperty[A]]
  type SeqProperty[A]                      = seq.SeqProperty[A, _ <: Property[A]]

  type Patch[+P <: ReadableProperty[_]]    = seq.Patch[P]

  type ValidationError                     = io.udash.properties.ValidationError
  type DefaultValidationError              = io.udash.properties.DefaultValidationError
  type Validator[T]                        = io.udash.properties.Validator[T]
  type ValidationResult                    = io.udash.properties.ValidationResult
  val  Valid                               = io.udash.properties.Valid
  val  Invalid                             = io.udash.properties.Invalid
  val  DefaultValidationError              = io.udash.properties.DefaultValidationError
}
