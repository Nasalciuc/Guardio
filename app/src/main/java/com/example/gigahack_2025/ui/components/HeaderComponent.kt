package com.example.gigahack_2025.ui.components

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
import com.example.gigahack_2025.ui.theme.*

@Composable
fun HeaderComponent(
    userName: String = "Stefan",
    idnp: String = "2014801555467",
    onQrCodeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Salut, $userName",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Normal,
                    color = FigmaDarkBlue
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = FigmaGray,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "IDNP $idnp",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FigmaGray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Filled.ContentCopy,
                    contentDescription = "Copy",
                    tint = FigmaGray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // QR Code button
        IconButton(
            onClick = onQrCodeClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.Filled.QrCode,
                contentDescription = "QR Code",
                tint = FigmaDarkBlue,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Notifications button
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                Icons.Filled.NotificationsNone,
                contentDescription = "Notifications",
                tint = FigmaDarkBlue,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
