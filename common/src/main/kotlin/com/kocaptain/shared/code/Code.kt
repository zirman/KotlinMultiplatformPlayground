@file:Suppress("unused")

package com.kocaptain.shared.code

import com.kocaptain.shared.data.*
import com.kocaptain.shared.operations.*
import com.kocaptain.utils.*

// App's shared code goes here

val doStuff: Free1<Either<Error, String>> =
    getRequestString("http://www.google.com/")

private const val loginIdKey = "loggedin_userkey"

val getLoginId: Free1<String?> = getSettingString(loginIdKey)

private const val guidKey = "loggedin_user_guid"

// Gets user's guid from settings, if not there, download user profile with guid and save.

val getUserGuid: Free1<String?> =
    getSettingString(guidKey)
        .bind { nullableGuid ->
            nullableGuid
                ?.let(::Lift)
                ?: Zip(getLoginId, getGpsLocation()) { nulLoginId, location ->
                    nulLoginId
                        ?.let { loginId ->
                            getProfile(loginId, location)
                                .bind { eitherProfile ->
                                    when (eitherProfile) {
                                        is Either.Right -> {
                                            putSettingString(guidKey, eitherProfile.value.guid)
                                                .apRight(Lift(eitherProfile.value.guid))
                                        }

                                        is Either.Left -> Lift(null)
                                    }
                                }
                        }
                        ?: Lift(null)
                }
        }
