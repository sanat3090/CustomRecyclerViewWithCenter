package com.example.healthcaremagic.librarysample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewPager.SnapRecyclerViewListener{
    RecyclerViewPager recyclerView;
    ActivityNearByMapRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerViewPager)findViewById(R.id.recyclerView);
        configViews();
    }
    private void configViews() {
        final int mWidth = getWindowManager().getDefaultDisplay().getWidth();
        recyclerView.addSnapListener(this);
        ArrayList<Integer> ITEMS = new ArrayList<>();
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        ITEMS.add(R.drawable.brand);
        adapter = new ActivityNearByMapRecyclerAdapter(this, ITEMS, mWidth) {
            @Override
            public void snapViewToCenter(View view) {
                int viewCenter = (view.getLeft() + view.getRight()) / 2;
                int scrollNeeded = viewCenter - (mWidth / 2);
                recyclerView.setScrollType(RecyclerViewPager.SwipeType.TAP);
                recyclerView.smoothScrollBy(scrollNeeded, 0);
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setScrollAnimation(true);
    }

    @Override
    public void onCenterItemSnapped(int centerHolderPosition, RecyclerView.ViewHolder centerHolder, RecyclerViewPager.SwipeType scrollType) {

    }

    @Override
    public void onLeftItemSnapped(int holderPosition, RecyclerView.ViewHolder leftHolder) {

    }

    @Override
    public void onRightItemSnapped(int holderPosition, RecyclerView.ViewHolder rightHolder) {

    }
}
