import Foundation
import LocalAuthentication

public class BiometricAuthService {
    public static let shared = BiometricAuthService()

    public func authenticate(reason: String = "Xác thực Face ID / Touch ID để mở khóa ứng dụng", completion: @escaping (Bool, String?) -> Void) {
        let context = LAContext()
        var error: NSError?

        if context.canEvaluatePolicy(.deviceOwnerAuthentication, error: &error) {
            context.evaluatePolicy(.deviceOwnerAuthentication, localizedReason: reason) { success, authError in
                DispatchQueue.main.async {
                    if success {
                        completion(true, nil)
                    } else {
                        completion(false, authError?.localizedDescription ?? "Xác thực thất bại")
                    }
                }
            }
        } else {
            completion(false, error?.localizedDescription ?? "Thiết bị không hỗ trợ bảo mật sinh trắc học")
        }
    }
}
