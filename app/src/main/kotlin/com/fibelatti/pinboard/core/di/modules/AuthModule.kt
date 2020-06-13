package com.fibelatti.pinboard.core.di.modules

import androidx.fragment.app.Fragment
import com.fibelatti.pinboard.core.di.mapkeys.FragmentKey
import com.fibelatti.pinboard.features.user.data.UserDataSource
import com.fibelatti.pinboard.features.user.domain.UserRepository
import com.fibelatti.pinboard.features.user.presentation.AuthFragment
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoMap

@InstallIn(ApplicationComponent::class)
@Module
abstract class AuthModule {

    @Binds
    abstract fun UserDataSource.userRepository(): UserRepository

    @Binds
    @IntoMap
    @FragmentKey(AuthFragment::class)
    abstract fun AuthFragment.authFragment(): Fragment
}
