package de.malteans.recipes

import android.app.Application
import de.malteans.recipes.di.androidAppModule
import de.malteans.recipes.di.initKoin
import org.koin.android.ext.koin.androidContext

class RecipesApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(androidAppModule) {
            androidContext(this@RecipesApplication)
        }
    }
}