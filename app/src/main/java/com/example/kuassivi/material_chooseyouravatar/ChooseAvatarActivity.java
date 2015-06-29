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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kuassivi.material_chooseyouravatar.adapter.MarginDecoration;
import com.example.kuassivi.material_chooseyouravatar.adapter.RecyclerViewAdapter;
import com.example.kuassivi.material_chooseyouravatar.animation.CircleTransform;
import com.example.kuassivi.material_chooseyouravatar.animation.ScalableBitmapAnimator;
import com.example.kuassivi.material_chooseyouravatar.animation.TargetAdapter;
import com.example.kuassivi.material_chooseyouravatar.transition.TransitionListenerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ChooseAvatarActivity extends AppCompatActivity
    implements RecyclerViewAdapter.OnItemClickListener {

    public static final String EXTRA_AVATAR = BuildConfig.APPLICATION_ID + ".EXTRA_AVATAR";

    private static List<ViewModel> items = new ArrayList<>();

    static {
        for (int i = 1; i <= 10; i++) {
            items.add(new ViewModel("http://lorempixel.com/500/500/people/" + i));
        }
    }

    private ImageButton mSharedAvatarView;
    private FrameLayout mHeaderView;
    private GalleryView mAvatarContainer;

    private int fabSize;
    private String imageUrl;

    public static void navigate(Activity context, View transitionView, String avatar) {
        Intent intent = new Intent(context, ChooseAvatarActivity.class);
        intent.putExtra(EXTRA_AVATAR, avatar);

        ActivityOptionsCompat optionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(context, transitionView,
                context.getString(R.string.transition_name));
        ActivityCompat.startActivityForResult(context, intent,
            ProfileActivity.REQUEST_AVATAR_SELECTION_CODE, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        initTransitions();
        setContentView(R.layout.activity_choose_avatar);
        checkExtras();
        loadResources();
        setViews();
        startAdapter();
        updateAvatarAndStartTransition();
    }

    private void checkExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            imageUrl = extras.getString(EXTRA_AVATAR);
        }
    }

    private void loadResources() {
        fabSize = getResources().getDimensionPixelSize(R.dimen.fab_avatar_size_expanded);
    }

    private void setViews() {
        mSharedAvatarView = (ImageButton) findViewById(R.id.shared_avatar_view);
        mHeaderView = (FrameLayout) findViewById(R.id.header_view);
        mAvatarContainer = (GalleryView) mHeaderView.findViewById(R.id.avatar_container);
    }

    private void startAdapter() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        adapter.setOnItemClickListener(this);
//        adapter.setAnimationStartAfter(
//                getResources().getInteger(R.integer.curve_motion_delay) * 2);
        recyclerView.setAdapter(adapter);
    }

    private void updateAvatarAndStartTransition() {
        if (imageUrl != null)
            Picasso.with(this)
                .load(imageUrl)
                .into(new TargetAdapter() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        updateSharedAvatarView(bitmap);

                        mAvatarContainer.setImageBitmap(bitmap);
                        mAvatarContainer.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        startPostponedEnterTransition();
                    }
                });
        else startPostponedEnterTransition();
    }

    @Override
    public void onItemClick(View view, final ViewModel viewModel) {
        imageUrl = viewModel.getImage();

        Picasso.with(this).load(viewModel.getImage()).into(
            new TargetAdapter() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    updateSharedAvatarView(bitmap);

                    long duration = getResources().getInteger(R.integer.bitmap_motion);
                    ScalableBitmapAnimator bitmapAnimator =
                        new ScalableBitmapAnimator(ScalableBitmapAnimator.SCALE_FROM);
                    bitmapAnimator.setDuration(duration)
                        .setInterpolator(new DecelerateInterpolator());
                    mAvatarContainer.setImageBitmap(bitmap, true)
                        .setBitmapAnimators(bitmapAnimator)
                        .startBitmapAnimation();

                }
            });
    }

    private void updateSharedAvatarView(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, fabSize, fabSize, true);
        CircleTransform circleTransform = new CircleTransform();
        mSharedAvatarView.setBackground(new BitmapDrawable(getResources(),
            circleTransform.transform(scaledBitmap)));
    }

    private void initTransitions() {

        getWindow().getSharedElementEnterTransition()
            .addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionStart(Transition transition) {
                    mHeaderView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    mHeaderView.setVisibility(View.VISIBLE);
                    mSharedAvatarView.setAlpha(0f);
                }
            });

        getWindow().getReturnTransition()
            .addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionStart(Transition transition) {

                    final long duration = getResources().getInteger(R.integer.duration_fast);

                    if (imageUrl != null) {
                        int dWidth = mAvatarContainer.getDrawableWidth(),
                            dHeight = mAvatarContainer.getDrawableHeight(),
                            vWidth = mAvatarContainer.getInsetWidth(),
                            vHeight = mAvatarContainer.getInsetHeight();

                        float scaleTo = (float) fabSize / (float) vHeight;
                        long delay = Float.valueOf((float) duration /
                            (((float) vWidth / (float) vHeight) + ((float) dWidth / (float) dHeight))).longValue();

                        ScalableBitmapAnimator bitmapAnimator =
                            new ScalableBitmapAnimator(scaleTo, ScalableBitmapAnimator.SCALE_TO);
                        bitmapAnimator.setScaleFactor(1f).setInterpolator(new DecelerateInterpolator())
                            .setDuration(duration).setStartDelay(delay);
                        mAvatarContainer.setBitmapAnimators(bitmapAnimator).startBitmapAnimation();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSharedAvatarView.setAlpha(1f);
                        }
                    }, duration);
                }
            });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("imageUrl", imageUrl);
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }
}
