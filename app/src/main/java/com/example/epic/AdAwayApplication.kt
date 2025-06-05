package com.example.epic

import android.app.Application
import com.example.epic.di.dataBaseModule
import com.example.epic.di.networkModule
import com.example.epic.di.repositoriesModule
import com.example.epic.di.serviceModule
import com.example.epic.di.sharedPrefsModule
import com.example.epic.di.useCaseModule
import com.example.epic.di.viewModelsModule
import com.example.epic.helper.NotificationHelper
import com.example.epic.helper.PreferenceHelper
import com.example.epic.model.adblocking.AdBlockMethod
import com.example.epic.model.adblocking.AdBlockModel
import com.example.epic.model.source.SourceModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * This class is a custom [Application] for AdAway app.
 *
 * @author Bruce BUJON (bruce.bujon(at)gmail(dot)com)
 */
class AdAwayApplication : Application() {
    private val koinModules = listOf(
        viewModelsModule,
        networkModule,
        sharedPrefsModule,
        serviceModule,
        repositoriesModule,
        useCaseModule,
        dataBaseModule
    )

    /**
     * Get the source model.
     *
     * @return The common source model for the whole application.
     */
    /**
     * The common source model for the whole application.
     */
    var sourceModel: SourceModel? = null
        private set

    /**
     * The common ad block model for the whole application.
     */
    var adBlockModel: AdBlockModel? = null
        /**
         * Get the ad block model.
         *
         * @return The common ad block model for the whole application.
         */
        get() {
            // Check cached model
            field = AdBlockModel.build(this, AdBlockMethod.VPN)
            return field
        }
        private set

    override fun onCreate() {
        // Delegate application creation
        super.onCreate()
        initKoin()
        PreferenceHelper.init(this)
        // Create notification channels
        NotificationHelper.createNotificationChannels(this);
        // Create models
        this.sourceModel = SourceModel(this)
    }


    private fun initKoin() {
        startKoin {
            androidContext(this@AdAwayApplication)
            modules(koinModules)
        }
    }
}

