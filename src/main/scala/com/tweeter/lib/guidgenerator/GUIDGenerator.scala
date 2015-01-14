package com.tweeter.lib.guidgenerator

import akka.actor.{Props, ActorSystem, Actor, ActorLogging}
import com.typesafe.config.ConfigFactory

object GUIDGenerator
{
  def main(args:Array[String]):Unit =
  {
    val sys = ActorSystem("AkkaTest", ConfigFactory.parseString(""))
    println(classOf[GUIDGenerator].getConstructors.size)
    println(classOf[GUIDGenerator].getConstructors.apply(0))
    val generator = sys.actorOf(Props(classOf[GUIDGenerator], 0, 1000))
    generator ! GetGUIDBlock
  }
}

/**
 * GUIDGenerator provide a globally unique block of ids. GUIDGenerator incrementally provides new block for each
 * request and assumes that any ids given within a block are no longer valid. The block size and start can be provided.
 * GUIDGenerator can be part of a cluster.
 * Created by Carlos on 1/12/2015.
 * @param start       The first guid that will be given
 * @param blockSize   The size of the block of guids that will be provided
 */
class GUIDGenerator(start:Int = 0, blockSize:Int = 1000) extends Actor with ActorLogging
{
  var guid = start

  final def receive =
  {
    case GetGUIDBlock =>
    {
      sender ! GUIDBlock(guid, blockSize)
      guid += blockSize
    }
    case x => println(s"got uknown $x")
  }
}
