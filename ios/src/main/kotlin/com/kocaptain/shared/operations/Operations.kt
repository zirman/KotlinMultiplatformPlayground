package com.kocaptain.shared.operations

import com.kocaptain.shared.data.Error
import com.kocaptain.shared.data.Location
import com.kocaptain.utils.Either
import com.kocaptain.shared.data.Profile

actual sealed class Operation<in A> {
    @Suppress("UNCHECKED_CAST")
    actual fun unerase(f: Function1<*, Unit>): Function1<A, Unit> = f as Function1<A, Unit>
}

actual class WriteLog actual constructor(actual val line: String) : Operation<Unit>()

actual class GetRequestString actual constructor(
    actual val url: String
) : Operation<Either<Error, String>>()

actual class GetSettingString actual constructor(
    actual val key: String
) : Operation<String?>()

actual class PutSettingString actual constructor(
    actual val key: String,
    actual val value: String
) : Operation<Unit>()

actual class GetGpsLocation actual constructor(
    actual val timeoutInSeconds: Long,
    actual val minAccuracyInMeters: Float
) : Operation<Location?>()

actual class GetProfile actual constructor(
    actual val loginId: String,
    actual val location: Location?
) : Operation<Either<Error, Profile>>()
