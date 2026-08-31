# 📱 Modern QR Code Generator & Security Suite

A lightweight, zero-configuration desktop application, mobile Android app, and command-line tool to generate standard, high-resolution QR codes from any website link or confidential text.

Fully compliant with **ISO/IEC 18004** standards — guaranteed **100% scannable** on all iOS, Android, and camera devices.

> **Author**: Created by **Hong Quang**  
> **Repository**: [https://github.com/hongquang699/Qrcode](https://github.com/hongquang699/Qrcode)

---

## 🛡️ Enterprise Security & Privacy Architecture

Our application integrates industry-grade security standards across Desktop and Mobile platforms:

1. **🔒 Zero-Knowledge & In-Memory Processing**:
   - 100% Offline execution. No URL, prompt, or sensitive text is ever transmitted to external servers.
2. **🌐 Strict Network Security Configuration (TLS 1.3 / HTTPS)**:
   - Cleartext HTTP communication is strictly blocked (`cleartextTrafficPermitted="false"`).
3. **🔍 Real-Time URL Security & Phishing Auditor**:
   - Automatically inspects URLs for unencrypted connections, raw IP destinations, suspicious redirect shorteners, and dangerous URI schemes (`javascript:`, `data:`).
4. **👁️ Anti-Snooping Screen Protection (`FLAG_SECURE`)**:
   - Built-in toggle to block screenshots and prevent screen recording/app preview sniffing in Recent Apps.
5. **🔐 Biometric App Lock & Authorization**:
   - Integrated biometric hardware authentication (Fingerprint, Face Unlock, Device PIN) to protect application access.
6. **📦 Scoped Storage & Safe FileProvider**:
   - Export images securely via Android `MediaStore` and `FileProvider` (`content://` URI scheme) without requiring broad device storage permissions.

---

## ✨ Features

- 🖱️ **1-Click Launch**: Instant desktop launch via `run.bat` (no terminal commands needed).
- 📱 **Android Mobile Application**: Native Jetpack Compose app with Material 3 UI and built-in security suite.
- 🖥️ **Modern Desktop GUI**: Clean interface with instant live preview & real-time security auditor.
- 📋 **Clipboard Integration**: Quick-paste links with the built-in Paste button or `Ctrl + V`.
- 🎨 **Color Customization**: Customize foreground and background colors with a visual color picker.
- 📦 **Multiple Export Formats**:
  - **PNG**: Crisp raster image for web and printing.
  - **SVG**: Infinite-resolution vector format for design and large-scale printing.
- ⚡ **CLI & Scripting Support**: Generate QR codes programmatically or interactively in the terminal.
- 🔄 **1-Click GitHub Sync**: Push & backup code to GitHub with [**`run_push.bat`**](file:///c:/Users/HOA%20BINH/QRcode/run_push.bat).

---

## 🚀 Getting Started

### Method 1: 1-Click Desktop App (Recommended)

Simply double-click:

👉 [**`run.bat`**](file:///c:/Users/HOA%20BINH/QRcode/run.bat)

1. Paste your website URL into the input field.
2. *(Optional)* Pick custom foreground/background colors.
3. Click **⚡ GENERATE QR CODE** to see the live preview.
4. Click **💾 Save PNG / SVG** to export your QR code.

---

### Method 2: Android App (`android_app`)

The Android application is located in the `android_app/` folder.
- Built with Kotlin, Jetpack Compose, Material 3, Biometrics, and ZXing Core.
- Enforces strict network security, Scoped Storage, and `FLAG_SECURE` screen protection.

---

### Method 3: Command Line Interface (CLI)

#### 1. Interactive Mode
Run the script without arguments to start the step-by-step wizard:
```bash
python generate_qr.py
```

#### 2. Direct Command Mode
Generate QR codes in a single command with customizable options:

```bash
# Basic QR code
python generate_qr.py "https://example.com" -o "my_qr.png"

# Vector SVG output
python generate_qr.py "https://example.com" -o "vector_qr.svg"

# Custom colors and module size
python generate_qr.py "https://github.com" -o "github_qr.png" --fg "#2563eb" --bg "#ffffff" --size 12 --border 4
```

---

## 🔄 1-Click GitHub Synchronization

To push all changes to GitHub:
- **Option 1 (1-Click)**: Double-click [**`run_push.bat`**](file:///c:/Users/HOA%20BINH/QRcode/run_push.bat).
- **Option 2 (CLI)**: Run `python github_service.py "Your commit message"`

Target Repository: `https://github.com/hongquang699/Qrcode.git`

---

## 📂 Project Structure

```text
QRcode/
├── run.bat            # 1-Click launcher for Desktop GUI
├── run_push.bat       # 1-Click launcher to Push/Sync code to GitHub
├── app_gui.py         # Tkinter Desktop GUI application with Security Auditor
├── generate_qr.py     # CLI and core QR generator engine
├── github_service.py  # GitHub automated/manual sync service
├── android_app/       # Android Jetpack Compose native mobile app
│   ├── app/           # App module (MainActivity, MainScreen, SecurityHelper, QRCodeHelper)
│   └── build.gradle.kts
├── qrcode/            # Bundled ISO/IEC standard QR engine
└── README.md          # Project documentation
```

---

## 👨‍💻 Author

Created with ❤️ by **Hong Quang**.

## 📄 License

Open-source and free to use for personal or commercial projects.
