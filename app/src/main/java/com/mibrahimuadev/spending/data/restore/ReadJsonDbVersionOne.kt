package com.mibrahimuadev.spending.data.restore

import android.app.Application
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.*
import com.mibrahimuadev.spending.data.entity.*
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.model.CategoryType
import com.mibrahimuadev.spending.data.model.TransactionType
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.*

class ReadJsonDbVersionOne(val application: Application) : ReadJsonDbVersion {

    /**
     * ATTENTION :
     * DONT CHANGE ANYTHING IF THIS VERSION IS NOT THE LATEST !!!
     *
     * Database version : 1
     * Affected column table :
     * 1. account
     * 2. backup_entity
     * 3. category
     * 4. setting_app
     * 5. drive_entity
     * 6. transaction_spend
     *
     * Not affected column table :
     * 7. currency
     * 8. icon_category
     * 9. room_master_table
     * 10. sqlite_sequence
     *
     */

    private val DATABASE_VERSION = 1
    private val accountDao: AccountDao
    private val backupDao: BackupDao
    private val categoryDao: CategoryDao
    private val settingDao: SettingDao
    private val driveDao: DriveDao
    private val transactionDao: TransactionDao

    val accountList: MutableList<AccountEntity> = mutableListOf()
    val backupEntityList: MutableList<BackupEntity> = mutableListOf()
    val categoryList: MutableList<CategoryEntity> = mutableListOf()
    val settingAppList: MutableList<SettingEntity> = mutableListOf()
    val driveEntityList: MutableList<DriveEntity> = mutableListOf()
    val transactionSpendList: MutableList<TransactionEntity> = mutableListOf()

    init {
        val database = AppDatabase.getInstance(application)
        accountDao = database.accountDao()
        backupDao = database.backupDao()
        categoryDao = database.categoryDao()
        settingDao = database.settingDao()
        driveDao = database.driveDao()
        transactionDao = database.transactionDao()
    }

    override fun readFileBackup(): String? {
        val databasePath = application.filesDir.path + "/BackupDB/spending_database.json"
        val backupDatabaseFile: String
        try {
            backupDatabaseFile = File(databasePath).bufferedReader().use {
                it.readText()
            }
            Timber.d(backupDatabaseFile)
        } catch (e: IOException) {
            Timber.d(e)
            return null
        }
        return backupDatabaseFile
    }

    override fun mappingDataBackup(backupDatabaseFile: String) {
        val jsonParser = JsonParser()
        val jsonTree = jsonParser.parse(backupDatabaseFile)

        if (jsonTree.isJsonObject) {
            val jsonObject = jsonTree.asJsonObject

            /**
             * Get version of app
             */
            val versionApp: JsonElement = jsonObject.get("version")

            /**
             * Get entities of app
             */
            val entitiesObject: JsonElement = jsonObject.get("entities")
            if (entitiesObject.isJsonArray) {
                val entities = entitiesObject.asJsonArray
                for (entity in entities) {
                    if (entity.isJsonObject) {
                        val entityTree = entity.asJsonObject

                        val tableName: JsonElement = entityTree.get("tableName")
                        Timber.d(tableName.toString())

                        if (tableName.asString.equals("account")) {
                            mappingAccountData(entityTree)
                        }

                        if (tableName.asString.equals("backup_entity")) {
                            mappingBackupEntityData(entityTree)
                        }

                        if (tableName.asString.equals("category")) {
                            mappingCategoryData(entityTree)
                        }

                        if (tableName.asString.equals("setting_app")) {
                            mappingSettingAppData(entityTree)
                        }

                        if (tableName.asString.equals("drive_entity")) {
                            mappingDriveEntityData(entityTree)
                        }

                        if (tableName.asString.equals("transaction")) {
                            mappingTransactionSpendData(entityTree)
                        }
                    }
                }
            }
        }
    }

    override suspend fun insertBackupDataToDatabase() {
        val readFileBackup = readFileBackup()
        readFileBackup?.let { mappingDataBackup(it) }

        for (account in accountList) {
            accountDao.insertOrUpdateLoggedUser(account)
        }

        for (backupEntity in backupEntityList) {
            backupDao.insertOrUpdateBackupDate(backupEntity)
        }

        for (category in categoryList) {
            categoryDao.insertOrUpdate(category)
        }

        for (settingApp in settingAppList) {
            settingApp.userId?.let { settingDao.updateSettingUserId(it) }
            settingDao.updateBackupSettingValue(
                settingApp.settingSysname,
                BackupSchedule.valueOf(settingApp.settingValue.toString())
            )
        }

        for (driveEntity in driveEntityList) {
            driveDao.insertFileDrive(driveEntity)
        }

        for (transactionSpend in transactionSpendList) {
            transactionDao.insertOrUpdate(transactionSpend)
        }
    }

    fun mappingAccountData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val account = row.asJsonObject
                    accountList.add(
                        AccountEntity(
                            userId = account.get("userId").asString,
                            userEmail = account.get("userEmail").asString,
                            userName = account.get("userName").asString
                        )
                    )
                }
            }
        }
    }

    fun mappingBackupEntityData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val backupEntity = row.asJsonObject
                    backupEntityList.add(
                        BackupEntity(
                            userId = backupEntity.get("userId").asString,
                            localBackup = Date(backupEntity.get("localBackup").asLong),
                            googleBackup = Date(backupEntity.get("googleBackup").asLong)
                        )
                    )
                }
            }
            Timber.d(backupEntityList.toString())
        }
    }

    fun mappingCategoryData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val categoryEntity = row.asJsonObject
                    categoryList.add(
                        CategoryEntity(
                            categoryId = categoryEntity.get("categoryId").asInt,
                            categoryName = categoryEntity.get("categoryName").asString,
                            iconId = categoryEntity.get("iconId").asInt,
                            categoryType = CategoryType.valueOf(categoryEntity.get("categoryType").asString)
                        )
                    )
                }
            }
        }
    }

    fun mappingSettingAppData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val settingEntity = row.asJsonObject
                    settingAppList.add(
                        SettingEntity(
                            settingId = settingEntity.get("settingId").asInt,
                            userId = settingEntity.get("userId").asString,
                            settingName = settingEntity.get("settingName").asString,
                            settingValue = settingEntity.get("settingValue").asString,
                            settingSysname = settingEntity.get("settingSysname").asString
                        )
                    )
                }
            }
        }
    }

    fun mappingDriveEntityData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val driveEntity = row.asJsonObject
                    driveEntityList.add(
                        DriveEntity(
                            fileId = driveEntity.get("fileId").asString,
                            fileName = driveEntity.get("fileName").asString,
                            fileType = driveEntity.get("fileType").asString,
                            lastModified = null
                        )
                    )
                }
            }
        }
    }

    fun mappingTransactionSpendData(entityTree: JsonObject) {
        val rowsObject: JsonElement = entityTree.get("rows")
        if (rowsObject.isJsonArray) {
            val rows = rowsObject.asJsonArray
            for (row in rows) {
                if (row.isJsonObject) {
                    val transactionEntity = row.asJsonObject
                    transactionSpendList.add(
                        TransactionEntity(
                            transactionDate = Date(transactionEntity.get("transactionDate").asLong),
                            transactionExpense = transactionEntity.get("transactionExpense").asDouble,
                            transactionId = transactionEntity.get("transactionId").asLong,
                            transactionIncome = transactionEntity.get("transactionIncome").asDouble,
                            transactionNote = transactionEntity.get("transactionNote").asString,
                            transactionType = TransactionType.valueOf(transactionEntity.get("transactionType").asString),
                            categoryId = transactionEntity.get("categoryId").asInt
                        )
                    )
                }
            }
        }
    }
}