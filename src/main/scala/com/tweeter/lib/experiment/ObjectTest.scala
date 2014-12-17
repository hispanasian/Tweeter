package com.tweeter.lib.experiment

import akka.actor.Actor.Receive
import akka.actor._

/**
 * Created by Carlos on 12/13/2014.
 */
object ObjectTest
{
  def main(args:Array[String]):Unit =
  {
    val system1 = ActorSystem("ObjectTest1")
    val tester1 = system1.actorOf(Props[ObjectTester])
    val system2 = ActorSystem("ObjectTest2")
    val tester2 = system1.actorOf(Props[ObjectTester])

    tester1 ! "test"
    tester2 ! "test"
    tester1 ! PoisonPill
    tester2 ! PoisonPill
  }
}

class ObjectTester extends Actor with ActorLogging
{
  override def receive: Receive =
  {
    case "test" => println(Testee.increment())
    case x =>
  }
}

object Testee
{
  var counter:Int = 0
  def increment():Int =
  {
    counter += 1
    return counter
  }
}
