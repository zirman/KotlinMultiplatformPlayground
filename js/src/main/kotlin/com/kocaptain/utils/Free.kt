package com.kocaptain.utils

import com.kocaptain.shared.operations.Operation

@Suppress("unused")
actual sealed class Free3<in A, in B, out C>

actual class Lift<out A> actual constructor(actual val x: A) : Free3<Nothing, Nothing, A>()

actual class Map<A, out B> actual constructor(
    actual val free: Free1<A>,
    actual val f: (A) -> B
) : Free3<Nothing, A, B>()

actual class Flatten<out A> actual constructor(
    actual val free: Free1<Free1<A>>
) : Free3<Nothing, Nothing, A>()

actual class Zip<A, B, out C> actual constructor(
    actual val free1: Free1<A>,
    actual val free2: Free1<B>,
    actual val f: (A, B) -> Free1<C>
) : Free3<A, B, C>()

actual class FreeOperation<A> actual constructor(
    actual val s: Operation<A>
) : Free3<Nothing, Nothing, A>()
