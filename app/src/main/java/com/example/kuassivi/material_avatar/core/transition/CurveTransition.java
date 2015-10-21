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
package com.example.kuassivi.material_avatar.core.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Path;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CurveTransition extends Transition {
    private static final String PROPNAME_VIEW_X = "kuassivi:curveTransition:viewX";
    private static final String PROPNAME_VIEW_Y = "kuassivi:curveTransition:viewY";
    private static final String[] sTransitionProperties = {
        PROPNAME_VIEW_X,
        PROPNAME_VIEW_Y
    };

    public CurveTransition() {
    }

    public CurveTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    private void captureValues(TransitionValues values) {
        View view = values.view;

        if (view.isLaidOut() || view.getX() != 0 || view.getY() != 0) {
            values.values.put(PROPNAME_VIEW_X, view.getX());
            values.values.put(PROPNAME_VIEW_Y, view.getY());
        }
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(final ViewGroup sceneRoot, TransitionValues startValues,
                                   TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final View view = endValues.view;
        float startX = (Float) startValues.values.get(PROPNAME_VIEW_X);
        float startY = (Float) startValues.values.get(PROPNAME_VIEW_Y);
        float endX = (Float) endValues.values.get(PROPNAME_VIEW_X);
        float endY = (Float) endValues.values.get(PROPNAME_VIEW_Y);

        ObjectAnimator animator = null;
        if (startX != endX || startY != endY) {
            Path xyPath = getPathMotion().getPath(startX, startY,
                endX, endY);
            animator = ObjectAnimator
                .ofFloat(view, View.X, View.Y, xyPath);
        }
        return animator;
    }

}

