package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun openLink(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun SocialLinksCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("social_links_card"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Social Links",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "অফিশিয়াল কমিউনিটি ও সোশ্যাল মিডিয়া",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Text(
                text = "প্রতিদিনের পেমেন্ট প্রুফ ও নতুন কাজের তথ্যের জন্য জয়েন করুন",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp
                ),
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialButton(
                    title = "Telegram",
                    subtitle = "অফিশিয়াল চ্যানেল",
                    icon = Icons.Default.Send,
                    badgeColor = Color(0xFF0088CC),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openLink(context, "https://t.me/Siamnexa")
                    }
                )

                SocialButton(
                    title = "YouTube",
                    subtitle = "@siamnexa",
                    icon = Icons.Default.PlayArrow,
                    badgeColor = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openLink(context, "https://youtube.com/@siamnexa?si=seEETG1fUPp_BmXD")
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialButton(
                    title = "TikTok",
                    subtitle = "@siamnexa",
                    icon = Icons.Default.VideoLibrary,
                    badgeColor = Color(0xFF0F172A),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openLink(context, "https://www.tiktok.com/@siamnexa?_r=1&_t=ZS-9ANxW4sNqhp")
                    }
                )

                SocialButton(
                    title = "Facebook",
                    subtitle = "অফিশিয়াল পেজ",
                    icon = Icons.AutoMirrored.Filled.OpenInNew,
                    badgeColor = Color(0xFF1877F2),
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openLink(context, "https://www.facebook.com/share/1FNKrBQbTv/")
                    }
                )
            }
        }
    }
}

@Composable
private fun SocialButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .testTag("social_btn_$title"),
        shape = RoundedCornerShape(10.dp),
        color = badgeColor.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = badgeColor,
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 9.5.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
