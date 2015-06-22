package com.github.jeremyrsmith.util

import com.twitter.util.Future

package object future {
  implicit def futureToFutureWrap[A](fut:Future[A]):FutureWrap[A] = new FutureWrap[A](fut)
  implicit def futureOfOptionToFutureOption[A](fO: Future[Option[A]]):FutureOption[A] = new FutureOption(fO)
  implicit def futureOptionToPartialFuture[A](fO: Future[Option[A]]):PartialFuture[A] = new PartialFuture(fO)
  implicit def futureSideEffectToSideEffect[A](f: A => Future[Unit]):A => Unit = (a:A) => { f(a) }
  implicit def futureOfSeqToFutureSeq[A](fut:Future[Seq[A]]):FutureSeq[A] = new FutureSeq[A](fut)

  implicit def bijectOptionToFuture[A](opt: Option[A]):PartialFuture[A] = new PartialFuture(Future.value(opt))

  implicit class OptionToFutureBijection[A](opt: Option[A]) {
    def asFuture = bijectOptionToFuture(opt)
  }
}
