package com.example.gigahack_2025.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.repository.SecurityRepository
import androidx.compose.ui.res.stringResource
import com.example.gigahack_2025.R
import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Build
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
// Uri already available via parameter type; avoid duplicate ambiguous imports

@Composable
fun IntentScanScreen(
    initialUrl: String? = null,
    initialFileUri: Uri? = null,
    onFinished: () -> Unit
) {
    val ctx = LocalContext.current
    val repo = remember { SecurityRepository() }
    var status by remember { mutableStateOf("scanning") } // scanning | safe | suspicious | malicious | error
    var detail by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        try {
            when {
                initialFileUri != null -> {
                    val res = repo.scanFile(ctx, initialFileUri)
                    res.fold(
                        onSuccess = {
                            status = it.verdict.lowercase()
                            detail = it.note
                        },
                        onFailure = { e ->
                            status = "error"
                            detail = e.message ?: "Unknown error"
                        }
                    )
                }
                !initialUrl.isNullOrBlank() -> {
                    val res = repo.scanUrl(initialUrl)
                    res.fold(
                        onSuccess = {
                            status = it.verdict.lowercase()
                            detail = it.details
                        },
                        onFailure = { e ->
                            status = "error"
                            detail = e.message ?: "Unknown error"
                        }
                    )
                }
                else -> {
                    status = "error"
                    detail = "No content to scan"
                }
            }
        } catch (e: Exception) {
            status = "error"
            detail = e.message ?: "Unknown error"
        }
    }

    // Removed auto-open to avoid system default browser prompt; user explicitly chooses.

    when (status) {
        "scanning" -> LoadingView()
        "malicious" -> BlockedView(onClose = onFinished)
        "suspicious" -> SuspiciousView(detail = detail, onClose = onFinished, url = initialUrl) {
            if (!initialUrl.isNullOrBlank()) {
                openExternal(initialUrl, context)
                onFinished()
            }
        }
        "safe" -> SafeView(
            detail = detail,
            url = initialUrl,
            onOpen = {
                if (!initialUrl.isNullOrBlank()) {
                    openExternal(initialUrl, ctx)
                    onFinished()
                }
            },
            onClose = onFinished
        )
        else -> ErrorView(detail = detail, onClose = onFinished)
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    Text(text = stringResource(id = R.string.scan_status_scanning), modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun BlockedView(onClose: () -> Unit) {
    ResultCard(
        icon = Icons.Filled.Cancel,
        title = stringResource(id = R.string.blocked_unsafe_title),
        titleColor = Color(0xFFC62828),
    body = stringResource(id = R.string.blocked_body),
        primaryLabel = stringResource(id = R.string.close),
        onPrimary = onClose
    )
}

@Composable
private fun SuspiciousView(detail: String, onClose: () -> Unit, url: String?, onOverride: () -> Unit) {
    ResultCard(
        icon = Icons.Filled.Warning,
        title = stringResource(id = R.string.suspicious_title),
        titleColor = Color(0xFFF9A825),
    body = detail.ifBlank { stringResource(id = R.string.suspicious_body) } + (if (url != null) "\n\n$url" else ""),
    primaryLabel = stringResource(id = R.string.close),
    onPrimary = onClose,
    secondaryLabel = stringResource(id = R.string.override_open_anyway),
    onSecondary = onOverride
    )
}

@Composable
private fun SafeView(
    detail: String,
    url: String?,
    onOpen: () -> Unit,
    onClose: () -> Unit
) {
    ResultCard(
        icon = Icons.Filled.CheckCircle,
        title = stringResource(id = R.string.safe_title),
        titleColor = Color(0xFF2E7D32),
        body = detail.ifBlank { stringResource(id = R.string.safe_body) },
        primaryLabel = stringResource(id = R.string.close),
        onPrimary = onClose,
        secondaryLabel = if (!url.isNullOrBlank()) stringResource(id = R.string.continue_to_site) else null,
        onSecondary = if (!url.isNullOrBlank()) onOpen else null,
        extraContent = {
            if (!url.isNullOrBlank()) {
                Text(text = url, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
        }
    )
}

@Composable
private fun ErrorView(detail: String, onClose: () -> Unit) {
    ResultCard(
        icon = Icons.Filled.Warning,
        title = stringResource(id = R.string.could_not_scan),
        titleColor = Color(0xFF6D4C41),
        body = detail,
        primaryLabel = stringResource(id = R.string.close),
        onPrimary = onClose
    )
}

@Composable
private fun ResultCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    titleColor: Color,
    body: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
    extraContent: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = titleColor)
        Text(text = title, style = MaterialTheme.typography.titleLarge, color = titleColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Text(text = body, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 8.dp))
    extraContent?.invoke()
    Row(modifier = Modifier.padding(top = 16.dp)) {
            if (secondaryLabel != null && onSecondary != null) {
                OutlinedButton(onClick = onSecondary) { Text(secondaryLabel) }
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
            }
            Button(onClick = onPrimary) { Text(primaryLabel) }
        }
    }
}

private fun openExternal(url: String, context: android.content.Context) {
    val normalized = if (!url.startsWith("http://") && !url.startsWith("https://")) "https://$url" else url
    val uri = Uri.parse(normalized)
    val pm = context.packageManager
    val myPackage = context.packageName

    // Prefer Firefox family.
    val firefoxCandidates = listOf(
        "org.mozilla.firefox",
        "org.mozilla.firefox_beta",
        "org.mozilla.fenix",
        "org.mozilla.firefox_klar"
    )
    for (pkg in firefoxCandidates) {
        val ok = runCatching {
            pm.getPackageInfo(pkg, 0)
            val i = Intent(Intent.ACTION_VIEW, uri).apply {
                setPackage(pkg)
                addCategory(Intent.CATEGORY_BROWSABLE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
            val res = pm.queryIntentActivities(i, 0)
            if (res.isNotEmpty()) {
                context.startActivity(i)
                true
            } else false
        }.getOrDefault(false)
        if (ok) return
    }

    // Fallback: find ANY other browser (exclude us) and launch explicitly to avoid recursion / chooser bug.
    val probe = Intent(Intent.ACTION_VIEW, uri).apply { addCategory(Intent.CATEGORY_BROWSABLE) }
    val all = pm.queryIntentActivities(probe, 0)
    val external = all.firstOrNull { it.activityInfo?.packageName != myPackage }
    if (external != null) {
        val explicit = Intent(Intent.ACTION_VIEW, uri).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
            setClassName(external.activityInfo.packageName, external.activityInfo.name)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(explicit); return }.onFailure { }
    }

    // Last resort: CustomTab (may still succeed if a provider exists) else toast.
    if (runCatching { CustomTabsIntent.Builder().setShowTitle(true).build().launchUrl(context, uri) }.isSuccess) return

    Toast.makeText(context, "Niciun browser extern găsit. Instalează Firefox.", Toast.LENGTH_LONG).show()
}
