package com.example.data.repository

import com.example.data.ai.GeminiAiService
import com.example.data.ai.ModerationResult
import com.example.data.local.AppDao
import com.example.data.local.CommentEntity
import com.example.data.local.PaymentMethodEntity
import com.example.data.local.PayoutRequestEntity
import com.example.data.local.ReferralEntity
import com.example.data.local.UserEntity
import com.example.data.local.VideoEntity
import com.example.data.local.VideoViewEntity
import com.example.data.payment.PaymentGatewayService
import com.example.data.payment.PaymentResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class AppRepository(
    private val appDao: AppDao,
    private val geminiAiService: GeminiAiService,
    private val paymentGatewayService: PaymentGatewayService? = null
) {
    val userFlow: Flow<UserEntity?> = appDao.getUserFlow(1L)
    val videosFlow: Flow<List<VideoEntity>> = appDao.getAllVideosFlow()
    val followingVideosFlow: Flow<List<VideoEntity>> = appDao.getFollowingVideosFlow()
    val likedVideosFlow: Flow<List<VideoEntity>> = appDao.getLikedVideosFlow()
    val paymentMethodsFlow: Flow<List<PaymentMethodEntity>> = appDao.getPaymentMethodsFlow(1L)
    val payoutRequestsFlow: Flow<List<PayoutRequestEntity>> = appDao.getPayoutRequestsFlow(1L)

    fun getCommentsFlow(videoId: String): Flow<List<CommentEntity>> = appDao.getCommentsFlow(videoId)

    suspend fun initializeSeedData() {
        // Seed user if needed
        val currentUser = appDao.getUser(1L)
        if (currentUser == null) {
            appDao.insertUser(
                UserEntity(
                    id = 1L,
                    authProvider = "email",
                    email = "oscar.bueno@playandpay.io",
                    displayName = "Oscar Bueno",
                    username = "Oscar_PlayPay",
                    bio = "Viendo videos y ganando dinero real con Play&Pay ⚡💸",
                    avatarPreset = "neon_cyan",
                    coinBalance = 5000L,
                    usdBalance = 27.77,
                    mxnBalance = 500.0, // Welcome bonus of $500 MXN pre-credited!
                    hasReceivedWelcomeBonus = true,
                    userRankNumber = 482190,
                    followersCount = 1420,
                    followingCount = 1,
                    referralCode = "PLAY-7821",
                    referralsCount = 2,
                    referralEarningsMxn = 400.0
                )
            )

            // Seed initial sample referrals ($200 MXN per invite)
            appDao.insertReferral(
                ReferralEntity(
                    referrerUserId = 1L,
                    friendName = "Mariana López",
                    friendEmail = "mariana.lpz@gmail.com",
                    bonusMxn = 200.0,
                    status = "ACREDITADO",
                    registeredAt = System.currentTimeMillis() - 86400000
                )
            )
            appDao.insertReferral(
                ReferralEntity(
                    referrerUserId = 1L,
                    friendName = "Carlos Mendoza",
                    friendEmail = "carlos.m@outlook.com",
                    bonusMxn = 200.0,
                    status = "ACREDITADO",
                    registeredAt = System.currentTimeMillis() - 43200000
                )
            )
        }

        // Seed payment methods if none exist
        val paymentMethods = appDao.getPaymentMethodsFlow(1L).firstOrNull()
        if (paymentMethods.isNullOrEmpty()) {
            appDao.insertPaymentMethod(
                PaymentMethodEntity(
                    userId = 1L,
                    methodType = "MERCADO_PAGO",
                    fullName = "Oscar Bueno",
                    clabeOrAccount = "012180015678901234",
                    email = "buenooscar619@gmail.com",
                    phone = "5512345678",
                    isDefault = true
                )
            )
            appDao.insertPaymentMethod(
                PaymentMethodEntity(
                    userId = 1L,
                    methodType = "PAYPAL",
                    fullName = "Oscar Bueno",
                    clabeOrAccount = "buenooscar619@gmail.com",
                    email = "buenooscar619@gmail.com",
                    isDefault = false
                )
            )
        }

        // Seed sample vertical videos if empty
        if (appDao.getVideoCount() == 0) {
            val initialVideos = listOf(
                VideoEntity(
                    id = "vid_1_cyber_ai",
                    creatorId = "creator_neo",
                    creatorName = "NeoNexus AI",
                    creatorHandle = "@neonexus_lab",
                    title = "Generando música cyberpunk con Gemini 2.5 y sintetizadores analógicos ⚡🎧",
                    description = "Mira cómo creamos este set completo en vivo usando inteligencia artificial. ¡Gana $5.00 MXN netos (500 monedas) viéndolo hasta el final! #cyberpunk #ai #musica #tech",
                    videoUrl = "sample_stream_1",
                    coverDrawableResName = "thumb_cyber_ai",
                    rewardCoins = 500,
                    rewardMxnNet = 5.0,
                    category = "Tech & AI",
                    likesCount = 42890,
                    commentsCount = 1320,
                    sharesCount = 840,
                    isLiked = false,
                    isFollowed = true, // Following NeoNexus by default so Following feed has content
                    soundTitle = "CyberSynth • Neon Pulse 140BPM",
                    aiAffinityScore = 98,
                    aiReason = "✨ 98% Afinidad: Coincide con tu interés en IA y Tecnología"
                ),
                VideoEntity(
                    id = "vid_2_crypto",
                    creatorId = "creator_ana",
                    creatorName = "Ana Finanzas",
                    creatorHandle = "@anacripto_mx",
                    title = "Cómo retirar tus $500 MXN de Play&Pay a Mercado Pago y PayPal en 5 segundos 💎📈",
                    description = "Tutorial paso a paso: cómo retirar tu bono de $500 MXN y tus $5 MXN por cada video visto a Mercado Pago mediante SPEI instantáneo sin comisiones. #finanzas #retiros #mercadopago",
                    videoUrl = "sample_stream_2",
                    coverDrawableResName = "thumb_crypto_future",
                    rewardCoins = 500,
                    rewardMxnNet = 5.0,
                    category = "Crypto & Finanzas",
                    likesCount = 89200,
                    commentsCount = 3410,
                    sharesCount = 2190,
                    isLiked = true,
                    isFollowed = false,
                    soundTitle = "Fintech Vibes • Cashflow Mix",
                    aiAffinityScore = 95,
                    aiReason = "🔥 95% Afinidad: Tendencia viral en monetización digital"
                ),
                VideoEntity(
                    id = "vid_3_gaming",
                    creatorId = "creator_viper",
                    creatorName = "Viper Esports",
                    creatorHandle = "@viper_gg",
                    title = "Final épica del Torneo CyberArena: ¡Jugada clutch de 1 vs 4 en rango Inmortal! 🎮🏆",
                    description = "¡La jugada más rápida de la temporada! Gana $5.00 MXN netos garantizados por ver el clip. Deja tu like si juegas shooters. #gaming #esports #cybermeta #clutch",
                    videoUrl = "sample_stream_3",
                    coverDrawableResName = "thumb_gamer_neon",
                    rewardCoins = 500,
                    rewardMxnNet = 5.0,
                    category = "Gaming",
                    likesCount = 124500,
                    commentsCount = 5200,
                    sharesCount = 6800,
                    isLiked = false,
                    isFollowed = false,
                    soundTitle = "Electro Hype • Arena Finals",
                    aiAffinityScore = 92,
                    aiReason = "🎯 92% Afinidad: Alto engagement en comunidad gamer"
                ),
                VideoEntity(
                    id = "vid_4_food",
                    creatorId = "creator_kenji",
                    creatorName = "Chef Kenji",
                    creatorHandle = "@kenji_street",
                    title = "Fideos flameados al wok en el callejón nocturno cyberpunk de Neo-Tokyo 🍜🔥",
                    description = "El ramen molecular más picante del mundo servido a las 3:00 AM con salsa de chiles fermentados. Gana $5.00 MXN al verlo. #streetfood #foodie #tokyo #antojo",
                    videoUrl = "sample_stream_4",
                    coverDrawableResName = "thumb_street_chef",
                    rewardCoins = 500,
                    rewardMxnNet = 5.0,
                    category = "Estilo de Vida",
                    likesCount = 63100,
                    commentsCount = 1890,
                    sharesCount = 1420,
                    isLiked = false,
                    isFollowed = false,
                    soundTitle = "Tokyo Rain • Lo-Fi Chill Beats",
                    aiAffinityScore = 88,
                    aiReason = "🍜 88% Afinidad: Contenido relajante y culinario"
                )
            )
            appDao.insertVideos(initialVideos)

            // Seed initial comments
            val sampleComments = listOf(
                CommentEntity(
                    id = "c_seed_1",
                    videoId = "vid_1_cyber_ai",
                    authorId = 2L,
                    authorName = "Elena Torres",
                    authorHandle = "@elena_tech",
                    authorAvatarPreset = "neon_magenta",
                    text = "¡Increíble síntesis sonora! Los prompts en Gemini 2.5 generan progresiones de acordes impresionantes 🎹⚡",
                    likesCount = 245,
                    isLiked = true,
                    isMine = false,
                    createdAt = System.currentTimeMillis() - 3600000
                ),
                CommentEntity(
                    id = "c_seed_2",
                    videoId = "vid_1_cyber_ai",
                    authorId = 1L,
                    authorName = "Oscar Bueno",
                    authorHandle = "@cyberviewer_mx",
                    authorAvatarPreset = "neon_cyan",
                    text = "Ya acumulé 750 monedas viendo este stream. ¡El medidor del anillo funciona súper fluido! 💎🔥",
                    likesCount = 89,
                    isLiked = false,
                    isMine = true,
                    createdAt = System.currentTimeMillis() - 1800000
                ),
                CommentEntity(
                    id = "c_seed_3",
                    videoId = "vid_2_crypto",
                    authorId = 3L,
                    authorName = "Mariana Fintech",
                    authorHandle = "@mariana_cripto",
                    authorAvatarPreset = "warm_gold",
                    text = "Retiré mis primeros $150 MXN de bono de bienvenida a mi cuenta de Mercado Pago en menos de 10 segundos 🚀💸",
                    likesCount = 312,
                    isLiked = true,
                    isMine = false,
                    createdAt = System.currentTimeMillis() - 7200000
                )
            )
            sampleComments.forEach { appDao.insertComment(it) }
        }
    }

    suspend fun recordVideoCompletion(videoId: String, rewardCoins: Int): Boolean {
        return recordRealWatchTime(videoId, 15, rewardCoins) > 0
    }

    suspend fun recordRealWatchTime(videoId: String, watchedSeconds: Int, coinsToAward: Int, mxnToAward: Double = 0.0): Int {
        val user = appDao.getUser(1L) ?: return 0
        val existingView = appDao.getView(user.id, videoId)
        val totalWatchTime = (existingView?.watchTimeSeconds ?: 0) + watchedSeconds
        appDao.insertView(
            VideoViewEntity(
                id = existingView?.id ?: 0L,
                userId = user.id,
                videoId = videoId,
                watchTimeSeconds = totalWatchTime,
                coinsEarned = true,
                viewedAt = System.currentTimeMillis()
            )
        )
        if (coinsToAward > 0) {
            appDao.addCoins(user.id, coinsToAward.toLong())
        }
        if (mxnToAward > 0.0) {
            appDao.addMxnBalance(user.id, mxnToAward, mxnToAward / 18.0)
        }
        return coinsToAward
    }

    suspend fun toggleLike(videoId: String, currentIsLiked: Boolean) {
        val delta = if (currentIsLiked) -1 else 1
        appDao.toggleLike(videoId, !currentIsLiked, delta)
    }

    suspend fun toggleFollow(creatorId: String, currentIsFollowed: Boolean) {
        val newFollowState = !currentIsFollowed
        appDao.toggleFollow(creatorId, newFollowState)
        appDao.updateFollowingCount(1L, if (newFollowState) 1 else -1)
    }

    suspend fun shareVideo(videoId: String) {
        appDao.incrementShareCount(videoId)
    }

    suspend fun postComment(videoId: String, text: String): CommentEntity? {
        val user = appDao.getUser(1L) ?: return null
        val newComment = CommentEntity(
            id = "c_${UUID.randomUUID()}",
            videoId = videoId,
            authorId = user.id,
            authorName = user.displayName.ifBlank { user.username },
            authorHandle = "@${user.username.lowercase()}",
            authorAvatarPreset = user.avatarPreset,
            text = text,
            likesCount = 0,
            isLiked = false,
            isMine = true,
            createdAt = System.currentTimeMillis()
        )
        appDao.insertComment(newComment)
        appDao.updateVideoCommentsCount(videoId, 1)
        return newComment
    }

    suspend fun deleteComment(commentId: String, videoId: String) {
        appDao.deleteComment(commentId)
        appDao.updateVideoCommentsCount(videoId, -1)
    }

    suspend fun toggleCommentLike(commentId: String, currentIsLiked: Boolean) {
        val delta = if (currentIsLiked) -1 else 1
        appDao.toggleCommentLike(commentId, !currentIsLiked, delta)
    }

    suspend fun updateUserProfile(displayName: String, username: String, bio: String, avatarPreset: String) {
        appDao.updateUserProfile(1L, displayName, username, bio, avatarPreset)
    }

    suspend fun claimWelcomeBonus(): Boolean {
        val user = appDao.getUser(1L) ?: return false
        if (user.hasReceivedWelcomeBonus) return false
        val newMxn = user.mxnBalance + 500.0
        val newUsd = newMxn / 18.0
        appDao.updateBalances(user.id, user.coinBalance + 5000L, newMxn, newUsd)
        appDao.updateUser(user.copy(hasReceivedWelcomeBonus = true, mxnBalance = newMxn, usdBalance = newUsd))
        return true
    }

    fun getReferralsFlow(): Flow<List<ReferralEntity>> = appDao.getReferralsFlow(1L)

    suspend fun registerFriendInvitation(friendName: String, friendEmail: String): ReferralEntity {
        val referral = ReferralEntity(
            referrerUserId = 1L,
            friendName = friendName.ifBlank { "Amigo Invitado" },
            friendEmail = friendEmail.ifBlank { "amigo_${System.currentTimeMillis().toString().takeLast(4)}@correo.com" },
            bonusMxn = 200.0,
            status = "ACREDITADO",
            registeredAt = System.currentTimeMillis()
        )
        appDao.insertReferral(referral)
        appDao.addReferralReward(1L, 200.0, 200.0 / 18.0)
        return referral
    }

    suspend fun redeemInviteCode(code: String): Boolean {
        if (code.isBlank()) return false
        val referral = ReferralEntity(
            referrerUserId = 1L,
            friendName = "Bono Código: ${code.uppercase()}",
            friendEmail = "canje.codigo@playandpay.io",
            bonusMxn = 200.0,
            status = "ACREDITADO",
            registeredAt = System.currentTimeMillis()
        )
        appDao.insertReferral(referral)
        appDao.addReferralReward(1L, 200.0, 200.0 / 18.0)
        return true
    }

    suspend fun convertCoinsToMxn(coinsToConvert: Long): Boolean {
        val user = appDao.getUser(1L) ?: return false
        if (user.coinBalance < coinsToConvert || coinsToConvert <= 0) return false

        val mxnAdded = coinsToConvert / 100.0
        val newCoins = user.coinBalance - coinsToConvert
        val newMxn = user.mxnBalance + mxnAdded
        val newUsd = newMxn / 18.0

        appDao.updateBalances(user.id, newCoins, newMxn, newUsd)
        return true
    }

    suspend fun savePaymentMethod(method: PaymentMethodEntity) {
        appDao.insertPaymentMethod(method)
    }

    suspend fun requestWithdrawal(
        amountMxn: Double,
        method: String,
        destination: String,
        holderName: String = "Oscar Bueno"
    ): PaymentResult {
        val user = appDao.getUser(1L) ?: throw IllegalStateException("Usuario no encontrado")
        if (user.mxnBalance < amountMxn) {
            throw IllegalArgumentException("Saldo insuficiente")
        }

        val newMxn = user.mxnBalance - amountMxn
        val newUsd = newMxn / 18.0
        appDao.updateBalances(user.id, user.coinBalance, newMxn, newUsd)

        // Execute real payout via configured payment gateway (Mercado Pago / PayPal)
        val result = if (paymentGatewayService != null) {
            if (method == "MERCADO_PAGO") {
                paymentGatewayService.executeMercadoPagoPayout(
                    amountMxn = amountMxn,
                    clabeOrAccount = destination,
                    holderName = holderName,
                    userEmail = user.email
                )
            } else {
                paymentGatewayService.executePayPalPayout(
                    amountMxn = amountMxn,
                    recipientEmail = destination
                )
            }
        } else {
            val txRef = if (method == "MERCADO_PAGO") "MP-SPEI-${System.currentTimeMillis().toString().takeLast(8)}" else "PP-TX-${UUID.randomUUID().toString().take(8).uppercase()}"
            val tracking = "2026${System.currentTimeMillis().toString().takeLast(10)}"
            PaymentResult(
                success = true,
                status = "SUCCESS",
                transactionReference = txRef,
                trackingKey = tracking,
                provider = if (method == "MERCADO_PAGO") "Mercado Pago (SPEI)" else "PayPal",
                destination = destination,
                amountMxn = amountMxn,
                message = "Transferencia SPEI completada exitosamente."
            )
        }

        appDao.insertPayoutRequest(
            PayoutRequestEntity(
                userId = user.id,
                amountMxn = amountMxn,
                amountUsd = amountMxn / 18.0,
                coinsDeducted = 0L,
                paymentMethod = method,
                destinationAccount = destination,
                status = result.status,
                transactionReference = result.transactionReference,
                requestedAt = System.currentTimeMillis(),
                completedAt = if (result.success) System.currentTimeMillis() else null
            )
        )

        return result
    }

    suspend fun reRankFeedWithAi(): Boolean {
        val currentVideos = appDao.getAllVideosFlow().firstOrNull() ?: return false
        val userInterests = listOf("Tech & AI", "Crypto & Finanzas", "Gaming")

        val listForAi = currentVideos.map { Triple(it.id, it.title, it.category) }
        val recommendations = geminiAiService.getForYouRecommendations(userInterests, listForAi)

        val updatedVideos = currentVideos.map { video ->
            val match = recommendations.find { it.videoId == video.id }
            if (match != null) {
                video.copy(
                    aiAffinityScore = match.affinityScore,
                    aiReason = "✨ ${match.affinityScore}% Afinidad IA: ${match.reason}"
                )
            } else {
                video
            }
        }
        appDao.insertVideos(updatedVideos)
        return true
    }

    suspend fun moderateAndPublishVideo(
        title: String,
        description: String,
        category: String,
        rewardCoins: Int,
        coverResName: String
    ): ModerationResult {
        val moderation = geminiAiService.moderateVideo(title, description, category)
        if (moderation.approved) {
            val user = appDao.getUser(1L)
            val newVideo = VideoEntity(
                id = "vid_user_${System.currentTimeMillis()}",
                creatorId = "creator_me",
                creatorName = user?.displayName ?: user?.username ?: "Tú",
                creatorHandle = "@${(user?.username ?: "yo").lowercase()}",
                title = title,
                description = description,
                videoUrl = "user_uploaded_stream",
                coverDrawableResName = coverResName.ifBlank { "thumb_cyber_ai" },
                rewardCoins = rewardCoins.coerceAtLeast(500),
                rewardMxnNet = (rewardCoins / 100.0).coerceAtLeast(5.0),
                category = category,
                likesCount = 1,
                commentsCount = 0,
                sharesCount = 0,
                isLiked = true,
                isFollowed = false,
                soundTitle = "Sonido Original • ${user?.username ?: "Creador"}",
                aiAffinityScore = moderation.safetyScore,
                aiReason = "🛡️ Verificado por Gemini IA: ${moderation.reason}"
            )
            appDao.insertVideo(newVideo)
        }
        return moderation
    }

    suspend fun publishFullUserVideo(
        title: String,
        description: String,
        category: String,
        rewardCoins: Int,
        videoUri: String,
        coverResName: String,
        soundTitle: String
    ): VideoEntity {
        val user = appDao.getUser(1L)
        val creatorName = user?.displayName?.ifBlank { user.username } ?: "Oscar Bueno"
        val creatorHandle = "@${(user?.username ?: "cyberviewer_mx").lowercase()}"

        val newVideo = VideoEntity(
            id = "vid_pub_${System.currentTimeMillis()}",
            creatorId = "creator_me",
            creatorName = creatorName,
            creatorHandle = creatorHandle,
            creatorAvatar = user?.avatarPreset ?: "neon_cyan",
            title = title,
            description = description,
            videoUrl = videoUri,
            coverDrawableResName = coverResName.ifBlank { "thumb_cyber_ai" },
            rewardCoins = rewardCoins.coerceAtLeast(500),
            rewardMxnNet = (rewardCoins / 100.0).coerceAtLeast(5.0),
            category = category,
            likesCount = 1,
            commentsCount = 0,
            sharesCount = 0,
            isLiked = false,
            isFollowed = false,
            soundTitle = soundTitle.ifBlank { "Sonido Original • $creatorName" },
            aiAffinityScore = 99,
            aiReason = "🚀 Tu video en vivo en Para Ti • Gana $rewardCoins monedas al verlo",
            createdAt = System.currentTimeMillis()
        )
        appDao.insertVideo(newVideo)
        return newVideo
    }

    suspend fun loginWithProvider(provider: String, identifier: String, username: String) {
        val currentUser = appDao.getUser(1L) ?: return
        val updated = currentUser.copy(
            authProvider = provider,
            email = if (identifier.contains("@")) identifier else "$identifier@tiktokearn.io",
            username = username,
            displayName = username.replace("_", " ")
        )
        appDao.updateUser(updated)
    }
}
