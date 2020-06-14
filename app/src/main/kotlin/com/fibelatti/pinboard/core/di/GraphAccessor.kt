package com.fibelatti.pinboard.core.di

import androidx.fragment.app.FragmentFactory
import com.fibelatti.pinboard.features.InAppUpdateManager
import com.fibelatti.pinboard.features.user.data.UserDataSource
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@EntryPoint
interface GraphAccessor {

    fun fragmentFactory(): FragmentFactory
    fun userDataSource(): UserDataSource
    fun inAppUpdateManager(): InAppUpdateManager
}
