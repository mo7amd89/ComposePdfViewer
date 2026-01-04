package com.composepdf.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ViewerConfigTest {
    
    @Test
    fun `default config has expected values`() {
        val config = ViewerConfig()
        
        assertEquals(ScrollDirection.VERTICAL, config.scrollDirection)
        assertEquals(FitMode.WIDTH, config.fitMode)
        assertFalse(config.isPageSnappingEnabled)
        assertFalse(config.isNightModeEnabled)
        assertEquals(1.5f, config.renderQuality, 0.01f)
        assertEquals(1f, config.minZoom, 0.01f)
        assertEquals(5f, config.maxZoom, 0.01f)
        assertTrue(config.isZoomGesturesEnabled)
        assertTrue(config.isLoadingIndicatorVisible)
    }
    
    @Test
    fun `config with custom values retains them`() {
        val config = ViewerConfig(
            scrollDirection = ScrollDirection.HORIZONTAL,
            isPageSnappingEnabled = true,
            isNightModeEnabled = true,
            maxZoom = 8f,
            renderQuality = 2f,
            isZoomGesturesEnabled = false
        )
        
        assertEquals(ScrollDirection.HORIZONTAL, config.scrollDirection)
        assertTrue(config.isPageSnappingEnabled)
        assertTrue(config.isNightModeEnabled)
        assertEquals(8f, config.maxZoom, 0.01f)
        assertEquals(2f, config.renderQuality, 0.01f)
        assertFalse(config.isZoomGesturesEnabled)
    }
    
    @Test
    fun `config copy creates independent instance`() {
        val original = ViewerConfig()
        val modified = original.copy(isNightModeEnabled = true, maxZoom = 8f)
        
        // Original unchanged
        assertFalse(original.isNightModeEnabled)
        assertEquals(5f, original.maxZoom, 0.01f)
        
        // Modified has new values
        assertTrue(modified.isNightModeEnabled)
        assertEquals(8f, modified.maxZoom, 0.01f)
    }
}
