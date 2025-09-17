package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.background
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
import com.example.gigahack_2025.R
import androidx.compose.ui.res.stringResource
import com.example.gigahack_2025.ui.theme.*

data class SearchResult(
    val id: String,
    val title: String,
    val description: String,
    val status: ScanStatus,
    val type: ResultType,
    val timestamp: String
)

enum class ScanStatus {
    SAFE, UNSAFE, SCANNING, ERROR
}

enum class ResultType {
    FILE, URL
}

@Composable
fun SearchResultsComponent(
    results: List<SearchResult> = getMockResults(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Istoria Verificărilor",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = FigmaDarkBlue
            )
            
            TextButton(
                onClick = { /* Clear all results */ }
            ) {
                Text(
                    text = "Închide",
                    style = MaterialTheme.typography.bodySmall,
                    color = FigmaGray
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Results list
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            results.forEach { result ->
                SearchResultCard(
                    result = result,
                    onClick = { /* Handle result click */ }
                )
            }
        }
    }
}

@Composable
fun SearchResultCard(
    result: SearchResult,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = FigmaWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(getStatusColor(result.status))
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Type icon
            Icon(
                imageVector = if (result.type == ResultType.FILE) Icons.Filled.AttachFile else Icons.Filled.Link,
                contentDescription = result.type.name,
                tint = FigmaGray,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FigmaDarkBlue,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = result.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = FigmaGray,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = result.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = FigmaLightGray,
                    maxLines = 1
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Status badge
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = getStatusBackgroundColor(result.status)
                )
            ) {
                val label = when (result.status) {
                    ScanStatus.SAFE -> stringResource(id = R.string.scan_status_safe)
                    ScanStatus.UNSAFE -> stringResource(id = R.string.scan_status_unsafe)
                    ScanStatus.SCANNING -> stringResource(id = R.string.scan_status_suspicious)
                    ScanStatus.ERROR -> stringResource(id = R.string.scan_status_error)
                }
                Text(text = label, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, color = getStatusColor(result.status), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

private fun getStatusColor(status: ScanStatus): Color {
    return when (status) {
        ScanStatus.SAFE -> Color(0xFF4CAF50) // Green
        ScanStatus.UNSAFE -> Color(0xFFF44336) // Red
        ScanStatus.SCANNING -> Color(0xFFFFC107) // Orange
        ScanStatus.ERROR -> Color(0xFF9E9E9E) // Gray
    }
}

private fun getStatusBackgroundColor(status: ScanStatus): Color {
    return when (status) {
        ScanStatus.SAFE -> Color(0xFFE8F5E8) // Light Green
        ScanStatus.UNSAFE -> Color(0xFFFFEBEE) // Light Red
        ScanStatus.SCANNING -> Color(0xFFFFF8E1) // Light Orange
        ScanStatus.ERROR -> Color(0xFFF5F5F5) // Light Gray
    }
}

private fun getMockResults(): List<SearchResult> {
    return listOf(
        SearchResult(
            id = "1",
            title = "document.pdf",
            description = "Financial report document",
            status = ScanStatus.SAFE,
            type = ResultType.FILE,
            timestamp = "2 minutes ago"
        ),
        SearchResult(
            id = "2",
            title = "https://example.com",
            description = "Official website",
            status = ScanStatus.SAFE,
            type = ResultType.URL,
            timestamp = "5 minutes ago"
        ),
        SearchResult(
            id = "3",
            title = "suspicious.exe",
            description = "Executable file",
            status = ScanStatus.UNSAFE,
            type = ResultType.FILE,
            timestamp = "10 minutes ago"
        ),
        SearchResult(
            id = "4",
            title = "https://malicious-site.com",
            description = "Potentially harmful website",
            status = ScanStatus.UNSAFE,
            type = ResultType.URL,
            timestamp = "15 minutes ago"
        ),
        SearchResult(
            id = "5",
            title = "presentation.pptx",
            description = "Business presentation",
            status = ScanStatus.SAFE,
            type = ResultType.FILE,
            timestamp = "20 minutes ago"
        ),
        SearchResult(
            id = "6",
            title = "https://phishing-attempt.com",
            description = "Suspicious website",
            status = ScanStatus.UNSAFE,
            type = ResultType.URL,
            timestamp = "1 hour ago"
        )
    )
}
