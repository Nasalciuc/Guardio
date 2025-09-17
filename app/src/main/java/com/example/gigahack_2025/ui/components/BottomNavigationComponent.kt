package com.example.gigahack_2025.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false,
    val onClick: () -> Unit = {}
)

@Composable
fun BottomNavigationComponent(
    items: List<BottomNavItem>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = item.selected,
                onClick = item.onClick,
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (item.selected) Color(0xFF1976D2) else Color.Gray
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (item.selected) Color(0xFF1976D2) else Color.Gray
                    )
                }
            )
        }
    }
}

@Composable
fun getDefaultBottomNavItems(
    selectedIndex: Int = 0,
    onItemClick: (Int) -> Unit = {}
): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            label = "Acasă",
            icon = Icons.Filled.Home,
            selected = selectedIndex == 0,
            onClick = { onItemClick(0) }
        ),
        BottomNavItem(
            label = "Documente",
            icon = Icons.Filled.Description,
            selected = selectedIndex == 1,
            onClick = { onItemClick(1) }
        ),
        BottomNavItem(
            label = "Plăți",
            icon = Icons.Filled.Payment,
            selected = selectedIndex == 2,
            onClick = { onItemClick(2) }
        ),
        BottomNavItem(
            label = "Cont",
            icon = Icons.Filled.Person,
            selected = selectedIndex == 3,
            onClick = { onItemClick(3) }
        )
    )
}
