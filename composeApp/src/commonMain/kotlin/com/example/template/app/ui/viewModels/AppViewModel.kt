package com.example.template.app.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.template.app.domain.entities.LocaleSettings
import com.example.template.app.domain.useCases.GetLocaleSettingsUseCase
import com.example.template.app.domain.useCases.SetLocaleSettingsUseCase
import org.koin.core.component.KoinComponent

class AppViewModel(
    private val setLocaleSettingsUseCase: SetLocaleSettingsUseCase,
    private val getLocaleSettingsUseCase: GetLocaleSettingsUseCase,
): ViewModel(), KoinComponent {

    init {
        this.initalizer()
        this.setLocaleSettingsUseCase.execute(
            localeSettings = LocaleSettings.PortugueseBrazil
        )
    }

    fun getLocaleSettings(): LocaleSettings {
        return this.getLocaleSettingsUseCase.execute()
    }

    fun initalizer() {
    }
}