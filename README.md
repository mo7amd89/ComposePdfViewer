# ComposePdfViewer

A **native Jetpack Compose PDF Viewer library** inspired by [AndroidPdfViewerV2](https://github.com/barteksc/AndroidPdfViewerV2), redesigned from the ground up for Jetpack Compose.

> 🙏 **Special thanks** to the developers of [AndroidPdfViewerV2](https://github.com/barteksc/AndroidPdfViewerV2) for their pioneering work in Android PDF viewing. This library builds upon their concepts while providing a modern, Compose-native implementation.

## Features

- ✅ **Compose-first** - No XML layouts or legacy view wrapping
- ✅ **Multiple PDF sources** - File, Asset, URI, InputStream, ByteArray, Remote URL
- ✅ **Remote loading** - Download PDFs from URLs with caching & progress
- ✅ **Gesture support** - Pinch-to-zoom, double-tap, pan, fling
- ✅ **Flexible layout** - Vertical/horizontal scrolling, page snapping
- ✅ **Night mode** - Built-in color inversion
- ✅ **Memory efficient** - LRU cache, bitmap pooling, lazy rendering
- ✅ **State restoration** - Survives configuration changes
- ✅ **API Guidelines** - Follows official Compose API & Component guidelines

## Installation

Add the module as a dependency in your app's `build.gradle.kts`:

```kotlin
dependencies {
    implementation(project(":pdfviewer"))
}
```

> **Note**: This library is currently available as a local module. Maven/JitPack publishing coming soon.

## Quick Start

```kotlin
@Composable
fun MyScreen() {
    val state = rememberPdfViewerState()
    
    PdfViewer(
        source = PdfSource.FromAsset("document.pdf"),
        state = state,
        modifier = Modifier.fillMaxSize()
    )
}
```

## Configuration

```kotlin
PdfViewer(
    source = PdfSource.FromUri(documentUri),
    modifier = Modifier.fillMaxSize(),
    state = rememberPdfViewerState(),
    config = ViewerConfig(
        scrollDirection = ScrollDirection.HORIZONTAL,
        isPageSnappingEnabled = true,
        isNightModeEnabled = true,
        maxZoom = 6f,
        renderQuality = 2f
    ),
    onPageChange = { page -> 
        println("Page: $page")
    }
)
```

## PDF Sources

| Source | Usage |
|--------|-------|
| File | `PdfSource.FromFile(File("/path/to/file.pdf"))` |
| Asset | `PdfSource.FromAsset("documents/sample.pdf")` |
| URI | `PdfSource.FromUri(contentUri)` |
| Bytes | `PdfSource.FromBytes(byteArray)` |
| Stream | `PdfSource.FromStream { inputStream }` |
| **Remote URL** | `PdfSource.Remote(url = "https://...")` |

## Remote PDF Loading

Load PDFs from URLs with automatic caching and progress tracking:

```kotlin
// Simple public URL
PdfViewer(
    source = PdfSource.Remote(url = "https://example.com/doc.pdf")
)

// Authenticated URL
PdfViewer(
    source = PdfSource.Remote(
        url = "https://api.example.com/docs/123.pdf",
        headers = mapOf("Authorization" to "Bearer $token"),
        cacheKey = "doc_123"
    )
)
```

### Observing Download Progress

```kotlin
val state = rememberPdfViewerState()

when (val remote = state.remoteState) {
    is RemotePdfState.Downloading -> {
        LinearProgressIndicator(progress = remote.progress ?: 0f)
    }
    is RemotePdfState.Error -> {
        Text("Error: ${remote.message}")
    }
    else -> { /* Ready */ }
}
```

### Cache Configuration

```kotlin
PdfSource.Remote(
    url = "https://...",
    cachePolicy = CachePolicy(
        maxAge = 7.days,        // Cache expiration
        maxSizeBytes = 100.MB   // Max cache size
    )
)
```

## ViewerConfig Options

| Property | Default | Description |
|----------|---------|-------------|
| `scrollDirection` | `VERTICAL` | `VERTICAL` or `HORIZONTAL` |
| `fitMode` | `WIDTH` | `WIDTH`, `HEIGHT`, or `BOTH` |
| `pageSpacing` | `8.dp` | Space between pages |
| `isPageSnappingEnabled` | `false` | Snap to full pages |
| `isNightModeEnabled` | `false` | Invert colors |
| `renderQuality` | `1.5f` | DPI multiplier |
| `minZoom` | `1f` | Minimum zoom level |
| `maxZoom` | `5f` | Maximum zoom level |
| `doubleTapZoom` | `2.5f` | Zoom level on double-tap |
| `isZoomGesturesEnabled` | `true` | Enable pinch & double-tap zoom |
| `isLoadingIndicatorVisible` | `true` | Show loading spinners |

## State Access

```kotlin
val state = rememberPdfViewerState()

// Current page (0-indexed)
val currentPage = state.currentPage

// Total pages
val pageCount = state.pageCount

// Zoom level
val zoom = state.zoom

// Loading state
val isLoading = state.isLoading
```

## Requirements

- Android 7.0+ (API 24)
- Kotlin 2.0+
- Jetpack Compose BOM 2024.02+

## Architecture

```
┌─────────────────────────────────┐
│         PdfViewer               │  Public API
├─────────────────────────────────┤
│  PdfViewerState │ ViewerConfig  │  State Layer
├─────────────────────────────────┤
│     PdfViewerController         │  Control Layer
├──────────────┬──────────────────┤
│ RenderScheduler │ BitmapCache   │  Rendering Layer
├──────────────┴──────────────────┤
│      PdfDocumentManager         │  Document Layer
└─────────────────────────────────┘
```

## Performance Notes

- **Lazy rendering**: Only visible pages + prefetch window are rendered
- **LRU cache**: 25% of heap used for bitmap cache
- **Bitmap pooling**: Recycled bitmaps reduce allocations
- **Coroutine cancellation**: Stale renders are cancelled on scroll/zoom
- **Zoom invalidation**: Re-renders at higher resolution when zoom changes significantly

## License

```
Apache License 2.0
```

## Credits

This library was developed with:

- 🤖 **[Google Antigravity](https://deepmind.google/)** - Advanced Agentic Coding by Google DeepMind
- 🧠 **[Claude Opus 4](https://anthropic.com/)** - AI Assistant by Anthropic

Inspired by and with gratitude to:

- 📄 **[AndroidPdfViewerV2](https://github.com/barteksc/AndroidPdfViewerV2)** - The original Android PDF viewing library that paved the way
