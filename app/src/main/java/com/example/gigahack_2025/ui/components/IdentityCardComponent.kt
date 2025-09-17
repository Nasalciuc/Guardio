package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun IdentityCardComponent(
    title: String = "Buletin de identitate",
    cardNumber: String = "A38078016",
    expiryDate: String = "Expiră la 20.05.2030",
    onQrCodeClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD) // Light blue background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Background pattern/emblem (simplified)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color(0xFFBBDEFB).copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
            )
            
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = cardNumber,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = expiryDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    
                    // QR Code button
                    IconButton(
                        onClick = onQrCodeClick,
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                Color.Black,
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            Icons.Filled.QrCode,
                            contentDescription = "QR Code",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
