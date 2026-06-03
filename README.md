# 🌌 Celestial Observatory (NASA Daily)

Welcome to the **Celestial Observatory**, a highly styled, modern Jetpack Compose application that acts as your telemetry hub for NASA's Astronomy Picture of the Day (APOD) APIs. Journey through active cosmic archives, track historic stardates, and customize your deep-space observatory experience.

---

## 🎨 Visual Philosophy & Aesthetic Direction
Our interface employs a **Cosmic Hyperwave / Retro-Future Dark** design built on Material Design 3 guidelines:
- **Neon Accents**: Electric Cyan secondary styling, Radiant Solar gold highlights, and rich Cosmic Iris magenta primary focal components.
- **Deep Space Depth**: Multilayered subtle coordinate visual grid backdrops with modern glassmorphism (semi-alpha surfaces) simulating a high-tech starship navigation panel.
- **Responsive Mastery**: Fully fluid split-pane layout optimization on tablet and landscape foldable displays alongside compact single-column formatting for mobile phone factors.

---

## 🚀 Key Functional Modules
1. **Interactive Observatory Board**: View the prime celestial visual log of the stardate, read corresponding scientific documentation, and interact with adaptive control switches.
2. **Dynamic Stardate Selector**: Travel across historical cosmic registry times using a stylized Material 3 stardate calendar picker dialogue.
3. **Cosmic Feed & Archives**: Toggle between clean compact list feeds and space-saving grid feeds of older logs retrieving directly from NASA's databases.
4. **Offline Vault Integration**: Bookmark favorite astronomical entities securely utilizing local, highperformance **Room Database persistence**.
5. **Secure Imagery Transmission**: Transmit high-res visual assets directly to your local file explorer storage via standard Android Download Manager integration.
6. **Privacy Protocols**: Inspect the on-board user telemetry protections clearly visible via the embedded **Secure Cosmic Protocols (Privacy Policy)**.

---

## 📂 Technical Architecture & Stack
- **Framework**: Modern native Android, 100% Kotlin.
- **UI Engine**: Jetpack Compose (Material Design 3 with custom telemetry canvas modifiers).
- **Architecture**: Clean MVVM (Model-View-ViewModel) utilizing reactive `StateFlow` and `collectAsStateWithLifecycle()` to achieve high-performance rendering.
- **Local Database**: Built on Jetpack **Room** with Kotlin Symbol Processing (KSP).
- **Asynchronous Flow**: Structured Kotlin Coroutines & Flows.

---

## 🔧 Build & Configuration Guidance
### Package & Versioning Properties
- **Application ID**: `com.aistudio.nasadaily.jklmn`
- **Target SDK**: 36 | **Minimum SDK**: 24
- **Version Code**: 4 | **Version Name**: 4.0

### Run & Build Gradle Commands
Use standard Gradle commands to compile and build the package:
```bash
gradle assembleDebug
```

---

## 📜 Privacy Assurance
The app is constructed with a dedicated local-first telemetry design. Explore carefully: no custom usage analytics, tracker tokens, or corporate advertising packages are merged within this secure scientific vessel. Review `/PRIVACY_POLICY.md` for our explicit digital guidelines.
