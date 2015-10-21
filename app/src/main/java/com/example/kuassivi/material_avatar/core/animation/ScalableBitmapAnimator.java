/*
 * Copyright (C) 2015 Francisco Gonzalez-Armijo Riádigos
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
package com.example.kuassivi.material_avatar.core.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.support.annotation.IntDef;
import android.widget.ImageView;

import com.example.kuassivi.material_avatar.core.view.GalleryView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ScalableBitmapAnimator extends AbstractBitmapAnimator {
    public static final float DEFAULT_SCALE_FACTOR = 1.5f;
    public static final int SCALE_FROM = 0x01;
    public static final int SCALE_TO = 0x02;
    private float mSourceScale = 0f;
    private float mCurrentScale;
    private int mDirection;

    public ScalableBitmapAnimator(@ScaleSource int direction) {
        mDirection = direction;
    }

    public ScalableBitmapAnimator(float sourceScale, @ScaleSource int direction) {
        mSourceScale = sourceScale;
        mDirection = direction;
    }

    @Override
    public Animator getAnimator(final GalleryView view) {
        configureBounds(view);
        ValueAnimator animator = configureAnimator();
        if (animator != null) {
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Matrix matrix = new Matrix();
                    float currentScale = (Float) animation.getAnimatedValue(), dx, dy;
                    int dWidth = view.getDrawableWidth(),
                        dHeight = view.getDrawableHeight(),
                        vWidth = view.getInsetWidth(),
                        vHeight = view.getInsetHeight();
                    dx = -((dWidth * currentScale) - vWidth) * 0.5f;
                    dy = -((dHeight * currentScale) - vHeight) * 0.5f;
                    matrix.setScale(currentScale, currentScale);
                    matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
                    view.setImageMatrix(matrix);
                }
            });
            animator.setInterpolator(getInterpolator());
            if (getDuration() != null)
                animator.setDuration(getDuration());
            if (getStartDelay() != null)
                animator.setStartDelay(getStartDelay());
        }
        return animator;
    }

    /**
     * Switch between scale sources to animate
     *
     * @return ValueAnimator
     */
    private ValueAnimator configureAnimator() {
        ValueAnimator currentAnimator = null;
        float scale = mCurrentScale * getScaleFactor();
        if (mSourceScale > 0)
            scale = mSourceScale;
        switch (mDirection) {
            case SCALE_FROM:
                currentAnimator = ValueAnimator.ofFloat(scale, mCurrentScale);
                break;
            case SCALE_TO:
                currentAnimator = ValueAnimator.ofFloat(mCurrentScale, scale);
                break;
        }
        return currentAnimator;
    }

    /**
     * It sets and configures the bounds of the loaded bitmap and map the drawable to fit the scale
     *
     * @param view GalleryView
     */
    private void configureBounds(GalleryView view) {
        if (view.getDrawable() == null) {
            return;
        }
        float dx, dy;
        int dWidth = view.getDrawableWidth(),
            dHeight = view.getDrawableHeight(),
            vWidth = view.getInsetWidth(),
            vHeight = view.getInsetHeight();
        view.setScaleType(ImageView.ScaleType.MATRIX);
        Matrix matrix = new Matrix();
        if (dWidth * vHeight > vWidth * dHeight) {
            mCurrentScale = (float) vHeight / (float) dHeight;
        } else {
            mCurrentScale = (float) vWidth / (float) dWidth;
        }
        dx = -((dWidth * mCurrentScale * getScaleFactor()) - vWidth) * 0.5f;
        dy = -((dHeight * mCurrentScale * getScaleFactor()) - vHeight) * 0.5f;
        matrix.setScale(mCurrentScale * getScaleFactor(), mCurrentScale * getScaleFactor());
        matrix.postTranslate((int) (dx + 0.5f), (int) (dy + 0.5f));
        view.setImageMatrix(matrix);
    }

    @Override
    public Float getScaleFactor() {
        if (super.getScaleFactor() == null)
            return DEFAULT_SCALE_FACTOR;
        return super.getScaleFactor();
    }

    @IntDef({SCALE_FROM, SCALE_TO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScaleSource {
    }
}
