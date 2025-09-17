package com.example.gigahack_2025.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gigahack_2025.ui.theme.*
import com.example.gigahack_2025.R
import com.example.gigahack_2025.repository.SecurityRepository
import com.example.gigahack_2025.data.ScanHistoryEntry
import com.example.gigahack_2025.data.ScanHistoryManager
import java.util.*
import java.util.regex.Pattern

// Data class for scan results
data class ScanResult(
    val verdict: String, // safe | suspicious | malicious
    val message: String,
    val details: String
)

@Composable
fun UnifiedSearchComponent(
    onFileSelected: (String) -> Unit = {},
    onUrlScanned: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var hasAttachment by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    var attachedFileUri by remember { mutableStateOf<Uri?>(null) }
    var urlError by remember { mutableStateOf<String?>(null) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var showResult by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val repository = remember { SecurityRepository() }
    
    // File picker launcher
    val contextForPicker = LocalContext.current
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = getFileNameFromUri(contextForPicker, it)
            attachedFileName = fileName
            attachedFileUri = it
            hasAttachment = true
            onFileSelected(fileName)
        }
    }
    
    // URL validation
    fun isValidUrl(url: String): Boolean {
        val urlPattern = Pattern.compile(
            "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$",
            Pattern.CASE_INSENSITIVE
        )
        return urlPattern.matcher(url).matches()
    }
    
    // Upload animation
    val infiniteTransition = rememberInfiniteTransition(label = "upload")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Main search box
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = FigmaWhite
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment button (for files)
                IconButton(
                    onClick = {
                        filePickerLauncher.launch("*/*")
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AttachFile,
                        contentDescription = "Attach File",
                        tint = if (hasAttachment) FigmaPrimaryBlue else FigmaGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Search input field
                BasicTextField(
                    value = searchText,
                    onValueChange = { incoming ->
                        // Intercept newline: treat as submit, strip newlines from text field.
                        val txt = incoming.text
                        val hasNewline = txt.contains('\n') || txt.contains('\r')
                        val cleaned = txt.replace("\r", "").replace("\n", "")
                        searchText = incoming.copy(text = cleaned, selection = TextRange(cleaned.length))
                        urlError = null
                        if (hasNewline) {
                            triggerScan(
                                cleaned,
                                hasAttachment,
                                attachedFileUri,
                                attachedFileName,
                                repository,
                                context,
                                onResult = { verdict, label, details ->
                                    scanResult = ScanResult(verdict, label, details)
                                    showResult = true
                                },
                                onStart = { if (cleaned.isNotEmpty() || hasAttachment) { isSearching = true; showResult = false } },
                                onDone = { isSearching = false },
                                onInvalidUrl = { urlError = it }
                            )
                        }
                    },
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = FigmaDarkBlue,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(FigmaPrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .onPreviewKeyEvent { keyEvent ->
                            if (keyEvent.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) {
                                // Simulate pressing the search button
                                triggerScan(
                                    searchText.text,
                                    hasAttachment,
                                    attachedFileUri,
                                    attachedFileName,
                                    repository,
                                    context,
                                    onResult = { verdict, label, details ->
                                        scanResult = ScanResult(verdict, label, details)
                                        showResult = true
                                    },
                                    onStart = { isSearching = true; urlError = null; showResult = false },
                                    onDone = { isSearching = false }
                                )
                                true
                            } else false
                        },
                    decorationBox = { innerTextField ->
                        if (searchText.text.isEmpty()) {
                            Text(
                                text = if (hasAttachment) "Introdu linkul pentru scanare" else "Introdu linkul sau fișier",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = FigmaGray,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        innerTextField()
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Search button
                IconButton(
                    onClick = {
                        triggerScan(
                            searchText.text,
                            hasAttachment,
                            attachedFileUri,
                            attachedFileName,
                            repository,
                            context,
                            onResult = { verdict, label, details ->
                                scanResult = ScanResult(verdict, label, details)
                                showResult = true
                            },
                            onStart = { if (searchText.text.isNotEmpty() || hasAttachment) { isSearching = true; urlError = null; showResult = false } },
                            onDone = { isSearching = false },
                            onInvalidUrl = { urlError = it }
                        )
                    },
                    modifier = Modifier.size(24.dp)
                ) {
                    if (isSearching) {
                        // Loading animation - simple pulsing circle
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = FigmaPrimaryBlue
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = FigmaPrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
        
        // URL Error Message
        AnimatedVisibility(
            visible = urlError != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            urlError?.let { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = FigmaRed,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = FigmaRed,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Status indicator
        AnimatedVisibility(
            visible = hasAttachment || searchText.text.isNotEmpty(),
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (hasAttachment) {
                        Icon(
                            imageVector = Icons.Filled.AttachFile,
                            contentDescription = "File attached",
                            tint = FigmaPrimaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "File: ${attachedFileName ?: "Unknown"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = FigmaPrimaryBlue,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
                    
                    if (searchText.text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "URL",
                            tint = FigmaGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "URL: ${searchText.text}",
                            style = MaterialTheme.typography.bodySmall,
                            color = FigmaGray,
                            maxLines = 1
                        )
                    }
                }
                
                // Clear button
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = {
                        searchText = TextFieldValue("")
                        hasAttachment = false
                        attachedFileName = null
                        isSearching = false
                        urlError = null
                        showResult = false
                        scanResult = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Clear",
                        style = MaterialTheme.typography.bodySmall,
                        color = FigmaGray
                    )
                }
            }
        }
        
        // Scan Result Dropdown
        AnimatedVisibility(
            visible = showResult && scanResult != null,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            scanResult?.let { result ->
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when (result.verdict) {
                            "safe" -> Color(0xFFE8F5E8) // Light green
                            "suspicious" -> Color(0xFFFFF8E1) // Light yellow
                            else -> Color(0xFFFFEBEE) // Light red
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Result icon
                        val icon = when (result.verdict) {
                            "safe" -> Icons.Filled.CheckCircle
                            "suspicious" -> Icons.Filled.Warning
                            else -> Icons.Filled.Cancel
                        }
                        val iconTint = when (result.verdict) {
                            "safe" -> Color(0xFF4CAF50)
                            "suspicious" -> Color(0xFFFFC107)
                            else -> Color(0xFFF44336)
                        }
                        Icon(imageVector = icon, contentDescription = result.message, tint = iconTint, modifier = Modifier.size(24.dp))
                        
                        // Result content
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            val titleColor = when (result.verdict) {
                                "safe" -> Color(0xFF2E7D32)
                                "suspicious" -> Color(0xFFF9A825) // Dark yellow
                                else -> Color(0xFFC62828)
                            }
                            Text(text = result.message, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = titleColor)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.details,
                                style = MaterialTheme.typography.bodySmall,
                                color = FigmaDarkGray
                            )
                        }
                        
                        // Close button
                        IconButton(
                            onClick = { 
                                showResult = false
                                scanResult = null
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = FigmaGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get file name from URI using ContentResolver
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String {
    return try {
        context.contentResolver.query(uri, arrayOf(android.provider.OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                val idx = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (idx >= 0) {
                    val raw = c.getString(idx) ?: return@use
                    return raw.substringAfterLast(":").removePrefix("msf").removePrefix(":")
                }
            }
        }
        uri.lastPathSegment?.substringAfterLast(":")?.removePrefix("msf")?.removePrefix(":") ?: "unknown_file"
    } catch (e: Exception) {
        uri.lastPathSegment ?: "unknown_file"
    }
}

// Shared scan trigger encapsulating previous logic
private fun triggerScan(
    text: String,
    hasAttachment: Boolean,
    attachedFileUri: Uri?,
    attachedFileName: String?,
    repository: com.example.gigahack_2025.repository.SecurityRepository,
    context: android.content.Context,
    onResult: (verdict: String, label: String, details: String) -> Unit,
    onStart: () -> Unit,
    onDone: () -> Unit,
    onInvalidUrl: (String) -> Unit = {}
) {
    if (text.isEmpty() && !hasAttachment) return
    if (text.isNotEmpty()) {
        val pattern = Pattern.compile(
            "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$",
            Pattern.CASE_INSENSITIVE
        )
        if (!pattern.matcher(text).matches()) {
            onInvalidUrl("Please enter a valid URL")
            return
        }
    }
    onStart()
    GlobalScope.launch {
        try {
            if (hasAttachment && attachedFileUri != null) {
                val fileResult = repository.scanFile(context, attachedFileUri)
                withContext(Dispatchers.Main) {
                    fileResult.fold(onSuccess = { response ->
                        val verdict = response.verdict.lowercase()
                        val label = when (verdict) {
                            "safe" -> context.getString(R.string.scan_status_safe)
                            "suspicious" -> context.getString(R.string.scan_status_suspicious)
                            "malicious" -> context.getString(R.string.scan_status_malicious)
                            else -> context.getString(R.string.scan_status_safe)
                        }
                        onResult(verdict, label, response.note)
                        com.example.gigahack_2025.data.ScanHistoryManager.addEntry(
                            context,
                            com.example.gigahack_2025.data.ScanHistoryEntry(
                                target = attachedFileName ?: context.getString(R.string.upload_file),
                                type = "file",
                                verdict = verdict
                            )
                        )
                    }, onFailure = { error ->
                        val verdict = if (Random().nextBoolean()) "safe" else "malicious"
                        val label = if (verdict == "safe") context.getString(R.string.scan_status_safe) else context.getString(R.string.scan_status_malicious)
                        onResult(verdict, label, "Backend unavailable. Showing mock result: ${error.message}")
                    })
                    onDone()
                }
            } else if (text.isNotEmpty()) {
                val urlResult = repository.scanUrl(text)
                withContext(Dispatchers.Main) {
                    urlResult.fold(onSuccess = { response ->
                        val verdict = response.verdict.lowercase()
                        val label = when (verdict) {
                            "safe" -> context.getString(R.string.scan_status_safe)
                            "suspicious" -> context.getString(R.string.scan_status_suspicious)
                            "malicious" -> context.getString(R.string.scan_status_malicious)
                            else -> context.getString(R.string.scan_status_safe)
                        }
                        onResult(verdict, label, response.details)
                        com.example.gigahack_2025.data.ScanHistoryManager.addEntry(
                            context,
                            com.example.gigahack_2025.data.ScanHistoryEntry(
                                target = text,
                                type = "url",
                                verdict = verdict
                            )
                        )
                    }, onFailure = { error ->
                        val verdict = if (Random().nextBoolean()) "safe" else "malicious"
                        val label = if (verdict == "safe") context.getString(R.string.scan_status_safe) else context.getString(R.string.scan_status_malicious)
                        onResult(verdict, label, "Backend unavailable. Showing mock result: ${error.message}")
                    })
                    onDone()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                val verdict = if (Random().nextBoolean()) "safe" else "malicious"
                val label = if (verdict == "safe") context.getString(R.string.scan_status_safe) else context.getString(R.string.scan_status_malicious)
                onResult(verdict, label, "Error: ${e.message}. Showing mock result.")
                onDone()
            }
        }
    }
}