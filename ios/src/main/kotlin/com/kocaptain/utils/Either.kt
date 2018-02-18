package com.kocaptain.utils

@Suppress("unused")
actual sealed class Either<out A, out B> {
    actual class Left<out A> actual constructor(actual val value: A) : Either<A, Nothing>()
    actual class Right<out B> actual constructor(actual val value: B) : Either<Nothing, B>()
}
