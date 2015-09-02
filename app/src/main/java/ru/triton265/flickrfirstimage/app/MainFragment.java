package ru.triton265.flickrfirstimage.app;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;

public class MainFragment extends Fragment {

    private Subscription mSubscription;
    private ImageView mImageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mImageView = (ImageView)rootView.findViewById(R.id.imageView);

        final EditText editText = (EditText)rootView.findViewById(R.id.editText);
        editText.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                findImage(((EditText) v).getText().toString());
                return true;
            }
            return false;
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mSubscription) {
            mSubscription.unsubscribe();
        }
    }

    private Observable<String> createSearchObservable(@NonNull final String searchText) {
        return Observable.create((Subscriber<? super String> subscriber) -> {
            try {
                subscriber.onNext(FlickrClient.searchFirst(searchText));
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<List<FlickrClient.Service.Size>> createImageSizesObservable(@NonNull final String photoId) {
        return Observable.create((Subscriber<? super List<FlickrClient.Service.Size>> subscriber) -> {
            try {
                subscriber.onNext(FlickrClient.getSizes(photoId));
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private void findImage(@NonNull final String searchText) {
        mSubscription = createSearchObservable(searchText)
                .subscribeOn(Schedulers.io())
                .flatMap(phoneId -> createImageSizesObservable(phoneId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sizeList -> {
                            final List<FlickrClient.Service.Size> medium = Stream.of(sizeList)
                                    .filter(size -> "Medium".equals(size.label))
                                    .collect(Collectors.toList());

                            if (null != medium && medium.size() > 0) {
                                Glide.with(MainFragment.this)
                                        .load(medium.get(0).source)
                                        .centerCrop()
                                        .placeholder(android.R.drawable.progress_indeterminate_horizontal)
                                        .into(mImageView);
                            }
                        },
                        e -> {
                            Toast.makeText(getActivity(), R.string.error_get_image, Toast.LENGTH_SHORT).show();
                        });
    }
}
