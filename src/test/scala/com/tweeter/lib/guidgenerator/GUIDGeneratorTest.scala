package com.tweeter.lib.guidgenerator

import akka.actor.{ActorSystem}
import akka.testkit.{TestProbe, TestActorRef, TestKit}
import com.tweeter.lib.tests.AkkaSpec
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.FiniteDuration

/**
 * Tests the GUIDGenerator class
 * Created by Carlos on 1/12/2015.
 */
class GUIDGeneratorTest(_system:ActorSystem) extends AkkaSpec(_system)
{
  def this() = this(ActorSystem("GUIDGeneratorTestSystem", ConfigFactory.parseString("""akka.remote.netty.tcp.port="3002"""").withFallback(ConfigFactory.load("test").withFallback(ConfigFactory.load("application")))))

  override def afterAll { TestKit.shutdownActorSystem(system); system.shutdown() }

  "A GUIDGenerator" should "respond with GUIDBlock(2500,500) when sent a fourth GetGUIBlock message and having been initialized with a start of 1000 and a blockSize of 500" in
  {
    val generator = TestActorRef(new GUIDGenerator(1000, 500))
    val probe = TestProbe()
    probe.send(generator, GetGUIDBlock)
    probe.expectMsg(GUIDBlock(1000,500))
    probe.send(generator, GetGUIDBlock)
    probe.expectMsg(GUIDBlock(1500,500))
    probe.send(generator, GetGUIDBlock)
    probe.expectMsg(GUIDBlock(2000,500))
    probe.send(generator, GetGUIDBlock)
    probe.expectMsg(GUIDBlock(2500,500))
  }
}
