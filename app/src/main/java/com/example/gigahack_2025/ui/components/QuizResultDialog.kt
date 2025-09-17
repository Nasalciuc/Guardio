package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.gigahack_2025.data.QuizResult
import com.example.gigahack_2025.ui.theme.*
import androidx.compose.ui.res.stringResource
import com.example.gigahack_2025.R

@Composable
fun QuizResultDialog(
    result: QuizResult,
    onDismiss: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = FigmaWhite),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(id = R.string.close),
                            tint = FigmaGray
                        )
                    }
                }
                
                // Score display
                Card(
                    modifier = Modifier.size(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            result.percentage >= 80 -> Color(0xFF4CAF50) // Green
                            result.percentage >= 60 -> Color(0xFFFF9800) // Orange
                            else -> Color(0xFFF44336) // Red
                        }
                    ),
                    shape = RoundedCornerShape(60.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${result.score}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = FigmaWhite
                            )
                            Text(
                                text = "/ ${result.totalQuestions}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = FigmaWhite.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
                
                // Percentage
                Text(
                    text = "${result.percentage}%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        result.percentage >= 80 -> Color(0xFF4CAF50)
                        result.percentage >= 60 -> Color(0xFFFF9800)
                        else -> Color(0xFFF44336)
                    }
                )
                
                // Title
                Text(
                    text = when {
                        result.percentage >= 90 -> stringResource(id = R.string.result_title_expert)
                        result.percentage >= 80 -> stringResource(id = R.string.result_title_great)
                        result.percentage >= 70 -> stringResource(id = R.string.result_title_well_done)
                        result.percentage >= 60 -> stringResource(id = R.string.result_title_good_effort)
                        result.percentage >= 50 -> stringResource(id = R.string.result_title_keep_learning)
                        else -> stringResource(id = R.string.result_title_keep_practicing)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = FigmaBlack,
                    textAlign = TextAlign.Center
                )
                
                // Message
                Text(
                    text = result.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = FigmaDarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Close button
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = FigmaPrimaryBlue
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, FigmaPrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.close),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    // Restart button
                    Button(
                        onClick = onRestart,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FigmaPrimaryBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.try_again),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = FigmaWhite
                        )
                    }
                }
            }
        }
    }
}
