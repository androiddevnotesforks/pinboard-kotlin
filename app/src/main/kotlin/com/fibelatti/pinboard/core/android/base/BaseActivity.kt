package com.fibelatti.pinboard.core.android.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import androidx.annotation.CallSuper
import androidx.annotation.ContentView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentActivity
import com.fibelatti.core.extension.showStyledDialog
import com.fibelatti.pinboard.BuildConfig
import com.fibelatti.pinboard.R
import com.fibelatti.pinboard.core.android.Appearance
import com.fibelatti.pinboard.core.di.AppComponent
import com.fibelatti.pinboard.core.di.AppComponentProvider
import com.fibelatti.pinboard.core.di.ViewModelProvider
import java.io.PrintWriter
import java.io.StringWriter

abstract class BaseActivity @ContentView constructor(
    @LayoutRes contentLayoutId: Int
) : AppCompatActivity(contentLayoutId) {

    protected val appComponent: AppComponent
        get() = (application as AppComponentProvider).appComponent
    val viewModelProvider: ViewModelProvider
        get() = appComponent

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = appComponent.fragmentFactory()
        setupTheme()
        super.onCreate(savedInstanceState)
    }

    fun handleError(error: Throwable) {
        sendErrorReport(error)
        if (BuildConfig.DEBUG) {
            error.printStackTrace()
        }
    }

    private fun setupTheme() {
        workaroundWebViewNightModeIssue()
        when (appComponent.userDataSource().getAppearance()) {
            Appearance.DarkTheme -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Appearance.LightTheme -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    /**
     * It turns out there is a strange bug where only the first time a WebView is created, it resets
     * the UI mode. Instantiating a dummy one before calling [AppCompatDelegate.setDefaultNightMode]
     * should be enough so WebViews can be used in the app without any issues.
     */
    private fun workaroundWebViewNightModeIssue() {
        try {
            WebView(this)
        } catch (ignored: Exception) {
        }
    }
}

fun FragmentActivity.sendErrorReport(
    throwable: Throwable,
    postAction: () -> Unit = {}
) {
    showStyledDialog(
        dialogStyle = R.style.AppTheme_AlertDialog,
        dialogBackground = R.drawable.background_contrast_rounded
    ) {
        setMessage(R.string.error_report_rationale)
        setPositiveButton(R.string.error_report) { dialog, _ ->
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))

            val emailBody = "Hi, can you please look into this report?\n\n$sw"
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("fibelatti+dev@gmail.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Pinkt - Error Report")
                putExtra(Intent.EXTRA_TEXT, emailBody)
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.error_send_email)))
            dialog?.dismiss()
            postAction.invoke()
        }
        setNegativeButton(R.string.error_ignore) { dialog, _ ->
            dialog?.dismiss()
            postAction.invoke()
        }
    }
}
