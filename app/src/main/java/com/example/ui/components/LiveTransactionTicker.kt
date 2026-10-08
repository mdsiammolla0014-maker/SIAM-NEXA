package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

data class LiveTx(
    val username: String,
    val type: String, // "উইথড্র" or "ডিপোজিট"
    val method: String,
    val amount: String,
    val isWithdraw: Boolean
)

@Composable
fun LiveTransactionTicker(
    modifier: Modifier = Modifier
) {
    val liveTransactions = remember {
        listOf(
            LiveTx("@siam_worker", "উইথড্র", "বিকাশ", "১২০", true),
            LiveTx("@rakib_ctg", "ডিপোজিট", "নগদ", "৫০", false),
            LiveTx("@tanvir99", "উইথড্র", "রকেট", "১৫০", true),
            LiveTx("@fahim_boss", "ডিপোজিট", "বিকাশ", "১০০", false),
            LiveTx("@hasan_ali", "উইথড্র", "বিকাশ", "২০০", true),
            LiveTx("@sumon_nx", "ডিপোজিট", "নগদ", "৩০", false),
            LiveTx("@shakil_01", "উইথড্র", "নগদ", "১২০", true),
            LiveTx("@mim_akter", "উইথড্র", "রকেট", "১৩০", true),
            LiveTx("@arif_hossain", "ডিপোজিট", "বিকাশ", "২০০", false)
        )
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3800L)
            currentIndex = (currentIndex + 1) % liveTransactions.size
        }
    }

    val currentTx = liveTransactions[currentIndex]

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = if (currentTx.isWithdraw) Color(0xFFF0FDF4) else Color(0xFFEFF6FF),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (currentTx.isWithdraw) Color(0xFF86EFAC) else Color(0xFF93C5FD)
        )
    ) {
        AnimatedContent(
            targetState = currentTx,
            transitionSpec = {
                (slideInVertically { height -> height } + fadeIn())
                    .togetherWith(slideOutVertically { height -> -height } + fadeOut())
            },
            label = "LiveTxAnimation"
        ) { tx ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (tx.isWithdraw) Color(0xFF16A34A) else Color(0xFF0284C7),
                    modifier = Modifier.size(22.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (tx.isWithdraw) Icons.Default.CheckCircle else Icons.Default.CurrencyExchange,
                            contentDescription = tx.type,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Text(
                    text = "${tx.username} এইমাত্র ${tx.method} এ ৳${tx.amount} ${tx.type} করেছেন!",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = if (tx.isWithdraw) Color(0xFF14532D) else Color(0xFF1E3A8A),
                        fontSize = 11.5.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
