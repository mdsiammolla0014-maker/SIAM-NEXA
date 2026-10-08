package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class AppScreen {
    object Jobs : AppScreen()
    object PostJob : AppScreen()
    object Deposit : AppScreen()
    object Withdraw : AppScreen()
    object MyTasks : AppScreen()
    object News : AppScreen()
    object Referrals : AppScreen()
    object AdminPanel : AppScreen()
    object Profile : AppScreen()
}

sealed class AuthResult {
    object Success : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val userDao = database.userDao()
    private val jobDao = database.jobDao()
    private val submissionDao = database.submissionDao()
    private val depositDao = database.depositDao()
    private val withdrawDao = database.withdrawDao()
    private val newsDao = database.newsDao()

    private val _currentScreen = MutableStateFlow<AppScreen>(AppScreen.Jobs)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Live update current user from DB whenever userId is set
    init {
        viewModelScope.launch {
            _currentUser.flatMapLatest { user ->
                if (user != null) {
                    userDao.getUserById(user.userId)
                } else {
                    flowOf(null)
                }
            }.collect { updatedUser ->
                if (updatedUser != null) {
                    _currentUser.value = updatedUser
                }
            }
        }
    }

    // Data streams
    val activeJobs = jobDao.getActiveJobs().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allJobs = jobDao.getAllJobs().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val activeNews = newsDao.getActiveNews().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allNews = newsDao.getAllNews().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allUsers = userDao.getAllUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allDeposits = depositDao.getAllDeposits().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allWithdrawals = withdrawDao.getAllWithdrawals().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allSubmissions = submissionDao.getAllSubmissions().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val mySubmissions = _currentUser.flatMapLatest { user ->
        if (user != null) submissionDao.getSubmissionsByWorker(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val myDeposits = _currentUser.flatMapLatest { user ->
        if (user != null) depositDao.getDepositsByUser(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val myWithdrawals = _currentUser.flatMapLatest { user ->
        if (user != null) withdrawDao.getWithdrawalsByUser(user.userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    // --- Authentication ---
    suspend fun login(identifier: String, pass: String): AuthResult {
        val cleanIdentifier = identifier.trim()
        val cleanPass = pass.trim()

        if (cleanIdentifier.isEmpty() || cleanPass.isEmpty()) {
            return AuthResult.Error("ইমেইল/ইউজার নেম এবং পাসওয়ার্ড দিন")
        }

        // Check Admin credentials
        if (cleanIdentifier.equals("mdsiammolla0014@gmail.com", ignoreCase = true) ||
            cleanIdentifier.equals("mdsiammolla0014", ignoreCase = true)
        ) {
            if (cleanPass == "MDSIAMMOLLA1234@@@@") {
                val adminInDb = userDao.getUserByEmailOrUsername(cleanIdentifier)
                if (adminInDb != null) {
                    if (adminInDb.isBanned) return AuthResult.Error("এই একাউন্টটি ব্যান করা হয়েছে!")
                    _currentUser.value = adminInDb
                    return AuthResult.Success
                } else {
                    val adminId = userDao.insertUser(
                        UserEntity(
                            name = "Siam Molla (Admin)",
                            username = "mdsiammolla0014",
                            email = "mdsiammolla0014@gmail.com",
                            password = cleanPass,
                            phone = "01786482309",
                            balance = 5000.0,
                            isAdmin = true,
                            hasClaimedSpin = true
                        )
                    )
                    _currentUser.value = UserEntity(
                        userId = adminId,
                        name = "Siam Molla (Admin)",
                        username = "mdsiammolla0014",
                        email = "mdsiammolla0014@gmail.com",
                        password = cleanPass,
                        phone = "01786482309",
                        balance = 5000.0,
                        isAdmin = true,
                        hasClaimedSpin = true
                    )
                    return AuthResult.Success
                }
            }
        }

        val user = userDao.getUserByEmailOrUsername(cleanIdentifier)
        if (user == null) {
            return AuthResult.Error("এই ইমেইল বা ইউজার নেম খুঁজে পাওয়া যায়নি")
        }

        if (user.isBanned) {
            return AuthResult.Error("আপনার একাউন্টটি ব্যান করা রয়েছে। এডমিনের সাথে যোগাযোগ করুন।")
        }

        if (user.password != cleanPass) {
            return AuthResult.Error("ভুল পাসওয়ার্ড! অনুগ্রহ করে আবার চেষ্টা করুন।")
        }

        _currentUser.value = user
        return AuthResult.Success
    }

    suspend fun register(
        name: String,
        username: String,
        email: String,
        pass: String,
        phone: String,
        referralCode: String = ""
    ): AuthResult {
        val cleanName = name.trim()
        val cleanUsername = username.trim().lowercase()
        val cleanEmail = email.trim().lowercase()
        val cleanPass = pass.trim()
        val cleanPhone = phone.trim()
        val cleanReferral = referralCode.trim().lowercase()

        if (cleanName.length < 2) return AuthResult.Error("সঠিক নাম লিখুন")
        if (cleanUsername.length < 3) return AuthResult.Error("ইউজার নেম কমপক্ষে ৩ অক্ষরের হতে হবে")
        if (!cleanEmail.contains("@") || !cleanEmail.contains(".")) return AuthResult.Error("সঠিক ইমেইল ঠিকানা দিন")
        if (cleanPass.length < 4) return AuthResult.Error("পাসওয়ার্ড কমপক্ষে ৪ অক্ষরের হতে হবে")

        val existingEmail = userDao.getUserByEmail(cleanEmail)
        if (existingEmail != null) return AuthResult.Error("এই ইমেইল দিয়ে ইতোমধ্যে একটি একাউন্ট তৈরি আছে")

        val existingUser = userDao.getUserByUsername(cleanUsername)
        if (existingUser != null) return AuthResult.Error("এই ইউজার নেমটি ইতোমধ্যে নেওয়া হয়ে গেছে")

        val isAdmin = cleanEmail.equals("mdsiammolla0014@gmail.com", ignoreCase = true)

        var validatedReferrer = ""
        if (cleanReferral.isNotBlank() && cleanReferral != cleanUsername) {
            val referrer = userDao.getUserByUsername(cleanReferral)
            if (referrer != null) {
                validatedReferrer = referrer.username
                // Reward referrer 5 BDT
                userDao.rewardReferrer(referrer.username, 5.0)
            }
        }

        val newUserId = userDao.insertUser(
            UserEntity(
                name = cleanName,
                username = cleanUsername,
                email = cleanEmail,
                password = cleanPass,
                phone = cleanPhone,
                balance = 0.0, // Initial balance 0.00 BDT as requested
                isAdmin = isAdmin,
                hasClaimedSpin = false, // Will be prompted to spin
                referredBy = validatedReferrer
            )
        )

        val newUser = UserEntity(
            userId = newUserId,
            name = cleanName,
            username = cleanUsername,
            email = cleanEmail,
            password = cleanPass,
            phone = cleanPhone,
            balance = 0.0,
            isAdmin = isAdmin,
            hasClaimedSpin = false,
            referredBy = validatedReferrer
        )
        _currentUser.value = newUser
        return AuthResult.Success
    }

    suspend fun forgotPassword(identifier: String, newPass: String): Boolean {
        val user = userDao.getUserByEmailOrUsername(identifier.trim()) ?: return false
        userDao.updatePassword(user.userId, newPass.trim())
        return true
    }

    fun logout() {
        _currentUser.value = null
        _currentScreen.value = AppScreen.Jobs
    }

    // --- Welcome Spin Claim (Guaranteed 5 BDT) ---
    fun claimSpinBonus(amount: Double = 5.0) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val newBal = user.balance + amount
            userDao.updateSpinClaimed(user.userId, true, newBal)
        }
    }

    // --- Job Actions ---
    fun postJob(
        title: String,
        category: String,
        description: String,
        taskLink: String,
        proofRequirement: String,
        rewardPerTask: Double,
        workers: Int,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "দয়া করে প্রথমে লগইন করুন")
            return
        }

        val totalCost = rewardPerTask * workers
        if (user.balance < totalCost) {
            onComplete(
                false,
                "পর্যাপ্ত ব্যালেন্স নেই! মোট খরচ: ৳${String.format("%.2f", totalCost)} BDT, আপনার ব্যালেন্স: ৳${String.format("%.2f", user.balance)} BDT। অনুগ্রহ করে ডিপোজিট করুন।"
            )
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val newBal = user.balance - totalCost
            userDao.updateBalance(user.userId, newBal)

            jobDao.insertJob(
                JobEntity(
                    title = title.trim(),
                    category = category,
                    description = description.trim(),
                    taskLink = taskLink.trim(),
                    proofRequirement = proofRequirement.trim(),
                    rewardPerTask = rewardPerTask,
                    targetWorkers = workers,
                    completedWorkers = 0,
                    creatorUserId = user.userId,
                    creatorEmail = user.email
                )
            )

            launch(Dispatchers.Main) {
                onComplete(true, "মাইক্রো-জব সফলভাবে পোস্ট করা হয়েছে!")
            }
        }
    }

    fun submitJobProof(
        job: JobEntity,
        proofText: String,
        proofImageUri: String?,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "দয়া করে লগইন করুন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            submissionDao.insertSubmission(
                SubmissionEntity(
                    jobId = job.jobId,
                    jobTitle = job.title,
                    workerUserId = user.userId,
                    workerEmail = user.email,
                    workerUsername = user.username,
                    proofText = proofText.trim(),
                    proofImageUri = proofImageUri,
                    rewardAmount = job.rewardPerTask,
                    status = "PENDING"
                )
            )

            launch(Dispatchers.Main) {
                onComplete(true, "কাজের প্রুফ জমা দেওয়া হয়েছে! যাচাই শেষে আপনার একাউন্টে ৳${job.rewardPerTask} BDT যোগ হবে।")
            }
        }
    }

    // --- Deposit Actions ---
    fun submitDeposit(
        paymentMethod: String,
        senderNumber: String,
        trxId: String,
        amount: Double,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "দয়া করে লগইন করুন")
            return
        }

        if (amount < 10.0) {
            onComplete(false, "সর্বনিম্ন ডিপোজিট পরিমাণ ১০ টাকা")
            return
        }

        if (senderNumber.trim().length < 11) {
            onComplete(false, "সঠিক ১১ ডিজিটের মোবাইল নাম্বার দিন")
            return
        }

        if (trxId.trim().length < 6) {
            onComplete(false, "সঠিক ট্রানজেকশন আইডি (TrxID) দিন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            depositDao.insertDeposit(
                DepositEntity(
                    userId = user.userId,
                    userEmail = user.email,
                    username = user.username,
                    paymentMethod = paymentMethod,
                    accountNumber = "01786482309",
                    senderNumber = senderNumber.trim(),
                    transactionId = trxId.trim().uppercase(),
                    amount = amount,
                    status = "PENDING"
                )
            )

            launch(Dispatchers.Main) {
                onComplete(true, "ডিপোজিট রিকোয়েস্ট জমা হয়েছে! এডমিন ভেরিফাই করে ব্যালেন্স যোগ করবেন।")
            }
        }
    }

    // --- Withdrawal Actions (Min 120 BDT) ---
    fun submitWithdrawal(
        paymentMethod: String,
        receiverNumber: String,
        amount: Double,
        onComplete: (Boolean, String) -> Unit
    ) {
        val user = _currentUser.value ?: run {
            onComplete(false, "দয়া করে লগইন করুন")
            return
        }

        if (user.balance < 120.0) {
            onComplete(false, "টাকা তোলার জন্য আপনার একাউন্টে সর্বনিম্ন ১২০ টাকা থাকতে হবে! বর্তমান ব্যালেন্স: ৳${String.format("%.2f", user.balance)}")
            return
        }

        if (amount < 120.0) {
            onComplete(false, "সর্বনিম্ন উত্তোলন ১২০ টাকা হতে হবে!")
            return
        }

        if (amount > user.balance) {
            onComplete(false, "আপনার একাউন্টে পর্যাপ্ত টাকা নেই!")
            return
        }

        if (receiverNumber.trim().length < 11) {
            onComplete(false, "সঠিক ১১ ডিজিটের মোবাইল নাম্বার দিন")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateBalance(user.userId, user.balance - amount)

            withdrawDao.insertWithdrawal(
                WithdrawEntity(
                    userId = user.userId,
                    userEmail = user.email,
                    username = user.username,
                    paymentMethod = paymentMethod,
                    receiverNumber = receiverNumber.trim(),
                    amount = amount,
                    status = "PENDING"
                )
            )

            launch(Dispatchers.Main) {
                onComplete(true, "উইথড্র রিকোয়েস্ট সফলভাবে পাঠানো হয়েছে! ২৪ ঘণ্টার মধ্যে $paymentMethod নাম্বারে টাকা পাঠিয়ে দেওয়া হবে।")
            }
        }
    }

    fun grantAdReward(amount: Double = 0.20) {
        val user = _currentUser.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateBalance(user.userId, user.balance + amount)
        }
    }

    // --- Admin Operations ---
    fun approveDeposit(deposit: DepositEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            depositDao.updateDeposit(deposit.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis()))
            val targetUser = userDao.getUserByEmailOrUsername(deposit.userEmail)
            if (targetUser != null) {
                userDao.updateBalance(targetUser.userId, targetUser.balance + deposit.amount)
            }
        }
    }

    fun rejectDeposit(deposit: DepositEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            depositDao.updateDeposit(deposit.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis()))
        }
    }

    fun approveSubmission(submission: SubmissionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            submissionDao.updateSubmission(submission.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis()))
            jobDao.incrementCompleted(submission.jobId)
            val worker = userDao.getUserByEmailOrUsername(submission.workerEmail)
            if (worker != null) {
                userDao.updateBalance(worker.userId, worker.balance + submission.rewardAmount)
            }
        }
    }

    fun rejectSubmission(submission: SubmissionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            submissionDao.updateSubmission(submission.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis()))
        }
    }

    fun approveWithdrawal(withdrawal: WithdrawEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            withdrawDao.updateWithdrawal(withdrawal.copy(status = "APPROVED", reviewedAt = System.currentTimeMillis()))
        }
    }

    fun rejectWithdrawal(withdrawal: WithdrawEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            withdrawDao.updateWithdrawal(withdrawal.copy(status = "REJECTED", reviewedAt = System.currentTimeMillis()))
            val targetUser = userDao.getUserByEmailOrUsername(withdrawal.userEmail)
            if (targetUser != null) {
                userDao.updateBalance(targetUser.userId, targetUser.balance + withdrawal.amount)
            }
        }
    }

    fun adjustUserBalance(userId: Long, newBalance: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.updateBalance(userId, newBalance)
        }
    }

    fun toggleUserBan(userId: Long, currentBanStatus: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.setBanned(userId, !currentBanStatus)
        }
    }

    fun deleteUser(user: UserEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            userDao.deleteUser(user)
        }
    }

    fun addNews(content: String, priority: Int = 1) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.insertNews(
                NewsEntity(content = content.trim(), priority = priority, isActive = true)
            )
        }
    }

    fun deleteNews(news: NewsEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            newsDao.deleteNews(news)
        }
    }

    fun deleteJob(job: JobEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            jobDao.deleteJob(job)
        }
    }
}
