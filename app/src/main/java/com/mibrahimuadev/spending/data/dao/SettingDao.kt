package com.mibrahimuadev.spending.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.mibrahimuadev.spending.data.entity.SettingEntity
import com.mibrahimuadev.spending.data.model.BackupSchedule
import com.mibrahimuadev.spending.data.backup.CreateJsonDbVersionOne
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingDao {
    @Query("SELECT * FROM setting_app WHERE settingSysname = :settingSysName")
    fun getSettingApp(settingSysName: String): Flow<SettingEntity>

    @Query("UPDATE setting_app SET userId = :userId")
    fun updateSettingUserId(userId: String)

    @Query("UPDATE setting_app SET settingValue = :settingValue WHERE settingSysname = :settingSysName")
    fun updateSettingValue(settingSysName: String, settingValue: BackupSchedule)

    @Query("UPDATE setting_app SET settingValue = null, userId = null")
    fun deleteAllSettingValue()

    @Query("SELECT * FROM setting_app")
    fun getAllSettingAppVersionOne(): List<CreateJsonDbVersionOne.SettingApp>
}