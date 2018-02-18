@file:Suppress("unused")

package com.kocaptain.utils

import com.kocaptain.shared.operations.Operation

expect sealed class Free3<in A, in B, out C>
typealias Free2<A, B> = Free3<Nothing, A, B>
typealias Free1<A> = Free2<Nothing, A>

expect class Lift<out A>(x: A) : Free1<A> {
    val x: A
}

expect class Map<A, out B>(free: Free1<A>, f: (A) -> B) : Free2<A, B> {
    val free: Free1<A>
    val f: (A) -> B
}

expect class Flatten<out A>(free: Free1<Free1<A>>) : Free1<A> {
    val free: Free1<Free1<A>>
}

expect class Zip<A, B, out C>(
    free1: Free1<A>,
    free2: Free1<B>,
    f: (A, B) -> Free1<C>
) : Free3<A, B, C> {
    val free1: Free1<A>
    val free2: Free1<B>
    val f: (A, B) -> Free1<C>
}

expect class FreeOperation<A>(s: Operation<A>) : Free1<A> {
    val s: Operation<A>
}

// Monad and Applicative functions on Free1<A>.

fun <A, B> Free1<A>.map(f: (A) -> B): Free2<A, B> = Map(this, f)
fun <A> Free1<Free1<A>>.flatten(): Free1<A> = Flatten(this)
fun <A, B> Free1<A>.bind(f: (A) -> Free1<B>): Free1<B> = map(f).flatten()
fun <A, B> Free1<A>.apPrev(sb: Free1<B>): Free1<A> = bind { x -> sb.bind { Lift(x) } }
fun <A, B> Free1<A>.apNext(sb: Free1<B>): Free1<B> = bind { sb }

// Utility functions on Free1<A?>

inline fun <A : Any, B : Any> Free1<A?>.bindIfNotNull(crossinline f: (A) -> Free1<B?>): Free1<B?> =
    bind { x -> x?.let(f) ?: Lift(null) }

inline fun <A : Any, B : Any> Free1<A?>.mapIfNotNull(crossinline f: (A) -> B?): Free1<B?> =
    map { x -> x?.let(f) }

fun <A : Any> Free1<A?>.apNextIfNull(av: Free1<A?>): Free1<A?> =
    bind { x -> x?.let(::Lift) ?: av }

fun <A : Any, B> Free1<A?>.doIfNotNull(f: (A) -> Free1<B>): Free1<A?> =
    bind { x -> x?.run { f(x).apNext(Lift(x)) } ?: Lift(null) }

// Utility functions on Free1<Either<A, B>>

inline fun <A, B, C> Free1<Either<A, B>>.mapRight(crossinline f: (B) -> C): Free1<Either<A, C>> =
    map { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> Either.Right(f(either.value))
        }
    }

fun <A, B : Any> Free1<Either<A, B?>>.rightOrNull(): Free1<B?> =
    map { either ->
        when (either) {
            is Either.Left -> null
            is Either.Right -> either.value
        }
    }

inline fun <A, B : Any, C : Any> Free1<Either<A, B?>>.bindIfRightNotNull(
    crossinline f: (B) -> Either<A, C>
): Free1<Either<A, C?>> =
    bind { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> either.value?.let(f) ?: Either.Right(null)
        }
            .let(::Lift)
    }

inline fun <A, B : Any, C : Any> Free1<Either<A, B?>>.mapRightIfNotNull(
    crossinline f: (B) -> C?
): Free1<Either<A, C?>> =
    map { either ->
        when (either) {
            is Either.Left -> either
            is Either.Right -> either.value?.let(f).let { Either.Right(it) }
        }
    }

fun <A, B : Any> Free1<Either<A, B?>>.apNextIfRightNull(
    av: Free1<Either<A, B?>>
): Free1<Either<A, B?>> =
    bind { either ->
        when (either) {
            is Either.Left -> Lift(either)
            is Either.Right -> either.value?.run { Lift(either) } ?: av
        }
    }
