package com.tweeter.module

import akka.actor.{ActorSystem, ActorRef}
import com.typesafe.config.{Config}

/**
 * Module will define the methods that all modules must support. A Module should be an object and it will provide a way
 * to start a Module as well as get a router associated with a  Module.
 * Created by Carlos on 12/11/2014.
 */
trait Module extends Component
{
  protected def defaultComponents():List[Component]

  /**
   * Returns system with this Module created on system.
   * @param system  The ActorSystem on which this Module is created
   * @param components  The components that will be a part of this module
   * @return        system
   */
  def start(system:ActorSystem, components:List[Component] = defaultComponents()):ActorSystem

  /**
   * Returns an ActorSystem with this Module and the default config loaded.
   * @param components  The components that will be a part of this module
   * @return  An ActorSystem with this Module
   */
  def start(components:List[Component] = defaultComponents()):ActorSystem = start(defaultConfig(), components)

  /**
   * Starts the cluster of Actors used by this Module. This method is only called once at the start of getModule.
   * @param config      The configuration used to start the Module
   * @param components  The components that will be a part of this module
   * @return            An ActorSystem that contains the Actors used by this Module
   */
  final def start(config:Config, components:List[Component] = defaultComponents()):ActorSystem =
  {
    val settings = new Settings(config)
    val system = ActorSystem(settings.clusterName, config)
    return start(system)
  }
}
