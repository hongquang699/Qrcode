import SwiftUI
import Photos

struct ColorPreset: Identifiable {
    let id = UUID()
    let name: String
    let color: Color
    let uiColor: UIColor
}

let colorPresets: [ColorPreset] = [
    ColorPreset(name: "Classic", color: .black, uiColor: .black),
    ColorPreset(name: "Blue", color: Color(red: 0.15, green: 0.39, blue: 0.92), uiColor: UIColor(red: 0.15, green: 0.39, blue: 0.92, alpha: 1)),
    ColorPreset(name: "Navy", color: Color(red: 0.12, green: 0.23, blue: 0.54), uiColor: UIColor(red: 0.12, green: 0.23, blue: 0.54, alpha: 1)),
    ColorPreset(name: "Emerald", color: Color(red: 0.02, green: 0.59, blue: 0.41), uiColor: UIColor(red: 0.02, green: 0.59, blue: 0.41, alpha: 1)),
    ColorPreset(name: "Purple", color: Color(red: 0.49, green: 0.23, blue: 0.93), uiColor: UIColor(red: 0.49, green: 0.23, blue: 0.93, alpha: 1)),
    ColorPreset(name: "Crimson", color: Color(red: 0.86, green: 0.15, blue: 0.15), uiColor: UIColor(red: 0.86, green: 0.15, blue: 0.15, alpha: 1))
]

struct ContentView: View {
    @State private var selectedCategory: QRCategory = .url
    @State private var selectedColor: ColorPreset = colorPresets[0]
    @State private var generatedImage: UIImage? = nil
    @State private var isAppLocked: Bool = false
    @State private var alertMessage: String = ""
    @State private var showAlert: Bool = false
    @State private var isSharePresented: Bool = false

    // Input States
    @State private var urlText: String = "https://"
    @State private var phoneText: String = ""
    @State private var wifiSsid: String = ""
    @State private var wifiPass: String = ""
    @State private var wifiIsOpen: Bool = false
    @State private var wifiIsHidden: Bool = false
    @State private var emailTo: String = ""
    @State private var emailSubject: String = ""
    @State private var emailBody: String = ""
    @State private var pdfUrl: String = "https://drive.google.com/"
    @State private var plainText: String = ""

    var computedPayload: String {
        switch selectedCategory {
        case .url:
            return urlText.trimmingCharacters(in: .whitespacesAndNewlines)
        case .phone:
            let clean = phoneText.replacingOccurrences(of: " ", with: "").replacingOccurrences(of: "-", with: "")
            return clean.isEmpty ? "" : "tel:\(clean)"
        case .wifi:
            guard !wifiSsid.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return "" }
            let sec = wifiIsOpen ? "nopass" : "WPA"
            let hidden = wifiIsHidden ? "true" : "false"
            if wifiIsOpen {
                return "WIFI:T:nopass;S:\(wifiSsid.trimmingCharacters(in: .whitespacesAndNewlines));H:\(hidden);;"
            } else {
                return "WIFI:T:\(sec);S:\(wifiSsid.trimmingCharacters(in: .whitespacesAndNewlines));P:\(wifiPass);H:\(hidden);;"
            }
        case .email:
            guard !emailTo.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return "" }
            let sub = emailSubject.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
            let body = emailBody.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed) ?? ""
            return "mailto:\(emailTo.trimmingCharacters(in: .whitespacesAndNewlines))?subject=\(sub)&body=\(body)"
        case .pdf:
            return pdfUrl.trimmingCharacters(in: .whitespacesAndNewlines)
        case .text:
            return plainText.trimmingCharacters(in: .whitespacesAndNewlines)
        }
    }

    var body: some View {
        NavigationView {
            if isAppLocked {
                // Face ID Lock Screen
                VStack(spacing: 20) {
                    Image(systemName: "lock.shield.fill")
                        .font(.system(size: 70))
                        .foregroundColor(.blue)
                    Text("Ứng dụng đang khóa")
                        .font(.title2.bold())
                    Text("Yêu cầu xác thực Face ID / Touch ID để tiếp tục")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal)

                    Button(action: unlockApp) {
                        Label("Mở khóa bằng Face ID", systemImage: "faceid")
                            .font(.headline)
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .cornerRadius(12)
                    }
                    .padding(.horizontal, 40)
                    .padding(.top, 10)
                }
                .navigationBarHidden(true)
            } else {
                ScrollView {
                    VStack(spacing: 16) {
                        // Category Cards Grid (2 Columns)
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Chọn loại mã QR")
                                .font(.headline)
                                .foregroundColor(.primary)

                            let columns = [GridItem(.flexible(), spacing: 10), GridItem(.flexible(), spacing: 10)]
                            LazyVGrid(columns: columns, spacing: 10) {
                                ForEach(QRCategory.allCases) { cat in
                                    let isSelected = selectedCategory == cat
                                    Button(action: {
                                        withAnimation(.easeInOut(duration: 0.2)) {
                                            selectedCategory = cat
                                        }
                                    }) {
                                        HStack(spacing: 10) {
                                            Image(systemName: cat.iconName)
                                                .font(.system(size: 20))
                                                .foregroundColor(isSelected ? .blue : .primary)
                                                .frame(width: 38, height: 38)
                                                .background(isSelected ? Color.blue.opacity(0.15) : Color(UIColor.tertiarySystemFill))
                                                .cornerRadius(10)

                                            VStack(alignment: .leading, spacing: 2) {
                                                Text(cat.title)
                                                    .font(.system(size: 14, weight: .bold))
                                                    .foregroundColor(isSelected ? .blue : .primary)
                                                Text(cat.description)
                                                    .font(.system(size: 10))
                                                    .foregroundColor(.secondary)
                                                    .lineLimit(2)
                                                    .multilineTextAlignment(.leading)
                                            }
                                            Spacer(minLength: 0)
                                            Image(systemName: "chevron.right")
                                                .font(.system(size: 12, weight: .semibold))
                                                .foregroundColor(isSelected ? .blue : .secondary.opacity(0.5))
                                        }
                                        .padding(10)
                                        .frame(height: 72)
                                        .background(Color(UIColor.secondarySystemGroupedBackground))
                                        .cornerRadius(14)
                                        .overlay(
                                            RoundedRectangle(cornerRadius: 14)
                                                .stroke(isSelected ? Color.blue : Color(UIColor.separator).opacity(0.3), lineWidth: isSelected ? 2 : 1)
                                        )
                                        .shadow(color: isSelected ? Color.blue.opacity(0.12) : Color.black.opacity(0.03), radius: 4, x: 0, y: 2)
                                    }
                                }
                            }
                        }

                        // Input Card
                        VStack(alignment: .leading, spacing: 12) {
                            Text("Nhập thông tin (\(selectedCategory.title))")
                                .font(.subheadline.bold())
                                .foregroundColor(.blue)

                            switch selectedCategory {
                            case .url:
                                TextField("https://example.com", text: $urlText)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                    .keyboardType(.URL)
                                    .autocapitalization(.none)

                            case .phone:
                                TextField("0912345678 hoặc +84...", text: $phoneText)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                    .keyboardType(.phonePad)

                            case .wifi:
                                TextField("Tên Wi-Fi (SSID)", text: $wifiSsid)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())

                                Toggle(isOn: $wifiIsOpen) {
                                    VStack(alignment: .leading) {
                                        Text("Mạng không có mật khẩu (Open)")
                                            .font(.subheadline)
                                        Text("Chỉ cần quét là tự động kết nối Wi-Fi")
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                }
                                .toggleStyle(SwitchToggleStyle(tint: .blue))

                                if !wifiIsOpen {
                                    SecureField("Mật khẩu Wi-Fi", text: $wifiPass)
                                        .textFieldStyle(RoundedBorderTextFieldStyle())
                                }

                                Toggle("Mạng ẩn (Hidden SSID)", isOn: $wifiIsHidden)
                                    .font(.subheadline)

                            case .email:
                                TextField("Email người nhận", text: $emailTo)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                    .keyboardType(.emailAddress)
                                    .autocapitalization(.none)
                                TextField("Tiêu đề thư", text: $emailSubject)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                TextField("Nội dung thư", text: $emailBody)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())

                            case .pdf:
                                TextField("Link file PDF / Google Drive", text: $pdfUrl)
                                    .textFieldStyle(RoundedBorderTextFieldStyle())
                                    .keyboardType(.URL)
                                    .autocapitalization(.none)

                            case .text:
                                TextEditor(text: $plainText)
                                    .frame(height: 70)
                                    .overlay(RoundedRectangle(cornerRadius: 8).stroke(Color(UIColor.separator), lineWidth: 1))
                            }

                            // Generate Button
                            Button(action: generateQR) {
                                Label("TẠO MÃ QR (GENERATE)", systemImage: "bolt.fill")
                                    .font(.headline)
                                    .foregroundColor(.white)
                                    .frame(maxWidth: .infinity)
                                    .padding(.vertical, 12)
                                    .background(Color.blue)
                                    .cornerRadius(12)
                            }
                        }
                        .padding(14)
                        .background(Color(UIColor.secondarySystemGroupedBackground))
                        .cornerRadius(16)

                        // Color Presets
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Màu sắc mã QR")
                                .font(.caption.bold())
                                .foregroundColor(.secondary)

                            HStack(spacing: 12) {
                                ForEach(colorPresets) { preset in
                                    let isSel = selectedColor.name == preset.name
                                    Circle()
                                        .fill(preset.color)
                                        .frame(width: 34, height: 34)
                                        .overlay(
                                            Circle().stroke(isSel ? Color.blue : Color.gray.opacity(0.3), lineWidth: isSel ? 3 : 1)
                                        )
                                        .onTapGesture {
                                            selectedColor = preset
                                            if generatedImage != nil {
                                                generateQR()
                                            }
                                        }
                                }
                            }
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal, 4)

                        // QR Code Preview Box
                        VStack(spacing: 14) {
                            if let img = generatedImage {
                                Image(uiImage: img)
                                    .interpolation(.none)
                                    .resizable()
                                    .scaledToFit()
                                    .frame(width: 220, height: 220)
                                    .background(Color.white)
                                    .cornerRadius(12)
                                    .shadow(color: Color.black.opacity(0.08), radius: 6, x: 0, y: 3)

                                HStack(spacing: 12) {
                                    Button(action: saveToPhotos) {
                                        Label("Lưu vào Ảnh", systemImage: "square.and.arrow.down.fill")
                                            .font(.subheadline.bold())
                                            .foregroundColor(.white)
                                            .padding(.horizontal, 16)
                                            .padding(.vertical, 10)
                                            .background(Color.green)
                                            .cornerRadius(10)
                                    }

                                    Button(action: { isSharePresented = true }) {
                                        Label("Chia sẻ", systemImage: "square.and.arrow.up")
                                            .font(.subheadline.bold())
                                            .foregroundColor(.blue)
                                            .padding(.horizontal, 16)
                                            .padding(.vertical, 10)
                                            .background(Color.blue.opacity(0.12))
                                            .cornerRadius(10)
                                    }
                                }
                            } else {
                                VStack(spacing: 8) {
                                    Image(systemName: "qrcode")
                                        .font(.system(size: 60))
                                        .foregroundColor(.secondary.opacity(0.4))
                                    Text("Chưa có mã QR\nChọn loại & bấm 'TẠO MÃ QR'")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                        .multilineTextAlignment(.center)
                                }
                                .frame(width: 220, height: 200)
                                .background(Color(UIColor.tertiarySystemFill))
                                .cornerRadius(14)
                            }
                        }
                        .padding()
                        .frame(maxWidth: .infinity)
                        .background(Color(UIColor.secondarySystemGroupedBackground))
                        .cornerRadius(16)

                        // Privacy & Face ID Settings Card
                        VStack(alignment: .leading, spacing: 10) {
                            Text("Bảo mật & Quyền riêng tư")
                                .font(.subheadline.bold())

                            HStack {
                                Label("Khóa ứng dụng bằng Face ID", systemImage: "faceid")
                                    .font(.subheadline)
                                Spacer()
                                Button(action: lockApp) {
                                    Text("Khóa ngay")
                                        .font(.caption.bold())
                                        .padding(.horizontal, 12)
                                        .padding(.vertical, 6)
                                        .background(Color.blue)
                                        .foregroundColor(.white)
                                        .cornerRadius(8)
                                }
                            }
                        }
                        .padding(14)
                        .background(Color(UIColor.secondarySystemGroupedBackground))
                        .cornerRadius(16)

                        // Footer Credit
                        VStack(spacing: 2) {
                            Text("✨ Created with ❤️ by Hong Quang")
                                .font(.footnote.bold())
                            Text("Apple CoreImage Engine • ISO/IEC 18004 Standard")
                                .font(.caption2)
                                .foregroundColor(.secondary)
                        }
                        .padding(.vertical, 10)
                    }
                    .padding(16)
                }
                .background(Color(UIColor.systemGroupedBackground).ignoresSafeArea())
                .navigationTitle("QR Code Generator")
                .navigationBarTitleDisplayMode(.inline)
            }
        }
        .alert(isPresented: $showAlert) {
            Alert(title: Text("Thông báo"), message: Text(alertMessage), dismissButton: .default(Text("OK")))
        }
        .sheet(isPresented: $isSharePresented) {
            if let img = generatedImage {
                ActivityViewController(activityItems: [img])
            }
        }
    }

    private func generateQR() {
        let payload = computedPayload
        guard !payload.isEmpty else {
            alertMessage = "Vui lòng nhập đầy đủ thông tin!"
            showAlert = true
            return
        }

        if let qr = QRCodeService.shared.generateQRCode(from: payload, fgColor: selectedColor.uiColor, bgColor: .white) {
            generatedImage = qr
        } else {
            alertMessage = "Không thể sinh mã QR!"
            showAlert = true
        }
    }

    private func saveToPhotos() {
        guard let img = generatedImage else { return }
        PHPhotoLibrary.requestAuthorization { status in
            if status == .authorized || status == .limited {
                UIImageWriteToSavedPhotosAlbum(img, nil, nil, nil)
                DispatchQueue.main.async {
                    alertMessage = "Đã lưu mã QR vào ứng dụng Ảnh (Photos) thành công!"
                    showAlert = true
                }
            } else {
                DispatchQueue.main.async {
                    alertMessage = "Vui lòng cấp quyền truy cập Ảnh trong Cài đặt iOS để lưu!"
                    showAlert = true
                }
            }
        }
    }

    private func lockApp() {
        isAppLocked = true
    }

    private func unlockApp() {
        BiometricAuthService.shared.authenticate { success, error in
            if success {
                isAppLocked = false
            } else if let error = error {
                alertMessage = error
                showAlert = true
            }
        }
    }
}

struct ActivityViewController: UIViewControllerRepresentable {
    var activityItems: [Any]
    var applicationActivities: [UIActivity]? = nil

    func makeUIViewController(context: UIViewControllerRepresentableContext<ActivityViewController>) -> UIActivityViewController {
        let controller = UIActivityViewController(activityItems: activityItems, applicationActivities: applicationActivities)
        return controller
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: UIViewControllerRepresentableContext<ActivityViewController>) {}
}
