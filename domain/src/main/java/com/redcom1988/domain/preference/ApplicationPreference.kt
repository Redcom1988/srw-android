package com.redcom1988.domain.preference

import com.redcom1988.core.preference.PreferenceStore
import com.redcom1988.core.preference.getEnum
import com.redcom1988.domain.theme.Themes

class ApplicationPreference(
    private val preferenceStore: PreferenceStore
) {
    fun appTheme() = preferenceStore.getEnum(
        key = "app_theme",
        defaultValue = Themes.SYSTEM,
    )
}