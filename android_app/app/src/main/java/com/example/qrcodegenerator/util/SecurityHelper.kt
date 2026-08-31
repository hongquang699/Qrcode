package com.example.qrcodegenerator.util

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.WindowManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.net.URI
import java.util.regex.Pattern

data class SecurityCheckResult(
    val isSafe: Boolean,
    val riskLevel: RiskLevel,
    val warnings: List<String>
)

enum class RiskLevel {
    SAFE,
    WARNING,
    DANGEROUS
}

object SecurityHelper {

    private val IP_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
    private val KNOWN_SHORTENERS = setOf("bit.ly", "tinyurl.com", "t.co", "is.gd", "buff.ly", "ow.ly")

    /**
     * Inspects input text/URL for security risks (phishing, unencrypted HTTP, suspicious IP, dangerous schemes).
     */
    fun auditInput(input: String): SecurityCheckResult {
        val trimmed = input.trim()
        if (trimmed.isBlank()) {
            return SecurityCheckResult(isSafe = true, riskLevel = RiskLevel.SAFE, warnings = emptyList())
        }

        val warnings = mutableListOf<String>()
        var riskLevel = RiskLevel.SAFE

        val lower = trimmed.lowercase()

        // Check dangerous schemes
        if (lower.startsWith("javascript:") || lower.startsWith("data:") || lower.startsWith("file:")) {
            warnings.add("⚠️ Potentially malicious URI scheme detected (${trimmed.take(15)}...)")
            riskLevel = RiskLevel.DANGEROUS
        }

        if (lower.startsWith("http://") || lower.startsWith("https://")) {
            try {
                val uri = URI(trimmed)
                val host = uri.host ?: ""

                // 1. HTTP vs HTTPS check
                if (lower.startsWith("http://")) {
                    warnings.add("🔓 Unencrypted HTTP: Data sent via this link is not secured with SSL/TLS.")
                    if (riskLevel == RiskLevel.SAFE) riskLevel = RiskLevel.WARNING
                }

                // 2. Direct IP Address check
                if (IP_PATTERN.matcher(host).matches()) {
                    warnings.add("🚨 Direct IP Host ($host): Legitimate websites rarely use raw IP addresses.")
                    riskLevel = RiskLevel.DANGEROUS
                }

                // 3. Shortened URL check
                if (KNOWN_SHORTENERS.any { host.contains(it) }) {
                    warnings.add("ℹ️ URL Shortener detected ($host): Target destination is obfuscated.")
                    if (riskLevel == RiskLevel.SAFE) riskLevel = RiskLevel.WARNING
                }

                // 4. Non-standard port check
                if (uri.port != -1 && uri.port != 80 && uri.port != 443 && uri.port != 8080) {
                    warnings.add("⚠️ Unusual port number (${uri.port}) detected in URL.")
                    if (riskLevel == RiskLevel.SAFE) riskLevel = RiskLevel.WARNING
                }
            } catch (e: Exception) {
                warnings.add("⚠️ Malformed URL structure.")
                if (riskLevel == RiskLevel.SAFE) riskLevel = RiskLevel.WARNING
            }
        }

        return SecurityCheckResult(
            isSafe = riskLevel == RiskLevel.SAFE,
            riskLevel = riskLevel,
            warnings = warnings
        )
    }

    /**
     * Toggles FLAG_SECURE to prevent screenshot capture & hide contents in Recent Apps.
     */
    fun setScreenshotProtection(activity: Activity, enable: Boolean) {
        if (enable) {
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
        } else {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    /**
     * Checks if biometric hardware is ready.
     */
    fun canAuthenticateBiometrics(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return biometricManager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    /**
     * Launches Biometric / PIN authentication prompt.
     */
    fun authenticate(
        activity: FragmentActivity,
        title: String = "App Lock Verification",
        subtitle: String = "Verify your fingerprint or face to unlock",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Authentication failed. Please try again.")
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()

        prompt.authenticate(promptInfo)
    }
}
