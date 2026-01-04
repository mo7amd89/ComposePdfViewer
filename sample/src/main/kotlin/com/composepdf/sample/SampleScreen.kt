package com.composepdf.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.composepdf.PdfViewer
import com.composepdf.rememberPdfViewerState
import com.composepdf.remote.CachePolicy
import com.composepdf.remote.RemotePdfState
import com.composepdf.source.PdfSource
import com.composepdf.state.ScrollDirection
import com.composepdf.state.ViewerConfig

/**
 * Sample screen demonstrating PdfViewer library features.
 * 
 * Features demonstrated:
 * - Loading PDF from assets
 * - Loading PDF from remote URL
 * - Page navigation
 * - Zoom controls
 * - Night mode toggle
 * - Scroll direction toggle
 * - Page snapping toggle
 * - Download progress for remote PDFs
 */
@Composable
fun SampleScreen() {
    // Source selection
    var useRemoteSource by remember { mutableStateOf(false) }
    
    // Configuration state
    var nightMode by remember { mutableStateOf(false) }
    var enableSnapping by remember { mutableStateOf(false) }
    var scrollDirection by remember { mutableStateOf(ScrollDirection.VERTICAL) }
    
    // PDF viewer state
    val pdfState = rememberPdfViewerState()
    
    // Build config based on current settings
    val config = remember(nightMode, enableSnapping, scrollDirection) {
        ViewerConfig(
            isNightModeEnabled = nightMode,
            isPageSnappingEnabled = enableSnapping,
            scrollDirection = scrollDirection,
            renderQuality = 1.5f,
            maxZoom = 5f,
            doubleTapZoom = 2.5f
        )
    }
    
    // PDF source - toggle between local and remote
    val pdfSource = remember(useRemoteSource) {
        if (useRemoteSource) {
            // Sample public PDF - replace with your own URL
            val url="https://www.univ-constantine2.dz/facsecsg/wp-content/uploads/sites/8/Masfofat.pdf"
           // val url="https://www.w3.org/WAI/WCAG21/Techniques/pdf/img/table-word.pdf"
            PdfSource.Remote(
                url =url,
                cachePolicy = CachePolicy.NoCache,
                cacheKey = url
                // cacheKey defaults to URL hash, ensuring unique cache per URL
            )
        } else {
            PdfSource.FromAsset("sample.pdf")
        }
    }
    
    MaterialTheme {
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // PDF Viewer (takes most of the screen)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    // ALWAYS keep PdfViewer in composition (otherwise download cancels)
                    PdfViewer(
                        source = pdfSource,
                        state = pdfState,
                        config = config,
                        modifier = Modifier.fillMaxSize(),
                        onPageChange = { page ->
                            // Log page changes if needed
                        },
                        onError = { error ->
                            // Handle error
                            error.printStackTrace()
                        }
                    )
                    
                    // Show download progress overlay for remote PDFs
                    when (val remoteState = pdfState.remoteState) {
                        is RemotePdfState.Downloading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Text(
                                        text = "Downloading PDF...",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    if (remoteState.progress != null) {
                                        LinearProgressIndicator(
                                            progress = { remoteState.progress!! },
                                            modifier = Modifier
                                                .width(200.dp)
                                                .height(8.dp)
                                        )
                                        Text(
                                            text = "${(remoteState.progress!! * 100).toInt()}%",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    } else {
                                        LinearProgressIndicator(
                                            modifier = Modifier.width(200.dp)
                                        )
                                    }
                                }
                            }
                        }
                        is RemotePdfState.Error -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Error loading PDF",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Text(
                                        text = remoteState.message,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        else -> {
                            // Idle or Cached - PdfViewer handles it
                        }
                    }
                    
                    // Page indicator overlay
                    if (pdfState.pageCount > 0) {
                        Card(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            )
                        ) {
                            Text(
                                text = "${pdfState.currentPage + 1} / ${pdfState.pageCount}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                
                // Controls
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Source selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = !useRemoteSource,
                                onClick = { useRemoteSource = false },
                                label = { Text("Local (Asset)") }
                            )
                            FilterChip(
                                selected = useRemoteSource,
                                onClick = { useRemoteSource = true },
                                label = { Text("Remote (URL)") }
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        // Toggle controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Night Mode")
                            Switch(
                                checked = nightMode,
                                onCheckedChange = { nightMode = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Page Snapping")
                            Switch(
                                checked = enableSnapping,
                                onCheckedChange = { enableSnapping = it }
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Horizontal Scroll")
                            Switch(
                                checked = scrollDirection == ScrollDirection.HORIZONTAL,
                                onCheckedChange = { 
                                    scrollDirection = if (it) {
                                        ScrollDirection.HORIZONTAL
                                    } else {
                                        ScrollDirection.VERTICAL
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
