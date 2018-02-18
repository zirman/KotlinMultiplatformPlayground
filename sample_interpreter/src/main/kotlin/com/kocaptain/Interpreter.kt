package com.kocaptain

import com.kocaptain.shared.data.*
import com.kocaptain.shared.operations.*
import com.kocaptain.utils.*
import com.kocaptain.utils.Map
import io.reactivex.Single
import io.reactivex.functions.BiFunction

private val settings = mutableMapOf<String, String>()

@Suppress("unused")
fun <A, B, C> evaluate(free: Free3<A, B, C>, cont: (C) -> Unit): Unit =
    when (free) {
        is Lift<C> -> cont(free.x)
        is Flatten<C> -> evaluate(free.free) { x -> evaluate(x, cont) }
        is Map<B, C> -> evaluate(free.free) { x -> cont(free.f(x)) }

        is Zip<A, B, C> -> {
            Single
                .zip<A, B, Free1<C>>(
                    Single.create { emitter -> evaluate(free.free1, emitter::onSuccess) },
                    Single.create { emitter -> evaluate(free.free2, emitter::onSuccess) },
                    BiFunction { a, b -> free.f(a, b) }
                )
                .subscribe({ c -> evaluate(c, cont) }, {})

            Unit
        }

        is FreeOperation<C> -> operation(free.s, cont)
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
