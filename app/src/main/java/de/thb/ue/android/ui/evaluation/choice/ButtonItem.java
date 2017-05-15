//package de.thb.ue.android.ui.evaluation.choice;
//
//import android.support.annotation.ColorRes;
//import android.support.annotation.DrawableRes;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import de.thb.ue.android.data.VOs.ChoiceVO;
//import de.thb.ue.android.utility.Utils;
//import de.thb.ue.android.utility.customized_classes.SingleChoiceButton;
//import eu.davidea.flexibleadapter.FlexibleAdapter;
//import eu.davidea.flexibleadapter.items.AbstractFlexibleItem;
//import eu.davidea.viewholders.FlexibleViewHolder;
//import thb.de.ue.android.R;
//
///**
// * Created by scorp on 14.05.2017.
// */
//
//public class ButtonItem extends AbstractFlexibleItem<ButtonItem.ButtonViewHolder> {
//    private final String mId;
//    private final ChoiceVO mChoice;
//    private final int mButtonBackground;
//    private final int mButtonFontColor;
//    private final View.OnClickListener mButtonClickListener;
//
//    private boolean mIsSelected;
//    private VariableChangeListener mIsSelectedListener;
//
//    public ButtonItem(String mId, ChoiceVO mChoice, @DrawableRes int buttonBackground, @ColorRes int buttonFontColor, View.OnClickListener buttonClickListener) {
//        this.mId = mId;
//        this.mChoice = mChoice;
//        this.mButtonBackground = buttonBackground;
//        this.mButtonFontColor = buttonFontColor;
//        this.mButtonClickListener = buttonClickListener;
//    }
//
//    @Override
//    public boolean equals(Object inObject) {
//        if (inObject instanceof ButtonItem) {
//            ButtonItem inItem = (ButtonItem) inObject;
//            return this.mId.equals(inItem.mId);
//        }
//        return false;
//    }
//
//    @Override
//    public int hashCode() {
//        return this.mId.hashCode();
//    }
//
//    @Override
//    public int getLayoutRes() {
//        return R.layout.button_list_element;
//    }
//
//
//    @Override
//    public ButtonViewHolder createViewHolder(FlexibleAdapter adapter, LayoutInflater inflater, ViewGroup parent) {
//        return new ButtonViewHolder(inflater.inflate(getLayoutRes(), parent, false), adapter);
//    }
//
//    @Override
//    public void bindViewHolder(FlexibleAdapter adapter, ButtonViewHolder holder, int position, List payloads) {
//        holder.mSendDelayButton.addOnClickListener(mButtonClickListener);
//        holder.mSendDelayButton.setText(mChoice.getChoiceText());
//        holder.mSendDelayButton.setmChoice(mChoice);
//        holder.mSendDelayButton.setBackground(Utils.getDrawableCompat(holder.itemView.getContext(), mButtonBackground));
//        holder.mSendDelayButton.setTextColor(Utils.getColorCompat(holder.itemView.getContext(), mButtonFontColor));
//        holder.mSendDelayButton.setSelected(mIsSelected);
//
//        this.mIsSelectedListener = () -> {
//          holder.mSendDelayButton.setSelected(mIsSelected);
//        };
//    }
//
//    boolean ismIsSelected() {
//        return mIsSelected;
//    }
//
//    void setmIsSelected(boolean mIsSelected) {
//        this.mIsSelected = mIsSelected;
//        this.mIsSelectedListener.onChange();
//    }
//
//    ChoiceVO getmChoice() {
//        return mChoice;
//    }
//
//    static final class ButtonViewHolder extends FlexibleViewHolder {
//
//        @BindView(R.id.choice_button)
//        SingleChoiceButton mSendDelayButton;
//
//        ButtonViewHolder(View view, FlexibleAdapter adapter) {
//            super(view, adapter);
//            ButterKnife.bind(this, view);
//        }
//    }
//
//    public interface VariableChangeListener{
//        void onChange();
//    }
//}
//
//
