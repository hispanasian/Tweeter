package com.tweeter.lib.experiment

import akka.actor.{PoisonPill, Props, ActorSystem, Actor}

/**
 * Created by Carlos on 12/11/2014.
 */
object SendClosure
{
  def main(args:Array[String]):Unit =
  {
    var sys = ActorSystem("SendClosure")

    val test = sys.actorOf(Props(classOf[Test]))

    test ! ((x:String) => s"I said: $x")
    test ! ((x:String) => s"WHOA!, he said $x")
    test ! PoisonPill
  }
}

class Test extends Actor
{
  override def receive: Receive =
  {
    case x:(String => String) => println(x("this works!"))
    case _ =>
  }
}
