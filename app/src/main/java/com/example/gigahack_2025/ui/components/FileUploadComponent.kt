package com.example.gigahack_2025.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.data.SecurityFile
import com.example.gigahack_2025.data.SecurityMockData
import com.example.gigahack_2025.data.FileStatus

@Composable
fun FileUploadComponent(
    onFileSelected: (SecurityFile) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    
    Column(modifier = modifier) {
        // Upload Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    isExpanded = !isExpanded
                    if (!isExpanded) {
                        // Simulate file upload
                        isUploading = true
                        // Mock upload process
                        // In real implementation, this would trigger file picker
                    }
                },
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
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = "Upload File",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Upload File",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Text(
                        text = "Click to select a file for security scanning",
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
                // File Type Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FileTypeButton(
                        text = "PDF",
                        icon = Icons.Filled.PictureAsPdf,
                        onClick = { /* Handle PDF upload */ },
                        modifier = Modifier.weight(1f)
                    )
                    FileTypeButton(
                        text = "DOC",
                        icon = Icons.Filled.Description,
                        onClick = { /* Handle DOC upload */ },
                        modifier = Modifier.weight(1f)
                    )
                    FileTypeButton(
                        text = "XLS",
                        icon = Icons.Filled.TableChart,
                        onClick = { /* Handle XLS upload */ },
                        modifier = Modifier.weight(1f)
                    )
                    FileTypeButton(
                        text = "Other",
                        icon = Icons.Filled.InsertDriveFile,
                        onClick = { /* Handle other file upload */ },
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recent Files
                Text(
                    text = "Recent Files",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                SecurityMockData.mockFiles.take(3).forEach { file ->
                    FileItem(
                        file = file,
                        onClick = { onFileSelected(file) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
        
        // Upload Progress
        if (isUploading) {
            UploadProgressComponent(
                onComplete = { 
                    isUploading = false
                    // Mock successful upload
                    onFileSelected(SecurityMockData.mockFiles.first())
                }
            )
        }
    }
}

@Composable
fun FileTypeButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black
            )
        }
    }
}

@Composable
fun FileItem(
    file: SecurityFile,
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
                imageVector = SecurityMockData.getFileTypeIcon(file.type),
                contentDescription = file.name,
                tint = Color(0xFF1976D2),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = file.size,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        getFileStatusColor(file.status)
                    )
            )
        }
    }
}

@Composable
fun UploadProgressComponent(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableStateOf(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "upload")
    
    LaunchedEffect(Unit) {
        // Simulate upload progress
        repeat(100) {
            progress = (it + 1) / 100f
            kotlinx.coroutines.delay(50)
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
                    imageVector = Icons.Filled.CloudUpload,
                    contentDescription = "Uploading",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scanning file for security threats...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF1976D2),
                trackColor = Color(0xFFBBDEFB)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF1976D2),
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

private fun getFileStatusColor(status: FileStatus): Color {
    return when (status) {
        FileStatus.SAFE -> Color(0xFF4CAF50) // Green
        FileStatus.UNSAFE -> Color(0xFFF44336) // Red
        FileStatus.SCANNING -> Color(0xFFFFC107) // Orange
        FileStatus.ERROR -> Color(0xFF9E9E9E) // Gray
    }
}
