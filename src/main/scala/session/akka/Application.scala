package session.akka

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions, ConfigResolveOptions}
import scaldi.{MutableInjectorAggregation, _}
import session.akka.db._

import scala.language.postfixOps

class Application private(env: Option[String]) {
  private var composeInjector: MutableInjectorAggregation = _

  try {
    val config = loadConfig()
    composeInjector  =  makeupInjectors(config)
    composeInjector.initNonLazy()
  } catch {
    case e: Exception =>
      Option(composeInjector) foreach (_.destroy())
      throw e
  }

  private def makeupInjectors(config: Config): MutableInjectorAggregation = {
    val coreModule = new Module {
      bind[Config] toNonLazy config
      bind[ActorSystem] toNonLazy new ActorSystemProvider().get() destroyWith ActorSystemProvider.shutDownSync
      bind[HttpProvider] toNonLazy HttpProvider.startup destroyWith (_.shutdownSync())
      bind[DB] toNonLazy injected[DB]
    }
    val serviceBindings = service.bindings()
    coreModule :: serviceBindings :: TypesafeConfigInjector(config)
  }

  private def loadConfig() = env match {
    case Some(envStr) =>
      ConfigFactory.load(
        s"application-$envStr",
        ConfigParseOptions.defaults().setAllowMissing(false),
        ConfigResolveOptions.defaults()
      ) withFallback ConfigFactory.load()
    case None =>
      ConfigFactory.load()
  }

  implicit val injector: Injector = ImmutableWrapper(composeInjector)

  def shutDown(): Unit = composeInjector.destroy()
}

object Application {
  private var isStarted = false

  var _instance: Application = _

  def main(args: Array[String]): Unit = {
    if (isStarted) throw new Exception("Application is started ")
    isStarted = true
    _instance = new Application(getArg(args, "env"))
  }

  private def getArg(args: Array[String], argName: String): Option[String] = {
    val index = args.indexOf(s"--$argName")
    if (index >= 0) Some(args(index + 1))
    else None
  }

  def apply: Application = _instance
}
