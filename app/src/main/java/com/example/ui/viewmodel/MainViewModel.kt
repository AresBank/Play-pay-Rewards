package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.GeminiAiService
import com.example.data.ai.ModerationResult
import com.example.data.local.AppDatabase
import com.example.data.local.CommentEntity
import com.example.data.local.PaymentMethodEntity
import com.example.data.local.PayoutRequestEntity
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.data.payment.PaymentGatewayConfig
import com.example.data.payment.PaymentGatewayService
import com.example.data.payment.PaymentResult
import com.example.data.repository.AppRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class NavigationTab {
    PARA_TI,
    DESCUBRIR,
    SUBIR,
    CARTERA,
    PERFIL
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val aiService = GeminiAiService()
    private val gatewayService = PaymentGatewayService(application)
    private val repository = AppRepository(database.appDao(), aiService, gatewayService)

    val user: StateFlow<UserEntity?> = repository.userFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val videos: StateFlow<List<VideoEntity>> = repository.videosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingVideos: StateFlow<List<VideoEntity>> = repository.followingVideosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedVideos: StateFlow<List<VideoEntity>> = repository.likedVideosFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val paymentMethods: StateFlow<List<PaymentMethodEntity>> = repository.paymentMethodsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val payoutRequests: StateFlow<List<PayoutRequestEntity>> = repository.payoutRequestsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedTab = MutableStateFlow(NavigationTab.PARA_TI)
    val selectedTab: StateFlow<NavigationTab> = _selectedTab.asStateFlow()

    private val _feedMode = MutableStateFlow(1) // 0: Siguiendo, 1: Para Ti
    val feedMode: StateFlow<Int> = _feedMode.asStateFlow()

    private val _activeVideoIndex = MutableStateFlow(0)
    val activeVideoIndex: StateFlow<Int> = _activeVideoIndex.asStateFlow()

    // Real Watch Time Tracking State
    private val _watchTimeSeconds = MutableStateFlow(0)
    val watchTimeSeconds: StateFlow<Int> = _watchTimeSeconds.asStateFlow()

    private val _targetDurationSeconds = MutableStateFlow(30)
    val targetDurationSeconds: StateFlow<Int> = _targetDurationSeconds.asStateFlow()

    private val _sessionEarnedCoins = MutableStateFlow(0)
    val sessionEarnedCoins: StateFlow<Int> = _sessionEarnedCoins.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(true)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    private val _coinRingProgress = MutableStateFlow(0f)
    val coinRingProgress: StateFlow<Float> = _coinRingProgress.asStateFlow()

    private val _showRewardCelebration = MutableStateFlow(false)
    val showRewardCelebration: StateFlow<Boolean> = _showRewardCelebration.asStateFlow()

    private val _lastEarnedCoins = MutableStateFlow(500)
    val lastEarnedCoins: StateFlow<Int> = _lastEarnedCoins.asStateFlow()

    // Referrals State ($200 MXN bonus per invited user)
    val referrals: StateFlow<List<com.example.data.local.ReferralEntity>> = repository.getReferralsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _showReferralModal = MutableStateFlow(false)
    val showReferralModal: StateFlow<Boolean> = _showReferralModal.asStateFlow()

    private val _isAiReRanking = MutableStateFlow(false)
    val isAiReRanking: StateFlow<Boolean> = _isAiReRanking.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _lastModerationResult = MutableStateFlow<ModerationResult?>(null)
    val lastModerationResult: StateFlow<ModerationResult?> = _lastModerationResult.asStateFlow()

    // Payment Gateway & Receipts State
    private val _latestPaymentReceipt = MutableStateFlow<PaymentResult?>(null)
    val latestPaymentReceipt: StateFlow<PaymentResult?> = _latestPaymentReceipt.asStateFlow()

    private val _showGatewayConfigModal = MutableStateFlow(false)
    val showGatewayConfigModal: StateFlow<Boolean> = _showGatewayConfigModal.asStateFlow()

    private val _gatewayConfig = MutableStateFlow(gatewayService.getConfig())
    val gatewayConfig: StateFlow<PaymentGatewayConfig> = _gatewayConfig.asStateFlow()

    // Video Studio modal state
    private val _showVideoStudioModal = MutableStateFlow(false)
    val showVideoStudioModal: StateFlow<Boolean> = _showVideoStudioModal.asStateFlow()

    private val _showAuthModal = MutableStateFlow(false)
    val showAuthModal: StateFlow<Boolean> = _showAuthModal.asStateFlow()

    private val _showCommentsSheet = MutableStateFlow(false)
    val showCommentsSheet: StateFlow<Boolean> = _showCommentsSheet.asStateFlow()

    private val _activeCommentsVideo = MutableStateFlow<VideoEntity?>(null)
    val activeCommentsVideo: StateFlow<VideoEntity?> = _activeCommentsVideo.asStateFlow()

    private val _activeCommentsList = MutableStateFlow<List<CommentEntity>>(emptyList())
    val activeCommentsList: StateFlow<List<CommentEntity>> = _activeCommentsList.asStateFlow()

    private val _showShareModal = MutableStateFlow(false)
    val showShareModal: StateFlow<Boolean> = _showShareModal.asStateFlow()

    private val _activeShareVideo = MutableStateFlow<VideoEntity?>(null)
    val activeShareVideo: StateFlow<VideoEntity?> = _activeShareVideo.asStateFlow()

    private val _showCreatorProfileSheet = MutableStateFlow(false)
    val showCreatorProfileSheet: StateFlow<Boolean> = _showCreatorProfileSheet.asStateFlow()

    private val _activeCreatorVideo = MutableStateFlow<VideoEntity?>(null)
    val activeCreatorVideo: StateFlow<VideoEntity?> = _activeCreatorVideo.asStateFlow()

    private val _showEditProfileModal = MutableStateFlow(false)
    val showEditProfileModal: StateFlow<Boolean> = _showEditProfileModal.asStateFlow()

    private val _showBonusBanner = MutableStateFlow(true)
    val showBonusBanner: StateFlow<Boolean> = _showBonusBanner.asStateFlow()

    private var coinTimerJob: Job? = null
    private var commentsJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initializeSeedData()
            startRealWatchTimer()
        }
    }

    fun setFeedMode(mode: Int) {
        _feedMode.value = mode
        _activeVideoIndex.value = 0
        startRealWatchTimer()
    }

    fun selectTab(tab: NavigationTab) {
        _selectedTab.value = tab
        if (tab == NavigationTab.PARA_TI) {
            startRealWatchTimer()
        } else {
            coinTimerJob?.cancel()
        }
    }

    fun onVideoChanged(index: Int) {
        _activeVideoIndex.value = index
        startRealWatchTimer()
    }

    fun togglePlayPause() {
        _isVideoPlaying.value = !_isVideoPlaying.value
    }

    private fun startRealWatchTimer() {
        coinTimerJob?.cancel()
        _watchTimeSeconds.value = 0
        _sessionEarnedCoins.value = 0
        _coinRingProgress.value = 0f
        _isVideoPlaying.value = true

        val currentList = if (_feedMode.value == 0) followingVideos.value else videos.value
        val currentVideo = currentList.getOrNull(_activeVideoIndex.value)
        val targetSeconds = 30 // standard 30s duration cycle
        // Every video pays minimum 500 coins and $5.00 MXN netos
        val totalRewardCoins = (currentVideo?.rewardCoins ?: 500).coerceAtLeast(500)
        val totalRewardMxn = (currentVideo?.rewardMxnNet ?: 5.0).coerceAtLeast(5.0)
        _targetDurationSeconds.value = targetSeconds

        coinTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L) // Real 1 second elapsed
                if (_isVideoPlaying.value && _selectedTab.value == NavigationTab.PARA_TI) {
                    val currentSecs = _watchTimeSeconds.value + 1
                    _watchTimeSeconds.value = currentSecs
                    val progress = (currentSecs.toFloat() / targetSeconds.toFloat()).coerceIn(0f, 1f)
                    _coinRingProgress.value = progress

                    // Continuous reward: Every 3 seconds of real watching awards proportional coins and MXN
                    if (currentSecs % 3 == 0) {
                        val incrementalCoins = (totalRewardCoins / 10).coerceAtLeast(50)
                        val incrementalMxn = (totalRewardMxn / 10.0).coerceAtLeast(0.50)
                        _sessionEarnedCoins.value += incrementalCoins
                        currentVideo?.let {
                            repository.recordRealWatchTime(it.id, 3, incrementalCoins, incrementalMxn)
                        }
                    }

                    // Complete cycle reward: guarantees full $5.00 MXN netos & 500 coins minimum
                    if (currentSecs >= targetSeconds) {
                        val remainingCoins = (totalRewardCoins - _sessionEarnedCoins.value).coerceAtLeast(50)
                        val remainingMxn = (totalRewardMxn - (_sessionEarnedCoins.value * 0.01)).coerceAtLeast(0.50)
                        currentVideo?.let {
                            repository.recordRealWatchTime(it.id, 1, remainingCoins, remainingMxn)
                        }
                        _sessionEarnedCoins.value += remainingCoins
                        _lastEarnedCoins.value = totalRewardCoins
                        _showRewardCelebration.value = true

                        // Loop for continued unlimited watching & earning!
                        _watchTimeSeconds.value = 0
                        _coinRingProgress.value = 0f
                    }
                }
            }
        }
    }

    fun toggleVideoPlayPause() {
        _isVideoPlaying.value = !_isVideoPlaying.value
    }

    fun dismissRewardCelebration() {
        _showRewardCelebration.value = false
    }

    // Referrals ($200 MXN per invited friend)
    fun openReferralModal() {
        _showReferralModal.value = true
    }

    fun dismissReferralModal() {
        _showReferralModal.value = false
    }

    fun inviteFriend(name: String, email: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.registerFriendInvitation(name, email)
            onComplete()
        }
    }

    fun redeemInviteCode(code: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val success = repository.redeemInviteCode(code)
            onResult(success)
        }
    }

    fun toggleLike(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleLike(video.id, video.isLiked)
        }
    }

    fun toggleFollow(video: VideoEntity) {
        viewModelScope.launch {
            repository.toggleFollow(video.creatorId, video.isFollowed)
        }
    }

    fun toggleFollowCreator(creatorId: String, currentIsFollowed: Boolean) {
        viewModelScope.launch {
            repository.toggleFollow(creatorId, currentIsFollowed)
        }
    }

    fun claimWelcomeBonus() {
        viewModelScope.launch {
            repository.claimWelcomeBonus()
            _showBonusBanner.value = false
            _lastEarnedCoins.value = 1000
            _showRewardCelebration.value = true
        }
    }

    fun dismissBonusBanner() {
        _showBonusBanner.value = false
    }

    fun convertCoins(coinsToConvert: Long) {
        viewModelScope.launch {
            repository.convertCoinsToMxn(coinsToConvert)
        }
    }

    fun requestPayout(
        amountMxn: Double,
        method: String,
        destination: String,
        holderName: String = "Oscar Bueno",
        onResult: (PaymentResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val result = repository.requestWithdrawal(amountMxn, method, destination, holderName)
                _latestPaymentReceipt.value = result
                onResult(result)
            } catch (e: Exception) {
                val errorResult = PaymentResult(
                    success = false,
                    status = "FAILED",
                    transactionReference = "ERR-${System.currentTimeMillis().toString().takeLast(6)}",
                    trackingKey = "N/A",
                    provider = if (method == "MERCADO_PAGO") "Mercado Pago" else "PayPal",
                    destination = destination,
                    amountMxn = amountMxn,
                    message = e.localizedMessage ?: "Error al procesar el retiro"
                )
                _latestPaymentReceipt.value = errorResult
                onResult(errorResult)
            }
        }
    }

    fun dismissReceipt() {
        _latestPaymentReceipt.value = null
    }

    fun showReceipt(receipt: PaymentResult) {
        _latestPaymentReceipt.value = receipt
    }

    fun openGatewayConfig() {
        _showGatewayConfigModal.value = true
    }

    fun closeGatewayConfig() {
        _showGatewayConfigModal.value = false
    }

    fun saveGatewayConfig(config: PaymentGatewayConfig) {
        gatewayService.saveConfig(config)
        _gatewayConfig.value = config
        _showGatewayConfigModal.value = false
    }

    fun openVideoStudio() {
        _showVideoStudioModal.value = true
    }

    fun closeVideoStudio() {
        _showVideoStudioModal.value = false
    }

    fun publishAndShowInFeed(
        title: String,
        description: String,
        category: String,
        rewardCoins: Int,
        videoUri: String,
        coverResName: String,
        soundTitle: String,
        textSticker: String,
        filter: String,
        durationSeconds: Int
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            val newVideo = repository.publishFullUserVideo(
                title = title,
                description = description,
                category = category,
                rewardCoins = rewardCoins,
                videoUri = videoUri,
                coverResName = coverResName,
                soundTitle = soundTitle
            )
            _isUploading.value = false
            _showVideoStudioModal.value = false

            // Direct jump to Para Ti, video on index 0, with live coin ring reward!
            _feedMode.value = 1
            _selectedTab.value = NavigationTab.PARA_TI
            _activeVideoIndex.value = 0
            _targetDurationSeconds.value = durationSeconds
            startRealWatchTimer()
        }
    }

    fun savePaymentMethod(type: String, fullName: String, clabeOrAccount: String, email: String, phone: String) {
        viewModelScope.launch {
            repository.savePaymentMethod(
                PaymentMethodEntity(
                    userId = 1L,
                    methodType = type,
                    fullName = fullName,
                    clabeOrAccount = clabeOrAccount,
                    email = email,
                    phone = phone,
                    isDefault = true
                )
            )
        }
    }

    fun reRankFeedWithAi() {
        viewModelScope.launch {
            _isAiReRanking.value = true
            repository.reRankFeedWithAi()
            delay(1200)
            _isAiReRanking.value = false
        }
    }

    fun uploadVideo(
        title: String,
        description: String,
        category: String,
        rewardCoins: Int,
        coverResName: String,
        onComplete: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isUploading.value = true
            val moderation = repository.moderateAndPublishVideo(
                title = title,
                description = description,
                category = category,
                rewardCoins = rewardCoins,
                coverResName = coverResName
            )
            _lastModerationResult.value = moderation
            _isUploading.value = false
            onComplete(moderation.approved, moderation.reason)
        }
    }

    // Comments handling
    fun openComments(video: VideoEntity) {
        _activeCommentsVideo.value = video
        _showCommentsSheet.value = true
        commentsJob?.cancel()
        commentsJob = viewModelScope.launch {
            repository.getCommentsFlow(video.id).collect { list ->
                _activeCommentsList.value = list
            }
        }
    }

    fun postComment(text: String) {
        val vid = _activeCommentsVideo.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.postComment(vid.id, text.trim())
        }
    }

    fun deleteComment(commentId: String) {
        val vid = _activeCommentsVideo.value ?: return
        viewModelScope.launch {
            repository.deleteComment(commentId, vid.id)
        }
    }

    fun toggleCommentLike(commentId: String, currentIsLiked: Boolean) {
        viewModelScope.launch {
            repository.toggleCommentLike(commentId, currentIsLiked)
        }
    }

    fun closeComments() {
        _showCommentsSheet.value = false
        commentsJob?.cancel()
    }

    // Share handling
    fun openShareModal(video: VideoEntity) {
        _activeShareVideo.value = video
        _showShareModal.value = true
    }

    fun closeShareModal() {
        _showShareModal.value = false
    }

    fun onSharePerformed(video: VideoEntity) {
        viewModelScope.launch {
            repository.shareVideo(video.id)
        }
    }

    // Creator profile sheet
    fun openCreatorProfile(video: VideoEntity) {
        _activeCreatorVideo.value = video
        _showCreatorProfileSheet.value = true
    }

    fun closeCreatorProfile() {
        _showCreatorProfileSheet.value = false
    }

    // Edit Profile Modal
    fun openEditProfile() {
        _showEditProfileModal.value = true
    }

    fun closeEditProfile() {
        _showEditProfileModal.value = false
    }

    fun saveProfile(displayName: String, username: String, bio: String, avatarPreset: String) {
        viewModelScope.launch {
            repository.updateUserProfile(displayName, username, bio, avatarPreset)
            _showEditProfileModal.value = false
        }
    }

    fun openAuthModal() {
        _showAuthModal.value = true
    }

    fun closeAuthModal() {
        _showAuthModal.value = false
    }

    fun loginWithProvider(provider: String, identifier: String, username: String) {
        viewModelScope.launch {
            repository.loginWithProvider(provider, identifier, username)
            _showAuthModal.value = false
        }
    }

    fun closeReferralModal() {
        dismissReferralModal()
    }
}
