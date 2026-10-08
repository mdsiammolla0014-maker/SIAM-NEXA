package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.*
import com.example.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        JobEntity::class,
        SubmissionEntity::class,
        DepositEntity::class,
        WithdrawEntity::class,
        NewsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun depositDao(): DepositDao
    abstract fun withdrawDao(): WithdrawDao
    abstract fun newsDao(): NewsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "siam_nexa_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database)
                    }
                }
            }

            suspend fun populateInitialData(database: AppDatabase) {
                val userDao = database.userDao()
                val jobDao = database.jobDao()
                val newsDao = database.newsDao()

                // Insert Admin
                userDao.insertUser(
                    UserEntity(
                        userId = 1,
                        name = "Siam Molla (Admin)",
                        username = "mdsiammolla0014",
                        email = "mdsiammolla0014@gmail.com",
                        password = "MDSIAMMOLLA1234@@@@",
                        phone = "01786482309",
                        balance = 5000.0,
                        isAdmin = true
                    )
                )

                // Insert Starter Micro Jobs
                jobDao.insertJob(
                    JobEntity(
                        title = "Subscribe SIAM NEXA YouTube & Like Latest Video",
                        category = "YouTube",
                        description = "Go to the official SIAM NEXA YouTube channel, hit Subscribe, watch any video for at least 30 seconds, and give a Like.",
                        taskLink = "https://youtube.com/@siamnexa?si=seEETG1fUPp_BmXD",
                        proofRequirement = "Submit your YouTube channel name / handle and screenshot of subscription.",
                        rewardPerTask = 1.50,
                        targetWorkers = 100,
                        completedWorkers = 12,
                        creatorUserId = 1,
                        creatorEmail = "mdsiammolla0014@gmail.com"
                    )
                )
                jobDao.insertJob(
                    JobEntity(
                        title = "Join SIAM NEXA Official Telegram Channel",
                        category = "Telegram",
                        description = "Join our official Telegram community for daily payment proofs, instant notices, and task updates.",
                        taskLink = "https://t.me/Siamnexa",
                        proofRequirement = "Provide your Telegram @username and confirmation screenshot.",
                        rewardPerTask = 1.00,
                        targetWorkers = 200,
                        completedWorkers = 45,
                        creatorUserId = 1,
                        creatorEmail = "mdsiammolla0014@gmail.com"
                    )
                )
                jobDao.insertJob(
                    JobEntity(
                        title = "Follow @siamnexa on TikTok & Like 3 Videos",
                        category = "TikTok",
                        description = "Open the TikTok profile, follow @siamnexa, and leave a like on the top 3 videos.",
                        taskLink = "https://www.tiktok.com/@siamnexa?_r=1&_t=ZS-9ANxW4sNqhp",
                        proofRequirement = "Submit your TikTok account username.",
                        rewardPerTask = 1.00,
                        targetWorkers = 150,
                        completedWorkers = 28,
                        creatorUserId = 1,
                        creatorEmail = "mdsiammolla0014@gmail.com"
                    )
                )
                jobDao.insertJob(
                    JobEntity(
                        title = "Follow Official Facebook Page & Share Post",
                        category = "Facebook",
                        description = "Like & follow our official Facebook page. Share the pinned announcement post to your profile.",
                        taskLink = "https://www.facebook.com/share/1FNKrBQbTv/",
                        proofRequirement = "Submit your Facebook profile link / username.",
                        rewardPerTask = 1.20,
                        targetWorkers = 100,
                        completedWorkers = 19,
                        creatorUserId = 1,
                        creatorEmail = "mdsiammolla0014@gmail.com"
                    )
                )
                jobDao.insertJob(
                    JobEntity(
                        title = "Watch Short Video & Comment with Username",
                        category = "YouTube",
                        description = "Watch the recommended YouTube short video completely, leave a positive comment, and like the video.",
                        taskLink = "https://youtube.com/@siamnexa?si=seEETG1fUPp_BmXD",
                        proofRequirement = "Submit the exact comment you posted.",
                        rewardPerTask = 0.50,
                        targetWorkers = 250,
                        completedWorkers = 73,
                        creatorUserId = 1,
                        creatorEmail = "mdsiammolla0014@gmail.com"
                    )
                )

                // Insert Initial News Updates
                newsDao.insertNews(
                    NewsEntity(
                        content = "📢 স্বাগতম SIAM NEXA তে! লাইক, কমেন্ট ও সাবস্ক্রাইব করে প্রতিদিন ১০০-৫০০ টাকা আয় করুন।",
                        priority = 10
                    )
                )
                newsDao.insertNews(
                    NewsEntity(
                        content = "💰 পেমেন্ট আপডেট: একাউন্টে ১২০ টাকা হলেই বিকাশ, নগদ বা রকেটের মাধ্যমে টাকা উইথড্র করতে পারবেন।",
                        priority = 9
                    )
                )
                newsDao.insertNews(
                    NewsEntity(
                        content = "🔥 মাত্র ১০ টাকা ডিপোজিট করে নিজের মাইক্রো-জব পোস্ট করুন! পেমেন্ট নাম্বার: 01786482309 (bKash & Nagad)",
                        priority = 8
                    )
                )
                newsDao.insertNews(
                    NewsEntity(
                        content = "🚀 অফিশিয়াল টেলিগ্রাম ও ইউটিউবে জয়েন থাকুন দ্রুত পেমেন্ট প্রুফ ও নতুন কাজের নোটিফিকেশন পেতে।",
                        priority = 7
                    )
                )
            }
        }
    }
}
