import SwiftUI
import CoreImage
import CoreImage.CIFilterBuiltins

public enum QRCategory: String, CaseIterable, Identifiable {
    case url = "url"
    case phone = "phone"
    case wifi = "wifi"
    case email = "email"
    case pdf = "pdf"
    case text = "text"

    public var id: String { rawValue }

    public var title: String {
        switch self {
        case .url: return "URL"
        case .phone: return "Điện thoại"
        case .wifi: return "Wi-Fi"
        case .email: return "E-mail"
        case .pdf: return "PDF"
        case .text: return "Văn bản"
        }
    }

    public var description: String {
        switch self {
        case .url: return "Tạo mã QR mở trang web."
        case .phone: return "Tạo mã QR để gọi số điện thoại."
        case .wifi: return "Tạo mã QR để kết nối Wi-Fi."
        case .email: return "Tạo mã QR bắt đầu bản thảo email."
        case .pdf: return "Tạo mã QR để chia sẻ PDF."
        case .text: return "Tạo mã QR với một thông điệp tùy chỉnh."
        }
    }

    public var iconName: String {
        switch self {
        case .url: return "globe"
        case .phone: return "phone.fill"
        case .wifi: return "wifi"
        case .email: return "envelope.fill"
        case .pdf: return "doc.fill"
        case .text: return "textformat"
        }
    }
}

public class QRCodeService {
    public static let shared = QRCodeService()
    private let context = CIContext()
    private let filter = CIFilter.qrCodeGenerator()

    public func generateQRCode(from content: String, fgColor: UIColor = .black, bgColor: UIColor = .white) -> UIImage? {
        guard !content.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty else { return nil }
        guard let data = content.data(using: .utf8) else { return nil }

        filter.setValue(data, forKey: "inputMessage")
        filter.setValue("M", forKey: "inputCorrectionLevel")

        guard let outputImage = filter.outputImage else { return nil }

        // Scale up to crisp high resolution (1024x1024)
        let transform = CGAffineTransform(scaleX: 16, y: 16)
        let scaledImage = outputImage.transformed(by: transform)

        // Colorize
        let colorFilter = CIFilter(name: "CIFalseColor")
        colorFilter?.setValue(scaledImage, forKey: "inputImage")
        colorFilter?.setValue(CIColor(color: fgColor), forKey: "inputColor0")
        colorFilter?.setValue(CIColor(color: bgColor), forKey: "inputColor1")

        guard let coloredImage = colorFilter?.outputImage,
              let cgImage = context.createCGImage(coloredImage, from: coloredImage.extent) else {
            return nil
        }

        return UIImage(cgImage: cgImage)
    }
}
