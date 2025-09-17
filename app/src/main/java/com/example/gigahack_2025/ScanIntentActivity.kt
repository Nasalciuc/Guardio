package com.example.gigahack_2025

import android.content.Intent
import android.net.Uri
import android.util.Patterns
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.gigahack_2025.ui.screens.IntentScanScreen
import com.example.gigahack_2025.ui.theme.GIGAHACK_2025Theme

class ScanIntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Extract possible URL/text or file Uri from the intent
        val action = intent?.action
        val type = intent?.type

        var initialUrl: String? = null
        var initialFileUri: Uri? = null

        if (Intent.ACTION_VIEW == action) {
            initialUrl = intent?.dataString
        } else if (Intent.ACTION_SEND == action) {
            if (type?.startsWith("text/") == true) {
                val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                initialUrl = extractFirstUrl(sharedText)
            } else {
                // Could be a file
                val typed = runCatching {
                    intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
                }.getOrNull()
                @Suppress("DEPRECATION")
                val legacy: Uri? = if (typed == null) intent.getParcelableExtra(Intent.EXTRA_STREAM) else null
                initialFileUri = typed ?: legacy
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action) {
            if (type?.startsWith("text/") == true) {
                val texts = intent.getStringArrayListExtra(Intent.EXTRA_TEXT)
                initialUrl = texts?.firstNotNullOfOrNull { extractFirstUrl(it) }
            } else {
                val list = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
                initialFileUri = list?.firstOrNull()
            }
        }

        setContent {
            GIGAHACK_2025Theme {
                IntentScanScreen(
                    initialUrl = initialUrl,
                    initialFileUri = initialFileUri,
                    onFinished = { finish() }
                )
            }
        }
    }

    private fun extractFirstUrl(text: String?): String? {
        if (text.isNullOrBlank()) return null
        val matcher = Patterns.WEB_URL.matcher(text)
        return if (matcher.find()) text.substring(matcher.start(), matcher.end()) else null
    }
}
