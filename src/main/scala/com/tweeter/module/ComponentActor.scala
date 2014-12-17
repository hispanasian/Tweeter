package com.tweeter.module

import akka.actor.{ActorLogging, Actor}
import akka.cluster.Cluster

/**
 * A component is similar to a Module except that it is expected to exist in a particular path of an ActorSystem
 * (defined by the Module for which this Component supports). Hence, both support very similar methods.
 * Created by Carlos on 12/16/2014.
 */
trait ComponentActor extends Actor with ActorLogging
{
  var cluster = Cluster(context.system)
}
