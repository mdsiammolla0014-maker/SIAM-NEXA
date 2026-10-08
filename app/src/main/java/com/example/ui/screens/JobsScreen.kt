package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.JobEntity
import com.example.ui.MainViewModel
import com.example.ui.components.AdsterraInterstitialDialog
import com.example.ui.components.SocialLinksCard
import com.example.ui.components.openLink

@Composable
fun JobsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val jobs by viewModel.activeJobs.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var selectedCategory by remember { mutableStateOf("ALL") }
    var selectedJobForProof by remember { mutableStateOf<JobEntity?>(null) }
    var showBonusAd by remember { mutableStateOf(false) }

    val categories = listOf("ALL", "YouTube", "Telegram", "TikTok", "Facebook", "Web")

    val filteredJobs = remember(jobs, selectedCategory) {
        if (selectedCategory == "ALL") jobs
        else jobs.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("jobs_list"),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Welcome & Balance Overview Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "স্বাগতম, ${currentUser?.name ?: "ব্যবহারকারী"}!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "বর্তমান ব্যালেন্স: ৳${String.format("%.2f", currentUser?.balance ?: 0.0)} BDT",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "মিনিমাম উত্তোলন ১২০ টাকা (বিকাশ / নগদ / রকেট)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        )
                    }

                    // Watch Ad bonus button
                    FilledTonalButton(
                        onClick = { showBonusAd = true },
                        modifier = Modifier.testTag("watch_bonus_ad_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PlayCircle,
                                contentDescription = "Watch Ad",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "+০.২০ বোনাস",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = if (cat == "ALL") "সব কাজ (All)" else cat,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedCategory == cat) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        leadingIcon = {
                            val icon = when (cat) {
                                "YouTube" -> Icons.Default.SmartDisplay
                                "Telegram" -> Icons.Default.Send
                                "TikTok" -> Icons.Default.MusicNote
                                "Facebook" -> Icons.Default.ThumbUp
                                "Web" -> Icons.Default.Language
                                else -> Icons.Default.Work
                            }
                            Icon(imageVector = icon, contentDescription = cat, modifier = Modifier.size(16.dp))
                        },
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "উপলব্ধ মাইক্রো জবসমূহ (${filteredJobs.size}টি)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
                Text(
                    text = "টাস্ক পূরণ করে সাথে সাথে আয় করুন",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        if (filteredJobs.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "No jobs",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "এই ক্যাটাগরিতে বর্তমানে কোনো কাজ নেই",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        } else {
            items(filteredJobs, key = { it.jobId }) { job ->
                JobCardItem(
                    job = job,
                    onWorkClick = { selectedJobForProof = job }
                )
            }
        }

        // Social Media Community Links Card
        item {
            Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                SocialLinksCard()
            }
        }
    }

    // Task Proof Submission Dialog
    if (selectedJobForProof != null) {
        JobProofDialog(
            job = selectedJobForProof!!,
            onDismiss = { selectedJobForProof = null },
            onSubmitProof = { proofText, imageUri ->
                viewModel.submitJobProof(
                    job = selectedJobForProof!!,
                    proofText = proofText,
                    proofImageUri = imageUri
                ) { success, msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) {
                        selectedJobForProof = null
                    }
                }
            }
        )
    }

    // Interstitial Bonus Ad
    if (showBonusAd) {
        AdsterraInterstitialDialog(
            onDismiss = { showBonusAd = false },
            onRewardEarned = {
                viewModel.grantAdReward(0.20)
                Toast.makeText(context, "বোনাস ৳০.২০ BDT একাউন্টে যোগ হয়েছে!", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun JobCardItem(
    job: JobEntity,
    onWorkClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .testTag("job_item_${job.jobId}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Category Badge & Reward
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = getCategoryColor(job.category).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, getCategoryColor(job.category).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(job.category),
                            contentDescription = job.category,
                            modifier = Modifier.size(14.dp),
                            tint = getCategoryColor(job.category)
                        )
                        Text(
                            text = job.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = getCategoryColor(job.category)
                            )
                        )
                    }
                }

                // Reward Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = "৳ ${String.format("%.2f", job.rewardPerTask)} BDT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job Title
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.5.sp
                )
            )

            // Description preview
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                ),
                maxLines = 2,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Worker progress & Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                    Text(
                        text = "কাজ সম্পন্ন: ${job.completedWorkers}/${job.targetWorkers}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    val progress = (job.completedWorkers.toFloat() / job.targetWorkers.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Button(
                    onClick = onWorkClick,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("work_btn_${job.jobId}")
                ) {
                    Text(text = "কাজ করুন", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Work",
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun JobProofDialog(
    job: JobEntity,
    onDismiss: () -> Unit,
    onSubmitProof: (String, String?) -> Unit
) {
    val context = LocalContext.current
    var proofText by remember { mutableStateOf("") }
    var screenshotUploaded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Task instructions
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "কাজের নিয়ম ও লিংক:",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = job.description,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Open Link button
                if (job.taskLink.isNotBlank()) {
                    OutlinedButton(
                        onClick = { openLink(context, job.taskLink) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = "Open Link",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কাজের লিংক ওপেন করুন (Open Task Link)")
                    }
                }

                // Proof requirements
                Text(
                    text = "প্রয়োজনীয় প্রুফ:\n${job.proofRequirement}",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                )

                // Proof input text
                OutlinedTextField(
                    value = proofText,
                    onValueChange = { proofText = it },
                    label = { Text("কাজের প্রুফ বা ইউজারনেম লিখুন *") },
                    placeholder = { Text("e.g. Subscribed as Siam Molla / user @siam01") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp)
                )

                // Screenshot Upload Mock / Confirmation
                Surface(
                    onClick = { screenshotUploaded = !screenshotUploaded },
                    shape = RoundedCornerShape(8.dp),
                    color = if (screenshotUploaded) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (screenshotUploaded) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Screenshot",
                            tint = if (screenshotUploaded) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = if (screenshotUploaded) "✓ স্ক্রিনশট সিলেক্ট করা হয়েছে" else "স্ক্রিনশট প্রুফ সংযুক্ত করুন (ক্লিক করুন)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (screenshotUploaded) FontWeight.Bold else FontWeight.Normal,
                                color = if (screenshotUploaded) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Text(
                    text = "রিওয়ার্ড: ৳${String.format("%.2f", job.rewardPerTask)} BDT (যাচাই শেষে যোগ হবে)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (proofText.trim().isEmpty()) {
                        Toast.makeText(context, "অনুগ্রহ করে প্রুফ বা ইউজারনেম লিখুন", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    val imageUri = if (screenshotUploaded) "mock_screenshot_uri_${System.currentTimeMillis()}" else null
                    onSubmitProof(proofText, imageUri)
                }
            ) {
                Text("প্রুফ সাবমিট করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

fun getCategoryColor(cat: String): Color {
    return when (cat.lowercase()) {
        "youtube" -> Color(0xFFDC2626)
        "telegram" -> Color(0xFF0088CC)
        "tiktok" -> Color(0xFF0F172A)
        "facebook" -> Color(0xFF1877F2)
        "web" -> Color(0xFF059669)
        else -> Color(0xFF0284C7)
    }
}

fun getCategoryIcon(cat: String): ImageVector {
    return when (cat.lowercase()) {
        "youtube" -> Icons.Default.SmartDisplay
        "telegram" -> Icons.Default.Send
        "tiktok" -> Icons.Default.MusicNote
        "facebook" -> Icons.Default.ThumbUp
        "web" -> Icons.Default.Language
        else -> Icons.Default.Work
    }
}
