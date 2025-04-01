package com.example.database_project
import android.content.ContentValues

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String
) {
    fun toContentValues(forInsert: Boolean = false): ContentValues {
        val values = ContentValues()
        if (forInsert) {
            values.put(Databeest.COLUMN_ID, id)
        }
        values.put(Databeest.COLUMN_NAME, name)
        values.put(Databeest.COLUMN_EMAIL, email)
        values.put(Databeest.COLUMN_PASSWORD, password)
        return values
    }

    override fun toString(): String {
        return "User(id=$id, name='$name', email='$email', password='$password')"
    }
}