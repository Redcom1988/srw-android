package com.redcom1988.core.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.redcom1988.core.preference.AndroidPreference.BooleanPrimitive
import com.redcom1988.core.preference.AndroidPreference.FloatPrimitive
import com.redcom1988.core.preference.AndroidPreference.IntPrimitive
import com.redcom1988.core.preference.AndroidPreference.LongPrimitive
import com.redcom1988.core.preference.AndroidPreference.Object
import com.redcom1988.core.preference.AndroidPreference.StringPrimitive
import com.redcom1988.core.preference.AndroidPreference.StringSetPrimitive
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class AndroidPreferenceStore(
    private val sharedPreferences: SharedPreferences,
    private val encryptedSharedPreferences: SharedPreferences,
) : PreferenceStore {

    private fun prefs(key: String) =
        if (Preference.isPrivate(key)) encryptedSharedPreferences else sharedPreferences

    private fun keyFlow(key: String) =
        if (Preference.isPrivate(key)) encryptedSharedPreferences.keyFlow else sharedPreferences.keyFlow

    override fun getString(key: String, defaultValue: String): Preference<String> {
        return StringPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun getLong(key: String, defaultValue: Long): Preference<Long> {
        return LongPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun getInt(key: String, defaultValue: Int): Preference<Int> {
        return IntPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun getFloat(key: String, defaultValue: Float): Preference<Float> {
        return FloatPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Preference<Boolean> {
        return BooleanPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun getStringSet(key: String, defaultValue: Set<String>): Preference<Set<String>> {
        return StringSetPrimitive(prefs(key), keyFlow(key), key, defaultValue)
    }

    override fun <T> getObject(
        key: String,
        defaultValue: T,
        serializer: (T) -> String,
        deserializer: (String) -> T,
    ): Preference<T> {
        return Object(
            preferences = prefs(key),
            keyFlow = keyFlow(key),
            key = key,
            defaultValue = defaultValue,
            serializer = serializer,
            deserializer = deserializer,
        )
    }

    override fun getAll(): Map<String, *> {
        val result = linkedMapOf<String, Any?>()
        sharedPreferences.all?.let { result.putAll(it) }
        try {
            encryptedSharedPreferences.all?.let { result.putAll(it) }
        } catch (_: UnsupportedOperationException) { }
        return result
    }
}

fun createEncryptedPreferenceStore(context: Context): AndroidPreferenceStore {
    return AndroidPreferenceStore(
        sharedPreferences = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE),
        encryptedSharedPreferences = EncryptedSharedPreferences.create(
            context,
            "app_pref_encrypted",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    )
}

private val SharedPreferences.keyFlow
    get() = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key: String? ->
            trySend(
                key,
            )
        }
        registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            unregisterOnSharedPreferenceChangeListener(listener)
        }
    }