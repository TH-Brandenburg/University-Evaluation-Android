package de.thb.ue.android.ui.evaluation.text;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.base.BaseFragment;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import de.thb.ue.android.utility.DialogFactory;
import de.thb.ue.android.utility.Utils;
import de.thb.ue.android.utility.customized_classes.BaseButton;
import thb.de.ue.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends BaseFragment implements TextMvpView {
    private static final String POSITION = "POSITION";
    private static final String QUESTION = "QUESTION";
    private static final String QUESTION_ID = "QUESTION_ID";


    @BindView(R.id.question_text_view)
    TextView mQuestionTextView;

    @BindView(R.id.edit_text)
    EditText mEditText;

    @BindView(R.id.camera_button)
    BaseButton mCameraButton;

    @BindView(R.id.comment_thumbnail)
    ImageButton mThumbnailButton;

    @Inject
    TextPresenter mPresenter;

    @Inject
    PermissionManager mPermissionManager;

    Unbinder mViewUnbinder;

    public TextFragment() {
        // Required empty public constructor
    }

    public static TextFragment newInstance(int position, String question, int questionId) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION, position);
        args.putInt(QUESTION_ID, questionId);
        args.putString(QUESTION, question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_text_view, container, false);
        mViewUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentComponent.bind(this);
        mPresenter.attachView(this);

        if (this.getArguments() != null) {
            Bundle args = this.getArguments();
            mPresenter.setmPosition(args.getInt(POSITION));
            mPresenter.setmQuestion(args.getString(QUESTION));
            mPresenter.setmQuestionId(args.getInt(QUESTION_ID));
        }

        mQuestionTextView.setText(mPresenter.getmQuestion());

        mCameraButton.addOnClickListener(v -> {
            String camTitle = getResources().getString(R.string.camera_permission_explanation_title);
            String camMessage = getResources().getString(R.string.camera_permission_explanation_message);
            mPermissionManager.with(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onPermissionGranted(this::goToCameraActivity)
                    .onPermissionDenied(() -> {})
                    .onPermissionShowRationale(this::displayStorageExplanation)
                    .request();
        });
    }

    @Override
    public void onPause() {
        super.onPause();

        String answer = mEditText.getText().toString();
        answer = answer.trim();

        mPresenter.putTextAnswer(answer);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewUnbinder.unbind();
        mPresenter.detachView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }

    private void goToCameraActivity() {
        try {
            String imageDirPath = Utils.getImageDirectory(getActivity()).getAbsolutePath();
            File image = new File(imageDirPath + mPresenter.getmQuestion() + ".jpg");
            image.delete();
            image.createNewFile();

            Intent intent = new CameraActivity.IntentBuilder(getActivity())
                    .facing(Facing.BACK)
                    .to(image)
                    .build();

            startActivityForResult(intent, 256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 256){

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displayStorageExplanation(PermissionRequest request) {
        AlertDialog dialog = DialogFactory.createAcceptDenyDialog(getActivity()
                , R.string.storage_camera_explanation_title
                , R.string.storage_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null);
        dialog.show();
    }
}
