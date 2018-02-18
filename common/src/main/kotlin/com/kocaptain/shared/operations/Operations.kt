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

fun writeLog(line: String): Free1<Unit> = FreeOperation(WriteLog(line))

expect class GetRequestString(url: String) : Operation<Either<Error, String>> {
    val url: String
}

fun getRequestString(url: String): Free1<Either<Error, String>> =
    FreeOperation(GetRequestString(url))

expect class GetSettingString(key: String) : Operation<String?> {
    val key: String
}

fun getSettingString(key: String): Free1<String?> =
    FreeOperation(GetSettingString(key))

expect class PutSettingString(key: String, value: String) : Operation<Unit> {
    val key: String
    val value: String
}

fun putSettingString(key: String, value: String): Free1<Unit> =
    FreeOperation(PutSettingString(key, value))

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
): Free1<Location?> =
    FreeOperation(GetGpsLocation(timeoutInSeconds, minAccuracyInMeters))

expect class GetProfile(loginId: String, location: Location?) : Operation<Either<Error, Profile>> {
    val loginId: String
    val location: Location?
}

fun getProfile(loginId: String, location: Location?): Free1<Either<Error, Profile>> =
    FreeOperation(GetProfile(loginId, location))
