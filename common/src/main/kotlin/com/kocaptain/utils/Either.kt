package com.kocaptain.utils

@Suppress("unused")
expect sealed class Either<out A, out B> {
    class Left<out A>(value: A) : Either<A, Nothing> {
        val value: A
    }

    class Right<out B>(value: B) : Either<Nothing, B> {
        val value: B
    }
}
