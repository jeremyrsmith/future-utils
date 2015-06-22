package com.github.jeremyrsmith.util.future

import com.twitter.util._

object PartialFutureTest extends App {

  implicit val timer = new JavaTimer

  def futureMethod1(arg:String) = {
    println("FutureMethod1")

    val p = Promise[Option[String]]()
    timer.schedule(Time.now + Duration.fromSeconds(1)) {
      p.setValue(None)
    }
    p
  }

  def futureMethod2(arg:String) = {
    println("FutureMethod2")

    val p = Promise[Option[String]]()

    timer.schedule(Time.now + Duration.fromSeconds(1)) {
      //p.setValue(Some("Result from futureMethod2"))
      p.setValue(None)
    }
    p
  }

  def futureMethod3(arg:String) = {
    println("FutureMethod3")

    val p = Promise[Option[String]]()

    timer.schedule(Time.now + Duration.fromSeconds(1)) {
      p.setValue(Some("Result from futureMethod3"))
      //p.setValue(None)
    }

    p
  }

  def futureMethod4(arg:String) = {
    println("FutureMethod4")

    val p = Promise[Option[String]]()

    timer.schedule(Time.now + Duration.fromSeconds(1)) {
      //p.setValue(Some("Result from futureMethod4"))
      p.setValue(None)
    }

    p
  }

  def sideEffect(arg:Option[String]) = println(arg)

  val tester = futureMethod1("hi") orElse (futureMethod2("hello") andThen sideEffect) orElse futureMethod3("bye") orElse futureMethod4("woo")
  println(Await.result(tester))
  System.exit(0)
}
