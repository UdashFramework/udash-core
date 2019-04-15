package io.udash.properties

trait PropertyCreatorImplicitsLow { this: PropertyCreator.type =>
  implicit def materializeSingle[T]: PropertyCreator[T] = macro io.udash.macros.PropertyMacros.reifyPropertyCreator[T]
}

trait PropertyCreatorImplicits extends PropertyCreatorImplicitsLow { this: PropertyCreator.type =>
  implicit def materializeSeq[T: PropertyCreator]: SeqPropertyCreator[T] = new SeqPropertyCreator[T]

  implicit def tuple1[T: PropertyCreator]: ModelPropertyCreator[Tuple1[T]] =
    ModelPropertyCreator.materialize[Tuple1[T]]
  implicit def tuple2[T1: PropertyCreator, T2: PropertyCreator]: ModelPropertyCreator[(T1, T2)] =
    ModelPropertyCreator.materialize[(T1, T2)]
  implicit def tuple3[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3)] =
    ModelPropertyCreator.materialize[(T1, T2, T3)]
  implicit def tuple4[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4)]
  implicit def tuple5[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5)]
  implicit def tuple6[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6)]
  implicit def tuple7[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7)]
  implicit def tuple8[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8)]
  implicit def tuple9[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9)]
  implicit def tuple10[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10)]
  implicit def tuple11[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11)]
  implicit def tuple12[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12)]
  implicit def tuple13[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13)]
  implicit def tuple14[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14)]
  implicit def tuple15[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15)]
  implicit def tuple16[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16)]
  implicit def tuple17[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17)]
  implicit def tuple18[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator, T18: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18)]
  implicit def tuple19[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator, T18: PropertyCreator, T19: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19)]
  implicit def tuple20[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator, T18: PropertyCreator, T19: PropertyCreator, T20: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20)]
  implicit def tuple21[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator, T18: PropertyCreator, T19: PropertyCreator, T20: PropertyCreator, T21: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21)]
  implicit def tuple22[T1: PropertyCreator, T2: PropertyCreator, T3: PropertyCreator, T4: PropertyCreator, T5: PropertyCreator, T6: PropertyCreator, T7: PropertyCreator, T8: PropertyCreator, T9: PropertyCreator, T10: PropertyCreator, T11: PropertyCreator, T12: PropertyCreator, T13: PropertyCreator, T14: PropertyCreator, T15: PropertyCreator, T16: PropertyCreator, T17: PropertyCreator, T18: PropertyCreator, T19: PropertyCreator, T20: PropertyCreator, T21: PropertyCreator, T22: PropertyCreator]: ModelPropertyCreator[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)] =
    ModelPropertyCreator.materialize[(T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22)]
}