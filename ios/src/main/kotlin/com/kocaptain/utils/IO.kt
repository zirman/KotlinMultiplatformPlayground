package com.kocaptain.utils

import com.kocaptain.shared.operations.Operation

@Suppress("unused")
actual sealed class IO3<in A, in B, out C>

actual class Pure<out A> actual constructor(actual val x: A) : IO3<Nothing, Nothing, A>()

actual class Map<A, out B> actual constructor(
    actual val io: IO1<A>,
    actual val f: (A) -> B
) : IO3<Nothing, A, B>()

actual class Flatten<out A> actual constructor(
    actual val io: IO1<IO1<A>>
) : IO3<Nothing, Nothing, A>()

actual class Zip<A, B, out C> actual constructor(
    actual val io1: IO1<A>,
    actual val io2: IO1<B>,
    actual val f: (A, B) -> IO1<C>
) : IO3<A, B, C>()

actual class Lift<A> actual constructor(
    actual val s: Operation<A>
) : IO3<Nothing, Nothing, A>()
