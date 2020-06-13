package com.fibelatti.pinboard.core.extension

import android.app.Activity
import androidx.annotation.StringRes
import androidx.core.app.ShareCompat
import dagger.hilt.android.EntryPointAccessors

fun Activity.shareText(@StringRes title: Int, text: String) {
    ShareCompat.IntentBuilder.from(this)
        .setType("text/plain")
        .setChooserTitle(title)
        .setText(text)
        .startChooser()
}

/**
 * Lazily retrieves a class annotated with [dagger.hilt.EntryPoint].
 *
 * This should not be called before [Activity.onCreate] since it depends on [Activity.getApplicationContext].
 */
inline fun <reified T> Activity.getEntryPoint(): Lazy<T> = lazy {
    EntryPointAccessors.fromApplication(applicationContext, T::class.java)
}
