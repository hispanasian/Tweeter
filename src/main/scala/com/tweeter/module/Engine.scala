package com.tweeter.module

import akka.actor.{Props, ActorSystem}
import com.typesafe.config.{Config}

/**
 * Module will define the methods that all modules must support. A Module should be an object and it will provide a way
 * to start a Module as well as get a router associated with a  Module.
 * Created by Carlos on 12/11/2014.
 */
trait Engine extends Module
{
  /**
   * Returns system with this Module created on system.
   * @param system      The ActorSystem on which this Module is created
   * @param modules  The modules that will be a part of this module
   * @return            system with this Module created on system.
   */
  def start(system: ActorSystem, modules: List[Module]): ActorSystem =
  {
    system.actorOf(Props(getModule(), modules), name = name())
    return system
  }

  /**
   * Returns system with this Module created on system.
   * @param system      The ActorSystem on which this Module is created
   * @return            system with this Module created on system.
   */
  def start(system:ActorSystem):ActorSystem = start(system, defaultModules())

  /**
   * Starts the cluster of Actors used by this Module. This method is only called once at the start of getModule.
   * @param config      The configuration used to start the Module
   * @param modules  The modules that will be a part of this module
   * @return            An ActorSystem that contains the Actors used by this Module
   */
  final def start(config:Config = moduleConfig(defaultModules()), modules:List[Module] = defaultModules()):ActorSystem =
  {
    val settings = new Settings(config)
    val system = ActorSystem(settings.clusterName, config)
    return start(system, modules)
  }
}
