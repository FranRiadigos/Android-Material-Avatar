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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.SystemClock;

/**
 * Extracted from {@link com.squareup.picasso.PicassoDrawable}
 */
public class FadeDrawable extends BitmapDrawable {
    private static final float FADE_DURATION = 600f; //ms

    long startTimeMillis;
    boolean animating;
    int alpha = 0xFF;

    public FadeDrawable(Context context, Bitmap bitmap) {
        this(context.getResources(), bitmap);
    }

    public FadeDrawable(Resources res, Bitmap bitmap) {
        super(res, bitmap);
        animating = true;
        startTimeMillis = SystemClock.uptimeMillis();
    }

    @Override
    public void draw(Canvas canvas) {
        float normalized = (SystemClock.uptimeMillis() - startTimeMillis) / FADE_DURATION;
        if (normalized >= 1f) {
            animating = false;
            super.draw(canvas);
        } else {
            int partialAlpha = (int) (alpha * normalized);
            setAlpha(partialAlpha);
            super.draw(canvas);
            setAlpha(alpha);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
                invalidateSelf();
            }
        }
    }
}
