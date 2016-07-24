package de.fhb.campusapp.eval.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

import de.fhb.campusapp.eval.activities.EnlargeImageActivity;
import de.fhb.campusapp.eval.custom.CustomEditText;
import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.ActivityInstanceAcquiredEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.ImageManager;
import de.fhb.campusapp.eval.utility.Observer.DeleteImagesObservable;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;

public class TextFragment extends BaseFragment implements PagerAdapterPageEvent {

    public static final String QUESTION = "QUESTION";
    public static final String POSITION = "POSITION";
    public static final String ID = "ID";
    public static final String PRIMARY = "PRIMARY";

    private String mQuestion;
    private String mImageName;
    private int mPosition;
    private int mQuestionID;
    private boolean mCurrentlyPrimary = false;

    /**
     * GettingPrimary is unfortunately called multiple times when fragment becomes primary
     * Some functions shall only executed once when this evrn is thrown though.
     */
    private boolean mGettingPrimaryCalledBefore = false;
    private boolean mReloadInitialized = false;


    private TextFragmentCommunicator mActivityCommunicator;

    @InjectView(R.id.question_text_view)
    private TextView mTextView;

    @InjectView(R.id.edit_text)
    private CustomEditText mEditText;

    @InjectView(R.id.comment_thumbnail)
    private ImageView mImageView;

    @InjectView(R.id.camera_button)
    private ImageButton mCameraButton;

    @InjectView(R.id.scrollView)
    private ScrollView mScrollView;

    @InjectView(R.id.image_progress_bar)
    private ProgressBar mProgressBar;

    @InjectView(R.id.text_fragment_root_layout)
    private RelativeLayout mRootLayout;

    @InjectView(R.id.delete_image_button)
    private ImageButton mDeleteButton;

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
        mActivityCommunicator = (TextFragmentCommunicator) activity;
        if(mCurrentlyPrimary && mGettingPrimaryCalledBefore){
            EventBus.get().post(new ActivityInstanceAcquiredEvent(activity));
        }
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
        return inflater.inflate(R.layout.fragment_text_view, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

                if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT && !DataHolder.getCommentaryImageMap().containsKey(mQuestion)){
                    configureImageViewAsButton();
                } else {
//                    Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 100);
                }

                mEditText.setHeight((mRootLayout.getHeight() - mTextView.getHeight()) / 2);
                mEditText.setMovementMethod(new ScrollingMovementMethod());

                TextQuestionVO dto = DataHolder.retrieveTextQuestionVO(mQuestion);

                // define EditTextView as number field
                if(dto.getOnlyNumbers()){
                    mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mEditText.setHint(getResources().getString(R.string.edit_text_click_hint_numeric));
                }

                //set the max number of symbols this editText can hold
                InputFilter.LengthFilter filter = new InputFilter.LengthFilter(dto.getMaxLength());
                InputFilter[] filters = {filter};
                mEditText.setFilters(filters);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        mEditText.setPagerAdapter((PagerAdapterSetPrimary) getActivity());
//
//        mEditText.setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER);
//
//        mEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
//            ((PagerAdapterSetPrimary) getActivity()).setPrimaryFragment(mPosition + 1);
//            return true;
//        });
    }

    private void configureImageViewAsButton() {
        mImageView.setBackgroundColor(getResources().getColor(R.color.campusapptheme_color_light_gray));

        Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
        Utility.animateView(mCameraButton, View.VISIBLE, 1.0f, 100);

        mCameraButton.setOnClickListener(listener -> {
            ImageManager manager = new ImageManager();
            File intentImage = manager.startCameraIntent(getActivity(), mImageName);
            mActivityCommunicator.setIntentImage(intentImage);
        });

        mImageView.setOnClickListener(listener -> {
            ImageManager manager = new ImageManager();
            File intentImage = manager.startCameraIntent(getActivity(), mImageName);
            mActivityCommunicator.setIntentImage(intentImage);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentlyPrimary){
            mReloadInitialized = initializeImageViews(mPosition);
        }
    }

    /**
     * Called when this fragment is displayed by the ViewPager.
     */
    @Override
    public void onGettingPrimary(int oldPosition) {
//        mActivityCommunicator.fragmentBecamePrimary(mQuestion, mImageName);

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
        //clean text of unnecessary information before storing
        String answer = mEditText.getText().toString();
        answer = answer.trim();
        StringUtils.replace(answer, "\n\r", "");
        StringUtils.replace(answer, "\n", "");

        //store the answer in answerDTO or refresh it if already present
        TextAnswerVO answerDTO = DataHolder.isTextQuestionAnswered(mQuestion);
        if(answerDTO == null){
            DataHolder.getAnswersVO().getTextAnswers().add(new TextAnswerVO(mQuestionID, mQuestion, answer));
        } else {
            answerDTO.setAnswerText(answer);
        }

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
                        if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT && !DataHolder.getCommentaryImageMap().containsKey(mQuestion)){
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
            ImageDataVO pathObj = DataHolder.getCommentaryImageMap().put(mQuestion, new ImageDataVO(imagePath, null, null));
            final File imageFile = new File(imagePath);
            // delete the images that became obsolete -> do not clutter the storage of the user
            if(pathObj != null){
                DeleteImagesObservable observable = new DeleteImagesObservable();
                observable.deleteImagePairInBackground(pathObj).observeOn(AndroidSchedulers.mainThread()).subscribe();
            }
            Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mImageView, View.INVISIBLE, 0, 100);

            if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                Utility.animateView(mCameraButton, View.INVISIBLE, 0, 100);
            }

            Picasso.with(getActivity()).cancelRequest(mImageView);
            Picasso.with(getActivity())
                    .load(imageFile)
                    .fit()
                    .centerCrop()
                    .into(mImageView, new ThumbnailLoadedCallback(imagePath));
        }
    }

    public void onReloadImage(){
        if(DataHolder.getCommentaryImageMap().containsKey(mQuestion)){
            //it is made visible in the onViewCreated method (GlobalLayoutListener)
            Utility.animateView(mProgressBar, View.INVISIBLE, 1.0f, 0);
            Utility.animateView(mImageView, View.INVISIBLE, 0, 100);

            if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                Utility.animateView(mCameraButton, View.INVISIBLE, 0, 100);
            }

            ImageDataVO pathObj = DataHolder.getCommentaryImageMap().get(mQuestion);
            Picasso.with(getActivity()).cancelRequest(mImageView);
            Picasso.with(getActivity())
                    .load(new File(pathObj.getmLargeImageFilePath()))
                    .fit()
                    .centerCrop()
                    .into(mImageView, new ThumbnailLoadedCallback(pathObj.getmLargeImageFilePath()));
        }
    }


    private class ThumbnailLoadedCallback implements Callback{

        private String imagePath;

        private ThumbnailLoadedCallback(String imagePath) {
            this.imagePath = imagePath;
        }

        @Override
        public void onSuccess() {
            if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                Utility.animateView(mCameraButton, View.INVISIBLE, 0, 0);
            }
            Utility.animateView(mProgressBar, View.INVISIBLE, 0, 100);
            Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mDeleteButton, View.VISIBLE, 1.0f, 100);


            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EnlargeImageActivity.class);
                    intent.putExtra(EnlargeImageActivity.IMAGE_FILE_PATH, imagePath);
                    startActivity(intent);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mImageView.setImageBitmap(null);
                    mImageView.setImageDrawable(null);
                    if(FeatureSwitch.IMAGEVIEW_OPENS_CAMERA_INTENT){
                        Utility.animateView(mCameraButton, View.VISIBLE, 10f, 100);
                        configureImageViewAsButton();
                    } else {
                        Utility.animateView(mImageView, View.INVISIBLE, 0, 100);
                    }
                    Utility.animateView(mDeleteButton, View.INVISIBLE, 0, 90);
                    ImageDataVO pathsVO = DataHolder.getCommentaryImageMap().remove(mQuestion);
                    DeleteImagesObservable observable = new DeleteImagesObservable();
                    observable.deleteImagePairInBackground(pathsVO).observeOn(AndroidSchedulers.mainThread()).subscribe();
                }
            });
        }

        @Override
        public void onError() {

        }
    }

    /********************************************
     *          Event Listener
     ********************************************/

    @SuppressWarnings("unused")
    public void onActivityInstanceAcquired(ActivityInstanceAcquiredEvent event){
        if(mCurrentlyPrimary){
            mActivityCommunicator = (TextFragmentCommunicator) event.getActivity();
            mActivityCommunicator.fragmentBecamePrimary(mQuestion, mImageName);
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
