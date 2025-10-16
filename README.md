# msc-ptiv2
Native PDF generator for MSC PTI (minimal)

## Project Overview
Android application that generates standardized Pre-Trip Inspection (PTI) forms for MSC (Mediterranean Shipping Company) reefer containers. The app creates multi-page PDF documents with inspection checklists using Android's native PDF generation capabilities.

## Features
- **Native PDF Generation**: Uses Android's `android.graphics.pdf.PdfDocument` API
- **Multi-page Support**: Automatic page breaks when content exceeds page boundaries
- **Standardized Layout**: A4 page format with configurable margins and columns
- **Inspection Checklist**: Table format with steps, locations, checks, descriptions, and checkboxes
- **Minimal Dependencies**: Lean implementation with only essential libraries
- **Coroutine-based**: Async PDF generation using Kotlin Coroutines

## Project Structure
```
msc-ptiv2/
├── app/
│   ├── build.gradle.kts              # App module configuration
│   └── src/main/
│       ├── AndroidManifest.xml       # App manifest
│       └── java/com/medlog/mscptiv2/
│           ├── HomeActivity.kt       # Entry point activity
│           ├── model/
│           │   ├── TemplateConfig.kt # PDF layout configuration
│           │   ├── RowItem.kt        # Data model for rows
│           │   ├── SampleData.kt     # Sample test data
│           │   └── samples/
│           │       └── sample-data.json
│           ├── pdf/
│           │   └── PdfGenerator.kt   # PDF generation logic
│           └── util/
│               └── MeasurementUtils.kt # Unit conversions
├── build.gradle.kts                  # Root build configuration
├── settings.gradle.kts               # Project settings
├── ANALYSIS.md                       # Detailed code analysis
└── README.md                         # This file
```

## Technical Details
- **Language**: Kotlin
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle 8.x with Kotlin DSL
- **Key Dependencies**:
  - AndroidX Core KTX
  - AndroidX AppCompat
  - Material Components
  - Kotlin Coroutines

## Building the Project
This is an Android project and requires Android SDK to build:

1. Install Android Studio
2. Open the project in Android Studio
3. Let Gradle sync and download dependencies
4. Build and run on an emulator or device

## How It Works
1. **HomeActivity** launches on app start
2. Creates default **TemplateConfig** with A4 page layout
3. Loads sample inspection data via **SampleData**
4. **PdfGenerator** creates PDF with:
   - Header with title
   - Table header with column labels
   - Inspection rows with automatic text wrapping
   - Checkboxes for completion tracking
   - Automatic pagination
5. PDF saved to app cache directory
6. Activity finishes after generation

## Configuration
PDF layout is controlled by `TemplateConfig` with parameters for:
- Page size (default: A4 - 210mm × 297mm)
- Margins (12mm left/right, 14mm top/bottom)
- Font sizes (title: 16pt, cells: 10pt, description: 9pt)
- Column widths (Step: 12mm, Location: 34mm, Check: 40mm, Description: 86mm, Checkbox: 10mm)
- Padding and spacing

## Recent Changes
The repository structure was reorganized to fix deeply nested directories:
- ✅ All files moved to correct package locations
- ✅ `PdfGenerator` separated into its own file
- ✅ `SampleData` class created for data loading
- ✅ Build configuration files renamed properly
- ✅ `.gitignore` added for Android projects
- ✅ Comprehensive analysis document created

## License
Not specified

## Contributing
This is a minimal implementation focused on MSC PTI form generation. For improvements or questions, please open an issue. 
