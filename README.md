# 📱 Modern QR Code Generator

A lightweight, zero-configuration desktop application and command-line tool to generate standard, high-resolution QR codes from any website link or text.

Fully compliant with **ISO/IEC 18004** standards — guaranteed **100% scannable** on all iOS, Android, and camera devices.

---

## ✨ Features

- 🖱️ **1-Click Launch**: Instant desktop launch via `run.bat` (no terminal commands needed).
- 🖥️ **Modern Desktop GUI**: Clean interface with instant live preview.
- 📋 **Clipboard Integration**: Quick-paste links with the built-in Paste button or `Ctrl + V`.
- 🎨 **Color Customization**: Customize foreground and background colors with a visual color picker.
- 📦 **Multiple Export Formats**:
  - **PNG**: Crisp raster image for web and printing.
  - **SVG**: Infinite-resolution vector format for design and large-scale printing.
- ⚡ **CLI & Scripting Support**: Generate QR codes programmatically or interactively in the terminal.
- 🔄 **1-Click GitHub Sync**: Push & backup code to GitHub with [**`run_push.bat`**](file:///c:/Users/HOA%20BINH/QRcode/run_push.bat).
- 🛡️ **Self-Contained**: Pre-bundled standard engine — works out of the box without complex dependencies.

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

### Method 2: Command Line Interface (CLI)

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

## 🛠️ CLI Options Reference

| Flag | Full Option | Default | Description |
| :--- | :--- | :--- | :--- |
| `url` | *(positional)* | *None* | Website URL or text content to encode |
| `-o` | `--output` | `qrcode.png` | Output file path (`.png` or `.svg`) |
| | `--size` | `10` | Pixel size of each QR code box |
| | `--border` | `4` | Border margin width (in boxes) |
| | `--fg` | `black` | Foreground color (name or Hex like `#2563eb`) |
| | `--bg` | `white` | Background color (name or Hex like `#ffffff`) |
| `-h` | `--help` | | Show help message and exit |

---

## 📂 Project Structure

```text
QRcode/
├── run.bat            # 1-Click launcher for Desktop GUI
├── run_push.bat       # 1-Click launcher to Push/Sync code to GitHub
├── app_gui.py         # Tkinter Desktop GUI application
├── generate_qr.py     # CLI and core QR generator script
├── github_service.py  # GitHub automated/manual sync service
├── qrcode/            # Bundled ISO/IEC standard QR engine
├── requirements.txt   # Dependencies list
└── README.md          # Project documentation
```

---

## 📄 License

Open-source and free to use for personal or commercial projects.
