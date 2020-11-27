package com.mibrahimuadev.spending.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mibrahimuadev.spending.data.entity.*
import com.mibrahimuadev.spending.data.local.dao.*


@Database(
    entities = [(Transaction::class), (Currency::class), (Category::class), (CategoryIcon::class)],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    abstract fun mataUangDao(): CurrencyDao

    abstract fun categoryDao(): CategoryDao

    abstract fun iconTransaksiDao(): TransactionIconDao

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
                        "spending_database"
                    ).fallbackToDestructiveMigration()
                        .createFromAsset("database/spending_database.db")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

