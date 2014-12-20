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
 * Envelope holds a Message as well as the original ActorRef(client) that sent the message as well as the ActorRef
 * of the Actor who should handle the response to mssg. This should be the primary means of communication for all
 * Modules.
 * @param mssg    The message being delivered
 * @param client  The originator of the request to whom the final response should be sent
 * @param handler The Actor who will handle the next message
 */
case class Envelope(mssg:Message, client:ActorRef, handler:ActorRef) extends Message