package com.kocaptain.shared.data

actual class Error actual constructor(actual val message: String)

actual class Location actual constructor(
    actual val latitude: Double,
    actual val longitude: Double,
    actual val altitude: Double,
    actual val accuracy: Double
)

actual class Profile actual constructor(
    actual val firstName: String,
    actual val lastName: String,
    actual val guid: String
)
