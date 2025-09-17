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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.gigahack_2025.ui.theme.*

data class ServiceItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)

@Composable
fun ServiceGridComponent(
    services: List<ServiceItem> = getDefaultServices(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // First row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            services.take(4).forEach { service ->
                ServiceTile(
                    service = service,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        // Second row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            services.drop(4).take(4).forEach { service ->
                ServiceTile(
                    service = service,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ServiceTile(
    service: ServiceItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = FigmaLightBlue // Light blue background matching Figma
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            onClick = service.onClick
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = service.icon,
                    contentDescription = service.label,
                    tint = FigmaPrimaryBlue, // Blue color matching Figma
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = service.label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = FigmaDarkBlue,
            maxLines = 2
        )
    }
}

private fun getDefaultServices(): List<ServiceItem> {
    return listOf(
        ServiceItem("Datele Mele", Icons.Filled.Description),
        ServiceItem("Fișierele Mele", Icons.Filled.Folder),
        ServiceItem("Plăți", Icons.Filled.Payment),
        ServiceItem("Servicii\nPublice", Icons.Filled.AccountBalance),
        ServiceItem("Sănătate", Icons.Filled.LocalHospital),
        ServiceItem("Permise", Icons.Filled.Badge),
        ServiceItem("Programări", Icons.Filled.Schedule),
        ServiceItem("Securitate", Icons.Filled.Security)
    )
}
