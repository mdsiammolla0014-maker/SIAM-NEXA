package com.example.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val name: String,
    val username: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val balance: Double = 0.0, // Initial balance 0.00 BDT
    val isAdmin: Boolean = false,
    val isBanned: Boolean = false,
    val hasClaimedSpin: Boolean = false, // Has completed the 5 BDT welcome spin
    val referredBy: String = "", // Referrer's username
    val referralCount: Int = 0, // Number of referred users
    val referralEarnings: Double = 0.0, // Total BDT earned from referrals (5 BDT per ref)
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val jobId: Long = 0,
    val title: String,
    val category: String, // YouTube, Facebook, Telegram, TikTok, Web, Other
    val description: String,
    val taskLink: String,
    val proofRequirement: String,
    val rewardPerTask: Double, // e.g. 0.50 BDT, 1.00 BDT
    val targetWorkers: Int,
    val completedWorkers: Int = 0,
    val creatorUserId: Long,
    val creatorEmail: String,
    val status: String = "ACTIVE", // ACTIVE, PAUSED, COMPLETED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val submissionId: Long = 0,
    val jobId: Long,
    val jobTitle: String,
    val workerUserId: Long,
    val workerEmail: String,
    val workerUsername: String = "",
    val proofText: String,
    val proofImageUri: String? = null,
    val rewardAmount: Double,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey(autoGenerate = true)
    val depositId: Long = 0,
    val userId: Long,
    val userEmail: String,
    val username: String = "",
    val paymentMethod: String, // bKash or Nagad
    val accountNumber: String = "01786482309",
    val senderNumber: String,
    val transactionId: String,
    val amount: Double,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "withdrawals")
data class WithdrawEntity(
    @PrimaryKey(autoGenerate = true)
    val withdrawId: Long = 0,
    val userId: Long,
    val userEmail: String,
    val username: String = "",
    val paymentMethod: String, // bKash, Nagad, or Rocket
    val receiverNumber: String,
    val amount: Double, // Min 120 BDT
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val createdAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null
)

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey(autoGenerate = true)
    val newsId: Long = 0,
    val content: String,
    val priority: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
