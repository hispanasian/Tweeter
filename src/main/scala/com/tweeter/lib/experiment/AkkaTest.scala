package com.tweeter.lib.experiment

import akka.actor._
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

/**
 * Created by Carlos on 12/11/2014.
 */
object AkkaTest
{
  def main(args:Array[String]):Unit =
  {
    var sys = ActorSystem("AkkaTest")

    val sender = sys.actorOf(Props(classOf[Sender]))
    val receiver = sys.actorOf(Props(classOf[Receiver]))

    sender ! receiver
    receiver ! sender

    sender ! "start"
    receiver ! "start"
  }
}

class Sender() extends Actor
{
  var receiver:ActorRef = null

  def receive =
  {
    case "start" =>
      self ! "send"
    case x:ActorRef => receiver = x
    case "send" =>
      receiver ! "message"
      self ! "send"
    case _ =>
  }
}

class Receiver() extends Actor
{
  var send:ActorRef = null
  var counter = 0
  var times:List[Int] = List()
  var average = 0

  def receive =
  {
    case "start" =>
      context.system.scheduler.scheduleOnce(10.second)(self ! "end")(context.system.dispatcher)
      context.system.scheduler.scheduleOnce(1.second)(self ! "time")(context.system.dispatcher)
    case x:ActorRef => send = x
    case "message" =>
      counter += 1
    case "time" =>
      println("Receiver received %s messages".format(counter))
      times = times :+ counter
      counter = 0
      context.system.scheduler.scheduleOnce(1.second)(self ! "time")(context.system.dispatcher)
    case "end" =>
      var sum = 0
      times.foreach(x => sum += x)
      println(times)
      println("Receiver received an average of %s messages per second".format(sum/times.size))
      send ! PoisonPill
      context.system.scheduler.scheduleOnce(1.second)(self ! PoisonPill)(context.system.dispatcher)
    case _ =>
  }
}
