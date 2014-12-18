package com.tweeter.module

import akka.actor.ActorRef

/**
 * Message will be the root trait from which all messages will be derived from in Tweeter.
 * Created by Carlos on 12/11/2014.
 */

/**
 * A Tweeter Message
 */
trait Message

/**
 * Envelope holds a Message as well as the original ActorRef that sent the message.
 * @param mssg
 */
case class Envelope(mssg:Message, from:ActorRef) extends Message

/**
 * This Message tells a ModuleActor to call it's corresponding sub-components start method
 */
case class Start() extends Message