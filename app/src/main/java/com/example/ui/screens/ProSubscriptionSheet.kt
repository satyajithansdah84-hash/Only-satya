package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.InstagramGradient
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.ProGold
import com.example.ui.theme.ProGoldGradient
import com.example.ui.theme.ReelMagenta
import com.example.ui.theme.ReelOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ProSubscriptionDialog(
    isOpen: Boolean,
    isAlreadyPro: Boolean,
    onDismiss: () -> Unit,
    onUpgradeConfirmed: () -> Unit
) {
    if (!isOpen) return

    var selectedPlan by remember { mutableStateOf("ANNUAL") } // "ANNUAL" or "MONTHLY"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = DarkBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333344)),
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted
                        )
                    }
                }

                // Crown / Star Pro Badge
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(ProGoldGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Pro VIP",
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "REEL MAKER PRO",
                    color = ProGold,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Unlock Ultra HD 4K, all VIP templates, and remove watermarks.",
                    color = TextMuted,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Feature List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkCard)
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProFeatureRow(
                        icon = Icons.Default.HighQuality,
                        title = "4K Ultra-HD Crisp Export",
                        desc = "Export at native 60fps with maximum bitrate"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Security,
                        title = "Zero Watermarks",
                        desc = "Clean, unbranded reels ready for brand deals"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.AutoAwesome,
                        title = "Unlimited Gemini AI Generation",
                        desc = "AI hooks, beat-matched captions & hashtags"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.MusicNote,
                        title = "Full VIP Music & Beat Catalog",
                        desc = "Access trending Phonk, Trap & Chillhop tracks"
                    )
                    ProFeatureRow(
                        icon = Icons.Default.Videocam,
                        title = "All Exclusive VIP Templates",
                        desc = "Viral cuts, Cyber Glitch, Beast Workout & more"
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Plan Cards Selector (Annual vs Monthly)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Annual Card
                    PlanCard(
                        title = "Annual Plan",
                        price = "$29.99 / yr",
                        subtext = "$2.49 / mo • 3 Days Free",
                        badge = "SAVE 50%",
                        isSelected = selectedPlan == "ANNUAL",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPlan = "ANNUAL" }
                    )

                    // Monthly Card
                    PlanCard(
                        title = "Monthly Plan",
                        price = "$4.99 / mo",
                        subtext = "Billed monthly",
                        badge = null,
                        isSelected = selectedPlan == "MONTHLY",
                        modifier = Modifier.weight(1f),
                        onClick = { selectedPlan = "MONTHLY" }
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                if (isAlreadyPro) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NeonGreen.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = NeonGreen
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "You are currently a PRO member!",
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onUpgradeConfirmed()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(InstagramGradient)
                            .testTag("upgrade_pro_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedPlan == "ANNUAL") "Start 3-Day Free Trial" else "Upgrade to PRO Now",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Recurring billing. Cancel anytime in Google Play Store.",
                        color = TextMuted,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProFeatureRow(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(ReelMagenta.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ReelOrange,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun PlanCard(
    title: String,
    price: String,
    subtext: String,
    badge: String?,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) ReelMagenta.copy(alpha = 0.2f) else DarkSurfaceVariant)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) ReelMagenta else DarkCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            if (badge != null) {
                Surface(
                    color = ProGold,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                Spacer(modifier = Modifier.height(18.dp))
            }

            Text(
                text = title,
                color = TextWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = price,
                color = if (isSelected) ProGold else TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}
