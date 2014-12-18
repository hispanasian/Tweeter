package com.tweeter.lib.experiment

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.{MemberUp, UnreachableMember, MemberEvent, InitialStateAsEvents}
import scala.concurrent.duration._
import akka.routing.{FromConfig}
import com.typesafe.config.{Config, ConfigFactory}

/**
 * Example of how to use a cluster aware router to communicate with a GROUP cluster (not pool)
 * Created by Carlos on 12/12/2014.
 */
object ClusterRouter
{
  def main(args:Array[String]) =
  {
    val config = ConfigFactory.parseString("""
    akka
    {
       #loglevel = "DEBUG"
       log-config-on-start = off
       actor
       {
         provider = "akka.cluster.ClusterActorRefProvider"
         deployment
         {
            /"*"/workerRouter
            #/client/workerRouter
            {
              #router = consistent-hashing-group
              router = adaptive-group
              #router = adaptive-pool
              nr-of-instances = 5
              routees.paths = ["/user/worker"]
              cluster
              {
                enabled = on
                allow-local-routees = on
                use-role = work
              }
            }
           debug
           {
              #autoreceive = on
           }
         }
       }
       remote
       {
          enabled-transports = ["akka.remote.netty.tcp"]
          log-received-messages = on
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
            "akka.tcp://ClusterRouter@127.0.0.1:3000",
            "akka.tcp://ClusterRouter@127.0.0.1:3001"
          ]
          auto-down-unreachable-after = 10s
       }
    }
                                           """)
    ClientApp.start(config, 3000)
    ClientApp.start(config, 3001)
    ClientApp.start(config, 3002)
    WorkerApp.start(config,3003)
  }
}

object WorkerApp
{
  def start(c:Config, port:Int):Unit =
  {
    val config = c.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[work]"))

    val system = ActorSystem("ClusterRouter", config)
    val worker = system.actorOf(Props[Worker], name = "worker")
  }
}

class Worker extends Actor with ActorLogging
{
  val cluster = Cluster(context.system)
  println(s"$self made")


  override def preStart():Unit =
  {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    self ! "ping"
  }
  override def receive: Receive =
  {
    case "ping" =>
      println(s"$self is still up")
      //context.system.scheduler.scheduleOnce(1.second)(self ! "ping")(context.system.dispatcher)
    case "test" => println("%s received test from %s".format(self, sender))
    case x:String => println(s"$self received $x from %s".format(sender))
    case MemberUp(m) => println(s"Worker ($self) got member $m is up with roles %s and address %s".format(m.roles, m.address))
    case x =>
  }
}

object ClientApp
{
  def start(c:Config, port:Int):Unit =
  {
    val roles = Seq[String]("work", "service")
    val jsonRoles = roles.addString(new StringBuilder(), "[", ",", "]")
    val config = c.withFallback(ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")).
      withFallback(ConfigFactory.parseString("akka.cluster.roles=[service]"))
      //withFallback(ConfigFactory.parseString(s"akka.cluster.roles=$jsonRoles"))

    val system = ActorSystem("ClusterRouter", config)
    val client = system.actorOf(Props[Client], name = "client")
//    val worker = system.actorOf(Props[Worker], name = "worker")
    client ! "start"
  }
}

class Client() extends Actor with ActorLogging
{
  val workerRouter = context.actorOf(FromConfig.props(Props[Worker]), name = "workerRouter")
  //val workerRouter = context.actorOf(FromConfig.props(Props.empty), name = "workerRouter")
  val cluster = Cluster(context.system)
  var counter = 0
  println(s"$self made")

  override def preStart():Unit =
  {
    //cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
    println(s"$self is starting as client with router $workerRouter")
    context.system.scheduler.scheduleOnce(10.second)(workerRouter ! "test")(context.system.dispatcher)
  }

  override def receive: Actor.Receive =
  {
    case "start" =>
      workerRouter ! "test: %s".format(counter)
      counter += 1
      self ! "ping"
      context.system.scheduler.scheduleOnce(1.second)(self ! "start")(context.system.dispatcher)
    case x:String =>
    //case "ping" => println(s"$self is got ping")
    case MemberUp(m) => println(s"Client ($self) got member $m is up with roles %s and address %s".format(m.roles, m.address))
    case x =>
  }
}