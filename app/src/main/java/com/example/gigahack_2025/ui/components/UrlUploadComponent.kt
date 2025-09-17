package com.example.gigahack_2025.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.data.SecurityUrl
import com.example.gigahack_2025.data.SecurityMockData
import com.example.gigahack_2025.data.UrlStatus

@Composable
fun UrlUploadComponent(
    onUrlSubmitted: (SecurityUrl) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var urlText by remember { mutableStateOf("") }
    var isScanning by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    fun triggerScan() {
        if (urlText.isBlank()) {
            showError = true
        } else {
            isScanning = true
        }
    }
    
    Column(modifier = modifier) {
        // URL Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Link,
                    contentDescription = "Scan URL",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Scan URL",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "Enter a URL to check for security threats",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF757575)
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = "Expand",
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Expanded Content
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // URL Input Field
                OutlinedTextField(
                    value = urlText,
                    onValueChange = {
                        urlText = it
                        showError = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onPreviewKeyEvent { kev ->
                            if (kev.nativeKeyEvent.keyCode == android.view.KeyEvent.KEYCODE_ENTER) { triggerScan(); true } else false
                        },
                    label = { Text("Enter URL") },
                    placeholder = { Text("https://example.com") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Link,
                            contentDescription = "URL",
                            tint = Color(0xFF757575)
                        )
                    },
                    trailingIcon = {
                        if (urlText.isNotEmpty()) {
                            IconButton(onClick = { urlText = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF757575)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    isError = showError,
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF1976D2),
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        errorBorderColor = Color(0xFFF44336)
                    )
                )
                
                if (showError) {
                    Text(
                        text = "Please enter a valid URL",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFF44336),
                        modifier = Modifier.padding(top = 4.dp, start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Scan Button
                Button(
                    onClick = { triggerScan() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isScanning,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1976D2)
                    )
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scanning...")
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Scan",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Scan URL")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recent URLs
                Text(
                    text = "Recent Scans",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                SecurityMockData.mockUrls.forEach { url ->
                    UrlItem(
                        url = url,
                        onClick = { onUrlSubmitted(url) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Scanning Progress
        if (isScanning) {
            UrlScanningComponent(
                url = urlText,
                onComplete = { 
                    isScanning = false
                    // Mock successful scan
                    val mockUrl = SecurityMockData.mockUrls.first().copy(
                        url = urlText,
                        domain = urlText.substringAfter("://").substringBefore("/")
                    )
                    onUrlSubmitted(mockUrl)
                }
            )
        }
    }
}

@Composable
fun UrlItem(
    url: SecurityUrl,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Link,
                contentDescription = "URL",
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = url.domain,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = url.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575),
                    maxLines = 1
                )
            }
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        getUrlStatusColor(url.status)
                    )
            )
        }
    }
}

@Composable
fun UrlScanningComponent(
    url: String,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }
    var currentStep by remember { mutableStateOf("Initializing scan...") }
    
    val steps = listOf(
        "Initializing scan...",
        "Checking domain reputation...",
        "Analyzing content...",
        "Scanning for malware...",
        "Generating report..."
    )
    
    LaunchedEffect(Unit) {
        steps.forEachIndexed { index, step ->
            currentStep = step
            repeat(20) {
                progress = (index * 20 + it + 1) / 100f
                kotlinx.coroutines.delay(100)
            }
        }
        onComplete()
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Security,
                    contentDescription = "Scanning",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scanning URL for threats...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = url,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF757575),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1976D2),
                trackColor = Color(0xFFBBDEFB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentStep,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1976D2)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF1976D2)
                )
            }
        }
    }
}

private fun getUrlStatusColor(status: UrlStatus): Color {
    return when (status) {
        UrlStatus.SAFE -> Color(0xFF4CAF50) // Green
        UrlStatus.UNSAFE -> Color(0xFFF44336) // Red
        UrlStatus.SCANNING -> Color(0xFFFFC107) // Orange
        UrlStatus.ERROR -> Color(0xFF9E9E9E) // Gray
    }
}
