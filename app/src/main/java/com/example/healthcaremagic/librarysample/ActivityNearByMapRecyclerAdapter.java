package com.example.healthcaremagic.librarysample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Filippo-TheAppExpert on 9/15/2015.
 */
public abstract class ActivityNearByMapRecyclerAdapter extends RecyclerView.Adapter<ActivityNearByMapRecyclerAdapter.ItemHolder> {

    public static List<Integer> mListData = Collections.emptyList();
    private LayoutInflater inflater;
    private Activity activity;
    private int TYPE_ITEM = 1;
    private int mWidth;

    public ActivityNearByMapRecyclerAdapter(Activity activity, List<Integer> planets, int width) {
        mListData = planets;
        mWidth = width / 2;
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View row = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_near_by_map_marker_view, viewGroup, false);
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(mWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        if (viewType == 0) {
            params.leftMargin = mWidth;
        } else if (viewType == 1) {
            params.rightMargin = mWidth;
        }
        row.setLayoutParams(params);
        return new ItemHolder(row);
    }

    public Integer getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, final int position) {
        try {
            if (holder instanceof ItemHolder && holder.getPosition() < mListData.size()) {
                final ItemHolder itemHolder = (ItemHolder) holder;
                int resID = activity.getResources().getIdentifier("google_plus", "drawable", activity.getPackageName());
                itemHolder.itemView.setBackgroundResource(resID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else if (position == (getItemCount() - 1)) {
            return 1;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ItemHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
        }
    }

    public abstract void snapViewToCenter(View view);

}
