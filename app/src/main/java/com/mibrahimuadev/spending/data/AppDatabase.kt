package com.mibrahimuadev.spending.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mibrahimuadev.spending.data.dao.*
import com.mibrahimuadev.spending.data.entity.*
import com.mibrahimuadev.spending.utils.Converters


@Database(
    version = 1,
    entities = [(TransactionEntity::class), (CurrencyEntity::class), (CategoryEntity::class),
        (IconCategoryEntity::class), (AccountEntity::class), (GoogleAuthEntity::class),
        (DriveEntity::class), (BackupEntity::class), (SettingEntity::class)],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun mataUangDao(): CurrencyDao

    abstract fun categoryDao(): CategoryDao

    abstract fun iconCategoryDao(): IconCategoryDao

    abstract fun accountDao(): AccountDao

    abstract fun googleAuthDao(): GoogleAuthDao

    abstract fun driveDao(): DriveDao

    abstract fun backupDao(): BackupDao

    abstract fun settingDao(): SettingDao

    companion object {
        /**
         * Annotate INSTANCE with @Volatile.
         * The value of a volatile variable will never be cached,
         * and all writes and reads will be done to and from the main memory.
         * This helps make sure the value of INSTANCE is always up-to-date and the same to all
         * execution threads.
         * It means that changes made by one thread to INSTANCE are visible to all other
         * threads immediately, and you don't get a situation
         * where, say, two threads each update the same entity in a cache,
         * which would create a problem.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null
        var DB_NAME = "spending_database"

        // Migration path definition from version 2 to version 3.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }

        fun getInstance(context: Context): AppDatabase {
            /**
             * Multiple threads can potentially ask for a database instance at the same time,
             * resulting in two databases instead of one.
             * This problem is not likely to happen in this sample app,
             * but it's possible for a more complex app.
             * Wrapping the code to get the database into synchronized means that only one thread of
             * execution at a time can enter this block of code,
             * which makes sure the database only gets initialized once.
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .createFromAsset("database/spending_database.db")
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

        // close database
        fun destroyInstance() {
            if (INSTANCE?.isOpen == true) {
                INSTANCE?.close()
            }
            INSTANCE = null
        }
    }
}

