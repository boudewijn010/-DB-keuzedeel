package com.example.database_project

fun String.sha256(): String {
    val bytes = this.toByteArray()
    val digest = java.security.MessageDigest.getInstance("SHA-256")
    val hash = digest.digest(bytes)
    return hash.joinToString("") { "%02x".format(it) }
}