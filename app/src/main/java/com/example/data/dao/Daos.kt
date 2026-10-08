package com.example.data.dao

import androidx.room.*
import com.example.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :id")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(username) = LOWER(:username) LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:identifier) OR LOWER(username) = LOWER(:identifier) LIMIT 1")
    suspend fun getUserByEmailOrUsername(identifier: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE LOWER(referredBy) = LOWER(:username) ORDER BY createdAt DESC")
    fun getUsersReferredBy(username: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET balance = :balance WHERE userId = :userId")
    suspend fun updateBalance(userId: Long, balance: Double)

    @Query("UPDATE users SET hasClaimedSpin = :claimed, balance = :newBalance WHERE userId = :userId")
    suspend fun updateSpinClaimed(userId: Long, claimed: Boolean, newBalance: Double)

    @Query("UPDATE users SET referralCount = referralCount + 1, referralEarnings = referralEarnings + :bonus, balance = balance + :bonus WHERE LOWER(username) = LOWER(:referrerUsername)")
    suspend fun rewardReferrer(referrerUsername: String, bonus: Double = 5.0)

    @Query("UPDATE users SET password = :newPassword WHERE userId = :userId")
    suspend fun updatePassword(userId: Long, newPassword: String)

    @Query("UPDATE users SET isBanned = :banned WHERE userId = :userId")
    suspend fun setBanned(userId: Long, banned: Boolean)

    @Delete
    suspend fun deleteUser(user: UserEntity)
}

@Dao
interface JobDao {
    @Query("SELECT * FROM jobs WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs ORDER BY createdAt DESC")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE jobId = :id")
    suspend fun getJobById(id: Long): JobEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Update
    suspend fun updateJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)

    @Query("UPDATE jobs SET completedWorkers = completedWorkers + 1 WHERE jobId = :jobId")
    suspend fun incrementCompleted(jobId: Long)
}

@Dao
interface SubmissionDao {
    @Query("SELECT * FROM submissions ORDER BY submittedAt DESC")
    fun getAllSubmissions(): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE workerUserId = :workerId ORDER BY submittedAt DESC")
    fun getSubmissionsByWorker(workerId: Long): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE jobId = :jobId ORDER BY submittedAt DESC")
    fun getSubmissionsByJob(jobId: Long): Flow<List<SubmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity): Long

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)
}

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY createdAt DESC")
    fun getAllDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDepositsByUser(userId: Long): Flow<List<DepositEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity): Long

    @Update
    suspend fun updateDeposit(deposit: DepositEntity)
}

@Dao
interface WithdrawDao {
    @Query("SELECT * FROM withdrawals ORDER BY createdAt DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawEntity>>

    @Query("SELECT * FROM withdrawals WHERE userId = :userId ORDER BY createdAt DESC")
    fun getWithdrawalsByUser(userId: Long): Flow<List<WithdrawEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawEntity): Long

    @Update
    suspend fun updateWithdrawal(withdrawal: WithdrawEntity)
}

@Dao
interface NewsDao {
    @Query("SELECT * FROM news WHERE isActive = 1 ORDER BY priority DESC, createdAt DESC")
    fun getActiveNews(): Flow<List<NewsEntity>>

    @Query("SELECT * FROM news ORDER BY createdAt DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNews(news: NewsEntity): Long

    @Update
    suspend fun updateNews(news: NewsEntity)

    @Delete
    suspend fun deleteNews(news: NewsEntity)
}
