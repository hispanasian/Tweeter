package com.tweeter.module

/**
 * CommunicationProtocol will be used to communicate a requested communication protocol to be used by a Module when
 * using getModule().
 */
sealed trait CommunicationProtocol extends Message
case class AKKA() extends CommunicationProtocol
case class REST() extends CommunicationProtocol
