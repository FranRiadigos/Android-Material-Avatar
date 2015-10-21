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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.kuassivi.material_avatar.core.adapter.MarginDecoration;
import com.example.kuassivi.material_avatar.core.adapter.RecyclerViewAdapter;
import com.example.kuassivi.material_avatar.core.animation.CircleTransform;
import com.example.kuassivi.material_avatar.core.animation.ScalableBitmapAnimator;
import com.example.kuassivi.material_avatar.core.animation.TargetAdapter;
import com.example.kuassivi.material_avatar.core.model.ViewModel;
import com.example.kuassivi.material_avatar.core.transition.TransitionListenerAdapter;
import com.example.kuassivi.material_avatar.core.view.GalleryView;
import com.example.kuassivi.material_avatar.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ChooseAvatarActivity extends AppCompatActivity
    implements RecyclerViewAdapter.OnItemClickListener, View.OnClickListener{

    /**
     * List of online avatars
     */
    private static List<ViewModel> items = new ArrayList<>();
    static {
        for (int i = 1; i <= 10; i++) {
            items.add(new ViewModel("http://lorempixel.com/500/500/people/" + i));
        }
    }

    /**
     * Shared view (biggest component)
     */
    private ImageButton mSharedAvatarView;

    /**
     * Our header view, where the shared view will appear
     */
    private FrameLayout mHeaderView;

    /**
     * A custom ImageView that will use the ScalableBitmapAnimator class to flash and scale down effect
     */
    private GalleryView mAvatarContainer;

    /**
     * The Fab button to apply selected avatar and return to ProfileActivity
     */
    private FloatingActionButton mFabView;

    /**
     * The size of the new shared view
     */
    private int sharedAvatarSize;

    /**
     * Variable used to store and deliver the selected avatar to ProfileActivity
     */
    private String mAvatarUrl;

    /**
     * Navigate to this Activity
     * <br>Start activity with material transition
     * <br>We use startActivityForResult() method to be able to deliver the chosen avatar on ProfileActivity
     *
     * @param context Activity
     * @param transitionView View
     * @param savedAvatarUrl String
     */
    public static void navigate(Activity context, View transitionView, String savedAvatarUrl) {
        Intent intent = new Intent(context, ChooseAvatarActivity.class);
        intent.putExtra(ProfileActivity.Extra.AVATAR_URL, savedAvatarUrl);

        ActivityOptionsCompat optionsCompat =
            ActivityOptionsCompat.makeSceneTransitionAnimation(context, transitionView,
                context.getString(R.string.transition_name));
        ActivityCompat.startActivityForResult(context, intent,
                ProfileActivity.REQUEST_AVATAR_SELECTION_CODE, optionsCompat.toBundle());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Postpone any pending transition before image url are loaded from Picasso
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            initTransitions();
        }

        setContentView(R.layout.activity_choose_avatar);

        // Setup views and adapter, then start postponed transitions once Picasso has loaded image
        setupViews();
        setupAdapter();

        if(savedInstanceState != null) {
            mAvatarUrl = savedInstanceState.getString(ProfileActivity.Extra.AVATAR_URL);
            showFabView();
        }

        updateAvatarAndStartTransition();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ProfileActivity.Extra.AVATAR_URL, mAvatarUrl);
        super.onSaveInstanceState(outState);
    }

    private void setupViews() {
        sharedAvatarSize = getResources().getDimensionPixelSize(R.dimen.avatar_size_expanded);

        mAvatarUrl = getIntent().getStringExtra(ProfileActivity.Extra.AVATAR_URL);

        mSharedAvatarView = (ImageButton) findViewById(R.id.shared_avatar_view);
        mHeaderView = (FrameLayout) findViewById(R.id.header_view);
        mAvatarContainer = (GalleryView) mHeaderView.findViewById(R.id.avatar_container);
        mFabView = (FloatingActionButton) findViewById(R.id.fab);
        mFabView.setVisibility(View.GONE);
        mFabView.setOnClickListener(this);
    }

    private void setupAdapter() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addItemDecoration(new MarginDecoration(this));
        recyclerView.setHasFixedSize(true);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(items);
        adapter.setOnItemClickListener(this);
//        adapter.setAnimationStartAfter(
//                getResources().getInteger(R.integer.curve_motion_delay) * 2);
        recyclerView.setAdapter(adapter);
    }

    /**
     * If there is no a previous avatar loaded, then just start material transitions
     */
    private void updateAvatarAndStartTransition() {
        if (mAvatarUrl != null) {
            Picasso.with(this)
                    .load(mAvatarUrl)
                    .into(new TargetAdapter() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            updateSharedAvatarView(bitmap);

                            mAvatarContainer.setImageBitmap(bitmap);
                            mAvatarContainer.setScaleType(ImageView.ScaleType.CENTER_CROP);

                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startPostponedEnterTransition();
                            }
                        }
                    });
        }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                startPostponedEnterTransition();
            }
        }
    }

    @Override
    public void onItemClick(View view, final ViewModel viewModel) {
        // Save the current image url, as our chosen avatar
        mAvatarUrl = viewModel.getImage();
        // Load the image, and then perform some cool animations on it
        Picasso.with(this).load(viewModel.getImage()).into(
            new TargetAdapter() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    updateSharedAvatarView(bitmap);

                    /*
                     * Scale down from a bigger image size
                     * And flash it at its first shown
                     */
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

        // Once we have chosen at least one avatar, show the fab view to apply the selected choice
        showFabView();
    }

    private void showFabView() {
        // Show only once
        if(mFabView.getVisibility() == View.GONE) {
            ViewCompat.setAlpha(mFabView, 0);
            mFabView.setVisibility(View.VISIBLE);
            mFabView.post(new Runnable() {
                @Override
                public void run() {
                    ViewCompat.setTranslationX(mFabView, mFabView.getMeasuredWidth());
                    ViewCompat.animate(mFabView).translationX(0).alpha(1).start();
                }
            });
        }
    }

    /**
     * This method updates the shared view we use on the material transition
     *
     * @param bitmap Bitmap
     */
    private void updateSharedAvatarView(Bitmap bitmap) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, sharedAvatarSize, sharedAvatarSize, true);
        CircleTransform circleTransform = new CircleTransform();
        mSharedAvatarView.setBackground(new BitmapDrawable(getResources(),
            circleTransform.transform(scaledBitmap)));
    }

    /**
     * This method will not fire on devices prior to LOLLIPOP
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initTransitions() {
        // Perform some visibility strategies on the header and shared views
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

        // Setup the return transition
        getWindow().getReturnTransition()
            .addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionStart(Transition transition) {

                    final long duration = getResources().getInteger(R.integer.duration_fast);

                    if (mAvatarUrl != null) {
                        int dWidth = mAvatarContainer.getDrawableWidth(),
                            dHeight = mAvatarContainer.getDrawableHeight(),
                            vWidth = mAvatarContainer.getInsetWidth(),
                            vHeight = mAvatarContainer.getInsetHeight();

                        float scaleTo = (float) sharedAvatarSize / (float) vHeight;
                        long delay = Float.valueOf((float) duration /
                            (((float) vWidth / (float) vHeight) + ((float) dWidth / (float) dHeight))).longValue();

                        ScalableBitmapAnimator bitmapAnimator =
                            new ScalableBitmapAnimator(scaleTo, ScalableBitmapAnimator.SCALE_TO);
                        bitmapAnimator
                                .setScaleFactor(1f)
                                .setInterpolator(new DecelerateInterpolator())
                                .setDuration(duration)
                                .setStartDelay(delay);
                        mAvatarContainer
                                .setBitmapAnimators(bitmapAnimator)
                                .startBitmapAnimation();
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(mSharedAvatarView != null && !isFinishing()) {
                                mSharedAvatarView.setAlpha(1f);
                            }
                        }
                    }, duration);
                }
            });
    }

    /**
     * When the Fab view is tapped, deliver the image url saved on ProfileActivity
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Intent returnIntent = new Intent();
                returnIntent.putExtra(ProfileActivity.Extra.AVATAR_URL, mAvatarUrl);
                setResult(RESULT_OK, returnIntent);
                supportFinishAfterTransition();
                ViewCompat.animate(v).translationX(v.getMeasuredWidth()).start();
                break;
        }
    }
}
