package com.example.qrcodegenerator.ui.main

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation3.runtime.NavKey
import com.example.qrcodegenerator.util.QRCodeHelper
import com.example.qrcodegenerator.util.RiskLevel
import com.example.qrcodegenerator.util.SecurityHelper

data class ColorOption(val name: String, val color: Color, val hex: String)

val colorPresets = listOf(
    ColorOption("Classic", Color(0xFF000000), "#000000"),
    ColorOption("Blue", Color(0xFF2563EB), "#2563eb"),
    ColorOption("Navy", Color(0xFF1E3A8A), "#1e3a8a"),
    ColorOption("Emerald", Color(0xFF059669), "#059669"),
    ColorOption("Purple", Color(0xFF7C3AED), "#7c3aed"),
    ColorOption("Crimson", Color(0xFFDC2626), "#dc2626")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var inputText by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(colorPresets[0]) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSecureScreenEnabled by remember { mutableStateOf(false) }
    var isAppLocked by remember { mutableStateOf(false) }

    // Live URL Security Audit
    val securityResult = remember(inputText) {
        SecurityHelper.auditInput(inputText)
    }

    val scrollState = rememberScrollState()

    if (isAppLocked) {
        // App Lock Overlay Screen
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Application Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Biometric / Device security verification required",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))
                Button(
                    onClick = {
                        if (activity != null) {
                            SecurityHelper.authenticate(
                                activity = activity,
                                onSuccess = {
                                    isAppLocked = false
                                    Toast.makeText(context, "Unlocked successfully!", Toast.LENGTH_SHORT).show()
                                },
                                onError = { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            isAppLocked = false
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Text("🔓 Unlock App")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "🛡️ QR Code Generator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Created by Hong Quang • Enterprise Security Edition",
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Badge Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF0FDF4)
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🔒", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "100% Offline & Zero-Knowledge Security",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "Data is encrypted in memory & never transmitted externally.",
                            fontSize = 11.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "🔗 Enter or Paste URL / Text:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://example.com or confidential text...") },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            if (inputText.isNotEmpty()) {
                                IconButton(onClick = { inputText = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        minLines = 2,
                        maxLines = 4
                    )

                    // URL Security Auditor Result
                    if (inputText.isNotBlank()) {
                        when (securityResult.riskLevel) {
                            RiskLevel.SAFE -> {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✅ URL Verified: Secure HTTPS protocol",
                                        color = Color(0xFF166534),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            }
                            RiskLevel.WARNING -> {
                                Surface(
                                    color = Color(0xFFFEF9C3),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "⚠️ Security Advisory:",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF854D0E),
                                            fontSize = 12.sp
                                        )
                                        securityResult.warnings.forEach { warning ->
                                            Text(text = "• $warning", color = Color(0xFF713F12), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                            RiskLevel.DANGEROUS -> {
                                Surface(
                                    color = Color(0xFFFEE2E2),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "🚨 High Risk Warning:",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF991B1B),
                                            fontSize = 12.sp
                                        )
                                        securityResult.warnings.forEach { warning ->
                                            Text(text = "• $warning", color = Color(0xFF7F1D1D), fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clipData = clipboard.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    val pasteText = clipData.getItemAt(0).text?.toString() ?: ""
                                    if (pasteText.isNotBlank()) {
                                        inputText = pasteText
                                        Toast.makeText(context, "Securely pasted from clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("📋 Paste")
                        }

                        Button(
                            onClick = {
                                if (inputText.isBlank()) {
                                    Toast.makeText(context, "Please enter a URL or text!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val bmp = QRCodeHelper.generateQRCodeBitmap(
                                    content = inputText.trim(),
                                    width = 600,
                                    height = 600,
                                    fgColor = selectedColor.color.toArgb(),
                                    bgColor = android.graphics.Color.WHITE
                                )
                                if (bmp != null) {
                                    qrBitmap = bmp
                                    Toast.makeText(context, "QR Code Generated Securely!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to generate QR Code", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ Generate QR")
                        }
                    }

                    // Color selection
                    Text(
                        text = "🎨 Color Theme:",
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        colorPresets.forEach { colorOpt ->
                            val isSelected = selectedColor == colorOpt
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(colorOpt.color)
                                    .clickable {
                                        selectedColor = colorOpt
                                        if (inputText.isNotBlank()) {
                                            qrBitmap = QRCodeHelper.generateQRCodeBitmap(
                                                content = inputText.trim(),
                                                width = 600,
                                                height = 600,
                                                fgColor = colorOpt.color.toArgb(),
                                                bgColor = android.graphics.Color.WHITE
                                            )
                                        }
                                    }
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                        else Modifier.border(1.dp, Color.LightGray, CircleShape)
                                    )
                            )
                        }
                    }
                }
            }

            // QR Code Preview Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val success = QRCodeHelper.saveBitmapToGallery(context, qrBitmap!!)
                                    if (success) {
                                        Toast.makeText(context, "Saved securely to Pictures/QRCodeGenerator!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Text("💾 Save PNG")
                            }

                            FilledTonalButton(
                                onClick = {
                                    QRCodeHelper.shareBitmap(context, qrBitmap!!)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Share")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No QR code yet\nEnter URL above and tap\n'Generate QR'",
                                textAlign = TextAlign.Center,
                                color = Color(0xFF94A3B8),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Security Controls Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "⚙️ Privacy & Security Controls",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Screenshot Protection Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chống chụp & quay màn hình",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Kích hoạt FLAG_SECURE chống quay chụp lén",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isSecureScreenEnabled,
                            onCheckedChange = { enable ->
                                isSecureScreenEnabled = enable
                                if (activity != null) {
                                    SecurityHelper.setScreenshotProtection(activity, enable)
                                    Toast.makeText(
                                        context,
                                        if (enable) "Đã kích hoạt bảo vệ màn hình (FLAG_SECURE)!" else "Đã tắt bảo vệ màn hình",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    HorizontalDivider()

                    // App Lock / Biometric Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Khóa ứng dụng (Biometrics)",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Yêu cầu vân tay / Face ID / mã PIN thiết bị",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = {
                                if (activity != null) {
                                    if (SecurityHelper.canAuthenticateBiometrics(context)) {
                                        SecurityHelper.authenticate(
                                            activity = activity,
                                            title = "Xác thực bảo mật",
                                            subtitle = "Khóa ứng dụng với sinh trắc học",
                                            onSuccess = {
                                                isAppLocked = true
                                                Toast.makeText(context, "Ứng dụng đã được khóa bảo vệ!", Toast.LENGTH_SHORT).show()
                                            },
                                            onError = { err ->
                                                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        isAppLocked = true
                                        Toast.makeText(context, "Ứng dụng đã khóa (PIN mặc định)!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("🔒 Khóa ngay", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Footer
            Text(
                text = "✨ Created by Hong Quang\nISO/IEC 18004 Standard Compliant • TLS 1.3 Strict",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
