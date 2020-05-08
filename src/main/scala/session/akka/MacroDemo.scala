package session.akka

import scala.language.experimental.macros
import scala.reflect.macros._
object MacroDemo {
  def defineClass(className:String): String = macro MacroImp.createClass
}

object MacroImp{
  def createClass(c: whitebox.Context)(className:c.Expr[String]): c.Expr[String] = {
    import  c.universe._



    c.Expr[String](
      q"""
         case class User(name: String, age: Int)
         classOf[User].getName
       """)
  }
}
