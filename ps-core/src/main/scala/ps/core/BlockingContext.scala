package ps.core

import cats.Applicative
import cats.effect.{ContextShift, Resource, Sync}
import io.chrisdavenport.linebacker.DualContext
import io.chrisdavenport.linebacker.contexts.Executors

import scala.concurrent.ExecutionContext

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

trait BlockingContext[F[_]] extends DualContext[F] {

  def shouldBlock: Boolean = true

  override def block[A](fa: F[A]): F[A] = {
    if (shouldBlock) super.block[A](fa) else fa
  }

}

object BlockingContext {

  def noBlock[F[_]](implicit C: ContextShift[F], A: Applicative[F]): Resource[F, BlockingContext[F]] = Resource.pure[F, BlockingContext[F]](new BlockingContext[F] {
    override def shouldBlock: Boolean = false

    override def blockingContext: ExecutionContext = ExecutionContext.Implicits.global

    override def contextShift: ContextShift[F] = C
  })

  def createFixedPool[F[_]: Sync](concurrency: Int)(implicit C: ContextShift[F]): Resource[F, BlockingContext[F]] = {
    Executors
      .fixedPool[F](concurrency)
      .flatMap(
        fixedExecutor =>
          Resource.pure(
            new BlockingContext[F] {
              override def blockingContext: ExecutionContext = ExecutionContext.fromExecutor(fixedExecutor)

              override def contextShift: ContextShift[F] = C
            }
          )
      )
  }

  def createForkJoinPool[F[_]: Sync](concurrency: Int)(implicit C: ContextShift[F]): Resource[F, BlockingContext[F]] = {
    Executors
      .forkJoinPool[F](concurrency)
      .flatMap(
        forkingExecutor =>
          Resource.pure(new BlockingContext[F] {
            override def blockingContext: ExecutionContext = ExecutionContext.fromExecutor(forkingExecutor)

            override def contextShift: ContextShift[F] = C
          })
      )
  }

  def createUnboundedPool[F[_]: Sync](implicit C: ContextShift[F]): Resource[F, BlockingContext[F]] = {
    Executors
      .unbound[F]
      .flatMap(
        fixedExecutor =>
          Resource.pure(
            new BlockingContext[F] {
              override def blockingContext: ExecutionContext = ExecutionContext.fromExecutor(fixedExecutor)

              override def contextShift: ContextShift[F] = C
            }
          )
      )
  }

  def createWorkStealingPool[F[_]: Sync](concurrency: Int)(implicit C: ContextShift[F]): Resource[F, BlockingContext[F]] = {
    Executors
      .workStealingPool[F](concurrency)
      .flatMap(
        workStealingExecutor =>
          Resource.pure(
            new BlockingContext[F] {
              override def blockingContext: ExecutionContext = ExecutionContext.fromExecutor(workStealingExecutor)

              override def contextShift: ContextShift[F] = C
            }
          )
      )
  }

}

