package io.udash.properties.seq

import io.udash.properties.single.ReadableProperty
import com.avsystem.commons._

/**
  * Describes changes in SeqProperty structure.
  *
  * @param idx Index where changes starts.
  * @param removed Properties removed from index `idx`.
  * @param added Properties added on index `idx`.
  * @tparam P Contained properties type.
  */
case class Patch[+P <: ReadableProperty[_]](idx: Int, removed: BSeq[P], added: BSeq[P], clearsProperty: Boolean)
