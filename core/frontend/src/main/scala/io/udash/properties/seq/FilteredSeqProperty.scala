package io.udash.properties.seq

import java.util.UUID

import io.udash.properties.{CallbackSequencer, PropertyRegistration}
import io.udash.properties.single.ReadableProperty
import io.udash.utils.Registration

import scala.collection.mutable
import scala.concurrent.ExecutionContext

class FilteredSeqProperty[A, ElemType <: ReadableProperty[A]]
(origin: ReadableSeqProperty[A, _ <: ElemType],
   matcher: A => Boolean,
   override val id: UUID) extends ReadableSeqProperty[A, ElemType] {

     private def loadPropsFromOrigin() =
       origin.elemProperties.filter(el => matcher(el.get))

     private var filteredProps: Seq[ElemType] = loadPropsFromOrigin()
     private var filteredValues: Seq[A] = filteredProps.map(_.get)

     private val filteredListeners: mutable.Set[(Seq[A]) => Any] = mutable.Set.empty
     private val structureListeners: mutable.Set[(Patch[ElemType]) => Any] = mutable.Set.empty

     private def elementChanged(p: ElemType)(v: A): Unit = {
       val props = loadPropsFromOrigin()
       val oldIdx = filteredProps.indexOf(p)
       val newIdx = props.indexOf(p)

       val patch = (oldIdx, newIdx) match {
         case (oi, -1) if oi != -1 =>
           filteredProps = filteredProps.slice(0, oi) ++ filteredProps.slice(oi+1, filteredProps.size)
           filteredValues = filteredProps.map(_.get)
           Patch[ElemType](oi, Seq(p), Seq.empty, filteredProps.isEmpty)
         case (-1, ni) if ni != -1 =>
           filteredProps = (filteredProps.slice(0, ni) :+ p) ++ filteredProps.slice(ni, filteredProps.size)
           filteredValues = filteredProps.map(_.get)
           Patch[ElemType](ni, Seq.empty, Seq(p), filteredProps.isEmpty)
         case _ => null
       }

       if (oldIdx != newIdx || oldIdx != -1) {
         val callbackProps = props.map(_.get)
         CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => filteredListeners.foreach(_.apply(callbackProps)))
       }

       if (patch != null)
         CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${p.id}", () => structureListeners.foreach(_.apply(patch)))
     }

     private val registrations = mutable.HashMap.empty[ElemType, Registration]

     origin.elemProperties.foreach(p => registrations(p) = p.listen(elementChanged(p)))
     origin.listenStructure(patch => {
       patch.removed.foreach(p => if (registrations.contains(p)) {
         registrations(p).cancel()
         registrations.remove(p)
       })
       patch.added.foreach(p => registrations(p) = p.listen(elementChanged(p)))

       val added = patch.added.filter(p => matcher(p.get))
       val removed = patch.removed.filter(p => matcher(p.get))
       if (added.nonEmpty || removed.nonEmpty) {
         val props = loadPropsFromOrigin()
         val idx = origin.elemProperties.slice(0, patch.idx).count(p => matcher(p.get))
         val callbackProps = props.map(_.get)

         filteredProps = filteredProps.slice(0, idx) ++ added ++ filteredProps.slice(idx + removed.size, filteredProps.size)
         filteredValues = filteredProps.map(_.get)

         val filteredPatch = Patch[ElemType](idx, removed, added, filteredProps.isEmpty)

         CallbackSequencer.queue(s"${this.id.toString}:fireValueListeners", () => filteredListeners.foreach(_.apply(callbackProps)))
         CallbackSequencer.queue(s"${this.id.toString}:fireElementsListeners:${patch.hashCode()}", () => structureListeners.foreach(_.apply(filteredPatch)))
       }
     })

     override def listen(l: (Seq[A]) => Any): Registration = {
       filteredListeners.add(l)
       new Registration {
         override def cancel(): Unit = filteredListeners.remove(l)
       }
     }

     override def listenStructure(l: (Patch[ElemType]) => Any): Registration = {
       structureListeners.add(l)
       new PropertyRegistration(structureListeners, l)
     }

     override def elemProperties: Seq[ElemType] =
       filteredProps

     override def get: Seq[A] =
       filteredValues

     override protected[properties] def fireValueListeners(): Unit =
       origin.fireValueListeners()

     override protected[properties] def parent: ReadableProperty[_] =
       origin.parent

     override def validate(): Unit =
       origin.validate()

     override protected[properties] def valueChanged(): Unit =
       origin.valueChanged()

     override implicit protected[properties] def executionContext: ExecutionContext =
       origin.executionContext
   }
