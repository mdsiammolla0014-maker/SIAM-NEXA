package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.*
import com.example.ui.MainViewModel

@Composable
fun AdminPanelScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allUsers by viewModel.allUsers.collectAsState()
    val allDeposits by viewModel.allDeposits.collectAsState()
    val allWithdrawals by viewModel.allWithdrawals.collectAsState()
    val allSubmissions by viewModel.allSubmissions.collectAsState()
    val allNews by viewModel.allNews.collectAsState()
    val allJobs by viewModel.allJobs.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ইউজার (${allUsers.size})", "ডিপোজিট (${allDeposits.count { it.status == "PENDING" }})", "উইথড্র (${allWithdrawals.count { it.status == "PENDING" }})", "সাবমিশন", "নোটিশ", "জব")

    var userForBalanceAdjust by remember { mutableStateOf<UserEntity?>(null) }
    var showAddNewsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_panel_screen")
    ) {
        // Admin Header
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = Color.White
                    )
                    Text(
                        text = "SIAM NEXA এডমিন কন্ট্রোল প্যানেল",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
                Text(
                    text = "ইউজার কন্ট্রোল, ব্যালেন্স অ্যাডজাস্ট, ডিপোজিট ও টাস্ক অনুমোদন",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.5.sp
                    )
                )
            }
        }

        // Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.5.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    // Users Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 70.dp)
                    ) {
                        items(allUsers, key = { it.userId }) { user ->
                            AdminUserCard(
                                user = user,
                                onAdjustBalance = { userForBalanceAdjust = user },
                                onToggleBan = {
                                    viewModel.toggleUserBan(user.userId, user.isBanned)
                                    val status = if (user.isBanned) "আনব্যান" else "ব্যান"
                                    Toast.makeText(context, "${user.name} $status করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                onDelete = {
                                    viewModel.deleteUser(user)
                                    Toast.makeText(context, "${user.name} মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                1 -> {
                    // Deposits Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 70.dp)
                    ) {
                        if (allDeposits.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("কোনো ডিপোজিট রিকোয়েস্ট নেই")
                                }
                            }
                        } else {
                            items(allDeposits, key = { it.depositId }) { dep ->
                                AdminDepositCard(
                                    deposit = dep,
                                    onApprove = {
                                        viewModel.approveDeposit(dep)
                                        Toast.makeText(context, "ডিপোজিট অনুমোদিত ও ব্যালেন্স যোগ হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    onReject = {
                                        viewModel.rejectDeposit(dep)
                                        Toast.makeText(context, "ডিপোজিট বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Withdrawals Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 70.dp)
                    ) {
                        if (allWithdrawals.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("কোনো উইথড্র রিকোয়েস্ট নেই")
                                }
                            }
                        } else {
                            items(allWithdrawals, key = { it.withdrawId }) { w ->
                                AdminWithdrawCard(
                                    withdrawal = w,
                                    onApprove = {
                                        viewModel.approveWithdrawal(w)
                                        Toast.makeText(context, "উইথড্র অনুমোদিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    onReject = {
                                        viewModel.rejectWithdrawal(w)
                                        Toast.makeText(context, "উইথড্র বাতিল এবং ব্যালেন্স ফেরত দেওয়া হয়েছে", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                3 -> {
                    // Submissions Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 70.dp)
                    ) {
                        if (allSubmissions.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("কোনো সাবমিশন জমা নেই")
                                }
                            }
                        } else {
                            items(allSubmissions, key = { it.submissionId }) { sub ->
                                AdminSubmissionCard(
                                    sub = sub,
                                    onApprove = {
                                        viewModel.approveSubmission(sub)
                                        Toast.makeText(context, "টাস্ক অনুমোদিত এবং কর্মীকে ৳${sub.rewardAmount} দেওয়া হয়েছে!", Toast.LENGTH_SHORT).show()
                                    },
                                    onReject = {
                                        viewModel.rejectSubmission(sub)
                                        Toast.makeText(context, "টাস্ক সাবমিশন বাতিল করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                        }
                    }
                }
                4 -> {
                    // News Ticker Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        Button(
                            onClick = { showAddNewsDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("নতুন নোটিশ / আপডেট যোগ করুন")
                        }

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 70.dp)
                        ) {
                            items(allNews, key = { it.newsId }) { news ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = news.content,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(onClick = { viewModel.deleteNews(news) }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                5 -> {
                    // Jobs Management Tab
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 70.dp)
                    ) {
                        items(allJobs, key = { it.jobId }) { job ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(job.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Text(
                                            "${job.category} • ৳${job.rewardPerTask} BDT • পূরণ: ${job.completedWorkers}/${job.targetWorkers}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    IconButton(onClick = {
                                        viewModel.deleteJob(job)
                                        Toast.makeText(context, "জবটি ডিলিট করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Adjust Balance Dialog
    if (userForBalanceAdjust != null) {
        val target = userForBalanceAdjust!!
        var newBalanceText by remember { mutableStateOf(target.balance.toString()) }

        AlertDialog(
            onDismissRequest = { userForBalanceAdjust = null },
            title = { Text("ব্যালেন্স সমন্বয়: ${target.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("বর্তমান ব্যালেন্স: ৳${String.format("%.2f", target.balance)} BDT")
                    OutlinedTextField(
                        value = newBalanceText,
                        onValueChange = { newBalanceText = it },
                        label = { Text("নতুন ব্যালেন্স (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bal = newBalanceText.toDoubleOrNull()
                        if (bal != null) {
                            viewModel.adjustUserBalance(target.userId, bal)
                            Toast.makeText(context, "ব্যালেন্স পরিবর্তন করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            userForBalanceAdjust = null
                        } else {
                            Toast.makeText(context, "সঠিক সংখ্যা লিখুন", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("সংরক্ষণ করুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { userForBalanceAdjust = null }) { Text("বাতিল") }
            }
        )
    }

    // Add News Dialog
    if (showAddNewsDialog) {
        var newsContent by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddNewsDialog = false },
            title = { Text("নতুন নোটিশ পোস্ট করুন") },
            text = {
                OutlinedTextField(
                    value = newsContent,
                    onValueChange = { newsContent = it },
                    label = { Text("নোটিশের টেক্সট লিখুন") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newsContent.isNotBlank()) {
                            viewModel.addNews(newsContent)
                            Toast.makeText(context, "নোটিশ যোগ করা হয়েছে!", Toast.LENGTH_SHORT).show()
                            showAddNewsDialog = false
                        }
                    }
                ) { Text("পোস্ট করুন") }
            },
            dismissButton = {
                TextButton(onClick = { showAddNewsDialog = false }) { Text("বাতিল") }
            }
        )
    }
}

@Composable
fun AdminUserCard(
    user: UserEntity,
    onAdjustBalance: () -> Unit,
    onToggleBan: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isBanned) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        if (user.isAdmin) {
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primary) {
                                Text("ADMIN", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                        if (user.isBanned) {
                            Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.error) {
                                Text("BANNED", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                            }
                        }
                    }
                    Text(
                        "${user.email} • @${user.username}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "৳ ${String.format("%.2f", user.balance)}",
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onAdjustBalance,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ব্যালেন্স", fontSize = 11.sp)
                }

                if (!user.isAdmin) {
                    FilledTonalButton(
                        onClick = onToggleBan,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (user.isBanned) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
                        ),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Text(if (user.isBanned) "আনব্যান" else "ব্যান করুন", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDepositCard(
    deposit: DepositEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${deposit.paymentMethod} • ৳${String.format("%.2f", deposit.amount)} BDT",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                StatusBadge(deposit.status)
            }

            Text("ইউজার: ${deposit.userEmail} (@${deposit.username})", style = MaterialTheme.typography.bodySmall)
            Text("প্রেরক নাম্বার: ${deposit.senderNumber} | TrxID: ${deposit.transactionId}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            if (deposit.status == "PENDING") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("অনুমোদন ও ক্রেডিট", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("বাতিল করুন", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminWithdrawCard(
    withdrawal: WithdrawEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${withdrawal.paymentMethod} • ৳${String.format("%.2f", withdrawal.amount)} BDT",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                StatusBadge(withdrawal.status)
            }

            Text("ইউজার: ${withdrawal.userEmail}", style = MaterialTheme.typography.bodySmall)
            Text("প্রাপক নাম্বার: ${withdrawal.receiverNumber}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            if (withdrawal.status == "PENDING") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("পেমেন্ট সম্পন্ন ✓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("বাতিল ও ফেরত", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSubmissionCard(
    sub: SubmissionEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(sub.jobTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                StatusBadge(sub.status)
            }

            Text("কর্মী: ${sub.workerEmail} (@${sub.workerUsername})", style = MaterialTheme.typography.labelSmall)
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("প্রুফ: ${sub.proofText}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
            }

            Text("রিওয়ার্ড: ৳${sub.rewardAmount} BDT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)

            if (sub.status == "PENDING") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Text("অনুমোদন করুন", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("বাতিল করুন", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
