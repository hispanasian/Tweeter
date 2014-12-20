package com.tweeter.module

import akka.actor._
import com.tweeter.module.relationship.{RelationshipMessage, Relationship}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * Tweeter will be the Engine used by the Tweeter application. It will start up all the Modules listed below and is
 * expected to be the starting point for the application.
 *
 * Modules:
 * Tweet
 * Relationship
 * Metric
 * Created by Carlos on 12/17/2014.
 */
object Tweeter extends Engine
{
  def main(args:Array[String]):Unit =
  {
    Tweeter.start()
  }

  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module](Relationship)

  /**
   * Returns system with this Module created on system.
   * @param system      The ActorSystem on which this Module is created
   * @param modules     The modules that will be a part of this module
   * @return            system with this Module created on system.
   */
  override def start(system: ActorSystem, modules: List[Module]): ActorSystem =
  {
    system.actorOf(Props(classOf[Tweeter], modules), name = name())
    return system;
  }

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the default Config for this Module.
   * @return  The config provided by application.conf
   */
  override protected def config(): Config = ConfigFactory.load().getConfig("application")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]("tweeter")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "tweeter"
   */
  override def name(): String = "tweeter"

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  override protected def getRESTRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns the 'topic' for which the message is categorized when being sent by this Module to the
   * DistributedPubSubMediator if it is a Message handled by this Module. If the Message is not handeled by this
   * Module, the default behavior is to return an empty string. The suggested topic for a Message is the full classpath.
   * @param message The Message for whose 'topic' is being searched
   * @return        The 'topic' associated with Message
   */
  override def getTopic(message: Message): String =
  {
    message match
    {
      case x:Message => classOf[Message].getCanonicalName
      case _ => ""
    }
  }

  /**
   * Returns an ActorRef that knows how to route a Message to the correct Actor
   * param context The ActorContext in which the router is created
   * @return        an ActorRef that can route a Message using Akka Actors
   */
  override protected def getAkkaRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of the ModuleActor used by this Module
   */
  override protected def getModule(): Class[_<:ModuleActor] = classOf[Tweeter]
}

class Tweeter(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
{
  val relationshipRouter = Relationship.getModule(context, AKKA())

  /**
   * Processes mssg and sends the response to handler. The final response should be sent back to client.
   * @param mssg    The mssg that is being processed
   * @param client  The originator of the request to whom the final response should be sent
   * @param handler The Actor who should handle the response for mssg
   */
  override def process(mssg: Message, client: ActorRef, handler: ActorRef): Unit =
  {
    mssg match
    {
      case x:RelationshipMessage => relationshipRouter ! Envelope(x, client, handler)
      case x => this.unknownMessage(x)
    }
  }
}