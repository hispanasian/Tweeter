package com.tweeter.module

import akka.actor._
import akka.cluster.Cluster
import com.typesafe.config.Config

/**
 * A component can be thought of as a part of a Module. It supports the same methods that a Module supports with the
 * addition of the ability to start in a given Context (or get a router in a specific context). The idea is that
 * a Module must start at the root path of an ActorSystem, but a component need not necessarily be at the root path.
 * Created by Carlos on 12/11/2014.
 */
trait Component extends Actor with ActorLogging
{
  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  def roles():List[String]

  /**
   * Returns the 'topic' for which the message is categorized when being sent by this Module to the
   * DistributedPubSubMediator if it is a Message handeled by this Module. If the Message is not handeled by this
   * Module, the default behavior is to return an empty string. The suggested topic for a Message is the full classpath.
   * @param message The Message for whose 'topic' is being searched
   * @return        The 'topic' associated with Message
   */
  def getTopic(message:Message):String

  /**
   * Returns the routing information used by Spray for this Module.
   */
  def getRoute:Unit

  /**
   * Returns the default Config for this Module.
   * @return  The default Config for this Module
   */
  protected def defaultConfig():Config

  /**
   * Starts this component in the given context and returns the ActorSystem for which context belongs
   * @param context The context in which this Module is started
   * @return        The ActorSystem for which context belongs
   */
  def start(context:ActorContext):ActorSystem

  /**
   * Returns an ActorRef that knows how to route a Message to the correct Actor
   * param context The ActorContext in which the router is created
   * @return        an ActorRef that can route a Message using Akka Actors
   */
  protected def getAkkaRouter(context:ActorContext):ActorRef

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  protected def getRESTRouter(context:ActorContext):ActorRef

  /**
   * Returns an ActorRef that provides access to this Module. protocol defaults to AKKA
   * @param context The ActorContext in which the router is created
   * @param protocol  The protocol to be supported by the ActorRef
   * @return          An ActorRef that provides access to this Module
   */
  final def getModule(context:ActorContext, protocol:CommunicationProtocol = AKKA()):ActorRef =
  {
    var module:ActorRef = null
    protocol match
    {
      case AKKA() => module =  getAkkaRouter(context)
      case REST() => module = getRESTRouter(context)
    }
    return module
  }
}
