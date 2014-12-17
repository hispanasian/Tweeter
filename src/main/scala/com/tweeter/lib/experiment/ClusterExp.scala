package com.tweeter.lib.experiment

import akka.actor.Actor.Receive
import akka.actor._
import akka.cluster.{Member, MemberStatus, Cluster}
import akka.cluster.ClusterEvent._
import com.typesafe.config.{Config, ConfigFactory}

/**
 * Created by Carlos on 12/12/2014.
 */
object ClusterExp
{
  def main(args:Array[String]) =
  {
    val config = ConfigFactory.parseString("""
    akka
    {
       actor
       {
         provider = "akka.cluster.ClusterActorRefProvider"
       }
       remote
       {
          enabled-transports = ["akka.remote.netty.tcp"]
          log-remote-lifecycle-events = off
          transport = ["akka.remote.netty.tcp"]
          netty.tcp
          {
            hostname = "127.0.0.1"
          }
       }
       cluster
       {
          seed-nodes =
          [
            "akka.tcp://ClusterExp@127.0.0.1:3000"
            "akka.tcp://ClusterExp@127.0.0.1:3001"
          ]
       }
    }
                                           """)
    ConsumerApp.start(config)
    ProducerApp.start(config)
  }
}

object ConsumerApp
{
  def start(c:Config):Unit =
  {
    val config = c.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=3000")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[consumer]"))
    val system = ActorSystem("ClusterExp", config)
    system.actorOf(Props[Consumer], name = "consumer")
  }
}

class Consumer extends Actor with ActorLogging
{
  val cluster = Cluster(context.system)

  override def preStart(): Unit =
  {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop():Unit = cluster.unsubscribe(self)

  override def receive: Receive =
  {
    case "hey" =>
      println("%s received hey from %s".format(self, sender))
      println("got it")
    case _ =>
  }
}

object ProducerApp
{
  def start(c:Config):Unit =
  {
    val config = c.withFallback(ConfigFactory.parseString("akka.remote.netty.tcp.port=3001")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[producer]"))
    val system = ActorSystem("ClusterExp", config)
    system.actorOf(Props[Producer], name = "producer")
  }
}

class Producer extends Actor with ActorLogging
{
  val cluster = Cluster(context.system)

  override def preStart(): Unit =
  {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop():Unit = cluster.unsubscribe(self)

  override def receive: Actor.Receive =
  {
    case MemberUp(m) => dealWith(m)
    case state:CurrentClusterState =>
      state.members.filter(_.status == MemberStatus.Up) foreach dealWith
    case x:MemberEvent => println(s"found MemberEvent $x")
    case x => println(x)
  }

  def dealWith(m:Member) =
  {
    println(s"found member $m")
    if(m.hasRole("consumer")) context.actorSelection(RootActorPath(m.address)/"user"/"consumer") ! "hey"
  }
}
