package ru.triton265.flickrfirstimage.app;

import android.support.annotation.NonNull;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.QueryMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlickrClient {
    private static final String FLICKR_BASEURL = "https://api.flickr.com";

    private static Map<String, String> OPTIONS = new HashMap<String, String>();

    static {
        OPTIONS.put("api_key", "96635d88513246ce894292de45c8f704");
        OPTIONS.put("format", "json");
        OPTIONS.put("nojsoncallback", "1");
    }

    public interface FlickrService {
        @GET("/services/rest")
        Call<SearchResult> searchFlickr(@QueryMap Map<String, String> options);

        class Photo {
            String id;
        }

        class Photos {
            ArrayList<Photo> photo;
            long total;
        }

        class SearchResult {
            Photos photos;
            String stat;
        }
    }

    static FlickrService.SearchResult search(@NonNull final String searchText) throws IOException {
        final Map<String, String> options = new HashMap<String, String>(OPTIONS);
        options.put("method", "flickr.photos.search");
        options.put("per_page", "1");
        options.put("text", searchText);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FLICKR_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final FlickrService flickrService = retrofit.create(FlickrService.class);
        return flickrService.searchFlickr(options).execute().body();
    }
}
