package com.desokolov.lists

import androidx.compose.ui.window.ComposeUIViewController
import com.desokolov.lists.di.appModules
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        startKoin {
            modules(appModules)
        }
    }
) {
    App()
}
