/*
 * Copyright (C) 2015 Kiko Gonzalez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.kuassivi.material_chooseyouravatar.transition;

import android.animation.Animator;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;

import com.example.kuassivi.material_chooseyouravatar.R;

public class RevealTransition extends Visibility {

  private float mStartRadius;

  public RevealTransition() { }

  public RevealTransition(Context context, AttributeSet attrs) {
    super(context, attrs);
    mStartRadius = context.getResources().getDimensionPixelSize(R.dimen.fab_avatar_size_expanded);
  }

  @Override
  public Animator onAppear(ViewGroup sceneRoot, final View view, TransitionValues startValues,
      TransitionValues endValues) {
    return createAnimator(view, true);
  }

  @Override
  public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues,
      TransitionValues endValues) {
    return createAnimator(view, false);
  }

  private Animator createAnimator(View view, boolean isOnAppear) {
    Animator wrapper = null;
    float startRadius, endRadius;
    if(view != null) {
      startRadius = isOnAppear ? (mStartRadius / 2f) : calculateMaxRadius(view.getWidth(), view.getHeight());
      endRadius = isOnAppear ? calculateMaxRadius(view.getWidth(), view.getHeight()) : (mStartRadius / 2f);
      int centerX = ((Float)(view.getWidth() / 2f)).intValue();
      int centerY = ((Float)(view.getHeight() / 2f)).intValue();
      Animator reveal = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius,
              endRadius);
      wrapper = new NoPauseAnimator(reveal);
    }
    return wrapper;
  }

  private float calculateMaxRadius(int width, int height) {
    float widthSquared = width * width;
    float heightSquared = height * height;
    return FloatMath.sqrt(widthSquared + heightSquared) / 2;
  }

}

