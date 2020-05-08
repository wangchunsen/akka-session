package session.akka



object MacroDemoTest{

  def main(args:Array[String]):Unit ={
    teest()
  }

  def teest(): Unit ={
    println(MacroDemo.defineClass("asdfad"))
  }
}
