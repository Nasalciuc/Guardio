package com.example.gigahack_2025.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class UserProfile(
    val name: String = "Ramona",
    val idnp: String = "2014801555467"
)

data class IdentityCard(
    val title: String = "Buletin de identitate",
    val cardNumber: String = "A38078016",
    val expiryDate: String = "Expiră la 20.05.2030"
)

data class ServiceItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String = ""
)

object AppData {
    val userProfile = UserProfile()
    val identityCard = IdentityCard()
    
    val services = listOf(
        ServiceItem("my_data", "Datele Mele", Icons.Filled.Description, "View and manage your personal data"),
        ServiceItem("my_files", "Fișierele Mele", Icons.Filled.Folder, "Access your digital files"),
        ServiceItem("payments", "Plăți", Icons.Filled.Payment, "Make payments and view transaction history"),
        ServiceItem("public_services", "Servicii\nPublice", Icons.Filled.AccountBalance, "Access public services"),
        ServiceItem("health", "Sănătate", Icons.Filled.LocalHospital, "Health services and medical records"),
        ServiceItem("permits", "Permise", Icons.Filled.Badge, "View and manage permits and licenses"),
        ServiceItem("appointments", "Programări", Icons.Filled.Schedule, "Schedule and manage appointments"),
        ServiceItem("authorizations", "Împuterniciri", Icons.Filled.Assignment, "Manage authorizations and powers of attorney")
    )
}
