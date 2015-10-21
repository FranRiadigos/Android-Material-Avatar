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
package com.example.kuassivi.material_avatar.core.adapter;

import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kuassivi.material_avatar.core.model.ViewModel;
import com.example.kuassivi.material_avatar.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final int DEFAULT_DELAY = 30;
    private static final int MIN_TRANSLATION_Y = 500;

    private List<ViewModel> items;
    private OnItemClickListener onItemClickListener;
    private long mLastPosition = -1;
    private long animationDelay = 0;

    public RecyclerViewAdapter(List<ViewModel> items) {
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ViewModel item = items.get(position);
        holder.image.setImageBitmap(null);
        Picasso.with(holder.image.getContext()).load(item.getImage()).into(holder.image);
        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Give some time to the ripple to finish the effect
                if (onItemClickListener != null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onItemClickListener.onItemClick(v, (ViewModel) v.getTag());
                        }
                    }, 200);
                }
            }
        });
        if (position > mLastPosition) {
            if (position % 2 == 0)
                animationDelay = position * DEFAULT_DELAY;

            ViewCompat.setTranslationY(holder.itemView, MIN_TRANSLATION_Y);
            ViewCompat.setAlpha(holder.itemView, 0);
            ViewCompat.animate(holder.itemView)
                .translationY(0)
                .alpha(1)
                .setStartDelay(animationDelay);

            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ViewModel viewModel);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
