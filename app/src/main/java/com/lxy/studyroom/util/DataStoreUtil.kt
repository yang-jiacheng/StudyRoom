package com.lxy.studyroom.util

import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder
import com.lxy.studyroom.StudyRoomApplication
import io.reactivex.Single
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * 数据存储工具
 * https://developer.android.google.cn/reference/kotlin/androidx/datastore/rxjava2/package-summary
 * 上面网址打不开就科学上网
 */

@OptIn(ExperimentalCoroutinesApi::class)
object DataStoreUtil {

    private val dataStore =
        RxPreferenceDataStoreBuilder(StudyRoomApplication.context,"config").build()

    fun putString(key: String, value: String){
        dataStore.updateDataAsync { preferences: Preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[stringPreferencesKey(key)] = value
            Single.just(
                mutablePreferences
            )
        }
    }

    fun getString(key: String, defaultValue: String): String{
        val value = dataStore.data().blockingFirst()[stringPreferencesKey(key)]
        return value ?: defaultValue
    }

    fun putInt(key: String, value: Int) {
        dataStore.updateDataAsync { preferences: Preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[intPreferencesKey(key)] = value
            Single.just(mutablePreferences)
        }
    }

    fun getInt(key: String, defValue: Int): Int {
        val value = dataStore.data().blockingFirst()[intPreferencesKey(key)]
        return value ?: defValue
    }

    fun putBoolean(key: String, value: Boolean) {
        dataStore.updateDataAsync { preferences: Preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[booleanPreferencesKey(key)] = value
            Single.just(mutablePreferences)
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val value = dataStore.data().blockingFirst()[booleanPreferencesKey(key)]
        return value ?: defaultValue
    }

    fun putDouble(key: String, value: Double) {
        dataStore.updateDataAsync { preferences: Preferences ->
            val mutablePreferences = preferences.toMutablePreferences()
            mutablePreferences[doublePreferencesKey(key)] = value
            Single.just(mutablePreferences)
        }
    }

    fun getDouble(key: String, defValue: Double): Double {
        val value = dataStore.data().blockingFirst()[doublePreferencesKey(key)]
        return value ?: defValue
    }

}