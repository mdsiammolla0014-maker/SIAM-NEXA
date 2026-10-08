package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppScreen
import com.example.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()

    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("YouTube") }
    var description by remember { mutableStateOf("") }
    var taskLink by remember { mutableStateOf("") }
    var proofRequirement by remember { mutableStateOf("") }
    var rewardPerTaskText by remember { mutableStateOf("0.50") }
    var workersText by remember { mutableStateOf("20") }
    var isPosting by remember { mutableStateOf(false) }

    val rewardPerTask = rewardPerTaskText.toDoubleOrNull() ?: 0.0
    val workersCount = workersText.toIntOrNull() ?: 0
    val totalCost = rewardPerTask * workersCount

    val categories = listOf("YouTube", "Telegram", "TikTok", "Facebook", "Web", "Other")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Balance Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
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
                        text = "আপনার অ্যাকাউন্ট ব্যালেন্স",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    Text(
                        text = "৳ ${String.format("%.2f", currentUser?.balance ?: 0.0)} BDT",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                FilledTonalButton(
                    onClick = { viewModel.navigateTo(AppScreen.Deposit) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddCard, contentDescription = "Deposit", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ডিপোজিট করুন", fontSize = 12.sp)
                }
            }
        }

        Text(
            text = "নতুন মাইক্রো-জব পোস্ট করুন",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )

        // Job Title
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("কাজের শিরোনাম (Job Title) *") },
            placeholder = { Text("যেমন: Subscribe YouTube Channel & Like") },
            leadingIcon = { Icon(Icons.Default.Title, contentDescription = "Title") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("post_job_title_input"),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Category dropdown
        var categoryExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("ক্যাটাগরি (Category) *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            selectedCategory = cat
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        // Task Link
        OutlinedTextField(
            value = taskLink,
            onValueChange = { taskLink = it },
            label = { Text("কাজের লিংক (YouTube/Facebook/Telegram URL)") },
            placeholder = { Text("https://...") },
            leadingIcon = { Icon(Icons.Default.Link, contentDescription = "Link") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Instructions
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("কাজের সম্পূর্ণ বিবরণ ও নিয়মাবলী *") },
            placeholder = { Text("ধাপে ধাপে লিখুন কিভাবে কাজটি করতে হবে...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            shape = RoundedCornerShape(10.dp)
        )

        // Proof Requirement
        OutlinedTextField(
            value = proofRequirement,
            onValueChange = { proofRequirement = it },
            label = { Text("প্রয়োজনীয় প্রুফ (Proof Required) *") },
            placeholder = { Text("যেমন: চ্যানেল নাম, ইউজারনেম এবং স্ক্রিনশট") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )

        // Pricing & Worker count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = rewardPerTaskText,
                onValueChange = { rewardPerTaskText = it },
                label = { Text("প্রতি কাজে টাকা (BDT)") },
                placeholder = { Text("0.50") },
                leadingIcon = { Icon(Icons.Default.Paid, contentDescription = "Cost") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("post_job_reward_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = workersText,
                onValueChange = { workersText = it },
                label = { Text("মোট কর্মী সংখ্যা") },
                placeholder = { Text("20") },
                leadingIcon = { Icon(Icons.Default.Group, contentDescription = "Workers") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("post_job_workers_input"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Summary Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "মোট আনুমানিক খরচ:",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Text(
                        text = "৳ ${String.format("%.2f", totalCost)} BDT",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }
                Text(
                    text = "(${workersCount} জন কর্মী × ৳${String.format("%.2f", rewardPerTask)} BDT)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                )
            }
        }

        // Submit Button
        Button(
            onClick = {
                if (title.isBlank() || description.isBlank() || proofRequirement.isBlank()) {
                    Toast.makeText(context, "সবগুলো আবশ্যক ফিল্ড পূরণ করুন", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (rewardPerTask <= 0.05) {
                    Toast.makeText(context, "প্রতি কাজের জন্য সর্বনিম্ন ০.০৫ টাকা হতে হবে", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (workersCount < 1) {
                    Toast.makeText(context, "কমপক্ষে ১ জন কর্মী থাকতে হবে", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                isPosting = true
                viewModel.postJob(
                    title = title,
                    category = selectedCategory,
                    description = description,
                    taskLink = taskLink,
                    proofRequirement = proofRequirement,
                    rewardPerTask = rewardPerTask,
                    workers = workersCount
                ) { success, msg ->
                    isPosting = false
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    if (success) {
                        viewModel.navigateTo(AppScreen.Jobs)
                    }
                }
            },
            enabled = !isPosting,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_post_job_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isPosting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = "মাইক্রো-জব পাবলিশ করুন",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
