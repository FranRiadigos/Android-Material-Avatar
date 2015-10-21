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
package com.example.kuassivi.material_avatar.feature;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.kuassivi.material_avatar.core.animation.CircleTransform;
import com.example.kuassivi.material_avatar.core.animation.TargetAdapter;
import com.example.kuassivi.material_avatar.BuildConfig;
import com.example.kuassivi.material_avatar.R;
import com.squareup.picasso.Picasso;


public class ProfileActivity extends AppCompatActivity {

    public interface Extra {
        String AVATAR_URL = BuildConfig.APPLICATION_ID + "AVATAR_URL";
    }

    /**
     * Request code to retrieve avatar url
     */
    public static final int REQUEST_AVATAR_SELECTION_CODE = 1;

    /**
     * The shared view, this is the small circle avatar
     */
    private ImageButton mSharedAvatarView;

    /**
     * The shared view size
     */
    private int mAvatarSize;

    /**
     * Variable to store the selected avatar
     */
    private String mAvatarUrl;

    /**
     * Setup a click listener on the Avatar view.
     *
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAvatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size);
        mSharedAvatarView = ((ImageButton) findViewById(R.id.avatar));
        mSharedAvatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send the current saved avatar
                ChooseAvatarActivity.navigate(ProfileActivity.this, v, mAvatarUrl);
            }
        });

        if(savedInstanceState != null) {
            mAvatarUrl = savedInstanceState.getString(Extra.AVATAR_URL);
            updateAvatar(mAvatarUrl);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Extra.AVATAR_URL, mAvatarUrl);
        super.onSaveInstanceState(outState);
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
            if(data != null) {
                if(data != null) {
                    updateAvatar(data.getStringExtra(Extra.AVATAR_URL));
                }
            }
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
                if(data != null) {
                    updateAvatar(data.getStringExtra(Extra.AVATAR_URL));
                }
            }
        }
    }

    /**
     * The resize method from Picasso creates a new Request if there is no such resized file cached before.
     * <br>The transform method of Picasso doesn't work properly with a Target destination.
     * <br>
     * <br>So I finally decided to do those stuffs after loading the same image from cache.
     * <br>
     * <br>So I finally decided to do those stuffs after loading the same image from cache.
     * <br>
     *     <b>TODO: This need to be performed!
     *
     * @param imageUrl String
     */
    private void updateAvatar(String imageUrl) {
        if(imageUrl != null) {
            mAvatarUrl = imageUrl;
            Picasso.with(this).load(mAvatarUrl)
                    .into(new TargetAdapter() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, mAvatarSize, mAvatarSize, true);
                            CircleTransform circleTransform = new CircleTransform();
                            mSharedAvatarView.setImageBitmap(circleTransform.transform(scaledBitmap));
                        }
                    });
        }
    }
}
