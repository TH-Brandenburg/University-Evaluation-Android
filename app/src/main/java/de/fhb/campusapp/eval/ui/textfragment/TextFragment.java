package de.fhb.campusapp.eval.ui.textfragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.ui.enlarge.EnlargeImageActivity;
import de.fhb.campusapp.eval.custom.CustomEditText;
import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.base.BaseFragment;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.Observer.DeleteImagesObservable;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import rx.android.schedulers.AndroidSchedulers;

public class TextFragment extends BaseFragment implements PagerAdapterPageEvent, TextMvpView {

    public static final String QUESTION = "QUESTION";
    public static final String POSITION = "POSITION";
    public static final String ID = "ID";
    public static final String PRIMARY = "PRIMARY";

    private static final int REQUEST_CAPTURE_IMAGE = 3000;

    private String mQuestion;
    private String mImageName;
    private int mPosition;
    private int mQuestionID;
    private boolean mCurrentlyPrimary = false;
    private boolean mReloadInitialized = false;

    private File mImageFile;


    /**
     * GettingPrimary is unfortunately called multiple times when fragment becomes primary
     * Some functions shall only executed once when this evrn is thrown though.
     */
    private boolean mGettingPrimaryCalledBefore = false;

    @Inject
    TextPresenter mTextPresenter;

    @Inject
    PermissionManager permissionManager;

//    private TextFragmentCommunicator mActivityCommunicator;

    @BindView(R.id.question_text_view)
    TextView mTextView;

    @BindView(R.id.edit_text)
    CustomEditText mEditText;

    @BindView(R.id.comment_thumbnail)
    ImageView mImageView;

    @BindView(R.id.camera_button)
    ImageButton mCameraButton;

    @BindView(R.id.scrollView)
    ScrollView mScrollView;

    @BindView(R.id.image_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.text_fragment_root_layout)
    RelativeLayout mRootLayout;

    @BindView(R.id.delete_image_button)
    ImageButton mDeleteButton;

//    private QuestionCommunicator questionCommunicator;

    public static TextFragment newInstance(String question, int position, int questionID) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question);
        args.putInt(POSITION, position);
        args.putInt(ID, questionID);
        fragment.setArguments(args);
        return fragment;
    }


    public TextFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        ((BaseActivity)activity).mActicityComponent.bind(this);

        mTextPresenter.attachView(this);
        mTextPresenter.setmActivityCommunicator((TextFragmentCommunicator)getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            Bundle args = this.getArguments();
            mPosition = args.getInt(POSITION);
            mQuestion = args.getString(QUESTION);
            mQuestionID = args.getInt(ID);
            this.mImageName = Utility.removeSpecialCharacters(mQuestion);

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && getActivity() != null){
            Utility.setKeyboardResizing(getActivity());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

      /*  if(mCurrentlyPrimary){
            mActivityCommunicator.fragmentBecamePrimary(mQuestion, mImageName);
        }*/

        mTextView.setText(mQuestion);
        mTextView.setMovementMethod(new ScrollingMovementMethod());

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            /*
                this is the first of 2 globalLayout listeners within this class. It is also the first one to be called.
                The other one starts listening AFTER onResume was called which might be to late for some operations.
             */
            @Override
            public void onGlobalLayout() {

                mImageView.setMaxHeight((mRootLayout.getHeight() - mTextView.getHeight())/2);
                mImageView.setMaxWidth(mRootLayout.getWidth());

                if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT && !DataManager.getCommentaryImageMap().containsKey(mQuestion)){
                    configureImageViewAsButton();
                } else {
//                    Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 100);
                }

                mEditText.setHeight((mRootLayout.getHeight() - mTextView.getHeight()) / 2);
                mEditText.setMovementMethod(new ScrollingMovementMethod());

                TextQuestionVO dto = DataManager.retrieveTextQuestionVO(mQuestion);

                // define EditTextView as number field
                if(dto.getOnlyNumbers()){
                    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mEditText.setHint(getResources().getString(R.string.edit_text_click_hint_numeric));
                }

                //set the max number of symbols this editText can hold
                InputFilter.LengthFilter filter = new InputFilter.LengthFilter(dto.getMaxLength());
                InputFilter[] filters = {filter};
                mEditText.setFilters(filters);

                ActivityUtil.removeGlobalLayoutListener(view, this);
            }

        });
        mEditText.setPagerAdapter((PagerAdapterSetPrimary) getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentlyPrimary){
            mReloadInitialized = initializeImageViews(mPosition);
        }
    }

    private void configureImageViewAsButton() {
        mImageView.setBackgroundColor(getResources().getColor(R.color.campusapptheme_color_light_gray));

        Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
        Utility.animateView(mCameraButton, View.VISIBLE, 1.0f, 100);

        mCameraButton.setOnClickListener(listener -> mImageFile = mTextPresenter.startCameraIntent(permissionManager));
        mImageView.setOnClickListener(listener -> mImageFile = mTextPresenter.startCameraIntent(permissionManager));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == Activity.RESULT_OK) {
//            ImageManager manager = new ImageManager();
//            manager.testForPossibility(getContentResolver(), mCurrentIntentImage);

            if(mCurrentlyPrimary){
                this.onPhotoTaken(mQuestion, mImageFile.getAbsolutePath());
            }
        }
    }

    @Override
    public void onDestroy() {
        mTextPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.handlePermissionResult(requestCode, grantResults);
    }

    /**
     * Called when this fragment is displayed by the ViewPager.
     */
    @Override
    public void onGettingPrimary(int oldPosition) {
//        if(mActivityCommunicator != null){
//            mActivityCommunicator.fragmentBecamePrimary(mQuestion, mImageName);
//        }

        DataManager.setCurrentQuestion(mQuestion);
        DataManager.setmCurrentPagerPosition(mPosition);

        if(!mGettingPrimaryCalledBefore){
            mReloadInitialized = initializeImageViews(oldPosition);
        }

        mGettingPrimaryCalledBefore = true;
        mCurrentlyPrimary = true;
    }

    /**
     * Called when this fragment ceases to be displayed by the ViewPager
     */
    @Override
    public void onLeavingPrimary(int newPosition) {
        mReloadInitialized = false;
        mCurrentlyPrimary = false;
        mGettingPrimaryCalledBefore = false;

        mTextPresenter.processAndStoreAnswer(mEditText, mQuestion, mQuestionID);

        //set bitmap free for garbage collection
        mImageView.setImageBitmap(null);
        mImageView.setImageDrawable(null);

        //stop all ongoing picasso requests
        Picasso.with(getActivity()).cancelRequest(mImageView);
    }

    /********************************************
     *          Photo Management
     ********************************************/

    /**
     * Manage the onReload method. Kind of a hack to workaround the issue that mImageView is null
     * when the navigation list is used to jump to this fragment from far away since onGettingPrimary is obviously
     * called before onCreateView in this scenario.
     */
    private boolean initializeImageViews(int oldPosition){
        boolean success = false;
        if(!mReloadInitialized && mImageView != null){
            success = true;

            if(oldPosition - mPosition >= 2 || mPosition - oldPosition >= 2){
                mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onReloadImage();
                        if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT && !DataManager.getCommentaryImageMap().containsKey(mQuestion)){
                            mCameraButton.setVisibility(View.VISIBLE);
                            mImageView.setVisibility(View.VISIBLE);
                            configureImageViewAsButton();
                        }
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                            mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
            } else {
                onReloadImage();
            }
        }
        return success;
    }


    public void onPhotoTaken(String question, String imagePath){
        if(/*mCurrentlyPrimary &&*/ question.equals(mQuestion)){
            ImageDataVO pathObj = DataManager.getCommentaryImageMap().put(mQuestion, new ImageDataVO(imagePath, null, null));
            final File imageFile = new File(imagePath);
            // delete the images that became obsolete -> do not clutter the storage of the user
            if(pathObj != null){
                DeleteImagesObservable observable = new DeleteImagesObservable();
                observable.deleteImagePairInBackground(pathObj).observeOn(AndroidSchedulers.mainThread()).subscribe();
            }
            Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mImageView, View.INVISIBLE, 0, 100);

            if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                Utility.animateView(mCameraButton, View.INVISIBLE, 0, 0);
            }

            Picasso.with(getActivity()).cancelRequest(mImageView);
            Picasso.with(getActivity())
                    .load(imageFile)
                    .fit()
                    .into(mImageView, new ThumbnailLoadedCallback(imagePath));
        }
    }

    public void onReloadImage(){
        if(DataManager.getCommentaryImageMap().containsKey(mQuestion)){
            if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                Utility.animateView(mCameraButton, View.INVISIBLE, 0, 0);
            }
            //it is made visible in the onViewCreated method (GlobalLayoutListener)
            Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 0);
            Utility.animateView(mImageView, View.INVISIBLE, 0, 100);


            ImageDataVO pathObj = DataManager.getCommentaryImageMap().get(mQuestion);
            Picasso.with(getActivity()).cancelRequest(mImageView);
            Picasso.with(getActivity())
                    .load(new File(pathObj.getmLargeImageFilePath()))
                    .fit()
                    .into(mImageView, new ThumbnailLoadedCallback(pathObj.getmLargeImageFilePath()));
        }
    }

    @Override
    public void showCameraAndStorageExplanation(PermissionRequest request) {
        Dialog dialog = DialogFactory.createSimpleOkErrorDialog(getActivity()
                , R.string.storage_camera_explanation_title
                , R.string.storage_camera_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public boolean isCameraPermissionGranted() {
       return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity()
               , Manifest.permission.CAMERA);
    }

    @Override
    public boolean isStoragePermissionGranted() {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getActivity()
                , Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public File startCameraIntent() {
        // create Intent to take a picture and return control to the calling application
        File intentImage =  Utility.createImageFile(mImageName, getActivity());
        Intent intent = new CameraActivity.IntentBuilder(getActivity())
                .skipConfirm()
                .facing(Facing.BACK)
                .to(intentImage)
                .build();

        this.startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        return intentImage;
    }


    private class ThumbnailLoadedCallback implements Callback {

        private String imagePath;

        private ThumbnailLoadedCallback(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        public void onSuccess() {
            Utility.animateView(mProgressBar, View.INVISIBLE, 0, 100);
            Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mDeleteButton, View.VISIBLE, 1.0f, 100);


            mImageView.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), EnlargeImageActivity.class);
                intent.putExtra(EnlargeImageActivity.IMAGE_FILE_PATH, imagePath);
                startActivity(intent);
            });

            mDeleteButton.setOnClickListener(view -> {
                mImageView.setImageBitmap(null);
                mImageView.setImageDrawable(null);
                if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                    Utility.animateView(mCameraButton, View.VISIBLE, 10f, 100);
                    configureImageViewAsButton();
                } else {
                    Utility.animateView(mImageView, View.INVISIBLE, 0, 100);
                }
                Utility.animateView(mDeleteButton, View.INVISIBLE, 0, 90);
                ImageDataVO pathsVO = DataManager.getCommentaryImageMap().remove(mQuestion);
                DeleteImagesObservable observable = new DeleteImagesObservable();
                observable.deleteImagePairInBackground(pathsVO).observeOn(AndroidSchedulers.mainThread()).subscribe();
            });
        }

        @Override
        public void onError() {

        }
    }

    public interface TextFragmentCommunicator{
        void fragmentBecamePrimary(String question, String imageName);
        void displayProgressOverlay();
        void setIntentImage(File intentImage);
    }

    public String getmQuestion() {
        return mQuestion;
    }

}
