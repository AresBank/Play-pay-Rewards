package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold
import com.example.ui.viewmodel.NavigationTab

@Composable
fun BottomNavigationBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xF008080C))
            .border(0.5.dp, Color(0x26FFFFFF), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Para Ti",
                selected = currentTab == NavigationTab.PARA_TI,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                activeColor = NeonCyan,
                onClick = { onTabSelected(NavigationTab.PARA_TI) },
                testTag = "nav_para_ti"
            )

            NavItem(
                label = "Descubrir",
                selected = currentTab == NavigationTab.DESCUBRIR,
                activeIcon = Icons.Filled.Explore,
                inactiveIcon = Icons.Outlined.Explore,
                activeColor = NeonMagenta,
                onClick = { onTabSelected(NavigationTab.DESCUBRIR) },
                testTag = "nav_descubrir"
            )

            // Center Glowing Upload (+) Button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(NeonCyan, Color(0xFFD946EF), WarmGold)
                        )
                    )
                    .clickable { onTabSelected(NavigationTab.SUBIR) }
                    .testTag("nav_subir_btn")
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PitchBlack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Subir Video",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            NavItem(
                label = "Cartera",
                selected = currentTab == NavigationTab.CARTERA,
                activeIcon = Icons.Filled.AccountBalanceWallet,
                inactiveIcon = Icons.Outlined.AccountBalanceWallet,
                activeColor = WarmGold,
                onClick = { onTabSelected(NavigationTab.CARTERA) },
                testTag = "nav_cartera"
            )

            NavItem(
                label = "Perfil",
                selected = currentTab == NavigationTab.PERFIL,
                activeIcon = Icons.Filled.Person,
                inactiveIcon = Icons.Outlined.Person,
                activeColor = NeonCyan,
                onClick = { onTabSelected(NavigationTab.PERFIL) },
                testTag = "nav_perfil"
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    activeColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = if (selected) activeIcon else inactiveIcon,
            contentDescription = label,
            tint = if (selected) activeColor else Color(0xFF71717A),
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = label,
            color = if (selected) activeColor else Color(0xFF71717A),
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}
