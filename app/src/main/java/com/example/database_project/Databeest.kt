package com.example.database_project

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Databeest(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_NAME = "database.db"
            private const val DATABASE_VERSION = 1
            const val TABLE_NAME = "users"
            const val COLUMN_ID = "_id"
            const val COLUMN_NAME = "name"
            const val COLUMN_EMAIL = "email"
            const val COLUMN_PASSWORD = "password"
        }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
          CREATE TABLE $TABLE_NAME (
          $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
          $COLUMN_NAME TEXT,
          $COLUMN_EMAIL TEXT,
          $COLUMN_PASSWORD TEXT
          )
          """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun resetTable() {
        val db = writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name = '$TABLE_NAME'")
        db.close()
    }

    fun addUser(user: User) {
        val db = writableDatabase
        val values = user.toContentValues(forInsert = true)
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getUser(id: Int): User? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD),
            "$COLUMN_ID = ?",
            arrayOf(id.toString()),
            null, null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return User(
                    it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD))
                )
            }
        }
        return null
    }

    private fun getPasswordByEmail(email: String): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_PASSWORD),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null, null, null, null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                return it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD))
            }
        }
        return null
    }

    fun password_verify(email: String, clearText: String): Boolean {
        val passwordFromDB = getPasswordByEmail(email)
        return clearText.sha256() == passwordFromDB
    }

    fun getAllUsers(): List<User> {
        val users = mutableListOf<User>()
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_EMAIL, COLUMN_PASSWORD),
            null,
            null,
            null,
            null,
            "$COLUMN_ID ASC"
        )

        cursor?.use {
            while (it.moveToNext()) {
                val user = User(
                    it.getInt(it.getColumnIndexOrThrow(COLUMN_ID)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_NAME)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_EMAIL)),
                    it.getString(it.getColumnIndexOrThrow(COLUMN_PASSWORD))
                )
                users.add(user)
            }
        }

        return users
    }

    fun updateItem(user: User): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(user.id.toString()))
    }
    }