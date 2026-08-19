package com.n0rbelio.familylocationalert.data

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private val MIGRATION_3_4 =
        object : Migration(3, 4) {

            override fun migrate(
                database: SupportSQLiteDatabase
            ) {

                database.execSQL(
                    """
                    ALTER TABLE locations
                    ADD COLUMN smsAlertMode TEXT NOT NULL DEFAULT 'BOTH'
                    """.trimIndent()
                )
            }
        }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(
        context: Context
    ): AppDatabase {

        return INSTANCE ?: synchronized(this) {

            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "family_location_alert.db"
            )
                .addMigrations(
                    MIGRATION_3_4
                )
                .build()
                .also {
                    INSTANCE = it
                }
        }
    }
}