package com.kocaptain

import com.kocaptain.shared.data.*
import com.kocaptain.shared.operations.*
import com.kocaptain.utils.*
import com.kocaptain.utils.Map
import io.reactivex.Single
import io.reactivex.functions.BiFunction

private val settings = mutableMapOf<String, String>()

@Suppress("unused")
fun <A, B, C> evaluate(io: IO3<A, B, C>, cont: (C) -> Unit): Unit =
    when (io) {
        is Pure<C> -> cont(io.x)
        is Flatten<C> -> evaluate(io.io) { x -> evaluate(x, cont) }
        is Map<B, C> -> evaluate(io.io) { x -> cont(io.f(x)) }

        is Zip<A, B, C> -> {
            Single
                .zip<A, B, IO1<C>>(
                    Single.create { emitter -> evaluate(io.io1, emitter::onSuccess) },
                    Single.create { emitter -> evaluate(io.io2, emitter::onSuccess) },
                    BiFunction { a, b -> io.f(a, b) }
                )
                .subscribe({ c -> evaluate(c, cont) }, {})

            Unit
        }

        is Lift<C> -> operation(io.s, cont)
    }

fun <A> operation(s: Operation<A>, cont: (A) -> Unit): Unit =
    when (s) {
        is WriteLog -> {
            println(s.line)
            s.unerase(cont).invoke(Unit)
        }

        is GetRequestString -> {
            // TODO: GetRequestString
            s.unerase(cont).invoke(Either.Left(Error("Not implemented yet")))
            Unit
        }

        is GetSettingString -> {
            s
                .unerase(cont)
                .invoke(settings[s.key])

            Unit
        }

        is PutSettingString -> {
            settings[s.key] = s.value
            s.unerase(cont).invoke(Unit)
            Unit
        }

        is GetProfile -> {
            // TODO: GetProfile
            s.unerase(cont).invoke(Either.Left(Error("Not implemented yet")))
            Unit
        }

        is GetGpsLocation -> {
            // TODO: GetGpsLocation
            s.unerase(cont).invoke(null)
            Unit
        }
    }
