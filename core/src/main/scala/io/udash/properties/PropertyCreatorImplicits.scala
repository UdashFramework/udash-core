package io.udash.properties

trait PropertyCreatorImplicitsLow {
  implicit def materializeSingle[T]: PropertyCreator[T] =
    macro io.udash.macros.PropertyMacros.reifyPropertyCreator[T]
}

trait PropertyCreatorImplicits extends PropertyCreatorImplicitsLow {
  implicit def materializeSeq[T : PropertyCreator]: SeqPropertyCreator[T] =
    new SeqPropertyCreator[T]

  private type PC[T] = PropertyCreator[T]
  implicit def tuple1[T : PC]: ModelPropertyCreator[Tuple1[T]] =
    ModelPropertyCreator.materialize[Tuple1[T]]
  implicit def tuple2[T1 : PC, T2 : PC]: ModelPropertyCreator[(T1, T2)] =
    ModelPropertyCreator.materialize[(T1, T2)]
  implicit def tuple3[T1 : PC, T2 : PC, T3 : PC]: ModelPropertyCreator[(T1, T2, T3)] =
    ModelPropertyCreator.materialize[(T1, T2, T3)]
  implicit def tuple4[T1 : PC, T2 : PC, T3 : PC, T4 : PC]: ModelPropertyCreator[(T1, T2, T3, T4)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4)]
  implicit def tuple5[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5)]
  implicit def tuple6[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6)]
  implicit def tuple7[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7)]
  implicit def tuple8[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8)]
  implicit def tuple9[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9)]
  implicit def tuple10[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)]
  implicit def tuple11[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)]
  implicit def tuple12[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)]
  implicit def tuple13[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)]
  implicit def tuple14[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)]
  implicit def tuple15[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)]
  implicit def tuple16[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)]
  implicit def tuple17[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)]
  implicit def tuple18[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC, T18 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)]
  implicit def tuple19[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC, T18 : PC, T19 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)]
  implicit def tuple20[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC, T18 : PC, T19 : PC, T20 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)]
  implicit def tuple21[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC, T18 : PC, T19 : PC, T20 : PC, T21 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)]
  implicit def tuple22[T1 : PC, T2 : PC, T3 : PC, T4 : PC, T5 : PC, T6 : PC, T7 : PC, T8 : PC, T9 : PC, T10 : PC, T11 : PC, T12 : PC, T13 : PC, T14 : PC, T15 : PC, T16 : PC, T17 : PC, T18 : PC, T19 : PC, T20 : PC, T21 : PC, T22 : PC]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)]
}