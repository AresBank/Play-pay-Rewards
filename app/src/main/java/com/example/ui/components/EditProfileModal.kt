package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.UserEntity
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold

@Composable
fun EditProfileModal(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSaveProfile: (displayName: String, username: String, bio: String, avatarPreset: String) -> Unit
) {
    var displayName by remember(user) { mutableStateOf(user?.displayName ?: "Oscar Bueno") }
    var username by remember(user) { mutableStateOf(user?.username ?: "CyberViewer_MX") }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "Explorando la frontera cyberpunk & ganando recompensas diarias con videos ⚡🎧") }
    var selectedAvatarPreset by remember(user) { mutableStateOf(user?.avatarPreset ?: "neon_cyan") }

    val presets = remember {
        listOf(
            "neon_cyan" to NeonCyan,
            "neon_magenta" to NeonMagenta,
            "warm_gold" to WarmGold,
            "emerald_green" to Color(0xFF10B981),
            "cyber_purple" to Color(0xFF8B5CF6)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xCC000000))
                .clickable { onDismiss() }
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF13131D))
                    .border(1.dp, Color(0xFF2C2C3E), RoundedCornerShape(24.dp))
                    .clickable(enabled = false) {}
                    .padding(20.dp)
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White.copy(alpha = 0.7f))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Editar Perfil de Creador",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Avatar Preview & Selector
                    val activeColor = presets.find { it.first == selectedAvatarPreset }?.second ?: NeonCyan

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.25f))
                            .border(2.dp, activeColor, CircleShape)
                    ) {
                        Text(
                            text = displayName.take(1).uppercase().ifBlank { "U" },
                            color = activeColor,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Selecciona el color de tu Avatar", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        presets.forEach { (key, color) ->
                            val isSelected = key == selectedAvatarPreset
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        2.dp,
                                        if (isSelected) Color.White else Color.Transparent,
                                        CircleShape
                                    )
                                    .clickable { selectedAvatarPreset = key },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = PitchBlack,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Display Name
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Nombre Visible", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_display_name_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkCard,
                                focusedIndicatorColor = NeonCyan,
                                unfocusedIndicatorColor = Color(0xFF333348),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Username
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Nombre de Usuario (@)", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("edit_username_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkCard,
                                focusedIndicatorColor = NeonCyan,
                                unfocusedIndicatorColor = Color(0xFF333348),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Bio
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Biografía del Creador", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().testTag("edit_bio_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = DarkSurface,
                                unfocusedContainerColor = DarkCard,
                                focusedIndicatorColor = NeonCyan,
                                unfocusedIndicatorColor = Color(0xFF333348),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            onSaveProfile(displayName.trim(), username.trim(), bio.trim(), selectedAvatarPreset)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("save_profile_button")
                    ) {
                        Text(text = "Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
