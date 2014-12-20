package com.tweeter.lib.tests

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpecLike, BeforeAndAfterAll}

/**
 * The class to be extended by any tests.
 * Created by Carlos on 12/18/2014.
 */
trait UnitSpec extends FlatSpecLike with MockFactory with BeforeAndAfterAll

/**
 * The class to be extended by an Akka related tests
 * @param _system
 */
abstract class AkkaSpec(_system:ActorSystem) extends TestKit(_system) with UnitSpec
