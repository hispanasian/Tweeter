package com.tweeter.module

import akka.actor.{ActorLogging, Actor}

/**
 * The ModuleActor will be extended by all Actor Modules for a Module. The ModuleActor is what does the actual Module
 * work via Akka Actors.
 * Created by Carlos on 12/16/2014.
 * @param components  The Modules loaded by this Module
 */
abstract class ModuleActor(components:List[Module]) extends ClusteredActor
{
  components.foreach(c => c.start(context))
}
