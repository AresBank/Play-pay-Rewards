package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: Long = 1L): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: Long = 1L): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET displayName = :displayName, username = :username, bio = :bio, avatarPreset = :avatarPreset WHERE id = :userId")
    suspend fun updateUserProfile(userId: Long = 1L, displayName: String, username: String, bio: String, avatarPreset: String)

    @Query("UPDATE users SET followingCount = followingCount + :delta WHERE id = :userId")
    suspend fun updateFollowingCount(userId: Long = 1L, delta: Int)

    @Query("UPDATE users SET coinBalance = coinBalance + :coins WHERE id = :userId")
    suspend fun addCoins(userId: Long = 1L, coins: Long)

    @Query("UPDATE users SET coinBalance = :coins, mxnBalance = :mxn, usdBalance = :usd WHERE id = :userId")
    suspend fun updateBalances(userId: Long = 1L, coins: Long, mxn: Double, usd: Double)

    // Videos
    @Query("SELECT * FROM videos ORDER BY aiAffinityScore DESC, createdAt DESC")
    fun getAllVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isFollowed = 1 ORDER BY createdAt DESC")
    fun getFollowingVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY createdAt DESC")
    fun getLikedVideosFlow(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE category = :category ORDER BY aiAffinityScore DESC")
    fun getVideosByCategoryFlow(category: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE creatorId = :creatorId ORDER BY createdAt DESC")
    fun getVideosByCreatorFlow(creatorId: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :videoId LIMIT 1")
    suspend fun getVideoById(videoId: String): VideoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :videoId")
    suspend fun toggleLike(videoId: String, isLiked: Boolean, delta: Int)

    @Query("UPDATE videos SET isFollowed = :isFollowed WHERE creatorId = :creatorId")
    suspend fun toggleFollow(creatorId: String, isFollowed: Boolean)

    @Query("UPDATE videos SET sharesCount = sharesCount + 1 WHERE id = :videoId")
    suspend fun incrementShareCount(videoId: String)

    @Query("UPDATE videos SET commentsCount = commentsCount + :delta WHERE id = :videoId")
    suspend fun updateVideoCommentsCount(videoId: String, delta: Int)

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int

    // Comments
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY createdAt DESC")
    fun getCommentsFlow(videoId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE videoId = :videoId")
    suspend fun getCommentCountForVideo(videoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: String)

    @Query("UPDATE comments SET isLiked = :isLiked, likesCount = likesCount + :delta WHERE id = :commentId")
    suspend fun toggleCommentLike(commentId: String, isLiked: Boolean, delta: Int)

    // Video Views
    @Query("SELECT * FROM video_views WHERE userId = :userId AND videoId = :videoId LIMIT 1")
    suspend fun getView(userId: Long, videoId: String): VideoViewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertView(view: VideoViewEntity)

    // Payment Methods
    @Query("SELECT * FROM user_payment_methods WHERE userId = :userId")
    fun getPaymentMethodsFlow(userId: Long = 1L): Flow<List<PaymentMethodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaymentMethod(method: PaymentMethodEntity)

    // Payout Requests
    @Query("SELECT * FROM payout_requests WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getPayoutRequestsFlow(userId: Long = 1L): Flow<List<PayoutRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayoutRequest(request: PayoutRequestEntity): Long

    @Query("UPDATE payout_requests SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updatePayoutStatus(id: Long, status: String, completedAt: Long?)

    // Referrals ($200 MXN bonus per registered invitation)
    @Query("SELECT * FROM referrals WHERE referrerUserId = :userId ORDER BY registeredAt DESC")
    fun getReferralsFlow(userId: Long = 1L): Flow<List<ReferralEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReferral(referral: ReferralEntity): Long

    @Query("SELECT COUNT(*) FROM referrals WHERE referrerUserId = :userId")
    suspend fun getReferralsCount(userId: Long = 1L): Int

    @Query("UPDATE users SET mxnBalance = mxnBalance + :mxnDelta, usdBalance = usdBalance + :usdDelta, referralsCount = referralsCount + 1, referralEarningsMxn = referralEarningsMxn + :mxnDelta WHERE id = :userId")
    suspend fun addReferralReward(userId: Long = 1L, mxnDelta: Double = 200.0, usdDelta: Double = 11.11)

    @Query("UPDATE users SET mxnBalance = mxnBalance + :mxnDelta, usdBalance = usdBalance + :usdDelta WHERE id = :userId")
    suspend fun addMxnBalance(userId: Long = 1L, mxnDelta: Double, usdDelta: Double)
}
