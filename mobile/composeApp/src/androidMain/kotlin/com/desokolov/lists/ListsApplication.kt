package com.desokolov.lists

import android.app.Application
import com.desokolov.lists.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ListsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@ListsApplication)
            modules(appModules)
        }
    }
}
