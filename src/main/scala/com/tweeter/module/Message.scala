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
 * Envelope holds a Letter as well as the original ActorRef(client) that sent the letter, the ActorRef of the Actor
 * who should handle the response to mssg, and the replyMessage that defines what message should be used to respond to
 * the handler. This should be the primary means of communication for all Modules.
 * @param mssg          The message being delivered
 * @param client        The originator of the request to whom the final response should be sent
 * @param handler       The Actor who will handle the next message
 * @param replyMessage  A function that defines the type of Message that should be returned to client or handler. This
 *                      defaults to the passed message.
 */
case class Envelope(mssg:Message, client:ActorRef, handler:ActorRef, replyMessage:(Message => Message) = ((x:Message) => x)) extends Message