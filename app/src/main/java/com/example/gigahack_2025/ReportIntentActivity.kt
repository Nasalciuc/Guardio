package com.example.gigahack_2025

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * Entry point when user shares to "Report with MScan". We reuse MainActivity and pass extras
 * that the ReportProblemScreen can later read (todo: wire extras consumption).
 */
class ReportIntentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val action = intent?.action
        val type = intent?.type
        var sharedText: String? = null
        var firstFile: Uri? = null
        if (Intent.ACTION_SEND == action) {
            if (type?.startsWith("text/") == true) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            } else {
                @Suppress("DEPRECATION")
                firstFile = intent.getParcelableExtra(Intent.EXTRA_STREAM)
            }
        } else if (Intent.ACTION_SEND_MULTIPLE == action) {
            val list = intent.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
            firstFile = list?.firstOrNull()
        }
        val forward = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("report_prefill_description", sharedText)
            putExtra("report_prefill_file_uri", firstFile)
            putExtra("navigate_to_report", true)
        }
        startActivity(forward)
        finish()
    }
}
