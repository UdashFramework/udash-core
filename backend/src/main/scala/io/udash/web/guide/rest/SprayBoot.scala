package io.udash.web.guide.rest

import akka.actor.{ActorRef, ActorSystem}
import io.udash.web.server.ApplicationServer
import spray.servlet.WebBoot

class SprayBoot extends WebBoot {
  override def system: ActorSystem = ActorSystem("spray-system")
  override def serviceActor: ActorRef = system.actorOf(DevsGuideRestActor.props(ApplicationServer.restPrefix))
}
