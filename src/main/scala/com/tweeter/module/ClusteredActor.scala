package com.tweeter.module

import akka.actor.{ActorRef, Actor, ActorLogging}
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension

/**
 * A ClusteredActor gives an Actor a cluster, mediator, and log
 * Created by Carlos on 12/19/2014.
 */
abstract class ClusteredActor extends Actor with ActorLogging
{
  /**
   * cluster will provide ModuleActor access to the cluster
   */
  protected val cluster = Cluster(context.system)

  /**
   * mediator will provide ModuleActor access to a DistributedPubSubMediator
   */
  protected val mediator = DistributedPubSubExtension(context.system).mediator

  /**
   * Processes mssg and sends the response to handler. The final response should be sent back to client. If the
   * receiving Actor does not know who the new handler should be when sending a response to handler, set the new
   * handler to the current handler.
   * @param mssg    The mssg that is being processed
   * @param client  The originator of the request to whom the final response should be sent
   * @param handler The Actor who should handle the response for mssg
   */
  def process(mssg:Message, client:ActorRef, handler:ActorRef):Unit

  final def receive =
  {
    case Envelope(mssg, client, handler) => process(mssg, client, handler)
    case x => unknownMessage(x)
  }

  /**
   * Default action that should be taken when an unknown message is received.
   * @param message The unkown message
   */
  def unknownMessage(message: Any) =
  {
    log.debug(s"$self received unknown message: $message")
  }
}
