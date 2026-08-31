# 📱 QR Code Generator

A fast, lightweight, and ISO/IEC 18004 standard-compliant QR Code generator for Windows.

---

## ⚡ Quick Start (1-Click Run)

Just double-click the batch file:

👉 [**`run.bat`**](file:///c:/Users/HOA%20BINH/QRcode/run.bat)

- Automatically opens the **Desktop GUI**.
- Paste link (`Ctrl + V` or click **Paste** button).
- Click **GENERATE QR CODE** to preview.
- Click **Save PNG / SVG** to download the high-resolution QR image.

---

## 🚀 Command Line Usage (CLI)

### 1. Interactive Prompt Mode:
```bash
python generate_qr.py
```

### 2. Single-line Command:
```bash
# Basic QR code
python generate_qr.py "https://google.com" -o "google.png"

# Custom colors and size
python generate_qr.py "https://github.com" -o "github.png" --fg "#2563eb" --bg "#ffffff" --size 12
```

### 3. Open Desktop GUI directly:
```bash
python app_gui.py
```
