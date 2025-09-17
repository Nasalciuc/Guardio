package com.example.gigahack_2025.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.gigahack_2025.ui.components.*
import com.example.gigahack_2025.ui.theme.*

@Composable
fun HomeScreen(
    onQrCodeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onServiceClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FigmaWhite)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with greeting and actions
        HeaderComponent(
            onQrCodeClick = onQrCodeClick,
            onNotificationsClick = onNotificationsClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Identity Cards Carousel
        IdentityCardCarousel(
            onQrCodeClick = { card -> 
                // Handle QR code click for specific card
                onQrCodeClick()
            }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Services Grid
        ServiceGridComponent(
            services = getDefaultServicesWithActions(onServiceClick)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun getDefaultServicesWithActions(
    onServiceClick: (String) -> Unit
): List<ServiceItem> {
    return listOf(
        ServiceItem("Datele Mele", Icons.Filled.Description) { onServiceClick("Datele Mele") },
        ServiceItem("Fișierele Mele", Icons.Filled.Folder) { onServiceClick("Fișierele Mele") },
        ServiceItem("Plăți", Icons.Filled.Payment) { onServiceClick("Plăți") },
        ServiceItem("Servicii\nPublice", Icons.Filled.AccountBalance) { onServiceClick("Servicii Publice") },
        ServiceItem("Sănătate", Icons.Filled.LocalHospital) { onServiceClick("Sănătate") },
        ServiceItem("Permise", Icons.Filled.Badge) { onServiceClick("Permise") },
        ServiceItem("Programări", Icons.Filled.Schedule) { onServiceClick("Programări") },
        ServiceItem("Securitate", Icons.Filled.Security) { onServiceClick("Securitate") }
    )
}
