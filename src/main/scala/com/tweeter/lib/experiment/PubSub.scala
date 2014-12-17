package com.tweeter.lib.experiment

import akka.actor.{Props, ActorSystem, ActorLogging, Actor}
import akka.contrib.pattern.{DistributedPubSubExtension, DistributedPubSubMediator}
import akka.contrib.pattern.DistributedPubSubMediator.{Publish, Subscribe, SubscribeAck}
import com.typesafe.config.{Config, ConfigFactory}
import scala.concurrent.duration._

/**
 * Created by Carlos on 12/15/2014.
 */
object PubSub
{
  def main(args:Array[String]):Unit =
  {
    val config = ConfigFactory.parseString("""
    akka
    {
       #loglevel = "DEBUG"
       log-config-on-start = off
       actor
       {
         provider = "akka.cluster.ClusterActorRefProvider"
       }
       remote
       {
          enabled-transports = ["akka.remote.netty.tcp"]
          log-received-messages = off
          log-sent-messages = on
          log-remote-lifecycle-events = on
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
            "akka.tcp://PubSubCluster@127.0.0.1:3000",
            "akka.tcp://PubSubCluster@127.0.0.1:3001"
          ]
          auto-down-unreachable-after = 10s
       }
       contrib
       {
          cluster
          {
            pub-sub
            {
              name = distributedPubSubMediator
              role = ""
              routing-logic = random
              gossip-interval = 1s
              removed-time-to-live = 120s
              max-delta-elements = 3000
            }
          }
       }
    }
                                           """)
    Subscriber.start(config, 3000, Seq[String]("TestTopic"), "group1")
    Subscriber.start(config, 3001, Seq[String]("TestTopic"), "group2")
    Subscriber.start(config, 3002, Seq[String]("TestTopic", "Topic2"), "group2")
    Publisher.start(config, "TestTopic", 3003)
  }
}

object Subscriber
{
  def start(c:Config, port:Int, topic:Seq[String], group:String):Unit = {
    val config = c.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
    val system = ActorSystem("PubSubCluster", config)
    val subscriber = system.actorOf(Props(classOf[Subscriber], topic, group), name = "subscriber")
  }
}

class Subscriber(topic:Seq[String], group:String) extends Actor with ActorLogging
{
  val mediator = DistributedPubSubExtension(context.system).mediator
  topic.foreach(t =>mediator ! DistributedPubSubMediator.Subscribe(t, Option[String](group), self) )

  override def receive: Receive =
  {
    case SubscribeAck(Subscribe(x, y, self)) => context.become(receive)
    case SubscribeAck(_) => context.become(receive)
    case x:String => println(s"Subscriber $self got $x from %s".format(sender))
    case x =>
  }
}

object Publisher
{
  def start(c:Config, topic:String, port:Int):Unit = {
    val config = c.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[sub]"))
    val system = ActorSystem("PubSubCluster", config)
    val publisher = system.actorOf(Props(classOf[Publisher], topic), name = "publisher")
  }
}

class Publisher(topic:String) extends Actor with ActorLogging
{
  val mediator = DistributedPubSubExtension(context.system).mediator
  var counter = 0
  mediator ! DistributedPubSubMediator.Subscribe(topic, None, self)
  self ! "ping"

  override def receive: Receive =
  {
    case "ping" =>
      context.system.scheduler.scheduleOnce(1.second)(self ! "ping")(context.system.dispatcher)
      if(counter % 2 == 0) mediator ! Publish(topic, s"some content $counter", sendOneMessageToEachGroup = false)
      else mediator ! Publish(topic, s"some content $counter", sendOneMessageToEachGroup = true)
      mediator ! DistributedPubSubMediator.SendToAll("*", "message for everyone")
      mediator ! Publish("Topic2", s"Topic2 $counter", sendOneMessageToEachGroup = true)
      counter += 1
      println(s"$self received ping and is at counter: $counter")
    case SubscribeAck(_) => context.become(receive)
    case x:String =>
      if(sender.compareTo(self) == 0) println(s"$sender got subscribe from self")
      else println(s"$sender got subscribe from someone else: %s".format(sender))
    case x => println(x)
  }
}