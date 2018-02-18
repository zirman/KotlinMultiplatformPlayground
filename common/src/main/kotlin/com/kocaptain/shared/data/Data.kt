@file:Suppress("unused")

package com.kocaptain.shared.data

// App's shared data goes here

expect class Error(message: String) {
    val message: String
}

expect class Location(
    latitude: Double,
    longitude: Double,
    altitude: Double,
    accuracy: Double
) {
    val latitude: Double
    val longitude: Double
    val altitude: Double
    val accuracy: Double
}

expect class Profile(firstName: String, lastName: String, guid: String) {
    val firstName: String
    val lastName: String
    val guid: String
}
