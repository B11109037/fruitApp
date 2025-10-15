package com.example.fruitapp

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 檔案最上層宣告，確保外部能透過 context.dataStore 存取
val Context.dataStore by preferencesDataStore("user_settings")

object UserPreferences {
    private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
    private val NOTIFICATION_ENABLED_KEY = booleanPreferencesKey("notification_enabled")
    private val AUTO_UPLOAD_KEY = booleanPreferencesKey("auto_upload_enabled")

    // ✅ 寫入深色模式
    suspend fun setDarkMode(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    // ✅ 寫入推播通知
    suspend fun setNotificationEnabled(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_ENABLED_KEY] = enabled
        }
    }

    // ✅ 寫入自動上傳
    suspend fun setAutoUpload(context: Context, enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[AUTO_UPLOAD_KEY] = enabled
        }
    }

    // 讀取深色模式設定
    fun getDarkModeFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[DARK_MODE_KEY] ?: false }

    // 讀取推播通知設定
    fun getNotificationEnabledFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[NOTIFICATION_ENABLED_KEY] ?: true }

    // 讀取自動上傳設定
    fun getAutoUploadFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[AUTO_UPLOAD_KEY] ?: false }
}
