@file:Suppress("unused")

package com.kocaptain.utils

import com.kocaptain.shared.operations.Operation

expect sealed class IO3<in A, in B, out C>
typealias IO2<A, B> = IO3<Nothing, A, B>
typealias IO1<A> = IO2<Nothing, A>

expect class Pure<out A>(x: A) : IO1<A> {
    val x: A
}

expect class Map<A, out B>(io: IO1<A>, f: (A) -> B) : IO2<A, B> {
    val io: IO1<A>
    val f: (A) -> B
}

expect class Flatten<out A>(io: IO1<IO1<A>>) : IO1<A> {
    val io: IO1<IO1<A>>
}

expect class Zip<A, B, out C>(
    io1: IO1<A>,
    io2: IO1<B>,
    f: (A, B) -> IO1<C>
) : IO3<A, B, C> {
    val io1: IO1<A>
    val io2: IO1<B>
    val f: (A, B) -> IO1<C>
}

expect class Lift<A>(s: Operation<A>) : IO1<A> {
    val s: Operation<A>
}

// Monad and Applicative functions on IO1<A>.

fun <A, B> IO1<A>.map(f: (A) -> B): IO2<A, B> = Map(this, f)
fun <A> IO1<IO1<A>>.flatten(): IO1<A> = Flatten(this)
fun <A, B> IO1<A>.bind(f: (A) -> IO1<B>): IO1<B> = map(f).flatten()
fun <A, B> IO1<A>.apPrev(sb: IO1<B>): IO1<A> = bind { x -> sb.bind { Pure(x) } }
fun <A, B> IO1<A>.apNext(sb: IO1<B>): IO1<B> = bind { sb }

// Utility functions on IO1<A?>

inline fun <A : Any, B : Any> IO1<A?>.bindIfNotNull(crossinline f: (A) -> IO1<B?>): IO1<B?> =
    bind { x -> x?.let(f) ?: Pure(null) }

inline fun <A : Any, B : Any> IO1<A?>.mapIfNotNull(crossinline f: (A) -> B?): IO1<B?> =
    map { x -> x?.let(f) }

fun <A : Any> IO1<A?>.apNextIfNull(av: IO1<A?>): IO1<A?> =
    bind { x -> x?.let(::Pure) ?: av }

fun <A : Any, B> IO1<A?>.doIfNotNull(f: (A) -> IO1<B>): IO1<A?> =
    bind { x -> x?.run { f(x).apNext(Pure(x)) } ?: Pure(null) }

// Utility functions on IO1<Either<A, B>>

inline fun <A, B, C> IO1<Either<A, B>>.mapRight(crossinline f: (B) -> C): IO1<Either<A, C>> =
    map { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> Either.Right(f(either.value))
        }
    }

fun <A, B : Any> IO1<Either<A, B?>>.rightOrNull(): IO1<B?> =
    map { either ->
        when (either) {
            is Either.Left -> null
            is Either.Right -> either.value
        }
    }

inline fun <A, B : Any, C : Any> IO1<Either<A, B?>>.bindIfRightNotNull(
    crossinline f: (B) -> Either<A, C>
): IO1<Either<A, C?>> =
    bind { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> either.value?.let(f) ?: Either.Right(null)
        }
            .let(::Pure)
    }

inline fun <A, B : Any, C : Any> IO1<Either<A, B?>>.mapRightIfNotNull(
    crossinline f: (B) -> C?
): IO1<Either<A, C?>> =
    map { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> either.value?.let(f).let { Either.Right(it) }
        }
    }

fun <A, B : Any> IO1<Either<A, B?>>.apNextIfRightNull(
    av: IO1<Either<A, B?>>
): IO1<Either<A, B?>> =
    bind { either ->
        when (either) {
            is Either.Left -> Pure(either)
            is Either.Right -> either.value?.run { Pure(either) } ?: av
        }
    }
