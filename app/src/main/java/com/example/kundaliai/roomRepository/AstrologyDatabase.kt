package com.example.kundaliai.roomRepository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Bumped version from 1 -> 2 to match the schema change (added unique index on `username`).
@Database(entities = [AstrologyReading::class], version = 2, exportSchema = true)
abstract class AstrologyDatabase : RoomDatabase() {
    abstract fun astrologyReadingDao(): AstrologyReadingDao

    companion object {
        @Volatile
        private var INSTANCE: AstrologyDatabase? = null

        // Migration from 1 -> 2: robustly handle duplicates and create the unique index on username.
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1) Create a new table with the desired schema (id autogen, username, jsonData, timestamp)
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS astrology_readings_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        jsonData TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // 2) Copy one row per username into the new table. Choose the row with the latest timestamp.
                db.execSQL(
                    """
                    INSERT INTO astrology_readings_new (username, jsonData, timestamp)
                    SELECT ar.username, ar.jsonData, ar.timestamp
                    FROM astrology_readings ar
                    INNER JOIN (
                        SELECT username, MAX(timestamp) as maxts
                        FROM astrology_readings
                        GROUP BY username
                    ) grouped
                    ON ar.username = grouped.username AND ar.timestamp = grouped.maxts
                    """.trimIndent()
                )

                // 3) Drop the old table
                db.execSQL("DROP TABLE IF EXISTS astrology_readings")

                // 4) Rename the new table to the expected name
                db.execSQL("ALTER TABLE astrology_readings_new RENAME TO astrology_readings")

                // 5) Create the unique index on username (Room expects this index)
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_astrology_readings_username ON astrology_readings(username)")
            }
        }

        fun getDatabase(context: Context): AstrologyDatabase {
            return INSTANCE ?: synchronized(this) {
                val builder = Room.databaseBuilder(
                    context,
                    AstrologyDatabase::class.java,
                    "astrology_db"
                )

                // Register the migration so existing DBs will be migrated in-place.
                // If you prefer to drop & recreate the DB during development instead, use
                // .fallbackToDestructiveMigration() instead.
                val instance = builder
                    .addMigrations(MIGRATION_1_2)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
