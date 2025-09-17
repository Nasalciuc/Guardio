package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gigahack_2025.ui.theme.*

@Composable
fun SecurityPopupMenu(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onReportIncident: () -> Unit,
    onTestKnowledge: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .wrapContentSize()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(12.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = FigmaWhite),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Report an incident option
                    PopupMenuItem(
                        icon = Icons.Filled.Report,
                        text = "Raportează incident",
                        onClick = {
                            onReportIncident()
                            onDismiss()
                        }
                    )
                    
                    // Test your knowledge option
                    PopupMenuItem(
                        icon = Icons.Filled.Quiz,
                        text = "Testează-ți cunoștințele",
                        onClick = {
                            onTestKnowledge()
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PopupMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = FigmaDarkBlue,
            modifier = Modifier.size(20.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Normal,
            color = FigmaBlack
        )
    }
}
