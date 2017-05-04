/*
 * Copyright (C) 2015 Wasabeef
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.thb.ue.android.utility.animators;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Interpolator;

import eu.davidea.flexibleadapter.common.FlexibleItemAnimator;

public class ScaleInAnimator extends FlexibleItemAnimator {

	public ScaleInAnimator() {
	}

	public ScaleInAnimator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	@Override
	protected void animateRemoveImpl(final RecyclerView.ViewHolder holder, final int index) {
		animateRemove(holder.itemView, 0, null, null, holder);
	}

	@Override
	protected boolean preAnimateAddImpl(final RecyclerView.ViewHolder holder) {
		ViewCompat.setScaleX(holder.itemView, 0);
		ViewCompat.setScaleY(holder.itemView, 0);
		return true;
	}

	@Override
	protected void animateAddImpl(final RecyclerView.ViewHolder holder, final int index) {
		animateAdd(holder.itemView, 1, null, null, holder);
	}

	public void scaleSingleView(View view, float toScale, int pivotX, int pivotY, @Nullable Runnable startAction, @Nullable Runnable endAction){
		view.setPivotX(pivotX);
		view.setPivotY(pivotY);
		animateAdd(view, toScale, startAction, endAction, null);
	}

	private void animateRemove(View view, float scale, Runnable startAction, Runnable endAction, @Nullable RecyclerView.ViewHolder holder){
		ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
				.scaleX(scale)
				.scaleY(scale)
				.setDuration(getRemoveDuration())
				.setInterpolator(mInterpolator);

		if(holder != null)		viewAnimator.setListener(new DefaultRemoveVpaListener(holder));
		if(startAction != null) 	viewAnimator.withStartAction(startAction);
		if(endAction != null)		viewAnimator.withEndAction(endAction);

		viewAnimator.start();
	}

	private void animateAdd(View view, float scale, Runnable startAction, Runnable endAction, @Nullable RecyclerView.ViewHolder holder){
		ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
				.scaleX(scale)
				.scaleY(scale)
				.setDuration(getAddDuration())
				.setInterpolator(mInterpolator);

		if(holder != null)			viewAnimator.setListener(new DefaultAddVpaListener(holder));
		if(startAction != null) 	viewAnimator.withStartAction(startAction);
		if(endAction != null)		viewAnimator.withEndAction(endAction);

		viewAnimator.start();
	}

}