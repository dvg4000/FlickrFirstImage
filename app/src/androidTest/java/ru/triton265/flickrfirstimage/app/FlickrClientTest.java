package ru.triton265.flickrfirstimage.app;

import android.text.TextUtils;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.List;

public class FlickrClientTest extends TestCase {

    public void testSearch() throws IOException {
        final String id = FlickrClient.searchFirst("ass");

        assertFalse(TextUtils.isEmpty(id));
    }

    public void testGetSizes() throws IOException {
        final String photoId = FlickrClient.searchFirst("ass");
        final List<FlickrClient.Service.Size> sizeList = FlickrClient.getSizes(photoId);

        assertNotNull(sizeList);
        assertTrue(sizeList.size() > 0);

        final List<FlickrClient.Service.Size> medium = Stream.of(sizeList)
                .filter(size -> "Medium".equals(size.label))
                .collect(Collectors.toList());

        assertNotNull(medium);
        assertTrue(medium.size() > 0);
    }
}
