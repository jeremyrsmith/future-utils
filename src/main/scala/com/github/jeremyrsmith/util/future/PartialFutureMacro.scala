package com.github.jeremyrsmith.util.future

import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context
import com.twitter.util.Future



object PartialFutureMacro {

  def rewriteDelayFutureInvocation[B](c:Context)(fut: c.Expr[Future[B]]):c.Expr[()=>Future[B]] = {
    import c.universe._
    val t = fut.tree
    c.Expr[()=>Future[B]](q"(() => {..$t})")
  }

}

object Danger {

  implicit def futureToFugureGen[A](fut:Future[A]):(()=>Future[A]) = macro PartialFutureMacro.rewriteDelayFutureInvocation[A]

}