package com.tweeter.module

import akka.actor._
import com.typesafe.config.Config

/**
 * A Module is a piece of software that supports the Engine. It will typically support some primary feature or be
 * composed of sub-modules to help break up the work.
 * Created by Carlos on 12/11/2014.
 */
trait Module
{
  /**
   * Returns the list of modules that this Module loads by default
   * @return  The list of modules that this Module loads by default
   */
  protected def defaultModules():List[Module]

  /**
   * Returns the class of the ModuleActor used by this Module
   * @return   The class of the ModuleActor used by this Module
   */
  protected def getModule():Class[_<:ModuleActor]

  /**
   * Returns a List[String] of the roles this Module handles in a cluster.
   * @return   a List[String] of the roles this Module handles in a cluster.
   */
  def roles():List[String]

  /**
   * Returns a list of every roles provided by this Module and it's sub-modules
   * @param modules     The modules that will be used by this Module. Defaults to the modules provided by
   *                    defaultModule()
   * @return            A list of every roles provided by this Module and it's sub-modules
   */
  final def moduleRoles(modules:List[Module] = defaultModules()):List[String] =
  {
    var role:List[String] = roles()
    modules.foreach(c => role = role ++ c.roles())
    return roles
  }

  /**
   * Returns the name that this Module is expected to run under when an actor is created. This should be used by
   * the Module's creator to know the path that a particular Actor will have when it is created.
   * @return The name of this Module
   */
  def name():String

  /**
   * Returns the 'topic' for which the message is categorized when being sent by this Module to the
   * DistributedPubSubMediator if it is a Message handled by this Module. If the Message is not handled by this
   * Module, the default behavior is to return an empty string. The suggested topic for a Message is the full classpath.
   * @param message The Message for whose 'topic' is being searched
   * @return        The 'topic' associated with Message
   */
  def getTopic(message:Message):String

  /**
   * Returns the routing information used by Spray for this Module.
   */
  def getRoute:Unit

  /**
   * Returns the config for this Module
   * @return  The config for this Module
   */
  protected def config():Config

  /**
   * Returns the config of all the modules
   * @param modules  The modules that will be used by this Module
   * @return         the config of all the modules.
   */
  def moduleConfig(modules:List[Module] = defaultModules()):Config =
  {
    var compConfig = config()
    modules.foreach(c => compConfig = compConfig.withFallback(c.config()))
    return compConfig
  }

  /**
   * Starts this module in the given context and returns the ActorSystem for which context belongs
   * @param context  The context in which this Module is started
   * @param modules  The modules used by this Module
   * @return         The ActorSystem for which context belongs
   */
  final def start(context:ActorContext, modules:List[Module]):ActorSystem =
  {
    context.actorOf(Props(getModule(), modules), name = name())
    return context.system
  }

  /**
   * Starts this module in the given context and returns the ActorSystem for which context belongs
   * @param context     The context in which this Module is started
   * @return            The ActorSystem for which context belongs
   */
  final def start(context:ActorContext):ActorSystem = start(context, defaultModules())

  /**
   * Returns an ActorRef that knows how to route a Message to the correct Actor
   * param context The ActorContext in which the router is created
   * @return        an ActorRef that can route a Message using Akka Actors
   */
  protected def getAkkaRouter(context:ActorRefFactory):ActorRef

  /**
   * Returns an ActorRef that knows how to send a Message using REST.
   * @param context The ActorContext in which the router is created
   * @return        An ActorRef that knows how to send a Message using REST.
   */
  protected def getRESTRouter(context:ActorRefFactory):ActorRef

  /**
   * Returns an ActorRef that provides access to this Module. protocol defaults to AKKA
   * @param context   The factory that will be used to create the actor
   * @param protocol  The protocol to be supported by the ActorRef
   * @return          An ActorRef that provides access to this Module
   */
  final def getModule(context:ActorRefFactory, protocol:CommunicationProtocol = AKKA()):ActorRef =
  {
    protocol match
    {
      case AKKA() => getAkkaRouter(context)
      case REST() => getRESTRouter(context)
      case _ => null
    }
  }
}
