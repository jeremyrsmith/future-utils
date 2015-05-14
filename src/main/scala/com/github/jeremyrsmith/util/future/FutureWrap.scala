package com.github.jeremyrsmith.util.future

import com.twitter.util.Awaitable.CanAwait
import com.twitter.util._

class FutureWrap[+A](underlying:Future[A]) extends Future[A] {

  //attaches side effects to completion of future in predictable order
  def andThen[B >: A](f:B => Any):Future[A] = {
    val p = Promise[A]()
    underlying.respond {
      case Return(v) =>
        f(v)
        p.setValue(v)
      case Throw(e) => p.setException(e)
    }
    p
  }


  //attaches a side effect to successful completion of this future, but resulting future will return immediately.
  //given f(A) will complete at some unpredictable time if this future is successful
  def andThenEventually[B >: A](f: B => Any ):Future[A] = {
    underlying onSuccess {
      v => f(v)
    }
  }

  //lifts a Future[A] into a Future[Option[A]] (discarding MatchError)
  def lift = underlying.liftToTry map {
    case Return(v) => Some(v)
    case Throw(e) => e match {
      case e:MatchError => None
      case other        => throw other
    }
  }

  //Discards the result of a future by returning a new Future[Unit] that completes when the underlying future completes
  def discard:Future[Unit] = underlying map (_ => Unit)

  override def respond(k: (Try[A]) => Unit) = underlying.respond(k)

  override def transform[B](f: (Try[A]) => Future[B]) = underlying.transform(f)

  override def raise(interrupt: Throwable) = underlying.raise(interrupt)

  override def poll = underlying.poll

  @throws[Exception](classOf[Exception])
  override def result(timeout: Duration)(implicit permit: CanAwait) = underlying.result(timeout)

  override def isReady(implicit permit: CanAwait) = underlying.isReady

  @throws[InterruptedException](classOf[InterruptedException])
  @throws[TimeoutException](classOf[TimeoutException])
  override def ready(timeout: Duration)(implicit permit: CanAwait) = this
}

class FutureSeq[+A](underlying:Future[Seq[A]]) extends FutureWrap[Seq[A]](underlying) {

  def first = underlying map (_.headOption)


}

class FutureOption[+A](underlying:Future[Option[A]]) extends FutureWrap[Option[A]](underlying) {

  def unLift = new PartialFuture(this)

}