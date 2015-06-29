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
package com.example.kuassivi.material_chooseyouravatar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.kuassivi.material_chooseyouravatar.animation.CircleTransform;
import com.example.kuassivi.material_chooseyouravatar.animation.TargetAdapter;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    public static final int REQUEST_AVATAR_SELECTION_CODE = 1;

    private ImageButton mSharedFab;
    private int mFabSize;
    private String mAvatarUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFabSize = getResources().getDimensionPixelSize(R.dimen.fab_avatar_size);
        mSharedFab = ((ImageButton) findViewById(R.id.avatar));
        mSharedFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseAvatarActivity.navigate(ProfileActivity.this, v, mAvatarUrl);
            }
        });
    }

    /**
     * Called before Reenter transition starts, so it gives some time to achieve goals
     *
     * @param resultCode int
     * @param data       Intent
     */
    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            updateAvatar(data);
        }
    }

    /**
     * Added for compatibility!
     *
     * @param requestCode int
     * @param resultCode  int
     * @param data        Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_AVATAR_SELECTION_CODE) {
            if (resultCode == RESULT_OK) {
                updateAvatar(data);
            }
        }
    }

    /**
     * The resize method of Picasso creates a new Request if there is no such resized file cached before.
     * <br>The transform method of Picasso doesn't work properly with a Target destination.
     * <br>
     * <br>So I finally decided to do those stuffs after loading the same image from cache.
     * <br><b>TODO: This need to be performed!
     *
     * @param data Intent
     */
    private void updateAvatar(Intent data) {
        mAvatarUrl = data.getStringExtra("imageUrl");
        Picasso.with(this).load(mAvatarUrl)
            .into(new TargetAdapter() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, mFabSize, mFabSize, true);
                    CircleTransform circleTransform = new CircleTransform();
                    mSharedFab.setImageBitmap(circleTransform.transform(scaledBitmap));
                }
            });
    }
}
