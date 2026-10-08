package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val DIRECT_SPIN_LINK = "https://www.profitableratecpmnetwork.com/ikpavk06?key=5b0bf4859cd27d0479abc79c93cd4b80"

data class WheelSlice(val amount: String, val color: Color)

@Composable
fun SpinWheelDialog(
    onDismiss: () -> Unit,
    onRewardClaimed: (Double) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Slices on the wheel: 8 slices (45 degrees each)
    // Slices: 0: ৳৫০, 1: ৳২০, 2: ৳১০, 3: ৳৮০, 4: ৳১, 5: ৳২, 6: ৳৫, 7: ৳১০০
    // Index 6 is "৳৫" (5 BDT)!
    // Top indicator is at 270 degrees (or top center).
    // Angle for slice 6 to land directly under top needle:
    // Slice angle = 360 / 8 = 45 deg.
    // Center of slice 6 is at (6 * 45 + 22.5) = 292.5 deg.
    // To bring 292.5 deg to top (270 deg):
    // Rotation = 270 - 292.5 + (8 * 360) = 2857.5 degrees!
    val slices = remember {
        listOf(
            WheelSlice("৳৫০", Color(0xFF0284C7)),
            WheelSlice("৳২০", Color(0xFF0D9488)),
            WheelSlice("৳১০", Color(0xFFD97706)),
            WheelSlice("৳৮০", Color(0xFFE11D48)),
            WheelSlice("৳১", Color(0xFF4F46E5)),
            WheelSlice("৳২", Color(0xFF059669)),
            WheelSlice("৳৫", Color(0xFF2563EB)), // The guaranteed target: 5 BDT
            WheelSlice("৳১০০", Color(0xFF9333EA))
        )
    }

    var hasVisitedDirectLink by remember { mutableStateOf(false) }
    var isSpinning by remember { mutableStateOf(false) }
    var spinFinished by remember { mutableStateOf(false) }

    val rotationAnim = remember { Animatable(0f) }

    // Start spin logic
    fun startSpinSequence() {
        if (isSpinning || spinFinished) return
        isSpinning = true
        coroutineScope.launch {
            // Target angle brings slice 6 ("৳৫") to top indicator
            // Target formula: 360 * 7 + (270 - (6 * 45 + 22.5)) = 2520 - 22.5 = 2497.5f
            val targetRotation = 360f * 6 + 247.5f
            rotationAnim.animateTo(
                targetValue = targetRotation,
                animationSpec = tween(
                    durationMillis = 4500,
                    easing = FastOutSlowInEasing
                )
            )
            isSpinning = false
            spinFinished = true
            delay(400)
            onRewardClaimed(5.0)
        }
    }

    Dialog(
        onDismissRequest = {
            if (spinFinished) onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = spinFinished, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("welcome_spin_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Spin",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "ওয়েলকাম বোনাস স্পিন!",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "চাকা ঘুরিয়ে জিতে নিন ফ্রি ব্যালেন্স",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }

                    if (spinFinished) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wheel Container with Needle
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Wheel Canvas
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotationAnim.value)
                    ) {
                        drawWheel(slices)
                    }

                    // Outer Rim Ring
                    Surface(
                        modifier = Modifier.size(236.dp),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(4.dp, Color(0xFFFBBF24))
                    ) {}

                    // Center Hub Cap
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        shadowElevation = 4.dp,
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFBBF24))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "NEXA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    // Needle / Arrow Indicator at top
                    Canvas(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        val path = Path().apply {
                            moveTo(size.width / 2f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width, 0f)
                            close()
                        }
                        drawPath(path, color = Color(0xFFEF4444))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!spinFinished) {
                    if (!hasVisitedDirectLink) {
                        // Step 1: Open direct link then spin
                        Button(
                            onClick = {
                                hasVisitedDirectLink = true
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DIRECT_SPIN_LINK))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Fallback
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("open_spin_link_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                contentDescription = "Link",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "স্পিন আনলক করুন (Spin Unlock Link)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "* স্পিন আনলক লিংকে প্রবেশ করে ব্যাক আসলেই চাকা ঘুরবে",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.5.sp
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        // Step 2: User returned, ready to spin!
                        Button(
                            onClick = { startSpinSequence() },
                            enabled = !isSpinning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("spin_wheel_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF059669)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isSpinning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "চাকা ঘুরছে...",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = "Spin",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "স্পিন করুন! (Spin Now)",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                } else {
                    // Congratulations Result Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Celebration,
                                    contentDescription = "Won",
                                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "অভিনন্দন! আপনি জিতেছেন ৫ টাকা 🎉",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                )
                            }
                            Text(
                                text = "আপনার একাউন্টে ৳৫.০০ BDT যোগ করা হয়েছে!",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                ),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("claim_spin_reward_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("ব্যালেন্স গ্রহণ করুন ✓", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawWheel(slices: List<WheelSlice>) {
    val radius = size.minDimension / 2f
    val center = Offset(size.width / 2f, size.height / 2f)
    val sweepAngle = 360f / slices.size

    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 28f
        isFakeBoldText = true
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }

    slices.forEachIndexed { index, slice ->
        val startAngle = index * sweepAngle
        drawArc(
            color = slice.color,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = true,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Fill
        )

        // Draw divider line
        val angleRad = Math.toRadians(startAngle.toDouble())
        val endX = center.x + radius * cos(angleRad).toFloat()
        val endY = center.y + radius * sin(angleRad).toFloat()
        drawLine(
            color = Color.White.copy(alpha = 0.6f),
            start = center,
            end = Offset(endX, endY),
            strokeWidth = 2.5f
        )

        // Draw text
        val textAngle = Math.toRadians((startAngle + sweepAngle / 2f).toDouble())
        val textRadius = radius * 0.68f
        val textX = center.x + textRadius * cos(textAngle).toFloat()
        val textY = center.y + textRadius * sin(textAngle).toFloat() + 10f

        drawContext.canvas.nativeCanvas.drawText(
            slice.amount,
            textX,
            textY,
            paint
        )
    }
}
