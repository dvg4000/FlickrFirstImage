package ru.triton265.flickrfirstimage.app;

import android.text.TextUtils;
import junit.framework.TestCase;
import retrofit.Callback;
import retrofit.Response;

import java.io.IOException;

public class FlickrClientTest extends TestCase {

    public void testSearch() throws IOException {
        FlickrClient.FlickrService.SearchResult result = FlickrClient.search("ass");

        assertNotNull(result);
        assertEquals("ok", result.stat);
        assertTrue(result.photos.total > 0);
        assertNotNull(result.photos.photo);
        assertFalse(TextUtils.isEmpty(result.photos.photo.get(0).id));
    }
}
