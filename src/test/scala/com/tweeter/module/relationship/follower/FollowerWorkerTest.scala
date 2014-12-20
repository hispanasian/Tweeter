package com.tweeter.module.relationship.follower

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import com.tweeter.lib.tests.{AkkaSpec}
import com.typesafe.config.ConfigFactory

/**
 * Created by Carlos on 12/20/2014.
 */
class FollowerWorkerTest(_system:ActorSystem) extends AkkaSpec(_system)
{
  val config = ConfigFactory.parseString("""
    akka
    {
      stdout-loglevel="OFF"
      loglevel = "OFF"
    }
                                         """)/*.withFallback(ConfigFactory.load())*/
  override implicit val system = ActorSystem("FollowerWorkerTestSystem", config)

  def fixture =
  {
    val probe = TestProbe()
    val handler = TestProbe()
    val client = TestProbe()
  }

  override def afterAll { TestKit.shutdownActorSystem(system) }

  "A FollowerWorker" should "return an empty list when  a new FollowerWorker is sent a getFollowers message" in
    {
      val f = fixture
      //val m = mock[Cache[Int,CachedObject]]
      //val worker =
    }
}
