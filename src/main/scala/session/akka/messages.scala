package session.akka

package messages {

 //import akka.actor.
 object Clear
 case class Get(key:String)
 case class Set(key:String, value:AnyRef)

 case class RequestCacheRef()
 case class ResponseCacheRef()


 case class SubscribeEvent(eventClasses:List[Class[Any]])
 case class UnSubscribeEvent(eventClasses:List[Class[Any]])
}
