package com.example.gigahack_2025.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.ui.components.*
import com.example.gigahack_2025.ui.theme.*

@Composable
fun SecurityScreen(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit = {},
    onReportClick: () -> Unit = {},
    onTestKnowledgeClick: () -> Unit = {},
    onArticleClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPopupMenu by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaWhite)
    ) {
        // Custom Top Bar with back button and history icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
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
                    text = "Securitate",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Normal,
                    color = FigmaDarkBlue
                )
            }
            
                // History and Report icons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // History icon
                    IconButton(onClick = onHistoryClick) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Scan History",
                            tint = FigmaDarkBlue
                        )
                    }
                    
                    // Report problem icon (three dots)
                    IconButton(onClick = { showPopupMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = FigmaPrimaryBlue
                        )
                    }
                }
        }
        
            // Main content area with scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Unified Search Component
                UnifiedSearchComponent(
                    onFileSelected = { file ->
                        // Handle file selection
                        // In real implementation, this would trigger file scan
                    },
                    onUrlScanned = { url ->
                        // Handle URL scanning
                        // In real implementation, this would trigger URL scan
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Latest Alerts Section - matches Figma exactly
                Text(
                    text = "Alerte",
                    style = MaterialTheme.typography.headlineMedium, // Figma: Roboto 22px weight-500
                    fontWeight = FontWeight.Medium,
                    color = FigmaBlack, // Figma: rgb(18, 18, 18)
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Security Articles Section
                SecurityArticlesComponent(
                    onArticleClick = { article ->
                        onArticleClick(article.id)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        
        // Popup Menu
        SecurityPopupMenu(
            isVisible = showPopupMenu,
            onDismiss = { showPopupMenu = false },
            onReportIncident = onReportClick,
            onTestKnowledge = onTestKnowledgeClick
        )
    }
}