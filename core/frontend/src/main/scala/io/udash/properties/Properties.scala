package io.udash.properties

trait Properties {
  val Property                             = io.udash.properties.Property
  val ModelProperty                        = io.udash.properties.ModelProperty
  val SeqProperty                          = io.udash.properties.SeqProperty
  val CallbackSequencer                    = io.udash.properties.CallbackSequencer

  type CastableReadableProperty[A]         = io.udash.properties.CastableReadableProperty[A]
  type CastableProperty[A]                 = io.udash.properties.CastableProperty[A]
  type ReadableProperty[A]                 = io.udash.properties.ReadableProperty[A]
  type Property[A]                         = io.udash.properties.Property[A]
  type ReadableModelProperty[A]            = io.udash.properties.ReadableModelProperty[A]
  type ModelProperty[A]                    = io.udash.properties.ModelProperty[A]
  type ReadableSeqProperty[A]              = io.udash.properties.ReadableSeqProperty[A, _ <: ReadableProperty[A]]
  type SeqProperty[A]                      = io.udash.properties.SeqProperty[A, _ <: Property[A]]

  type Patch[+P <: ReadableProperty[_]]    = io.udash.properties.Patch[P]

  type Validator[T]                        = io.udash.properties.Validator[T]
  type ValidationResult                    = io.udash.properties.ValidationResult
  val  Valid                               = io.udash.properties.Valid
  val  Invalid                             = io.udash.properties.Invalid
}
