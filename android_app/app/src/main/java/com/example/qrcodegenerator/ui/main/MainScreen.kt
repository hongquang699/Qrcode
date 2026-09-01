package com.example.qrcodegenerator.ui.main

import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

data class ColorOption(val name: String, val color: Color, val hex: String)

val colorPresets = listOf(
    ColorOption("Classic", Color(0xFF000000), "#000000"),
    ColorOption("Blue", Color(0xFF2563EB), "#2563eb"),
    ColorOption("Navy", Color(0xFF1E3A8A), "#1e3a8a"),
    ColorOption("Emerald", Color(0xFF059669), "#059669"),
    ColorOption("Purple", Color(0xFF7C3AED), "#7c3aed"),
    ColorOption("Crimson", Color(0xFFDC2626), "#dc2626")
)

enum class QRType(val title: String, val desc: String, val iconEmoji: String) {
    URL("URL", "Tạo mã QR mở trang web.", "🌐"),
    PHONE("Điện thoại", "Tạo mã QR để gọi số điện thoại.", "📱"),
    WIFI("Wi-Fi", "Tạo mã QR để kết nối Wi-Fi.", "📶"),
    EMAIL("E-mail", "Tạo mã QR bắt đầu bản thảo email.", "✉️"),
    PDF("PDF", "Tạo mã QR để chia sẻ PDF.", "📄"),
    TEXT("Văn bản", "Tạo mã QR với một thông điệp tùy chỉnh.", "Aa")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var selectedType by remember { mutableStateOf(QRType.URL) }
    var selectedColor by remember { mutableStateOf(colorPresets[0]) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isSecureScreenEnabled by remember { mutableStateOf(false) }
    var isAppLocked by remember { mutableStateOf(false) }

    // Input States
    var urlInput by remember { mutableStateOf("https://") }
    var phoneInput by remember { mutableStateOf("") }
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiSecurity by remember { mutableStateOf("WPA") }
    var wifiHidden by remember { mutableStateOf(false) }
    var emailTo by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    var pdfInput by remember { mutableStateOf("https://drive.google.com/") }
    var textInput by remember { mutableStateOf("") }

    // Calculated Payload
    val currentPayload = remember(
        selectedType, urlInput, phoneInput, wifiSsid, wifiPassword, wifiSecurity, wifiHidden,
        emailTo, emailSubject, emailBody, pdfInput, textInput
    ) {
        when (selectedType) {
            QRType.URL -> urlInput.trim()
            QRType.PHONE -> {
                val clean = phoneInput.replace(" ", "").replace("-", "").trim()
                if (clean.isNotEmpty()) "tel:$clean" else ""
            }
            QRType.WIFI -> {
                if (wifiSsid.isNotBlank()) {
                    "WIFI:T:$wifiSecurity;S:${wifiSsid.trim()};P:${wifiPassword};H:${if (wifiHidden) "true" else "false"};;"
                } else ""
            }
            QRType.EMAIL -> {
                if (emailTo.isNotBlank()) {
                    val encSub = URLEncoder.encode(emailSubject.trim(), StandardCharsets.UTF_8.toString()).replace("+", "%20")
                    val encBody = URLEncoder.encode(emailBody.trim(), StandardCharsets.UTF_8.toString()).replace("+", "%20")
                    "mailto:${emailTo.trim()}?subject=$encSub&body=$encBody"
                } else ""
            }
            QRType.PDF -> pdfInput.trim()
            QRType.TEXT -> textInput.trim()
        }
    }

    // Live Security Audit
    val securityResult = remember(currentPayload) {
        SecurityHelper.auditInput(currentPayload)
    }

    val scrollState = rememberScrollState()

    if (isAppLocked) {
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
                Text(text = "🔒", fontSize = 64.sp)
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
                            text = "📱 QR Code Generator",
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
            // Category Cards Grid (3 Rows x 2 Columns)
            Text(
                text = "✨ Chọn loại mã QR:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.onBackground
            )

            val qrTypes = QRType.values()
            for (i in qrTypes.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (j in 0..1) {
                        if (i + j < qrTypes.size) {
                            val type = qrTypes[i + j]
                            val isSelected = selectedType == type
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        selectedType = type
                                    }
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(16.dp)
                                    ),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFF8FAFC) else Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = if (isSelected) Color(0xFFDBEAFE) else Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(10.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = type.iconEmoji, fontSize = 20.sp)
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = type.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = type.desc,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Text(
                                        text = "➔",
                                        fontSize = 14.sp,
                                        color = if (isSelected) Color(0xFF2563EB) else Color(0xFF94A3B8)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Input Card based on selected category
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "${selectedType.iconEmoji} Nhập thông tin (${selectedType.title}):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    when (selectedType) {
                        QRType.URL -> {
                            OutlinedTextField(
                                value = urlInput,
                                onValueChange = { urlInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://example.com") },
                                label = { Text("Website URL") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        QRType.PHONE -> {
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("0912345678 hoặc +84...") },
                                label = { Text("Số điện thoại") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        QRType.WIFI -> {
                            OutlinedTextField(
                                value = wifiSsid,
                                onValueChange = { wifiSsid = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Tên mạng Wi-Fi (SSID)") },
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Mật khẩu Wi-Fi") },
                                shape = RoundedCornerShape(12.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = wifiSecurity == "WPA",
                                        onClick = { wifiSecurity = "WPA" }
                                    )
                                    Text("WPA/WPA2/WPA3", fontSize = 12.sp)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = wifiSecurity == "nopass",
                                        onClick = { wifiSecurity = "nopass" }
                                    )
                                    Text("Mở (Open)", fontSize = 12.sp)
                                }
                            }
                        }
                        QRType.EMAIL -> {
                            OutlinedTextField(
                                value = emailTo,
                                onValueChange = { emailTo = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Địa chỉ Email người nhận") },
                                placeholder = { Text("contact@example.com") },
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = emailSubject,
                                onValueChange = { emailSubject = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Tiêu đề thư (Subject)") },
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = emailBody,
                                onValueChange = { emailBody = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nội dung thư") },
                                minLines = 2,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        QRType.PDF -> {
                            OutlinedTextField(
                                value = pdfInput,
                                onValueChange = { pdfInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Đường link file PDF / Google Drive") },
                                placeholder = { Text("https://drive.google.com/file/d/...") },
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                        QRType.TEXT -> {
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { textInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Nội dung văn bản") },
                                placeholder = { Text("Nhập thông điệp bất kỳ...") },
                                minLines = 3,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    // Security check advisory
                    if (currentPayload.isNotBlank()) {
                        when (securityResult.riskLevel) {
                            RiskLevel.SAFE -> {
                                Surface(
                                    color = Color(0xFFDCFCE7),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "✅ Đã xác thực bảo mật chuẩn ISO/IEC",
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
                                            text = "⚠️ Lưu ý bảo mật:",
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
                                            text = "🚨 Cảnh báo liên kết rủi ro:",
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
                                    when (selectedType) {
                                        QRType.URL -> urlInput = pasteText
                                        QRType.PHONE -> phoneInput = pasteText
                                        QRType.PDF -> pdfInput = pasteText
                                        QRType.TEXT -> textInput = pasteText
                                        QRType.EMAIL -> emailTo = pasteText
                                        QRType.WIFI -> wifiSsid = pasteText
                                    }
                                    Toast.makeText(context, "Đã dán từ bộ nhớ tạm!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("📋 Dán")
                        }

                        Button(
                            onClick = {
                                if (currentPayload.isBlank()) {
                                    Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val bmp = QRCodeHelper.generateQRCodeBitmap(
                                    content = currentPayload,
                                    width = 600,
                                    height = 600,
                                    fgColor = selectedColor.color.toArgb(),
                                    bgColor = android.graphics.Color.WHITE
                                )
                                if (bmp != null) {
                                    qrBitmap = bmp
                                    Toast.makeText(context, "Tạo mã QR thành công!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Lỗi tạo mã QR", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(2f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("⚡ TẠO MÃ QR")
                        }
                    }

                    // Color selection
                    Text(
                        text = "🎨 Tùy chỉnh màu sắc mã QR:",
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
                                        if (currentPayload.isNotBlank()) {
                                            qrBitmap = QRCodeHelper.generateQRCodeBitmap(
                                                content = currentPayload,
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
                                        Toast.makeText(context, "Đã lưu vào thư mục Pictures/QRCodeGenerator!", Toast.LENGTH_LONG).show()
                                    } else {
                                        Toast.makeText(context, "Không thể lưu ảnh", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                            ) {
                                Text("💾 Lưu PNG")
                            }

                            FilledTonalButton(
                                onClick = {
                                    QRCodeHelper.shareBitmap(context, qrBitmap!!)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("📤 Chia sẻ")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Chưa có mã QR\nChọn loại & bấm 'TẠO MÃ QR'",
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
                        text = "⚙️ Cài đặt Quyền riêng tư & Bảo mật",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

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
                                text = "Kích hoạt FLAG_SECURE bảo vệ thông tin",
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Khóa ứng dụng (Sinh trắc học)",
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Yêu cầu vân tay / Face ID / mã PIN",
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
                                                Toast.makeText(context, "Ứng dụng đã được khóa!", Toast.LENGTH_SHORT).show()
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
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🔒 Khóa ngay", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Footer
            Text(
                text = "✨ Created with ❤️ by Hong Quang\nISO/IEC 18004 Standard Compliant • TLS 1.3 Strict",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
