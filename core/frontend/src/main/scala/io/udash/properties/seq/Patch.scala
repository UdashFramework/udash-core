package io.udash.properties.seq

import io.udash.properties.single.ReadableProperty

/**
  * Describes changes in SeqProperty structure.
  *
  * @param idx Index where changes starts.
  * @param removed Properties removed from index `idx`.
  * @param added Properties added on index `idx`.
  * @tparam P Contained properties type.
  */
case class Patch[+P <: ReadableProperty[_]](idx: Int, removed: Seq[P], added: Seq[P], clearsProperty: Boolean)
