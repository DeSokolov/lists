import SwiftUI
import FirebaseCore

@main
struct iOSApp: App {

    init() {
        // GoogleService-Info.plist must be present before calling configure().
        // Add the file from Firebase Console before first launch.
        if Bundle.main.path(forResource: "GoogleService-Info", ofType: "plist") != nil {
            FirebaseApp.configure()
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.keyboard)
        }
    }
}
