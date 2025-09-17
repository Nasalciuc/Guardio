package com.example.gigahack_2025.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.ui.components.*
import com.example.gigahack_2025.ui.theme.*
import com.example.gigahack_2025.data.ScanHistoryManager
import com.example.gigahack_2025.data.ScanHistoryEntry

@Composable
fun ScanHistoryScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaWhite)
    ) {
        // Custom Top Bar with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Înapoi",
                    tint = FigmaDarkBlue
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Istoria Verificărilor",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Normal,
                color = FigmaDarkBlue
            )
        }
        
        // Main content area with scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            val context = androidx.compose.ui.platform.LocalContext.current
            var entries by remember { mutableStateOf(ScanHistoryManager.getEntries(context)) }
            LaunchedEffect(Unit) {
                // Trigger read at start; entries are persisted across launches
                entries = ScanHistoryManager.getEntries(context)
            }
            val results = entries.map { e ->
                SearchResult(
                    id = e.id,
                    title = e.target,
                    description = if (e.type == "file") "Fișier scanat" else "URL scanat",
                    status = when (e.verdict) {
                        "safe" -> ScanStatus.SAFE
                        "suspicious" -> ScanStatus.SCANNING // Use orange/yellow for Suspicious
                        else -> ScanStatus.UNSAFE
                    },
                    type = if (e.type == "file") ResultType.FILE else ResultType.URL,
                    timestamp = android.text.format.DateUtils.getRelativeTimeSpanString(e.timestamp).toString()
                )
            }
            SearchResultsComponent(results = results)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
