package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.WarmGold
import kotlinx.coroutines.delay

data class SponsorAd(
    val id: String,
    val brand: String,
    val tag: String,
    val callToAction: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun BannerAdBar(
    modifier: Modifier = Modifier,
    onAdClicked: (SponsorAd) -> Unit = {}
) {
    val ads = remember {
        listOf(
            SponsorAd(
                "ad1",
                "NeonPay",
                "SPEI Instantáneo 0% Comisión",
                "Abrir Cuenta",
                Icons.Default.ElectricBolt,
                NeonCyan
            ),
            SponsorAd(
                "ad2",
                "CyberMeta 2077",
                "Gana criptos jugando en la nube",
                "Jugar Gratis",
                Icons.Default.SportsEsports,
                Color(0xFFD946EF)
            ),
            SponsorAd(
                "ad3",
                "CryptoVault",
                "Staking con 14.5% APY asegurado",
                "Invertir",
                Icons.Default.Security,
                WarmGold
            )
        )
    }

    var currentAdIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000)
            currentAdIndex = (currentAdIndex + 1) % ads.size
        }
    }

    val currentAd = ads[currentAdIndex]

    Box(
        modifier = modifier
            .testTag("banner_ad_bar")
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x990A0A10))
            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(12.dp))
            .clickable { onAdClicked(currentAd) }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        AnimatedContent(
            targetState = currentAd,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ad_anim"
        ) { ad ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ad.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = ad.icon,
                            contentDescription = ad.brand,
                            tint = ad.accentColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Patrocinado: ",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Text(
                        text = "${ad.brand} • ${ad.tag}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(ad.accentColor.copy(alpha = 0.25f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = ad.callToAction,
                        color = ad.accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = "Ir",
                        tint = ad.accentColor,
                        modifier = Modifier.size(11.dp)
                    )
                }
            }
        }
    }
}
