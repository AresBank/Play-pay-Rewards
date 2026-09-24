package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PaymentMethodEntity
import com.example.data.local.PayoutRequestEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    user: UserEntity?,
    paymentMethods: List<PaymentMethodEntity>,
    payoutRequests: List<PayoutRequestEntity>,
    onConvertCoins: (Long) -> Unit,
    onRequestWithdrawal: (amount: Double, method: String, destination: String, holderName: String) -> Unit,
    onSavePaymentMethod: (type: String, fullName: String, clabe: String, email: String, phone: String) -> Unit,
    onOpenGatewayConfig: () -> Unit = {},
    onViewReceipt: (PayoutRequestEntity) -> Unit = {},
    onOpenReferralModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0: Mercado Pago, 1: PayPal
    var selectedAmountMxn by remember { mutableStateOf(500.0) }

    // Mercado Pago form state
    val mpSaved = paymentMethods.firstOrNull { it.methodType == "MERCADO_PAGO" }
    var mpFullName by remember(mpSaved) { mutableStateOf(mpSaved?.fullName ?: "Oscar Bueno") }
    var mpClabe by remember(mpSaved) { mutableStateOf(mpSaved?.clabeOrAccount ?: "012180015678901234") }
    var mpPhone by remember(mpSaved) { mutableStateOf(mpSaved?.phone ?: "5512345678") }

    // PayPal form state
    val ppSaved = paymentMethods.firstOrNull { it.methodType == "PAYPAL" }
    var ppEmail by remember(ppSaved) { mutableStateOf(ppSaved?.email ?: "buenooscar619@gmail.com") }

    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val mxnBalance = user?.mxnBalance ?: 150.0
    val usdBalance = user?.usdBalance ?: 8.33
    val coinBalance = user?.coinBalance ?: 2500L

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 36.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cartera & Pagos",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Dispersión instantánea mediante SPEI y PayPal API",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onOpenGatewayConfig,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkCard,
                    contentColor = NeonCyan
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text("⚙️ Pasarelas API", fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Balance Dashboard Card (Futuristic Cyber Glass)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E1736), Color(0xFF0F0B1E), Color(0xFF0A0714))
                    )
                )
                .border(
                    1.5.dp,
                    Brush.horizontalGradient(listOf(NeonCyan, Color(0xFFD946EF), WarmGold)),
                    RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SALDO DISPONIBLE PARA RETIRO",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WarmGold.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Bono Activo",
                            color = WarmGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$${String.format(Locale.US, "%.2f", mxnBalance)}",
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MXN",
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "≈ $${String.format(Locale.US, "%.2f", usdBalance)} USD",
                        color = Color(0xFF94A3B8),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Sub-card: Coins accumulated
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66000000))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = WarmGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${String.format("%,d", coinBalance)} Monedas",
                            color = WarmGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (coinBalance >= 500) {
                                onConvertCoins(500L)
                                feedbackMessage = "Convertiste 500 monedas a $5.00 MXN."
                            } else {
                                feedbackMessage = "Se requieren al menos 500 monedas para convertir."
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarmGold,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("convert_coins_button")
                    ) {
                        Icon(Icons.Default.CurrencyExchange, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Canjear (+5 MXN)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Amount Selection Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Monto a Retirar (MXN)",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "⚡ Retiro Express Validado",
                color = Color(0xFF22C55E),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(100.0, 200.0, 500.0, 1000.0).forEach { amt ->
                val isSelected = amt == selectedAmountMxn
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan else DarkCard)
                        .border(1.dp, if (isSelected) NeonCyan else Color(0xFF2A2A38), RoundedCornerShape(12.dp))
                        .clickable { selectedAmountMxn = amt }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$${amt.toInt()} MXN",
                        color = if (isSelected) PitchBlack else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Unlimited Rewards & Referral Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenReferralModal() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141324)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(WarmGold, NeonCyan))
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(WarmGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "Bono Invitación",
                            tint = WarmGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎁 Bono $200 MXN por Invitado",
                            color = WarmGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Ganancias ilimitadas • Más invitas, más cobras",
                            color = Color(0xFFE2E8F0),
                            fontSize = 11.sp
                        )
                    }
                }

                Button(
                    onClick = onOpenReferralModal,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Invitar", color = PitchBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Payment Method Forms (Mercado Pago / PayPal)
        Text(
            text = "Método de Pago para Retiro",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedMethodTab,
            containerColor = DarkCard,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedMethodTab]),
                    color = NeonCyan
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF2A2A38), RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedMethodTab == 0,
                onClick = { selectedMethodTab = 0 },
                text = { Text("Mercado Pago (SPEI)", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.AccountBalance, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
            Tab(
                selected = selectedMethodTab == 1,
                onClick = { selectedMethodTab = 1 },
                text = { Text("PayPal Instantáneo", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                icon = { Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Method-specific Form Fields
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .border(1.dp, Color(0xFF262634), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            if (selectedMethodTab == 0) {
                // Mercado Pago Form
                Column {
                    Text(
                        text = "Detalles de Transferencia SPEI / Mercado Pago",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Nombre Completo del Titular", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = mpFullName,
                        onValueChange = { mpFullName = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("mp_fullname_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedIndicatorColor = NeonCyan,
                            unfocusedIndicatorColor = Color(0xFF333344),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("CLABE Interbancaria (18 dígitos) o Teléfono", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = mpClabe,
                        onValueChange = { mpClabe = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("mp_clabe_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedIndicatorColor = NeonCyan,
                            unfocusedIndicatorColor = Color(0xFF333344),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSavePaymentMethod("MERCADO_PAGO", mpFullName, mpClabe, "", mpPhone)
                            feedbackMessage = "Datos de Mercado Pago guardados correctamente."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252536), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End).height(34.dp)
                    ) {
                        Text("Guardar Cuenta", fontSize = 11.sp)
                    }
                }
            } else {
                // PayPal Form
                Column {
                    Text(
                        text = "Detalles de Transferencia PayPal API",
                        color = NeonMagenta,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Correo Electrónico de PayPal", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = ppEmail,
                        onValueChange = { ppEmail = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pp_email_input"),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = DarkCard,
                            unfocusedContainerColor = DarkCard,
                            focusedIndicatorColor = NeonMagenta,
                            unfocusedIndicatorColor = Color(0xFF333344),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onSavePaymentMethod("PAYPAL", user?.username ?: "Usuario", ppEmail, ppEmail, "")
                            feedbackMessage = "Cuenta de PayPal guardada correctamente."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252536), contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.align(Alignment.End).height(34.dp)
                    ) {
                        Text("Guardar Cuenta", fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feedback alert if any
        AnimatedVisibility(visible = feedbackMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x3300F2FE))
                    .border(1.dp, NeonCyan, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = feedbackMessage ?: "",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Big Withdrawal Action Button
        val methodChosen = if (selectedMethodTab == 0) "MERCADO_PAGO" else "PAYPAL"
        val destinationChosen = if (selectedMethodTab == 0) mpClabe else ppEmail
        val canWithdraw = mxnBalance >= selectedAmountMxn && destinationChosen.isNotBlank()

        Button(
            onClick = {
                if (canWithdraw) {
                    val holder = if (selectedMethodTab == 0) mpFullName.ifBlank { user?.displayName ?: "Oscar Bueno" } else user?.displayName ?: "Oscar Bueno"
                    onRequestWithdrawal(selectedAmountMxn, methodChosen, destinationChosen, holder)
                    feedbackMessage = "¡Dispersión de fondos enviada a la pasarela! Comprobante generado."
                } else if (mxnBalance < selectedAmountMxn) {
                    feedbackMessage = "Saldo insuficiente. Gana más monedas viendo videos."
                } else {
                    feedbackMessage = "Por favor ingresa tu cuenta de retiro."
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedMethodTab == 0) NeonCyan else Color(0xFF0070BA),
                contentColor = PitchBlack
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("withdraw_action_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = PitchBlack,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Retirar $${selectedAmountMxn.toInt()} MXN a ${if (selectedMethodTab == 0) "Mercado Pago" else "PayPal"}",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Payout Requests / Transaction History
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Historial de Retiros y Transacciones",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Toca para ver recibo",
                color = NeonCyan,
                fontSize = 10.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        if (payoutRequests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Aún no tienes retiros. Solicita tu primer retiro arriba.",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                payoutRequests.forEach { req ->
                    PayoutRequestItemCard(req = req, onClick = { onViewReceipt(req) })
                }
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
private fun PayoutRequestItemCard(
    req: PayoutRequestEntity,
    onClick: () -> Unit
) {
    val dateStr = remember(req.requestedAt) {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        sdf.format(Date(req.requestedAt))
    }

    val (statusColor, statusText, statusIcon) = when (req.status) {
        "PENDING" -> Triple(Color(0xFFF59E0B), "⏳ Pendiente", Icons.Default.HourglassTop)
        "PROCESSING" -> Triple(NeonCyan, "⚙️ Procesando SPEI", Icons.Default.Sync)
        "SUCCESS" -> Triple(Color(0xFF10B981), "✅ Pagado SPEI", Icons.Default.CheckCircle)
        else -> Triple(Color(0xFFEF4444), "Rechazado", Icons.Default.HourglassTop)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkCard)
            .border(1.dp, Color(0xFF262638), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Retiro $${String.format(Locale.US, "%.2f", req.amountMxn)} MXN",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• Recibo 🧾",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${if (req.paymentMethod == "MERCADO_PAGO") "Mercado Pago (SPEI)" else "PayPal Instant"} • ${req.destinationAccount.take(14)}...",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ref: ${req.transactionReference} • $dateStr",
                    color = Color(0xFF64748B),
                    fontSize = 10.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
