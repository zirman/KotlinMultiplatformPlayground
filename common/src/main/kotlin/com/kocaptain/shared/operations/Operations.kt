@file:Suppress("unused")

package com.kocaptain.shared.operations

import com.kocaptain.shared.data.*
import com.kocaptain.utils.*

// App's shared operations goes here

expect sealed class Operation<in A> {
    fun unerase(f: Function1<*, Unit>): Function1<A, Unit>
}

expect class WriteLog(line: String) : Operation<Unit> {
    val line: String
}

fun writeLog(line: String): IO1<Unit> = Lift(WriteLog(line))

expect class GetRequestString(url: String) : Operation<Either<Error, String>> {
    val url: String
}

fun getRequestString(url: String): IO1<Either<Error, String>> =
    Lift(GetRequestString(url))

expect class GetSettingString(key: String) : Operation<String?> {
    val key: String
}

fun getSettingString(key: String): IO1<String?> =
    Lift(GetSettingString(key))

expect class PutSettingString(key: String, value: String) : Operation<Unit> {
    val key: String
    val value: String
}

fun putSettingString(key: String, value: String): IO1<Unit> =
    Lift(PutSettingString(key, value))

expect class GetGpsLocation(
    timeoutInSeconds: Long,
    minAccuracyInMeters: Float
) : Operation<Location?> {
    val timeoutInSeconds: Long
    val minAccuracyInMeters: Float
}

fun getGpsLocation(
    timeoutInSeconds: Long = 6,
    minAccuracyInMeters: Float = 120f
): IO1<Location?> =
    Lift(GetGpsLocation(timeoutInSeconds, minAccuracyInMeters))

expect class GetProfile(loginId: String, location: Location?) : Operation<Either<Error, Profile>> {
    val loginId: String
    val location: Location?
}

fun getProfile(loginId: String, location: Location?): IO1<Either<Error, Profile>> =
    Lift(GetProfile(loginId, location))
