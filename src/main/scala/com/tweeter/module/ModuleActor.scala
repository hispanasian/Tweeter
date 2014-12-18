package com.tweeter.module

import akka.actor.{ActorLogging, Actor}
import akka.cluster.Cluster
import akka.contrib.pattern.DistributedPubSubExtension

/**
 * The ModuleActor will be extended by all Actor Modules for a Module. The ModuleActor is what does the actual Module
 * work via Akka Actors.
 * Created by Carlos on 12/16/2014.
 * @param components  The Modules loaded by this Module
 */
abstract class ModuleActor(components:List[Module]) extends Actor with ActorLogging
{
  components.foreach(c => c.start(context))

  /**
   * cluster will provide ModuleActor access to the cluster
   */
  protected val cluster = Cluster(context.system)

  /**
   * mediator will provide ModuleActor access to a DistributedPubSubMediator
   */
  protected val mediator = DistributedPubSubExtension(context.system).mediator
}
