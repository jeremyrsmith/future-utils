package com.github.jeremyrsmith.util.future

import com.twitter.util.Awaitable.CanAwait
import com.twitter.util._
import scala.language.experimental.macros



class FutureGen[+A](underlying: ()=>Future[Option[A]]) extends (() => Future[Option[A]]) {
  def apply() = underlying()

  def orElse[B >: A](next:()=>Future[Option[B]]):() => Future[Option[B]] = () => {
    apply() flatMap {
      case Some(v) => Future.value(Some(v))
      case None => next()
    }
  }

}

class PartialFuture[+A](underlying:Future[Option[A]]) extends Future[A] {

  def orElse[B >: A](next:()=>Future[Option[B]]):Future[Option[B]] = underlying flatMap {
    case Some(v) => Future.value(Some(v))
    case None => next()
  }

  def lift = underlying

  override def respond(k: (Try[A]) => Unit): Future[A] = underlying.map {
    case Some(v) => v
    case None => throw new MatchError("PartialFuture was not defined")
  } respond k

  override def transform[B](f: (Try[A]) => Future[B]): Future[B] = underlying.flatMap[B] {
    case Some(v) => f(Return(v))
    case None => f(Throw(new MatchError("PartialFuture was not defined")))
  }

  override def raise(interrupt: Throwable): Unit = underlying.raise(interrupt)

  override def poll: Option[Try[A]] = underlying.poll match {
    case None => None
    case Some(t) => t match {
      case Return(opt) => opt match {
        case Some(v) => Some(Return(v))
        case None => Some(Throw(new MatchError("PartialFuture was not defined")))
      }
      case Throw(e) => Some(Throw(e))
    }
  }

  override def result(timeout: Duration)(implicit permit: CanAwait): A =
    underlying.result(timeout).getOrElse(throw new MatchError("PartialFuture was not defined"))

  override def isReady(implicit permit: CanAwait): Boolean =
    underlying.isReady

  override def ready(timeout: Duration)(implicit permit: CanAwait): this.type = this
}