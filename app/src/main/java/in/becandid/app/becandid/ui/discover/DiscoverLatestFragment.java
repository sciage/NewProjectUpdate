package in.becandid.app.becandid.ui.discover;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.becandid.app.becandid.BuildConfig;
import in.becandid.app.becandid.di.component.ActivityComponent;
import in.becandid.app.becandid.di.rxbus.RxBus;
import in.becandid.app.becandid.dto.PostsModel;
import in.becandid.app.becandid.ui.base.VoicemeApplication;
import in.becandid.app.becandid.ui.profile.SimpleDividerItemDecoration;
import in.becandid.app.becandid.R;
import in.becandid.app.becandid.ui.base.BaseFragment;
import in.becandid.app.becandid.infrastructure.MySharedPreferences;
import in.becandid.app.becandid.ui.InfiniteScrollProvider;
import in.becandid.app.becandid.ui.OnLoadMoreListener;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;


public class DiscoverLatestFragment extends BaseFragment implements OnLoadMoreListener, DiscoverLatestMvpView, ShareInterface {
    public static final String ARG_LATEST_PAGE = "ARG_LATEST_PAGE";

   // recyclerView = (RecyclerView) view.findViewById(R.id.fragment_discover_recyclerview);

    // progressFrame = view.findViewById(R.id.activity_discover_latest);


   // private LatestListAdapter latestListAdapter;
    @BindView(R.id.fragment_discover_recyclerview) RecyclerView recyclerView;
    @BindView(R.id.activity_discover_latest) View progressFrame;
    @BindView(R.id.main_progress_discover_latest) ProgressBar progressBar;
    @BindView(R.id.error_btn_retry) Button error_btn_retry;
    @BindView(R.id.no_post_textview) TextView no_post_textview;
    @BindView(R.id.error_txt_cause) TextView txtError;

    LinearLayout no_post_layout;
    LinearLayout errorLayout;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    // limiting to 5 for this tutorial, since total pages in actual API is very large. Feel free to modify.
    private int TOTAL_PAGES = 500;

    private static final int PAGE_START = 1;

    private int currentPage = PAGE_START;
 //   private List<String> popularDiscoverPage;


    private LatestListAdapter latestListAdapter;
    List<PostsModel> response;
    SwipeRefreshLayout layout;
    View view;

    @Inject
    DiscoverLatestMvpPresenter<DiscoverLatestMvpView> mPresenter;

    public DiscoverLatestFragment() {
        // Required empty public constructor
    }

    public static DiscoverLatestFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_LATEST_PAGE, page);
        DiscoverLatestFragment fragment = new DiscoverLatestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity()!=null){
            latestListAdapter = new LatestListAdapter(getActivity(), this);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


    }

    void sendLikes(){
       // mPresenter method
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discover_latest, container, false);
        ActivityComponent component = getActivityComponent();
        if (component != null) {
            component.inject(this);
            setUnBinder(ButterKnife.bind(this, view));
            mPresenter.onAttach(this);

        }


        layout = (SwipeRefreshLayout) view.findViewById(R.id.discover_latest_swipeRefreshLayout);

        layout.setColorSchemeResources(android.R.color.holo_orange_light,
                android.R.color.holo_green_light,android.R.color.holo_red_light,android.R.color.holo_blue_light);

        layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            //    progressFrame.setVisibility(View.VISIBLE);

                currentPage = PAGE_START;
                try {
                    mPresenter.getLatestPostsOnline(MySharedPreferences.getUserId(preferences), currentPage);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //after shuffle id done then swife refresh is off
                layout.setRefreshing(false);

           /*     new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        currentPage = PAGE_START;
                        try {
                            mPresenter.getLatestPostsOnline(MySharedPreferences.getUserId(preferences), currentPage);

                            //   getLatestPosts();
                            // loadFirstPage();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //after shuffle id done then swife refresh is off
                        layout.setRefreshing(false);
                    }
                },5000); */


            }
        });

        try {
            initUiView(view);
        } catch (Exception e) {
            e.printStackTrace();
        }

        error_btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    progressFrame.setVisibility(View.VISIBLE);

                    mPresenter.getLatestPostsOnline(MySharedPreferences.getUserId(preferences), currentPage);

                 //   getLatestPosts(MySharedPreferences.getUserId(preferences), currentPage);

                //    loadFirstPage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        //recyclerview
//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_discover_recyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
//        recyclerView.setAdapter(new MyRecyclerAdapter(this.getActivity(), getDiscoverLatest()));
        return view;
    }

    private void initUiView(View view) {
      //  LinearLayoutManager linearLayout = new LinearLayoutManager(this.getActivity());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(recyclerView.getContext());
        if (getActivity()!=null){
            latestListAdapter = new LatestListAdapter(getActivity(), this);
        }

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(view.getContext()));
        recyclerView.setAdapter(latestListAdapter);
        InfiniteScrollProvider infiniteScrollProvider=new InfiniteScrollProvider();
        infiniteScrollProvider.attach(recyclerView,this);



        try {
            progressFrame.setVisibility(View.VISIBLE);

            mPresenter.getLatestPostsOnline(MySharedPreferences.getUserId(preferences), currentPage);

        //    getLatestPosts(MySharedPreferences.getUserId(preferences), currentPage);

        //    loadFirstPage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void showErrorView(Throwable throwable) {

        errorLayout = (LinearLayout) view.findViewById(R.id.error_layout);

        progressBar.setVisibility(View.GONE);
        txtError.setText(fetchErrorMessage(throwable));
    }

    private void showEmptyView() {
        no_post_layout = (LinearLayout) view.findViewById(R.id.no_post_layout);
        progressBar.setVisibility(View.GONE);
        no_post_textview.setText("There are no Latest Posts");
    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {

                final Uri resultUri = UCrop.getOutput(data);

                File file = new File(resultUri.getPath());

                shareScreenshot(file);



                // Timber.e(String.valueOf(Uri.fromFile(tempOutputFile)));

                // avatarProgressFrame.setVisibility(View.VISIBLE);
                // getBus.post(new Account.ChangeAvatarRequest(Uri.fromFile(tempOutputFile)));
            }
        }
    }

    private void shareScreenshot(File uri) {

        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                BuildConfig.APPLICATION_ID + ".provider",
                uri);

        // shareStatus = false;

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Candid App");
        intent.putExtra(android.content.Intent.EXTRA_TEXT, "You can download Candid app from play store https://play.google.com/store/apps/details?id=in.beacandid.app.beacandid");
        intent.putExtra(Intent.EXTRA_STREAM, photoURI);//pass uri here
        startActivity(Intent.createChooser(intent, "SHARE"));

    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (getActivity()!=null){
            if (!isNetworkConnected()) {
                errorMsg = getResources().getString(R.string.error_msg_no_internet);
            } else if (throwable instanceof TimeoutException) {
                errorMsg = getResources().getString(R.string.error_msg_timeout);
            }
        }


        return errorMsg;
    }



    @Override
    public String toString() {
        return "documentary";
    }

    private void showRecycleWithDataFilled(final List<PostsModel> myList) {

    }


  /*  @Override
    public void retryPageLoad() {
        try {
            loadNextPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } */

    private void arraylistCurrent(List<PostsModel> response){
        this.response = response;
    }

    @Override
    public void onLoadMore() {
        if (response.size() == 25){
            try {
                currentPage += 1;
                progressBar.setVisibility(View.VISIBLE);

                mPresenter.getLatestPostsOnline02(MySharedPreferences.getUserId(preferences), currentPage);

              //  loadNextPage();
                isLoading = true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Timber.d("nothing");
        }
    }

    @Override
    public void getLatestPosts(List<PostsModel> response) {
        progressBar.setVisibility(View.GONE);
     //   progressFrame.setVisibility(View.GONE);

        Log.e("RESPONSE:::", "Size===" + response.size());
        //         List<PostsModel> body = (List<PostsModel>) response.get(0).body();

        //   List<PostsModel> model = fetchResults(response);
        //   showRecycleWithDataFilled(response);
        if (response.size() == 0){
            showEmptyView();
            progressFrame.setVisibility(View.GONE);

        } else {
            latestListAdapter.addAll(response);
            arraylistCurrent(response);
            progressFrame.setVisibility(View.GONE);


        }
    }

    void sendFireBaseData(){

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "2");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "text");


        ( (VoicemeApplication)getActivity().getApplication()).getFireBase().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

       // mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

    }

    @Override
    public void getLatestPosts02(List<PostsModel> response) {
        progressBar.setVisibility(View.GONE);

        isLoading = false;
        latestListAdapter.addAll(response);
        arraylistCurrent(response);

    }

    @Override
    public void onDestroyView() {


        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mPresenter.onDetach();

            }
        });

        thread.start();
        super.onDetach();
    }

    @Override
    public void share(String path) {
     //   Timber.d(url);

       // Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();

        startCropActivity(Uri.fromFile(new File(path)), new File(path));
    }

    private void startCropActivity(@NonNull Uri uri, File file) {

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(file));

        uCrop = advancedConfig(uCrop);

        uCrop.start(getActivity());
    }


    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();

        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(90);
        options.withMaxResultSize(960, 1028);
        //    options.setCompressionQuality(DEFAULT_COMPRESS_QUALITY);
        options.setFreeStyleCropEnabled(true);
        uCrop.useSourceImageAspectRatio();

        options.setShowCropGrid(false);
        options.setMaxScaleMultiplier(10.0f);
        return uCrop.withOptions(options);
    }
}


/*
private void loadFirstPage() throws Exception {
        Log.d(TAG, "loadFirstPage: ");
        hideErrorView();

        if(currentPage > PAGE_START){
            currentPage = PAGE_START;
        }
        application.getWebService()
                .getLatestFeed(MySharedPreferences.getUserId(preferences), currentPage)
                //.retryWhen(new RetryWithDelay(3,2000))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<PostsModel>>() {
                    @Override
                    public void onNext(List<PostsModel> response) {
                        progressBar.setVisibility(View.GONE);
                        progressFrame.setVisibility(View.GONE);
                        hideErrorView();
                        Log.e("RESPONSE:::", "Size===" + response.size());
                        //         List<PostsModel> body = (List<PostsModel>) response.get(0).body();

                        //   List<PostsModel> model = fetchResults(response);
                        //   showRecycleWithDataFilled(response);
                        if (response.size() == 0){
                            showEmptyView();
                        } else {
                            latestListAdapter.addAll(response);
                            progressFrame.setVisibility(View.GONE);
                            arraylistCurrent(response);

                        }

                    }
                    @Override
                    public void onError(Throwable e){
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        progressFrame.setVisibility(View.GONE);
                        showErrorView(e);
                    }
                });
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);
        hideErrorView();

        application.getWebService()
                .getLatestFeed(MySharedPreferences.getUserId(preferences), currentPage)
               // .retryWhen(new RetryWithDelay(3,2000))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseSubscriber<List<PostsModel>>() {
                    @Override
                    public void onNext(List<PostsModel> response) {
                        hideErrorView();
                        isLoading = false;
                        latestListAdapter.addAll(response);
                        arraylistCurrent(response);

                    }
                    @Override
                    public void onError(Throwable e){
                        e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        progressFrame.setVisibility(View.GONE);
                        Timber.e("error is: " + e);
                        latestListAdapter.showRetry(true, fetchErrorMessage(e));
                    }
                });

    }
 */