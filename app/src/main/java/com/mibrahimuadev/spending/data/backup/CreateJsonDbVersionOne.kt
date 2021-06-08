package com.mibrahimuadev.spending.data.backup

import android.app.Application
import com.mibrahimuadev.spending.data.AppDatabase
import com.mibrahimuadev.spending.data.dao.*
import com.mibrahimuadev.spending.data.model.CategoryType
import com.mibrahimuadev.spending.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.*


class CreateJsonDbVersionOne(val application: Application) : CreateJsonDbVersion {

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

    init {
        val database = AppDatabase.getInstance(application.applicationContext)
        accountDao = database.accountDao()
        backupDao = database.backupDao()
        categoryDao = database.categoryDao()
        settingDao = database.settingDao()
        driveDao = database.driveDao()
        transactionDao = database.transactionDao()
    }

    val accountList: MutableList<Account> = mutableListOf()
    val backupEntityList: MutableList<BackupEntity> = mutableListOf()
    val categoryList: MutableList<Category> = mutableListOf()
    val settingAppList: MutableList<SettingApp> = mutableListOf()
    val driveEntityList: MutableList<DriveEntity> = mutableListOf()
    val transactionSpendList: MutableList<TransactionSpend> = mutableListOf()

    override suspend fun fetchData() {
        return withContext(Dispatchers.IO) {
            accountList.addAll(accountDao.getAllUserVersionOne())
            backupEntityList.addAll(backupDao.getAllBackupEntityVersionOne())
            categoryList.addAll(categoryDao.getAllCategoryVersionOne())
            settingAppList.addAll(settingDao.getAllSettingAppVersionOne())
            driveEntityList.addAll(driveDao.getAllDriveEntityVersionOne())
            transactionSpendList.addAll(transactionDao.getAllTransactionSpendVersionOne())
        }
    }

    override fun writeJSONToFile(): String {
        var result: String = ""
        val fileJson: FileWriter

        val rootObject = JSONObject()
        rootObject.put("version", DATABASE_VERSION)

        val accountArray = JSONArray()
        val backupArray = JSONArray()
        val categoryArray = JSONArray()
        val settingArray = JSONArray()
        val driveArray = JSONArray()
        val transactionArray = JSONArray()


        for (account in accountList) {
            val accountObject = JSONObject()
            accountObject.put("userEmail", account.userEmail)
            accountObject.put("userId", account.userId)
            accountObject.put("userName", account.userName)
            accountArray.put(accountObject)
        }

        for (backup in backupEntityList) {
            val backupObject = JSONObject()
            backupObject.put("googleBackup", backup.googleBackup)
            backupObject.put("localBackup", backup.localBackup)
            backupObject.put("userId", backup.userId)
            backupArray.put(backupObject)
        }

        for (category in categoryList) {
            val categoryObject = JSONObject()
            categoryObject.put("categoryId", category.categoryId)
            categoryObject.put("categoryName", category.categoryName)
            categoryObject.put("categoryType", category.categoryType)
            categoryObject.put("iconId", category.iconId)
            categoryArray.put(categoryObject)
        }

        for (drive in driveEntityList) {
            val driveObject = JSONObject()
            driveObject.put("fileId", drive.fileId)
            driveObject.put("fileName", drive.fileName)
            driveObject.put("fileType", drive.fileType)
            driveObject.put("lastModified", drive.lastModified)
            driveArray.put(driveObject)
        }

        for (setting in settingAppList) {
            val settingObject = JSONObject()
            settingObject.put("settingValue", setting.settingValue)
            settingObject.put("settingId", setting.settingId)
            settingObject.put("settingName", setting.settingName)
            settingObject.put("settingSysname", setting.settingSysname)
            settingObject.put("userId", setting.userId)
            settingArray.put(settingObject)
        }

        for (transaction in transactionSpendList) {
            val transactionObject = JSONObject()
            transactionObject.put("categoryId", transaction.categoryId)
            transactionObject.put("transactionDate", transaction.transactionDate)
            transactionObject.put("transactionExpense", transaction.transactionExpense)
            transactionObject.put("transactionId", transaction.transactionId)
            transactionObject.put("transactionIncome", transaction.transactionIncome)
            transactionObject.put("transactionNote", transaction.transactionNote)
            transactionObject.put("transactionType", transaction.transactionType)
            transactionArray.put(transactionObject)
        }

        val tableAccount = JSONObject()
        tableAccount.put("tableName", "account")
        tableAccount.put("rows", accountArray)

        val tableBackupEntity = JSONObject()
        tableBackupEntity.put("tableName", "backup_entity")
        tableBackupEntity.put("rows", backupArray)

        val tableCategory = JSONObject()
        tableCategory.put("tableName", "category")
        tableCategory.put("rows", categoryArray)

        val tableDriveEntity = JSONObject()
        tableDriveEntity.put("tableName", "drive_entity")
        tableDriveEntity.put("rows", driveArray)

        val tableSettingApp = JSONObject()
        tableSettingApp.put("tableName", "setting_app")
        tableSettingApp.put("rows", settingArray)

        val tableTransaction = JSONObject()
        tableTransaction.put("tableName", "transaction")
        tableTransaction.put("rows", transactionArray)

        val entitiesArray = JSONArray()
        entitiesArray.put(tableAccount)
        entitiesArray.put(tableBackupEntity)
        entitiesArray.put(tableCategory)
        entitiesArray.put(tableDriveEntity)
        entitiesArray.put(tableSettingApp)
        entitiesArray.put(tableTransaction)


        rootObject.put("entities", entitiesArray)
//        println(rootObject)
        try {
            fileJson = FileWriter(
                File(
                    application.filesDir,
                    "BackupDB"
                ).toString() + "/spending_database.json"
            )
            fileJson.write(rootObject.toString())
            fileJson.close()
        } catch (e: IOException) {
            Timber.d(e)
        }
        result = rootObject.toString()

        return result
    }

    data class Account(
        val userId: String,
        val userName: String,
        val userEmail: String,
    )

    data class BackupEntity(
        val userId: String,
        val localBackup: Date?,
        val googleBackup: Date?,
    )

    data class Category(
        val categoryId: Int = 0,
        val categoryName: String? = "",
        val iconId: Int,
        val categoryType: CategoryType
    )

    data class SettingApp(
        val settingId: Int,
        val userId: String?,
        val settingName: String,
        val settingValue: String?,
        val settingSysname: String,
    )

    data class DriveEntity(
        val fileType: String,
        val fileId: String?,
        val fileName: String?,
        val lastModified: Date?
    )

    data class TransactionSpend(
        val transactionId: Long = 0L,
        val transactionType: TransactionType,
        val transactionIncome: Double?,
        val transactionExpense: Double?,
        val categoryId: Int,
        val transactionDate: Date,
        val transactionNote: String? = ""
    )

}