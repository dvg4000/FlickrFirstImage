package ru.triton265.flickrfirstimage.app;

import android.support.annotation.NonNull;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.GET;
import retrofit.http.QueryMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlickrClient {
    private static final String FLICKR_BASEURL = "https://api.flickr.com";

    private static Map<String, String> OPTIONS = new HashMap<String, String>();

    static {
        OPTIONS.put("api_key", "96635d88513246ce894292de45c8f704");
        OPTIONS.put("format", "json");
        OPTIONS.put("nojsoncallback", "1");
    }

    interface Service {
        @GET("/services/rest")
        Call<SearchResult> search(@QueryMap Map<String, String> options);

        @GET("/services/rest")
        Call<GetSizesResult> getSizes(@QueryMap Map<String, String> options);

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

        class Size {
            String label;
            String source;
            int width;
            int height;
        }

        class Sizes {
            ArrayList<Size> size;
        }

        class GetSizesResult {
            String stat;
            Sizes sizes;
        }
    }

    static String searchFirst(@NonNull final String searchText) throws IOException {
        final Map<String, String> options = new HashMap<>(OPTIONS);
        options.put("method", "flickr.photos.search");
        options.put("per_page", "1");
        options.put("text", searchText);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FLICKR_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Service service = retrofit.create(Service.class);
        final Service.SearchResult result = service.search(options).execute().body();
        if ("ok".equals(result.stat)) {
            if (result.photos.total > 0) {
                return result.photos.photo.get(0).id;
            }
        } else {
            throw new IllegalStateException(result.stat);
        }
        return null;
    }

    static List<Service.Size> getSizes(@NonNull final String photoId) throws IOException {
        final Map<String, String> options = new HashMap<>(OPTIONS);
        options.put("method", "flickr.photos.getSizes");
        options.put("photo_id", photoId);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FLICKR_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Service service = retrofit.create(Service.class);
        final Service.GetSizesResult result = service.getSizes(options).execute().body();
        if ("ok".equals(result.stat)) {
            return result.sizes.size;
        } else {
            throw new IllegalStateException(result.stat);
        }
    }
}
