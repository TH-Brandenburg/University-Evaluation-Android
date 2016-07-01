package de.fhb.campusapp.eval.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
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
import org.joda.time.Instant;

import java.io.File;

import de.fhb.campusapp.eval.activities.EnlargeImageActivity;
import de.fhb.campusapp.eval.custom.CustomEditText;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.interfaces.PagerAdapterPageEvent;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.Observer.DeleteImagesObservable;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;

public class TextFragment extends BaseFragment implements PagerAdapterPageEvent {

    public static final String QUESTION = "QUESTION";
    public static final String POSITION = "POSITION";
    public static final String ID = "ID";

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
        if(isVisibleToUser){
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
            @Override
            public void onGlobalLayout() {
                mImageView.setMaxHeight((mRootLayout.getHeight() - mTextView.getHeight())/2);
                mImageView.setMaxWidth(mRootLayout.getWidth());

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

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                ((PagerAdapterSetPrimary) getActivity()).setPrimaryFragment(mPosition + 1);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mCurrentlyPrimary){
            mReloadInitialized = initializeReload();
        }
    }

    /**
     * Called when this fragment is displayed by the ViewPager.
     */
    @Override
    public void onGettingPrimary() {
        mActivityCommunicator.fragmentBecamePrimary(mQuestion, mImageName);
        if(!mGettingPrimaryCalledBefore){
            mReloadInitialized = initializeReload();
        }
        mGettingPrimaryCalledBefore = true;
        mCurrentlyPrimary = true;
//        questionCommunicator.updateImageName(mImageName);
//        EventBus.getEventBus().post(new FragmentGotPrimaryEvent(mQuestion, mImageName));
    }

    /**
     * Called when this fragment ceases to be displayed by the ViewPager
     */
    @Override
    public void onLeavingPrimary() {
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
    private boolean initializeReload(){
        boolean success = false;
        if(!mReloadInitialized && mImageView != null){
            success = true;
            mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(DataHolder.getCommentaryImageMap().containsKey(mQuestion)){
                        onReloadImage();
                    }
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                        mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        mImageView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
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
//            Picasso.with(getActivity()).setLoggingEnabled(true);
            Picasso.with(getActivity()).cancelRequest(mImageView);
            Picasso.with(getActivity())
                    .load(imageFile)
                    .fit()
                    .centerCrop()
                    .into(mImageView, new ThumbnailLoadedCallback(imagePath));
        }
    }

    public void onReloadImage(){
        if( DataHolder.getCommentaryImageMap().containsKey(mQuestion)){
            Utility.animateView(mProgressBar, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mImageView, View.INVISIBLE, 0, 100);
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
            Utility.animateView(mProgressBar, View.INVISIBLE, 0, 100);
            Utility.animateView(mImageView, View.VISIBLE, 1.0f, 100);
            Utility.animateView(mDeleteButton, View.VISIBLE, 1.0f, 100);


            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), EnlargeImageActivity.class);
                    intent.putExtra(EnlargeImageActivity.IMAGE_FILE_PATH, imagePath);
//                    mActivityCommunicator.displayProgressOverlay();
                    startActivity(intent);
                }
            });

            mDeleteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    mImageView.setImageBitmap(null);
                    mImageView.setImageDrawable(null);
                    Utility.animateView(mImageView, View.GONE, 0, 100);
                    Utility.animateView(mDeleteButton, View.GONE, 0, 90);
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

    public interface TextFragmentCommunicator{
        public void fragmentBecamePrimary(String question, String imageName);
        public void displayProgressOverlay();
    }

    public String getmQuestion() {
        return mQuestion;
    }

}
