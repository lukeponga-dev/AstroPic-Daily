package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun PrivacyPolicyDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        val scrollState = rememberScrollState()
        
        // Background and glowing outline styling
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                .padding(20.dp)
                .testTag("privacy_policy_dialog_container"),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 700.dp)
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.tertiary
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                // Subtle coordinate visual grid in background
                val gridColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.02f)
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                val tertiaryColor = MaterialTheme.colorScheme.tertiary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            val w = size.width
                            val h = size.height
                            // Draw horizontal grid lines
                            for (i in 1..8) {
                                val y = h * (i / 9f)
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            // Draw vertical grid lines
                            for (i in 1..4) {
                                val x = w * (i / 5f)
                                drawLine(
                                    color = gridColor,
                                    start = Offset(x, 0f),
                                    end = Offset(x, h),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                        }
                        .padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Close Action at the Top Right
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .testTag("close_privacy_button")
                                    .background(
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss info",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Header Icon and Heading
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 8.dp)
                        )

                        Text(
                            text = "SECURE COSMIC PROTOCOLS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 1.5.sp,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "PRIVACY METRICS & POLICY READOUT",
                            style = MaterialTheme.typography.labelMedium,
                            color = secondaryColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                        )

                        // Divider Block with Futuristic Styling
                        Divider(
                            modifier = Modifier.padding(bottom = 20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        )

                        // Core Policy Sections (glowing neon accents in typography and borders)
                        PolicySectionCard(
                            icon = Icons.Default.PrivacyTip,
                            title = "I. LOCAL TELEMETRY FIRST",
                            description = "Celestial Observatory operates strictly as a self-contained, local-first scientific vessel. We do not upload, sell, package, or transmit your individual browsing metrics, app logs, or telemetry variables to third-party ad networks or corporate servers.",
                            accentColor = primaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PolicySectionCard(
                            icon = Icons.Default.Code,
                            title = "II. NASA OPEN DATA COUPLING",
                            description = "All astronomy visual logs, textual explanations, and space coordinate telemetry are obtained in real-time from NASA's public Open APIs. Operating this portal requires direct network coupling with NASA servers; however, no user-specific credentials are ever sent.",
                            accentColor = secondaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PolicySectionCard(
                            icon = Icons.Default.Storage,
                            title = "III. ENCRYPTED DEVICE VAULT",
                            description = "Your bookmarked stellar coordinates, customized stardates, and highlighted flight logs are written entirely to a local Room SQLite database partition on your physical device. No remote mirrors or off-site synchronization channels exist.",
                            accentColor = tertiaryColor
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PolicySectionCard(
                            icon = Icons.Default.CloudDownload,
                            title = "IV. STARMAPPED TRANSMISSIONS",
                            description = "When choosing to transmit high-resolution imagery to external storage slots via the save mechanics, the application leverages the standard native Android Download Manager protocols. We do not inject tracking IDs or metadata into visual artifacts.",
                            accentColor = primaryColor
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Acknowledgment Button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("acknowledge_privacy_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = primaryColor,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text(
                                text = "ACKNOWLEDGE PROTOCOLS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = "STARDATE READOUT: SECURE & ENCRYPTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PolicySectionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
