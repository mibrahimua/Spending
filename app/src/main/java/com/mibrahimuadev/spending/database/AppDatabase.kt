package com.mibrahimuadev.spending.database

import android.content.Context
import android.util.Log
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mibrahimuadev.spending.database.dao.*
import com.mibrahimuadev.spending.database.entity.*
import com.mibrahimuadev.spending.database.model.TipeTransaksi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(
    entities = [(Transaksi::class), (MataUang::class), (Kategori::class), (TransaksiKategori::class), (SubKategori::class), (IconTransaksi::class)],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transaksiDao(): TransaksiDao

    abstract fun mataUangDao(): MataUangDao

    abstract fun kategoriDao(): KategoriDao

    abstract fun subKategoriDao(): SubKategoriDao

    abstract fun iconTransaksiDao(): IconTransaksiDao

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
//                        .addCallback(TransaksiDatabaseCallback(scope))
                        .createFromAsset("database/spending_database.db")
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

//    private class TransaksiDatabaseCallback(private val scope: CoroutineScope) :
//        RoomDatabase.Callback() {
//
//        override fun onOpen(db: SupportSQLiteDatabase) {
//            super.onOpen(db)
//            INSTANCE?.let { database ->
//                scope.launch {
//                    populateDatabase(database)
//                }
//            }
//        }
//
//        suspend fun populateDatabase(database: AppDatabase) {
//
//            database.transaksiDao().deleteAllTransaksi()
//            database.kategoriDao().deleteAllKategori()
//            database.mataUangDao().deleteAllMataUang()
//            database.subKategoriDao().deleteAllSubKategori()
//
//            val transaksi1 = Transaksi(1, TipeTransaksi.PEMASUKAN, 1, 1, 15000.00)
//            val transaksi2 = Transaksi(2, TipeTransaksi.PENGELUARAN, 2, 2, 20000.00)
//            val transaksi3 = Transaksi(3, TipeTransaksi.PENGELUARAN, 3, 3, 17000.00)
//
//            database.transaksiDao().insertTransaksi(transaksi1)
//            database.transaksiDao().insertTransaksi(transaksi2)
//            database.transaksiDao().insertTransaksi(transaksi3)
//
//            val kategori: MutableList<Kategori> = mutableListOf()
//            var i = 1
//            kategori.add(Kategori(i++, "Food", 0))
//            kategori.add(Kategori(i++, "Bilss", 0))
//            kategori.add(Kategori(i++, "Transportation", 0))
//            kategori.add(Kategori(i++, "Home", 0))
//            kategori.add(Kategori(i++, "Car", 0))
//            kategori.add(Kategori(i++, "Entertainment", 0))
//            kategori.add(Kategori(i++, "Shopping", 0))
//            kategori.add(Kategori(i++, "Telephone", 0))
//
//            for (data in kategori) {
//                database.kategoriDao().insertKategori(data)
//            }
//
//            val subKategori: MutableList<SubKategori> = mutableListOf()
//            var l = 1
//            subKategori.add(SubKategori(l++, 1, "Sarapan", 0))
//            subKategori.add(SubKategori(l++, 2, "Sarapan", 0))
//            subKategori.add(SubKategori(l++, 3, "Sarapan", 0))
//            subKategori.add(SubKategori(l++, 4, "Sarapan", 0))
//
//            for (data in subKategori) {
//                database.subKategoriDao().insertSubKategori(data)
//            }
//
//            val mataUang: MutableList<MataUang> = mutableListOf()
//            var j = 1
//            mataUang.add(MataUang(j++, "Rupiah", "IDR"))
//            mataUang.add(MataUang(j++, "Euro", "EUR"))
//            mataUang.add(MataUang(j++, "United Stated Dollar", "USD"))
//
//            for (data in mataUang) {
//                database.mataUangDao().insertMataUang(data)
//            }
//        }
//    }
}

