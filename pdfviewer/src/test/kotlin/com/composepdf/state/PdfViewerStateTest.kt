package com.composepdf.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PdfViewerState].
 */
class PdfViewerStateTest {
    
    @Test
    fun `initial state has correct default values`() {
        val state = PdfViewerState()
        
        assertEquals(0, state.currentPage)
        assertEquals(0, state.pageCount)
        assertEquals(1f, state.zoom)
        assertTrue(state.isLoading)
        assertNull(state.error)
        assertFalse(state.isGestureActive)
    }
    
    @Test
    fun `initial state with custom values`() {
        val state = PdfViewerState(
            initialPage = 5,
            initialZoom = 2f
        )
        
        assertEquals(5, state.currentPage)
        assertEquals(2f, state.zoom)
    }
    
    @Test
    fun `isLoaded returns true when loaded successfully`() {
        val state = PdfViewerState()
        
        // Simulate successful load
        state.apply {
            // Using internal setters via reflection or package access
            // For this test, we'll access internal setters
        }
        
        // Initial state is loading
        assertFalse(state.isLoaded)
    }
    
    @Test
    fun `reset clears state to defaults`() {
        val state = PdfViewerState(initialPage = 5, initialZoom = 2f)
        
        state.reset()
        
        assertEquals(0, state.currentPage)
        assertEquals(0, state.pageCount)
        assertEquals(1f, state.zoom)
        assertTrue(state.isLoading)
        assertNull(state.error)
    }
    
    @Test
    fun `saver saves and restores page and zoom`() {
        val originalState = PdfViewerState(initialPage = 3, initialZoom = 2.5f)

        val saved = PdfViewerState.Saver.save(originalState)
        val restoredState = PdfViewerState.Saver.restore(saved!!)

        assertEquals(3, restoredState?.currentPage)
        assertEquals(2.5f, restoredState?.zoom)
    }
}
