package com.example.domain.preference

import com.example.core.preference.PreferenceStore
import com.example.core.preference.getEnum
import com.example.domain.theme.Themes

class ApplicationPreference(
    private val preferenceStore: PreferenceStore
) {
    fun testPreference() = preferenceStore.getBoolean(
        key = "test_preference",
        defaultValue = false
    )

    fun baseUrl() = preferenceStore.getString(
        key = "api_base_url",
        defaultValue = ""
    )

    fun appTheme() = preferenceStore.getEnum(
        key = "app_theme",
        defaultValue = Themes.SYSTEM,
    )
}