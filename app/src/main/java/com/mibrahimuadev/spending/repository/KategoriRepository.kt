package com.mibrahimuadev.spending.repository

import androidx.lifecycle.LiveData
import com.mibrahimuadev.spending.database.dao.KategoriDao
import com.mibrahimuadev.spending.database.dao.SubKategoriDao
import com.mibrahimuadev.spending.database.entity.Kategori

class KategoriRepository(kategoriDao: KategoriDao) {

    val allKategori: LiveData<List<Kategori>> = kategoriDao.lihatSemuaKategori()

}