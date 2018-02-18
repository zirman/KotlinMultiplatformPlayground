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

expect class FreeOperation<A>(s: Operation<A>) :
    Free1<A> {
    val s: Operation<A>
}

// Monad and Applicative functions on Free.

fun <A, B> Free1<A>.map(f: (A) -> B): Free2<A, B> = Map(this, f)
fun <A> Free1<Free1<A>>.flatten(): Free1<A> = Flatten(this)
fun <A, B> Free1<A>.bind(f: (A) -> Free1<B>): Free1<B> = map(f).flatten()
fun <A, B> Free1<A>.apLeft(sb: Free1<B>): Free1<A> = bind { x -> sb.bind { Lift(x) } }
fun <A, B> Free1<A>.apRight(sb: Free1<B>): Free1<B> = bind { sb }
