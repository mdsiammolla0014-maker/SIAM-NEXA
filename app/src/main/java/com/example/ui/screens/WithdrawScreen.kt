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
import com.example.data.entity.WithdrawEntity
import com.example.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WithdrawScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val withdrawals by viewModel.myWithdrawals.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var paymentMethod by remember { mutableStateOf("bKash") }
    var receiverNumber by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("120") }
    var isSubmitting by remember { mutableStateOf(false) }

    val userBalance = currentUser?.balance ?: 0.0
    val canWithdraw = userBalance >= 120.0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("withdraw_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Balance Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (canWithdraw) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "উত্তোলনযোগ্য ব্যালেন্স:",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "৳ ${String.format("%.2f", userBalance)} BDT",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (canWithdraw) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (canWithdraw) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.errorContainer
                        ) {
                            Text(
                                text = if (canWithdraw) "উত্তোলন সম্ভব ✓" else "মিনিমাম ১২০ টাকা প্রয়োজন",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (canWithdraw) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onErrorContainer
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "★ বিকাশ, নগদ বা রকেট একাউন্টে সর্বনিম্ন ১২০ টাকা উত্তোলন করতে পারবেন।",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Withdrawal Request Form
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "টাকা উইথড্র ফরম (Withdraw Request)",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    // Method selector: bKash, Nagad, Rocket
                    Text(
                        text = "পেমেন্ট মাধ্যম বেছে নিন:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WithdrawMethodTab(
                            title = "বিকাশ",
                            subtitle = "bKash",
                            isSelected = paymentMethod == "bKash",
                            brandColor = Color(0xFFD12053),
                            modifier = Modifier.weight(1f),
                            onClick = { paymentMethod = "bKash" }
                        )

                        WithdrawMethodTab(
                            title = "নগদ",
                            subtitle = "Nagad",
                            isSelected = paymentMethod == "Nagad",
                            brandColor = Color(0xFFF7931E),
                            modifier = Modifier.weight(1f),
                            onClick = { paymentMethod = "Nagad" }
                        )

                        WithdrawMethodTab(
                            title = "রকেট",
                            subtitle = "Rocket",
                            isSelected = paymentMethod == "Rocket",
                            brandColor = Color(0xFF8C3494),
                            modifier = Modifier.weight(1f),
                            onClick = { paymentMethod = "Rocket" }
                        )
                    }

                    // Receiver Number
                    OutlinedTextField(
                        value = receiverNumber,
                        onValueChange = { receiverNumber = it },
                        label = { Text("$paymentMethod নাম্বার (Mobile Number) *") },
                        placeholder = { Text("01XXXXXXXXX (১১ ডিজিট)") },
                        leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = "Receiver") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_receiver_number_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Amount
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("টাকার পরিমাণ (মিনিমাম ১২০ টাকা) *") },
                        placeholder = { Text("120") },
                        leadingIcon = { Icon(Icons.Default.Paid, contentDescription = "Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("withdraw_amount_input"),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Quick Amount Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("120", "150", "200", "500").forEach { quickAmt ->
                            SuggestionChip(
                                onClick = { amountText = quickAmt },
                                label = { Text("৳$quickAmt") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        if (userBalance >= 120.0) {
                            SuggestionChip(
                                onClick = { amountText = String.format(Locale.US, "%.0f", userBalance) },
                                label = { Text("সব ব্যালেন্স") },
                                shape = RoundedCornerShape(8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            isSubmitting = true
                            viewModel.submitWithdrawal(
                                paymentMethod = paymentMethod,
                                receiverNumber = receiverNumber,
                                amount = amount
                            ) { success, msg ->
                                isSubmitting = false
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                if (success) {
                                    receiverNumber = ""
                                    amountText = "120"
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_withdraw_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(
                                text = "টাকা উইথড্র রিকোয়েস্ট পাঠান",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Withdrawal History
        item {
            Text(
                text = "আমার উইথড্র হিস্ট্রি",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (withdrawals.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "এখনো কোনো উইথড্র রিকোয়েস্ট করেননি",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        } else {
            items(withdrawals, key = { it.withdrawId }) { item ->
                WithdrawItemRow(item)
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun WithdrawMethodTab(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    brandColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) brandColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) brandColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) brandColor else MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = if (isSelected) brandColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
fun WithdrawItemRow(withdrawal: WithdrawEntity) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${withdrawal.paymentMethod} - ৳${String.format("%.2f", withdrawal.amount)} BDT",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "প্রাপক নাম্বার: ${withdrawal.receiverNumber}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Text(
                    text = dateFormat.format(Date(withdrawal.createdAt)),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp
                    )
                )
            }

            StatusBadge(withdrawal.status)
        }
    }
}
