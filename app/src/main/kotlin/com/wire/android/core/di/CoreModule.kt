package com.wire.android.core.di

import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.wire.android.BuildConfig
import com.wire.android.core.accessibility.Accessibility
import com.wire.android.core.accessibility.AccessibilityConfig
import com.wire.android.core.accessibility.InputFocusViewModel
import com.wire.android.core.async.DefaultDispatcherProvider
import com.wire.android.core.async.DispatcherProvider
import com.wire.android.core.compatibility.Compatibility
import com.wire.android.core.config.AppVersionNameConfig
import com.wire.android.core.config.LocaleConfig
import com.wire.android.core.network.di.networkModule
import com.wire.android.core.ui.dialog.DialogBuilder
import com.wire.android.core.ui.navigation.FragmentStackHandler
import com.wire.android.core.ui.navigation.Navigator
import com.wire.android.core.ui.navigation.UriNavigationHandler
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val accessibilityModule: Module = module {
    factory { androidContext().getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager }
    factory { Accessibility(get()) }
    factory { AccessibilityConfig(get()) }
    viewModel { InputFocusViewModel(get()) }
}

val compatibilityModule: Module = module {
    factory { Compatibility() }
}

val localeModule: Module = module {
    factory { LocaleConfig(androidContext()) }
}

val appConfigModule: Module = module {
    factory { AppVersionNameConfig(BuildConfig.VERSION_NAME) }
}


val asyncModule: Module = module {
    single<DispatcherProvider> { DefaultDispatcherProvider() }
}

val uiModule: Module = module {
    factory { DialogBuilder() }

    single { Navigator(get(), get(), get(), get()) }
    single { FragmentStackHandler() }
    single { UriNavigationHandler() }
}
