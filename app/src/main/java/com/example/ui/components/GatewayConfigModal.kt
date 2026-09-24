package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.payment.PaymentGatewayConfig
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold

@Composable
fun GatewayConfigModal(
    currentConfig: PaymentGatewayConfig,
    onSaveConfig: (PaymentGatewayConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var isSandbox by remember { mutableStateOf(currentConfig.isSandboxMode) }
    var mpToken by remember { mutableStateOf(currentConfig.mpAccessToken) }
    var ppClientId by remember { mutableStateOf(currentConfig.paypalClientId) }
    var ppSecret by remember { mutableStateOf(currentConfig.paypalSecret) }
    var feedback by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(1.dp, Color(0xFF333348), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Configuración de Pasarelas API",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sandbox vs Live Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = if (isSandbox) "Modo Pruebas / Sandbox" else "Modo Producción En Vivo",
                            color = if (isSandbox) WarmGold else Color(0xFF10B981),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isSandbox) "Emula SPEI y PayPal con comprobantes oficiales" else "Envía dinero real mediante APIs bancarias",
                            color = Color(0xFF94A3B8),
                            fontSize = 10.sp
                        )
                    }
                    Switch(
                        checked = !isSandbox,
                        onCheckedChange = { isSandbox = !it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PitchBlack,
                            checkedTrackColor = Color(0xFF10B981),
                            uncheckedThumbColor = WarmGold,
                            uncheckedTrackColor = DarkCard
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mercado Pago Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkCard)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Mercado Pago Access Token",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Token de API (TEST-... o APP_USR-...). Si se deja vacío, usa el simulador oficial con Folio Banxico SPEI.",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mpToken,
                        onValueChange = { mpToken = it },
                        placeholder = { Text("TEST-12345678-...", color = Color(0xFF64748B), fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedIndicatorColor = NeonCyan,
                            unfocusedIndicatorColor = Color(0xFF333348),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // PayPal Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkCard)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "PayPal Payouts REST Credentials",
                        color = NeonMagenta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("PayPal Client ID", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ppClientId,
                        onValueChange = { ppClientId = it },
                        placeholder = { Text("AXXXXXX...", color = Color(0xFF64748B), fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedIndicatorColor = NeonMagenta,
                            unfocusedIndicatorColor = Color(0xFF333348),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PayPal Client Secret", color = Color(0xFF94A3B8), fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ppSecret,
                        onValueChange = { ppSecret = it },
                        placeholder = { Text("EXXXXXX...", color = Color(0xFF64748B), fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkSurface,
                            unfocusedContainerColor = DarkSurface,
                            focusedIndicatorColor = NeonMagenta,
                            unfocusedIndicatorColor = Color(0xFF333348),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }

                if (feedback != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = feedback ?: "", color = NeonCyan, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Save button
                Button(
                    onClick = {
                        val newConfig = PaymentGatewayConfig(
                            isSandboxMode = isSandbox,
                            mpAccessToken = mpToken.trim(),
                            paypalClientId = ppClientId.trim(),
                            paypalSecret = ppSecret.trim()
                        )
                        onSaveConfig(newConfig)
                        feedback = "Configuración guardada exitosamente."
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = PitchBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text("Guardar Configuración", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
