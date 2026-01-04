package com.composepdf.cache

import android.graphics.Bitmap
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

/**
 * Unit tests for [BitmapCache].
 */
class BitmapCacheTest {
    
    private lateinit var cache: BitmapCache
    private lateinit var bitmapPool: BitmapPool
    
    @Before
    fun setup() {
        bitmapPool = BitmapPool()
        // Use a smaller cache size for testing
        cache = BitmapCache(maxSizeBytes = 1024 * 1024, bitmapPool = bitmapPool)
    }
    
    @After
    fun tearDown() {
        cache.clear()
        bitmapPool.clear()
    }
    
    @Test
    fun `put and get returns same bitmap`() {
        val bitmap = createMockBitmap(100, 100)
        val key = PageCacheKey(0, 1f, 100, 100)
        
        cache.put(key, bitmap)
        
        assertEquals(bitmap, cache.get(key))
    }
    
    @Test
    fun `get returns null for missing key`() {
        val key = PageCacheKey(0, 1f, 100, 100)
        
        assertNull(cache.get(key))
    }
    
    @Test
    fun `different zoom levels have different cache entries`() {
        val bitmap1 = createMockBitmap(100, 100)
        val bitmap2 = createMockBitmap(200, 200)
        
        val key1 = PageCacheKey(0, 1f, 100, 100)
        val key2 = PageCacheKey(0, 2f, 200, 200)
        
        cache.put(key1, bitmap1)
        cache.put(key2, bitmap2)
        
        assertEquals(bitmap1, cache.get(key1))
        assertEquals(bitmap2, cache.get(key2))
    }
    
    @Test
    fun `clearPage removes all entries for page`() {
        val bitmap1 = createMockBitmap(100, 100)
        val bitmap2 = createMockBitmap(200, 200)
        
        val key1 = PageCacheKey(0, 1f, 100, 100)
        val key2 = PageCacheKey(0, 2f, 200, 200)
        val key3 = PageCacheKey(1, 1f, 100, 100)
        
        cache.put(key1, bitmap1)
        cache.put(key2, bitmap2)
        cache.put(key3, bitmap1)
        
        cache.clearPage(0)
        
        assertNull(cache.get(key1))
        assertNull(cache.get(key2))
        assertNotNull(cache.get(key3))
    }
    
    @Test
    fun `clear removes all entries`() {
        val bitmap = createMockBitmap(100, 100)
        
        cache.put(PageCacheKey(0, 1f, 100, 100), bitmap)
        cache.put(PageCacheKey(1, 1f, 100, 100), bitmap)
        
        cache.clear()
        
        assertEquals(0, cache.size())
    }
    
    private fun createMockBitmap(width: Int, height: Int): Bitmap {
        val bitmap = mock(Bitmap::class.java)
        `when`(bitmap.width).thenReturn(width)
        `when`(bitmap.height).thenReturn(height)
        `when`(bitmap.allocationByteCount).thenReturn(width * height * 4)
        `when`(bitmap.isRecycled).thenReturn(false)
        return bitmap
    }
}
