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

public class SlideInDownAnimator extends FlexibleItemAnimator {

	public SlideInDownAnimator() {

	}

	public SlideInDownAnimator(Interpolator interpolator) {
		mInterpolator = interpolator;
	}

	@Override
	protected void animateRemoveImpl(final RecyclerView.ViewHolder holder, final int index) {
		animateRemove(holder.itemView, holder);
	}

	@Override
	protected boolean preAnimateAddImpl(final RecyclerView.ViewHolder holder) {
		ViewCompat.setTranslationY(holder.itemView, -holder.itemView.getHeight());
		ViewCompat.setAlpha(holder.itemView, 0);
		return true;
	}

	@Override
	protected void animateAddImpl(final RecyclerView.ViewHolder holder, final int index) {
		animateAdd(holder.itemView, holder);
	}

	public void slideInDownSingleView(View view){
		ViewCompat.setTranslationY(view, -view.getHeight());
		ViewCompat.setAlpha(view, 0);
		animateAdd(view, null);
	}

	public void slideOutDownSingleView(View view){
		animateRemove(view, null);
	}

	private void animateRemove(View view, @Nullable RecyclerView.ViewHolder holder){
		ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
				.translationY(-view.getHeight())
				.alpha(0)
				.setDuration(getRemoveDuration())
				.setInterpolator(mInterpolator);

		if(holder != null){
			viewAnimator.setListener(new DefaultRemoveVpaListener(holder));
		}

		viewAnimator.start();
	}

	private void animateAdd(View view, @Nullable RecyclerView.ViewHolder holder){
		ViewPropertyAnimatorCompat viewAnimator = ViewCompat.animate(view)
				.translationY(0)
				.alpha(1)
				.setDuration(getAddDuration())
				.setInterpolator(mInterpolator);

		if(holder != null){
			viewAnimator.setListener(new DefaultAddVpaListener(holder));
		}

		viewAnimator.start();
	}

}