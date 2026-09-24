package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.payment.PaymentResult
import com.example.ui.components.AuthModal
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.CelebrationModal
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CreatorProfileModal
import com.example.ui.components.DigitalReceiptModal
import com.example.ui.components.EditProfileModal
import com.example.ui.components.GatewayConfigModal
import com.example.ui.components.ReferralModal
import com.example.ui.components.ShareModal
import com.example.ui.components.VideoStudioModal
import com.example.ui.screens.DiscoverScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.PitchBlack
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.viewmodel.NavigationTab

@Composable
fun TikTokEarnApp(
    viewModel: MainViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val followingVideos by viewModel.followingVideos.collectAsState()
    val likedVideos by viewModel.likedVideos.collectAsState()
    val feedMode by viewModel.feedMode.collectAsState()
    val paymentMethods by viewModel.paymentMethods.collectAsState()
    val payoutRequests by viewModel.payoutRequests.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val coinRingProgress by viewModel.coinRingProgress.collectAsState()
    val showRewardCelebration by viewModel.showRewardCelebration.collectAsState()
    val lastEarnedCoins by viewModel.lastEarnedCoins.collectAsState()
    val isAiReRanking by viewModel.isAiReRanking.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val lastModerationResult by viewModel.lastModerationResult.collectAsState()
    val showAuthModal by viewModel.showAuthModal.collectAsState()
    val showCommentsSheet by viewModel.showCommentsSheet.collectAsState()
    val activeCommentsVideo by viewModel.activeCommentsVideo.collectAsState()
    val activeCommentsList by viewModel.activeCommentsList.collectAsState()
    val showShareModal by viewModel.showShareModal.collectAsState()
    val activeShareVideo by viewModel.activeShareVideo.collectAsState()
    val showCreatorProfileSheet by viewModel.showCreatorProfileSheet.collectAsState()
    val activeCreatorVideo by viewModel.activeCreatorVideo.collectAsState()
    val showEditProfileModal by viewModel.showEditProfileModal.collectAsState()
    val showBonusBanner by viewModel.showBonusBanner.collectAsState()

    // Real watch time states
    val watchTimeSeconds by viewModel.watchTimeSeconds.collectAsState()
    val targetDurationSeconds by viewModel.targetDurationSeconds.collectAsState()
    val sessionEarnedCoins by viewModel.sessionEarnedCoins.collectAsState()
    val isVideoPlaying by viewModel.isVideoPlaying.collectAsState()

    // Real Payment & Studio states
    val latestPaymentReceipt by viewModel.latestPaymentReceipt.collectAsState()
    val showGatewayConfigModal by viewModel.showGatewayConfigModal.collectAsState()
    val gatewayConfig by viewModel.gatewayConfig.collectAsState()
    val showVideoStudioModal by viewModel.showVideoStudioModal.collectAsState()
    val showReferralModal by viewModel.showReferralModal.collectAsState()
    val referrals by viewModel.referrals.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            BottomNavigationBar(
                currentTab = selectedTab,
                onTabSelected = { viewModel.selectTab(it) }
            )
        },
        containerColor = PitchBlack
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .background(PitchBlack)
        ) {
            when (selectedTab) {
                NavigationTab.PARA_TI -> {
                    FeedScreen(
                        videos = videos,
                        followingVideos = followingVideos,
                        feedMode = feedMode,
                        user = user,
                        coinRingProgress = coinRingProgress,
                        watchTimeSeconds = watchTimeSeconds,
                        targetDurationSeconds = targetDurationSeconds,
                        sessionEarnedCoins = sessionEarnedCoins,
                        isPlaying = isVideoPlaying,
                        onTogglePlayPause = { viewModel.toggleVideoPlayPause() },
                        showBonusBanner = showBonusBanner,
                        onFeedModeChanged = { viewModel.setFeedMode(it) },
                        onVideoChanged = { viewModel.onVideoChanged(it) },
                        onLikeClick = { viewModel.toggleLike(it) },
                        onCommentClick = { viewModel.openComments(it) },
                        onShareClick = { viewModel.openShareModal(it) },
                        onFollowClick = { viewModel.toggleFollow(it) },
                        onCreatorClick = { viewModel.openCreatorProfile(it) },
                        onCoinRingClick = { viewModel.claimWelcomeBonus() },
                        onBonusBannerClick = { viewModel.selectTab(NavigationTab.CARTERA) },
                        onDismissBonusBanner = { viewModel.dismissBonusBanner() },
                        onSearchClick = { viewModel.selectTab(NavigationTab.DESCUBRIR) }
                    )
                }
                NavigationTab.DESCUBRIR -> {
                    DiscoverScreen(
                        videos = videos,
                        isAiReRanking = isAiReRanking,
                        onReRankFeedClick = { viewModel.reRankFeedWithAi() },
                        onVideoSelected = {
                            val idx = videos.indexOf(it)
                            if (idx != -1) {
                                viewModel.onVideoChanged(idx)
                                viewModel.selectTab(NavigationTab.PARA_TI)
                            }
                        }
                    )
                }
                NavigationTab.SUBIR -> {
                    UploadScreen(
                        isUploading = isUploading,
                        lastModerationResult = lastModerationResult,
                        onUploadClick = { title, desc, cat, reward, cover, callback ->
                            viewModel.uploadVideo(title, desc, cat, reward, cover, callback)
                        },
                        onNavigateToFeed = { viewModel.selectTab(NavigationTab.PARA_TI) },
                        onOpenVideoStudio = { viewModel.openVideoStudio() }
                    )
                }
                NavigationTab.CARTERA -> {
                    WalletScreen(
                        user = user,
                        paymentMethods = paymentMethods,
                        payoutRequests = payoutRequests,
                        onConvertCoins = { viewModel.convertCoins(it) },
                        onRequestWithdrawal = { amt, method, dest, holder ->
                            viewModel.requestPayout(amt, method, dest, holder)
                        },
                        onSavePaymentMethod = { type, name, clabe, email, phone ->
                            viewModel.savePaymentMethod(type, name, clabe, email, phone)
                        },
                        onOpenGatewayConfig = { viewModel.openGatewayConfig() },
                        onOpenReferralModal = { viewModel.openReferralModal() },
                        onViewReceipt = { req ->
                            val receipt = PaymentResult(
                                success = req.status == "SUCCESS" || req.status == "PROCESSING",
                                status = req.status,
                                transactionReference = req.transactionReference,
                                trackingKey = "SPEI-BANXICO-${req.transactionReference.takeLast(7)}",
                                provider = if (req.paymentMethod == "MERCADO_PAGO") "Mercado Pago" else "PayPal",
                                destination = req.destinationAccount,
                                amountMxn = req.amountMxn,
                                message = if (req.status == "SUCCESS") "Transferencia liquidada exitosamente." else "Solicitud en proceso de compensación."
                            )
                            viewModel.showReceipt(receipt)
                        }
                    )
                }
                NavigationTab.PERFIL -> {
                    ProfileScreen(
                        user = user,
                        myVideos = videos.filter { it.creatorId == "creator_me" },
                        likedVideos = likedVideos,
                        onOpenAuthModal = { viewModel.openAuthModal() },
                        onEditProfileClick = { viewModel.openEditProfile() },
                        onVideoSelected = {
                            val idx = videos.indexOf(it)
                            if (idx != -1) {
                                viewModel.onVideoChanged(idx)
                                viewModel.selectTab(NavigationTab.PARA_TI)
                            }
                        },
                        onOpenVideoStudio = { viewModel.openVideoStudio() },
                        onOpenReferralModal = { viewModel.openReferralModal() }
                    )
                }
            }

            // Digital Receipt Modal (Live SPEI / PayPal confirmation)
            latestPaymentReceipt?.let { receipt ->
                DigitalReceiptModal(
                    receipt = receipt,
                    onDismiss = { viewModel.dismissReceipt() }
                )
            }

            // Payment Gateway Configuration Modal (Mercado Pago & PayPal API keys)
            if (showGatewayConfigModal) {
                GatewayConfigModal(
                    currentConfig = gatewayConfig,
                    onSaveConfig = { viewModel.saveGatewayConfig(it) },
                    onDismiss = { viewModel.closeGatewayConfig() }
                )
            }

            // 4-Step Video Studio Modal (Record / Pick -> Edit -> Details & Coins -> Para Ti)
            if (showVideoStudioModal) {
                VideoStudioModal(
                    onDismiss = { viewModel.closeVideoStudio() },
                    onUploadAndShowInFeed = { title, desc, cat, coins, uri, cover, sound, sticker, filter, dur ->
                        viewModel.publishAndShowInFeed(
                            title = title,
                            description = desc,
                            category = cat,
                            rewardCoins = coins,
                            videoUri = uri,
                            coverResName = cover,
                            soundTitle = sound,
                            textSticker = sticker,
                            filter = filter,
                            durationSeconds = dur
                        )
                    }
                )
            }

            // Coin Earn Celebration Modal
            if (showRewardCelebration) {
                CelebrationModal(
                    coinsEarned = lastEarnedCoins,
                    onDismiss = { viewModel.dismissRewardCelebration() }
                )
            }

            // Comments Sheet (with post, delete, like)
            if (showCommentsSheet) {
                CommentsBottomSheet(
                    video = activeCommentsVideo,
                    comments = activeCommentsList,
                    onDismiss = { viewModel.closeComments() },
                    onSendComment = { viewModel.postComment(it) },
                    onDeleteComment = { viewModel.deleteComment(it) },
                    onToggleCommentLike = { id, liked -> viewModel.toggleCommentLike(id, liked) }
                )
            }

            // Share Modal (Copy link, WhatsApp, Telegram, X, Android Chooser)
            if (showShareModal) {
                ShareModal(
                    video = activeShareVideo,
                    onDismiss = { viewModel.closeShareModal() },
                    onShareCountIncrement = { viewModel.onSharePerformed(it) }
                )
            }

            // Creator Profile Modal (Follow button, creator info & video showcase)
            if (showCreatorProfileSheet) {
                CreatorProfileModal(
                    video = activeCreatorVideo,
                    allVideos = videos,
                    onDismiss = { viewModel.closeCreatorProfile() },
                    onToggleFollow = { viewModel.toggleFollow(it) },
                    onSelectVideo = {
                        val idx = videos.indexOf(it)
                        if (idx != -1) {
                            viewModel.onVideoChanged(idx)
                            viewModel.selectTab(NavigationTab.PARA_TI)
                        }
                    }
                )
            }

            // Edit Profile Modal (Avatar, Bio, Username, Display Name)
            if (showEditProfileModal) {
                EditProfileModal(
                    user = user,
                    onDismiss = { viewModel.closeEditProfile() },
                    onSaveProfile = { dName, uName, bio, preset ->
                        viewModel.saveProfile(dName, uName, bio, preset)
                    }
                )
            }

            // Auth Modal
            if (showAuthModal) {
                AuthModal(
                    onDismiss = { viewModel.closeAuthModal() },
                    onLoginSuccess = { provider, id, uname ->
                        viewModel.loginWithProvider(provider, id, uname)
                    }
                )
            }

            // Referral & Unlimited Bonus Modal ($200 MXN per invite)
            if (showReferralModal) {
                ReferralModal(
                    user = user,
                    referrals = referrals,
                    onDismiss = { viewModel.closeReferralModal() },
                    onInviteFriend = { name, email -> viewModel.inviteFriend(name, email) },
                    onRedeemCode = { code -> viewModel.redeemInviteCode(code) }
                )
            }
        }
    }
}
