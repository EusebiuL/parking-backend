package ps

import busymachines.effects.async.{EitherSyntaxAsync, FutureSyntax, FutureTypeDefinitions, IOSyntax, IOTypeDefinitions, OptionSyntaxAsync, ResultSyntaxAsync, TaskSyntax, TaskTypeDefinitions, TrySyntaxAsync, ValidatedSyntaxAsync}
import busymachines.effects.sync.{EitherSyntax, OptionSyntax, ResultCompanionAliases, ResultSyntax, ResultTypeDefinitions, TrySyntax, TryTypeDefinitons}
import busymachines.effects.sync.validated.{ValidatedSyntax, ValidatedTypeDefinitions}

/**
  * @author Denis-Eusebiu Lazar eusebiu.lazar@busymachines.com
  * @since 2019-05-11
  *
  */

package object effects
  extends AnyRef with OptionSyntax.Implicits with OptionSyntaxAsync.Implcits with TryTypeDefinitons
    with TrySyntax.Implicits with TrySyntaxAsync.Implcits with EitherSyntax.Implicits with EitherSyntaxAsync.Implcits
    with ResultTypeDefinitions with ResultCompanionAliases with ResultSyntax.Implicits with ResultSyntaxAsync.Implcits
    with FutureTypeDefinitions with FutureSyntax.Implicits with IOTypeDefinitions with IOSyntax.Implicits
    with TaskTypeDefinitions with TaskSyntax.Implicits {

  type NonEmptyList[A] = cats.data.NonEmptyList[A]
  @inline def NonEmptyList: cats.data.NonEmptyList.type = cats.data.NonEmptyList

  type Seq[A] = scala.collection.immutable.Seq[A]
  @inline def Seq: scala.collection.immutable.Seq.type = scala.collection.immutable.Seq

  type Sync[F[_]] = cats.effect.Sync[F]
  @inline def Sync: cats.effect.Sync.type = cats.effect.Sync

  type Async[F[_]] = cats.effect.Async[F]
  @inline def Async: cats.effect.Async.type = cats.effect.Async

  type Effect[F[_]] = cats.effect.Effect[F]
  @inline def Effect: cats.effect.Effect.type = cats.effect.Effect

  type Concurrent[F[_]] = cats.effect.Concurrent[F]
  @inline def Concurrent: cats.effect.Concurrent.type = cats.effect.Concurrent

  type ConcurrentEffect[F[_]] = cats.effect.ConcurrentEffect[F]
  @inline def ConcurrentEffect: cats.effect.ConcurrentEffect.type = cats.effect.ConcurrentEffect

  type Monad[F[_]] = cats.Monad[F]
  @inline def Monad: cats.Monad.type = cats.Monad

  type MonadError[F[_], E] = cats.MonadError[F, E]
  @inline def MonadError: cats.MonadError.type = cats.MonadError

  type MonadThrowable[F[_]] = cats.MonadError[F, Throwable]
  @inline def MonadThrowable: cats.MonadError.type = cats.MonadError

  type Applicative[F[_]] = cats.Applicative[F]
  @inline def Applicative: cats.Applicative.type = cats.Applicative

  type ApplicativeThrowable[F[_]] = cats.ApplicativeError[F, Throwable]
  val ApplicativeThrowable: cats.ApplicativeError.type = cats.ApplicativeError

  object validated extends ValidatedTypeDefinitions with ValidatedSyntax.Implicits with ValidatedSyntaxAsync.Implcits

}

