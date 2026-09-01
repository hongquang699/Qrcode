"""
============================================================
QR CODE GENERATOR - BILINGUAL DESKTOP APPLICATION (VI / EN)
ISO/IEC 18004 Standard Compliant - 100% Scannable Everywhere
Created by Hong Quang
============================================================
"""

import os
import sys
import urllib.parse
import tkinter as tk
from tkinter import ttk, messagebox, filedialog, colorchooser

# Add current directory to sys.path
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

import qrcode
from generate_qr import generate_qr, parse_color_to_rgb

STRINGS = {
    "vi": {
        "title": "📱 TRÌNH TẠO MÃ QR & BẢO MẬT",
        "subtitle": "Được tạo bởi Hong Quang • Chuẩn ISO/IEC 18004",
        "cat_title": "Chọn loại mã QR cần tạo:",
        "sec_badge_default": "🔒 100% Xử lý ngoại tuyến • Bảo mật bộ nhớ",
        "sec_safe": "✅ Liên kết an toàn: Mã hóa với SSL/TLS (HTTPS)",
        "sec_http": "⚠️ Lưu ý: Kết nối HTTP không được mã hóa!",
        "sec_danger": "🚨 Cảnh báo: Định dạng liên kết có rủi ro cao!",
        "sec_wifi_open": "🔓 Mạng Wi-Fi mở (Không mật khẩu): Quét mã là kết nối trực tiếp",
        "sec_wifi_sec": "🔒 Wi-Fi bảo mật với mật khẩu (WPA/WPA2/WPA3)",
        "sec_phone": "📞 Giao thức quay số điện thoại tiêu chuẩn",
        "btn_fg": "⬛ Màu mã QR",
        "btn_bg": "⬜ Màu nền",
        "btn_generate": "⚡ TẠO MÃ QR (GENERATE)",
        "preview_title": " Bản xem trước QR ",
        "placeholder": "Chưa có mã QR\nChọn loại & bấm 'TẠO MÃ QR'",
        "btn_save": "💾 Lưu ảnh PNG / SVG",
        "btn_open": "📂 Mở file vừa lưu",
        "footer": "✨ Được tạo bởi Hong Quang | Bản bảo mật doanh nghiệp",
        "paste": "📋 Dán",
        "save_success": "Đã lưu mã QR thành công tại:\n\n",
        "empty_err": "Vui lòng nhập đầy đủ thông tin!",
        "lang_btn": "🇬🇧 English",
        "cats": {
            "url": {"name": "URL", "desc": "Mở trang web"},
            "phone": {"name": "Điện thoại", "desc": "Gọi số điện thoại"},
            "wifi": {"name": "Wi-Fi", "desc": "Kết nối Wi-Fi"},
            "email": {"name": "E-mail", "desc": "Bản thảo email"},
            "pdf": {"name": "PDF", "desc": "Chia sẻ PDF"},
            "text": {"name": "Văn bản", "desc": "Thông điệp tùy chỉnh"},
        },
        "labels": {
            "url_box": " 🌐 Nhập địa chỉ Website URL ",
            "phone_box": " 📞 Nhập số điện thoại cần gọi ",
            "wifi_box": " 📶 Cấu hình Wi-Fi tự động kết nối ",
            "email_box": " ✉️ Soạn thư E-mail nhanh ",
            "pdf_box": " 📄 Đường dẫn chia sẻ File / PDF ",
            "text_box": " 📝 Nhập văn bản tùy ý ",
            "wifi_ssid": "Tên Wi-Fi (SSID):",
            "wifi_pass": "Mật khẩu:",
            "wifi_sec": "Mã hóa:",
            "wifi_hidden": "Mạng ẩn",
            "email_to": "Gửi đến:",
            "email_sub": "Tiêu đề:",
            "email_body": "Nội dung:",
            "wifi_opts": ["WPA/WPA2/WPA3", "Không mật khẩu (Open)", "WEP"]
        }
    },
    "en": {
        "title": "📱 QR CODE GENERATOR & SECURITY SUITE",
        "subtitle": "Created by Hong Quang • ISO/IEC 18004 Standard Compliant",
        "cat_title": "Select QR Code Category:",
        "sec_badge_default": "🔒 100% Offline Processing • In-Memory Protection",
        "sec_safe": "✅ Secure Link: Encrypted with SSL/TLS (HTTPS)",
        "sec_http": "⚠️ Advisory: Unencrypted HTTP connection detected!",
        "sec_danger": "🚨 Warning: High risk URI scheme detected!",
        "sec_wifi_open": "🔓 Open Wi-Fi (No Password): Devices connect directly upon scanning",
        "sec_wifi_sec": "🔒 Password Protected Wi-Fi (WPA/WPA2/WPA3)",
        "sec_phone": "📞 Standard Direct Phone Dialer Scheme",
        "btn_fg": "⬛ QR Color",
        "btn_bg": "⬜ Background",
        "btn_generate": "⚡ GENERATE QR CODE",
        "preview_title": " QR Code Preview ",
        "placeholder": "No QR code yet\nSelect category & click 'GENERATE QR CODE'",
        "btn_save": "💾 Save PNG / SVG",
        "btn_open": "📂 Open Saved File",
        "footer": "✨ Created with ❤️ by Hong Quang | Enterprise Security Edition",
        "paste": "📋 Paste",
        "save_success": "QR Code successfully saved at:\n\n",
        "empty_err": "Please fill in all required details!",
        "lang_btn": "🇻🇳 Tiếng Việt",
        "cats": {
            "url": {"name": "URL", "desc": "Open website"},
            "phone": {"name": "Phone", "desc": "Dial phone number"},
            "wifi": {"name": "Wi-Fi", "desc": "Connect Wi-Fi"},
            "email": {"name": "E-mail", "desc": "Compose email"},
            "pdf": {"name": "PDF", "desc": "Share PDF/file"},
            "text": {"name": "Text", "desc": "Custom message"},
        },
        "labels": {
            "url_box": " 🌐 Enter Website Link (URL) ",
            "phone_box": " 📞 Enter Phone Number to Dial ",
            "wifi_box": " 📶 Wi-Fi Network Configuration ",
            "email_box": " ✉️ Quick E-mail Composer ",
            "pdf_box": " 📄 Document / PDF Cloud Link ",
            "text_box": " 📝 Custom Text Content ",
            "wifi_ssid": "Wi-Fi Name (SSID):",
            "wifi_pass": "Password:",
            "wifi_sec": "Security:",
            "wifi_hidden": "Hidden SSID",
            "email_to": "To Email:",
            "email_sub": "Subject:",
            "email_body": "Message:",
            "wifi_opts": ["WPA/WPA2/WPA3", "No Password (Open)", "WEP"]
        }
    }
}

CATEGORIES_META = [
    {"id": "url", "icon": "🌐"},
    {"id": "phone", "icon": "📞"},
    {"id": "wifi", "icon": "📶"},
    {"id": "email", "icon": "✉️"},
    {"id": "pdf", "icon": "📄"},
    {"id": "text", "icon": "📝"},
]

class QRCodeGUIApp:
    def __init__(self, root):
        self.root = root
        self.current_lang = "vi"
        self.root.title("QR Code Generator - by Hong Quang")
        self.root.geometry("640x780")
        self.root.resizable(False, False)
        self.root.configure(bg="#f8fafc")

        self.selected_category = "url"
        self.current_payload = ""
        self.current_matrix = None
        self.fg_color = "#000000"
        self.bg_color = "#ffffff"
        self.last_saved_path = None

        # Set window icon
        icon_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "app_icon.ico")
        if os.path.exists(icon_path):
            try:
                self.root.iconbitmap(icon_path)
            except Exception:
                pass

        self.create_widgets()
        self.switch_category("url")

    def tr(self, key):
        return STRINGS[self.current_lang].get(key, "")

    def toggle_language(self):
        self.current_lang = "en" if self.current_lang == "vi" else "vi"
        self.btn_lang.config(text=self.tr("lang_btn"))
        self.title_lbl.config(text=self.tr("title"))
        self.author_header.config(text=self.tr("subtitle"))
        self.cat_lbl.config(text=self.tr("cat_title"))
        self.btn_fg.config(text=self.tr("btn_fg"))
        self.btn_bg.config(text=self.tr("btn_bg"))
        self.btn_gen.config(text=self.tr("btn_generate"))
        self.preview_box.config(text=self.tr("preview_title"))
        self.btn_save.config(text=self.tr("btn_save"))
        self.btn_open.config(text=self.tr("btn_open"))
        self.footer.config(text=self.tr("footer"))

        # Update category buttons text
        cat_dict = STRINGS[self.current_lang]["cats"]
        for cat in CATEGORIES_META:
            cid = cat["id"]
            if cid in self.cat_buttons:
                cinfo = cat_dict[cid]
                self.cat_buttons[cid].config(text=f"{cat['icon']} {cinfo['name']}\n{cinfo['desc']}")

        self.switch_category(self.selected_category)

    def create_widgets(self):
        # Header banner
        header = tk.Frame(self.root, bg="#2563eb", height=70)
        header.pack(fill="x")

        # Language switcher in top right of header
        self.btn_lang = tk.Button(
            header,
            text=self.tr("lang_btn"),
            font=("Segoe UI", 9, "bold"),
            bg="#1d4ed8",
            fg="white",
            activebackground="#1e40af",
            activeforeground="white",
            relief="flat",
            cursor="hand2",
            padx=8,
            pady=2,
            command=self.toggle_language
        )
        self.btn_lang.pack(side="right", padx=14, pady=10)

        self.title_lbl = tk.Label(
            header,
            text=self.tr("title"),
            font=("Segoe UI", 13, "bold"),
            bg="#2563eb",
            fg="white"
        )
        self.title_lbl.pack(pady=(8, 1))

        self.author_header = tk.Label(
            header,
            text=self.tr("subtitle"),
            font=("Segoe UI", 9, "italic"),
            bg="#2563eb",
            fg="#e0e7ff"
        )
        self.author_header.pack(pady=(0, 6))

        # Main scrollable/content frame
        main_content = tk.Frame(self.root, bg="#f8fafc")
        main_content.pack(fill="both", expand=True, padx=18, pady=8)

        # 1. Category Selection Cards (Horizontal Grid 3x2)
        self.cat_lbl = tk.Label(
            main_content,
            text=self.tr("cat_title"),
            font=("Segoe UI", 10, "bold"),
            bg="#f8fafc",
            fg="#1e293b",
            anchor="w"
        )
        self.cat_lbl.pack(fill="x", pady=(0, 4))

        self.cat_cards_frame = tk.Frame(main_content, bg="#f8fafc")
        self.cat_cards_frame.pack(fill="x", pady=(0, 8))

        self.cat_buttons = {}
        cat_dict = STRINGS[self.current_lang]["cats"]
        for idx, cat in enumerate(CATEGORIES_META):
            row = idx // 3
            col = idx % 3
            cid = cat["id"]
            cinfo = cat_dict[cid]
            btn = tk.Button(
                self.cat_cards_frame,
                text=f"{cat['icon']} {cinfo['name']}\n{cinfo['desc']}",
                font=("Segoe UI", 8, "bold"),
                bg="#ffffff",
                fg="#334155",
                activebackground="#eff6ff",
                relief="groove",
                bd=1,
                cursor="hand2",
                justify="center",
                padx=4,
                pady=6,
                command=lambda c=cid: self.switch_category(c)
            )
            btn.grid(row=row, column=col, sticky="nsew", padx=3, pady=3)
            self.cat_cards_frame.columnconfigure(col, weight=1)
            self.cat_buttons[cid] = btn

        # 2. Dynamic Input Fields Container
        self.input_container = tk.LabelFrame(
            main_content,
            text=" Nhập thông tin ",
            font=("Segoe UI", 9, "bold"),
            bg="#ffffff",
            fg="#2563eb",
            relief="solid",
            bd=1,
            padx=12,
            pady=8
        )
        self.input_container.pack(fill="x", pady=(0, 8))

        # 3. Security Status Badge
        self.lbl_security = tk.Label(
            main_content,
            text=self.tr("sec_badge_default"),
            font=("Segoe UI", 8, "bold"),
            bg="#f0fdf4",
            fg="#166534",
            anchor="w",
            padx=8,
            pady=3
        )
        self.lbl_security.pack(fill="x", pady=(0, 6))

        # 4. Color & Action Buttons
        opt_frame = tk.Frame(main_content, bg="#f8fafc")
        opt_frame.pack(fill="x", pady=(0, 6))

        self.btn_fg = tk.Button(
            opt_frame,
            text=self.tr("btn_fg"),
            font=("Segoe UI", 9),
            bg="#ffffff",
            relief="groove",
            cursor="hand2",
            command=self.choose_fg_color
        )
        self.btn_fg.pack(side="left", expand=True, fill="x", padx=(0, 4))

        self.btn_bg = tk.Button(
            opt_frame,
            text=self.tr("btn_bg"),
            font=("Segoe UI", 9),
            bg="#ffffff",
            relief="groove",
            cursor="hand2",
            command=self.choose_bg_color
        )
        self.btn_bg.pack(side="left", expand=True, fill="x", padx=(4, 0))

        self.btn_gen = tk.Button(
            main_content,
            text=self.tr("btn_generate"),
            font=("Segoe UI", 10, "bold"),
            bg="#2563eb",
            fg="white",
            activebackground="#1d4ed8",
            activeforeground="white",
            relief="flat",
            cursor="hand2",
            command=self.on_generate
        )
        self.btn_gen.pack(fill="x", pady=(0, 8), ipady=4)

        # 5. Preview Canvas Container
        self.preview_box = tk.LabelFrame(
            main_content,
            text=self.tr("preview_title"),
            font=("Segoe UI", 8, "bold"),
            bg="#ffffff",
            fg="#475569",
            relief="solid",
            bd=1
        )
        self.preview_box.pack(fill="both", expand=True, pady=(0, 6))

        self.canvas = tk.Canvas(self.preview_box, bg="#ffffff", highlightthickness=0, width=220, height=220)
        self.canvas.pack(expand=True, pady=4)
        self.show_placeholder()

        # 6. Action Buttons (Save & Open)
        action_row = tk.Frame(main_content, bg="#f8fafc")
        action_row.pack(fill="x", pady=(0, 4))

        self.btn_save = tk.Button(
            action_row,
            text=self.tr("btn_save"),
            font=("Segoe UI", 9, "bold"),
            bg="#16a34a",
            fg="white",
            activebackground="#15803d",
            relief="flat",
            cursor="hand2",
            state="disabled",
            command=self.save_image
        )
        self.btn_save.pack(side="left", expand=True, fill="x", padx=(0, 4), ipady=3)

        self.btn_open = tk.Button(
            action_row,
            text=self.tr("btn_open"),
            font=("Segoe UI", 9),
            bg="#64748b",
            fg="white",
            activebackground="#475569",
            relief="flat",
            cursor="hand2",
            state="disabled",
            command=self.open_saved_image
        )
        self.btn_open.pack(side="left", expand=True, fill="x", padx=(4, 0), ipady=3)

        # Footer
        self.footer = tk.Label(
            self.root,
            text=self.tr("footer"),
            font=("Segoe UI", 8),
            bg="#f8fafc",
            fg="#94a3b8"
        )
        self.footer.pack(side="bottom", pady=(0, 4))

    def switch_category(self, cat_id):
        self.selected_category = cat_id
        labels = STRINGS[self.current_lang]["labels"]
        for cid, btn in self.cat_buttons.items():
            if cid == cat_id:
                btn.config(bg="#dbeafe", fg="#1d4ed8", bd=2, relief="solid")
            else:
                btn.config(bg="#ffffff", fg="#334155", bd=1, relief="groove")

        for widget in self.input_container.winfo_children():
            widget.destroy()

        if cat_id == "url":
            self.input_container.config(text=labels["url_box"])
            row = tk.Frame(self.input_container, bg="#ffffff")
            row.pack(fill="x", pady=2)
            self.entry_url = ttk.Entry(row, font=("Segoe UI", 10))
            self.entry_url.pack(side="left", fill="x", expand=True, ipady=3)
            self.entry_url.insert(0, "https://")
            self.entry_url.bind("<KeyRelease>", lambda e: self.audit_security(self.entry_url.get()))
            btn_paste = tk.Button(row, text=self.tr("paste"), font=("Segoe UI", 8), bg="#e2e8f0", command=lambda: self.paste_to_entry(self.entry_url))
            btn_paste.pack(side="right", padx=(6, 0))

        elif cat_id == "phone":
            self.input_container.config(text=labels["phone_box"])
            row = tk.Frame(self.input_container, bg="#ffffff")
            row.pack(fill="x", pady=2)
            self.entry_phone = ttk.Entry(row, font=("Segoe UI", 10))
            self.entry_phone.pack(side="left", fill="x", expand=True, ipady=3)
            self.entry_phone.insert(0, "09" if self.current_lang == "vi" else "+1")
            self.entry_phone.bind("<KeyRelease>", lambda e: self.audit_security(self.entry_phone.get()))
            btn_paste = tk.Button(row, text=self.tr("paste"), font=("Segoe UI", 8), bg="#e2e8f0", command=lambda: self.paste_to_entry(self.entry_phone))
            btn_paste.pack(side="right", padx=(6, 0))

        elif cat_id == "wifi":
            self.input_container.config(text=labels["wifi_box"])
            r1 = tk.Frame(self.input_container, bg="#ffffff")
            r1.pack(fill="x", pady=1)
            tk.Label(r1, text=labels["wifi_ssid"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=16, anchor="w").pack(side="left")
            self.entry_wifi_ssid = ttk.Entry(r1, font=("Segoe UI", 9))
            self.entry_wifi_ssid.pack(side="right", fill="x", expand=True)

            r2 = tk.Frame(self.input_container, bg="#ffffff")
            r2.pack(fill="x", pady=1)
            tk.Label(r2, text=labels["wifi_pass"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=16, anchor="w").pack(side="left")
            self.entry_wifi_pass = ttk.Entry(r2, font=("Segoe UI", 9), show="*")
            self.entry_wifi_pass.pack(side="right", fill="x", expand=True)

            r3 = tk.Frame(self.input_container, bg="#ffffff")
            r3.pack(fill="x", pady=1)
            tk.Label(r3, text=labels["wifi_sec"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=16, anchor="w").pack(side="left")
            self.combo_wifi_sec = ttk.Combobox(r3, values=labels["wifi_opts"], state="readonly", font=("Segoe UI", 8))
            self.combo_wifi_sec.current(0)
            self.combo_wifi_sec.pack(side="left", padx=(0, 8))
            self.combo_wifi_sec.bind("<<ComboboxSelected>>", self.on_wifi_sec_changed)

            self.var_wifi_hidden = tk.BooleanVar(value=False)
            chk_hidden = tk.Checkbutton(r3, text=labels["wifi_hidden"], variable=self.var_wifi_hidden, bg="#ffffff", font=("Segoe UI", 8))
            chk_hidden.pack(side="left")

        elif cat_id == "email":
            self.input_container.config(text=labels["email_box"])
            r1 = tk.Frame(self.input_container, bg="#ffffff")
            r1.pack(fill="x", pady=1)
            tk.Label(r1, text=labels["email_to"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=12, anchor="w").pack(side="left")
            self.entry_email_to = ttk.Entry(r1, font=("Segoe UI", 9))
            self.entry_email_to.pack(side="right", fill="x", expand=True)

            r2 = tk.Frame(self.input_container, bg="#ffffff")
            r2.pack(fill="x", pady=1)
            tk.Label(r2, text=labels["email_sub"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=12, anchor="w").pack(side="left")
            self.entry_email_sub = ttk.Entry(r2, font=("Segoe UI", 9))
            self.entry_email_sub.pack(side="right", fill="x", expand=True)

            r3 = tk.Frame(self.input_container, bg="#ffffff")
            r3.pack(fill="x", pady=1)
            tk.Label(r3, text=labels["email_body"], font=("Segoe UI", 8, "bold"), bg="#ffffff", width=12, anchor="w").pack(side="left")
            self.entry_email_body = ttk.Entry(r3, font=("Segoe UI", 9))
            self.entry_email_body.pack(side="right", fill="x", expand=True)

        elif cat_id == "pdf":
            self.input_container.config(text=labels["pdf_box"])
            row = tk.Frame(self.input_container, bg="#ffffff")
            row.pack(fill="x", pady=2)
            self.entry_pdf = ttk.Entry(row, font=("Segoe UI", 10))
            self.entry_pdf.pack(side="left", fill="x", expand=True, ipady=3)
            self.entry_pdf.insert(0, "https://drive.google.com/")
            self.entry_pdf.bind("<KeyRelease>", lambda e: self.audit_security(self.entry_pdf.get()))
            btn_paste = tk.Button(row, text=self.tr("paste"), font=("Segoe UI", 8), bg="#e2e8f0", command=lambda: self.paste_to_entry(self.entry_pdf))
            btn_paste.pack(side="right", padx=(6, 0))

        elif cat_id == "text":
            self.input_container.config(text=labels["text_box"])
            self.entry_text = tk.Text(self.input_container, font=("Segoe UI", 9), height=3, relief="solid", bd=1)
            self.entry_text.pack(fill="x", pady=2)

    def on_wifi_sec_changed(self, event=None):
        choice = self.combo_wifi_sec.get()
        if "Không" in choice or "No Password" in choice or "Open" in choice:
            self.entry_wifi_pass.delete(0, tk.END)
            self.entry_wifi_pass.config(state="disabled")
            self.lbl_security.config(text=self.tr("sec_wifi_open"), bg="#f0fdf4", fg="#166534")
        else:
            self.entry_wifi_pass.config(state="normal")
            self.lbl_security.config(text=self.tr("sec_wifi_sec"), bg="#f0fdf4", fg="#166534")

    def paste_to_entry(self, entry_widget):
        try:
            text = self.root.clipboard_get().strip()
            entry_widget.delete(0, tk.END)
            entry_widget.insert(0, text)
            self.audit_security(text)
        except Exception:
            messagebox.showwarning("Notice", "Clipboard is empty!")

    def audit_security(self, text):
        text = text.strip()
        if not text:
            self.lbl_security.config(text=self.tr("sec_badge_default"), bg="#f0fdf4", fg="#166534")
            return

        lower = text.lower()
        if lower.startswith("https://"):
            self.lbl_security.config(text=self.tr("sec_safe"), bg="#f0fdf4", fg="#166534")
        elif lower.startswith("http://"):
            self.lbl_security.config(text=self.tr("sec_http"), bg="#fef9c3", fg="#854d0e")
        elif lower.startswith("javascript:") or lower.startswith("data:"):
            self.lbl_security.config(text=self.tr("sec_danger"), bg="#fee2e2", fg="#991b1b")
        elif self.selected_category == "wifi":
            self.lbl_security.config(text=self.tr("sec_wifi_sec"), bg="#f0fdf4", fg="#166534")
        elif self.selected_category == "phone":
            self.lbl_security.config(text=self.tr("sec_phone"), bg="#f0fdf4", fg="#166534")
        else:
            self.lbl_security.config(text=self.tr("sec_badge_default"), bg="#f1f5f9", fg="#475569")

    def choose_fg_color(self):
        color = colorchooser.askcolor(title="Choose Color", initialcolor=self.fg_color)
        if color[1]:
            self.fg_color = color[1]
            self.btn_fg.config(text=f"{self.tr('btn_fg')} ({self.fg_color})")
            if self.current_matrix:
                self.draw_matrix_on_canvas(self.current_matrix)

    def choose_bg_color(self):
        color = colorchooser.askcolor(title="Choose Background", initialcolor=self.bg_color)
        if color[1]:
            self.bg_color = color[1]
            self.btn_bg.config(text=f"{self.tr('btn_bg')} ({self.bg_color})")
            if self.current_matrix:
                self.draw_matrix_on_canvas(self.current_matrix)

    def build_payload(self):
        cid = self.selected_category
        if cid == "url":
            val = self.entry_url.get().strip()
            if not val or val == "https://":
                raise ValueError(self.tr("empty_err"))
            return val

        elif cid == "phone":
            val = self.entry_phone.get().strip()
            if not val:
                raise ValueError(self.tr("empty_err"))
            clean_phone = val.replace(" ", "").replace("-", "")
            return f"tel:{clean_phone}"

        elif cid == "wifi":
            ssid = self.entry_wifi_ssid.get().strip()
            if not ssid:
                raise ValueError(self.tr("empty_err"))
            sec_choice = self.combo_wifi_sec.get()
            sec_type = "WPA"
            if "WEP" in sec_choice:
                sec_type = "WEP"
            elif "Không" in sec_choice or "No Password" in sec_choice or "Open" in sec_choice:
                sec_type = "nopass"

            pwd = self.entry_wifi_pass.get() if sec_type != "nopass" else ""
            hidden = "true" if self.var_wifi_hidden.get() else "false"
            if sec_type == "nopass":
                return f"WIFI:T:nopass;S:{ssid};H:{hidden};;"
            else:
                return f"WIFI:T:{sec_type};S:{ssid};P:{pwd};H:{hidden};;"

        elif cid == "email":
            to = self.entry_email_to.get().strip()
            if not to:
                raise ValueError(self.tr("empty_err"))
            sub = urllib.parse.quote(self.entry_email_sub.get().strip())
            body = urllib.parse.quote(self.entry_email_body.get().strip())
            return f"mailto:{to}?subject={sub}&body={body}"

        elif cid == "pdf":
            val = self.entry_pdf.get().strip()
            if not val:
                raise ValueError(self.tr("empty_err"))
            return val

        elif cid == "text":
            val = self.entry_text.get("1.0", tk.END).strip()
            if not val:
                raise ValueError(self.tr("empty_err"))
            return val

        return ""

    def on_generate(self):
        try:
            payload = self.build_payload()
            qr = qrcode.QRCode(
                version=None,
                error_correction=qrcode.constants.ERROR_CORRECT_M,
                box_size=10,
                border=4,
            )
            qr.add_data(payload)
            qr.make(fit=True)
            self.current_matrix = qr.get_matrix()
            self.current_payload = payload

            self.draw_matrix_on_canvas(self.current_matrix)
            self.btn_save.config(state="normal")
            self.audit_security(payload)

        except Exception as e:
            messagebox.showerror("Notice", str(e))

    def show_placeholder(self):
        self.canvas.delete("all")
        self.canvas.create_text(
            110, 110,
            text=self.tr("placeholder"),
            font=("Segoe UI", 9),
            fill="#94a3b8",
            justify="center"
        )

    def draw_matrix_on_canvas(self, matrix):
        self.canvas.delete("all")
        rows = len(matrix)
        cols = len(matrix[0])
        canvas_width = 220
        cell_size = canvas_width / max(rows, cols)

        self.canvas.create_rectangle(0, 0, canvas_width, canvas_width, fill=self.bg_color, outline="")

        for r in range(rows):
            for c in range(cols):
                if matrix[r][c]:
                    x1 = c * cell_size
                    y1 = r * cell_size
                    x2 = x1 + cell_size
                    y2 = y1 + cell_size
                    self.canvas.create_rectangle(x1, y1, x2, y2, fill=self.fg_color, outline="")

    def save_image(self):
        if not self.current_payload:
            return

        file_path = filedialog.asksaveasfilename(
            defaultextension=".png",
            filetypes=[
                ("PNG Image (*.png)", "*.png"),
                ("SVG Vector Image (*.svg)", "*.svg"),
                ("All Files (*.*)", "*.*")
            ],
            initialfile="my_qrcode.png",
            title="Save QR Code"
        )
        if file_path:
            try:
                saved = generate_qr(
                    self.current_payload,
                    file_path,
                    box_size=12,
                    border=4,
                    fg_color=self.fg_color,
                    bg_color=self.bg_color
                )
                self.last_saved_path = saved
                self.btn_open.config(state="normal")
                messagebox.showinfo("Success 🎉", f"{self.tr('save_success')}{saved}")
            except Exception as e:
                messagebox.showerror("Error", f"Could not save file: {e}")

    def open_saved_image(self):
        if self.last_saved_path and os.path.exists(self.last_saved_path):
            try:
                os.startfile(self.last_saved_path)
            except Exception as e:
                messagebox.showerror("Error", f"Could not open file: {e}")

def main():
    root = tk.Tk()
    app = QRCodeGUIApp(root)
    root.mainloop()

if __name__ == "__main__":
    main()
