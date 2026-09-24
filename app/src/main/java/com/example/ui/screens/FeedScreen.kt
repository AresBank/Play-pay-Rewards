package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.ui.components.BannerAdBar
import com.example.ui.components.CoinRingMeter
import com.example.ui.components.VideoFeedItem
import com.example.ui.components.WelcomeBonusBanner
import com.example.ui.theme.DarkCard
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.WarmGold

@Composable
fun FeedScreen(
    videos: List<VideoEntity>,
    followingVideos: List<VideoEntity>,
    feedMode: Int, // 0: Siguiendo, 1: Para Ti
    user: UserEntity?,
    coinRingProgress: Float,
    watchTimeSeconds: Int = 0,
    targetDurationSeconds: Int = 30,
    sessionEarnedCoins: Int = 0,
    isPlaying: Boolean = true,
    onTogglePlayPause: () -> Unit = {},
    showBonusBanner: Boolean,
    onFeedModeChanged: (Int) -> Unit,
    onVideoChanged: (Int) -> Unit,
    onLikeClick: (VideoEntity) -> Unit,
    onCommentClick: (VideoEntity) -> Unit,
    onShareClick: (VideoEntity) -> Unit,
    onFollowClick: (VideoEntity) -> Unit,
    onCreatorClick: (VideoEntity) -> Unit,
    onCoinRingClick: () -> Unit,
    onBonusBannerClick: () -> Unit,
    onDismissBonusBanner: () -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeList = if (feedMode == 0) followingVideos else videos

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { activeList.size }
    )

    LaunchedEffect(pagerState, activeList.size) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onVideoChanged(page)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
    ) {
        if (activeList.isNotEmpty()) {
            VerticalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("vertical_video_pager")
            ) { page ->
                val video = activeList[page]
                VideoFeedItem(
                    video = video,
                    isActive = pagerState.currentPage == page,
                    watchTimeSeconds = watchTimeSeconds,
                    targetDurationSeconds = targetDurationSeconds,
                    sessionEarnedCoins = sessionEarnedCoins,
                    isPlaying = isPlaying,
                    onTogglePlayPause = onTogglePlayPause,
                    onLikeClick = { onLikeClick(video) },
                    onCommentClick = { onCommentClick(video) },
                    onShareClick = { onShareClick(video) },
                    onFollowClick = { onFollowClick(video) },
                    onCreatorClick = { onCreatorClick(video) }
                )
            }
        } else {
            // Empty state for Following feed
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkCard)
                        .padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Aún no sigues a ningún creador",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Explora el feed 'Para Ti', sigue a tus creadores favoritos y sus videos aparecerán aquí.",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { onFeedModeChanged(1) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonCyan,
                            contentColor = PitchBlack
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Ir a Para Ti", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Top Overlay Header: Search icon, Siguiendo | Para Ti, CoinRingMeter
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onSearchClick,
                    modifier = Modifier.testTag("top_search_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = Color.White
                    )
                }

                // Center Tabs: Siguiendo | Para Ti
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Siguiendo",
                            color = if (feedMode == 0) Color.White else Color(0x99FFFFFF),
                            fontSize = 15.sp,
                            fontWeight = if (feedMode == 0) FontWeight.Black else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { onFeedModeChanged(0) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("tab_siguiendo")
                        )
                        if (feedMode == 0) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.5.dp)
                                    .background(NeonCyan, RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "•", color = Color(0x44FFFFFF), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Para Ti",
                            color = if (feedMode == 1) Color.White else Color(0x99FFFFFF),
                            fontSize = 16.sp,
                            fontWeight = if (feedMode == 1) FontWeight.Black else FontWeight.Normal,
                            modifier = Modifier
                                .clickable { onFeedModeChanged(1) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("tab_para_ti")
                        )
                        if (feedMode == 1) {
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(2.5.dp)
                                    .background(NeonCyan, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Floating Coin Ring Meter
                CoinRingMeter(
                    progress = coinRingProgress,
                    coinsToAward = if (activeList.isNotEmpty()) {
                        val curr = activeList.getOrNull(pagerState.currentPage)
                        curr?.rewardCoins ?: 150
                    } else 150,
                    onClick = onCoinRingClick
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Non-intrusive Sponsor Banner Ad Bar
            BannerAdBar(
                onAdClicked = { /* Opens sponsor landing */ }
            )

            // Welcome Bonus Banner
            if (showBonusBanner && user != null) {
                Spacer(modifier = Modifier.height(8.dp))
                WelcomeBonusBanner(
                    userRank = user.userRankNumber,
                    onDismiss = onDismissBonusBanner,
                    onClick = onBonusBannerClick
                )
            }
        }
    }
}
