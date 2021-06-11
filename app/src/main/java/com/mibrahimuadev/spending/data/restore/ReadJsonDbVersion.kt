package com.mibrahimuadev.spending.data.restore

interface ReadJsonDbVersion {
    /**
     * Todo :
     * Read spending_database.json di folder BackupDB
     * cek versi file
     * insert data dari spending_database.json dengan "menyesuaikan versi file" ke database sqlite
     *
     * menyesuaikan versi file maksudnya menyesuaikan data model yang ada di spending_database.json
     * dengan database sqlite aplikasi
     *
     * fun readFileBackup using GSON
     * mapping data ke dalam masing2 list
     */

    fun readFileBackup(): String?

    fun mappingDataBackup(backupDatabaseFile: String)

    suspend fun insertBackupDataToDatabase()
}