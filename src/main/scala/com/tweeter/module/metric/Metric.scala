package com.tweeter.module.metric

import akka.actor.{ActorRef, ActorRefFactory}
import com.tweeter.module.{Message, ModuleActor, Module}
import com.typesafe.config.{ConfigFactory, Config}

/**
 * The Tweet Module will deal with aggregating the Tweeter metrics (number of tweets per second, etc.) by subscribing
 * to the various messages published by the Tweeter application.
 * Created by Carlos on 12/18/2014.
 */
object Metric extends Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  override protected def defaultModules(): List[Module] = List[Module]()

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of Metric
   */
  override protected def getModule(): Class[_ <: ModuleActor] = classOf[Metric]

  /**
   * Returns the routing information used by Spray for this Module.
   */
  override def getRoute: Unit = ???

  /**
   * Returns the config for this Module
   * @return  The config for this Module
   */
  override protected def config(): Config = ConfigFactory.load("metric")

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  override def roles(): List[String] = List[String]("metric")

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return "metric"
   */
  override def name(): String = "metric"

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  override protected def getRESTRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns an ActorRef that knows how to route a Message to the correct Actor
   * param context The ActorContext in which the router is created
   * @return        an ActorRef that can route a Message using Akka Actors
   */
  override protected def getAkkaRouter(context: ActorRefFactory): ActorRef = ???

  /**
   * Returns the 'topic' for which the message is categorized when being sent by this Module to the
   * DistributedPubSubMediator if it is a Message handled by this Module. If the Message is not handled by this
   * Module, the default behavior is to return an empty string. The suggested topic for a Message is the full classpath.
   * @param message The Message for whose 'topic' is being searched
   * @return        The 'topic' associated with Message
   */
  override def getTopic(message: Message): String =
  {
    message match
    {
      case x:MetricMessage => classOf[MetricMessage].getCanonicalName
      case _ => ""
    }
  }
}

class Metric(modules: List[Module] = List[Module]()) extends ModuleActor(modules)
{
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
      case x => unknownMessage(x)
    }
  }
}

