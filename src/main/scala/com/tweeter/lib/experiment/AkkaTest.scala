package com.tweeter.lib.experiment

import akka.actor.{ActorRef, Props, ActorSystem, Actor}
import com.typesafe.config.ConfigFactory

/**
 * Created by Carlos on 12/11/2014.
 */
class AkkaTest
{
  var config = ConfigFactory.parseString("""
    akka {
       actor {
           provider = "akka.remote.RemoteActorRefProvider"
             }
       remote {
           transport = ["akka.remote.netty.tcp"]
       netty.tcp {
           hostname = "localhost"
           port = 6677
                 }
             }
        }
                                         """)

  var sys = ActorSystem("AkkaTest")

  val sender = sys.actorOf(Props(classOf[Sender]))
  val receiver = sys.actorOf(Props(classOf[Receiver]))

  sender ! receiver
  receiver ! sender
}

class Sender() extends Actor
{
  var receiver:ActorRef = null

  def receive =
  {
    case "start" =>
    case x:ActorRef =>
    case "receiver" =>
    case _ =>
  }
}

class Receiver() extends Actor
{
  var send:ActorRef = null

  def receive =
  {
    case "start" =>
    case x:ActorRef =>
    case "message" =>
    case "time" =>
    case "end" =>

    case _ =>
  }
}
