package com.example.healthcaremagic.librarysample;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.ArrayList;


/**
 * Created by Filippo on 9/15/2015.
 */

public class RecyclerViewPager extends RecyclerView {

    private static final String TAG = RecyclerViewPager.class.getSimpleName();
    private static final int INITIAL_POSITION = -1;
    private int mRecyclerViewWidth;
    private int mPosition = INITIAL_POSITION;

    private ArrayList<SnapRecyclerViewListener> mListeners = new ArrayList<>();
    private SnapHorizontalLinearLayoutManager mSnapHorizontalLinearLayoutManager;
    private int mOldPosition;
    private boolean mScrollToLeft;
    private int mCenterHolderPosition;
    private SwipeType mScrollType;
    Context context;
    boolean matchRightScreen;

    public boolean isScrollAnimation() {
        return scrollAnimation;
    }

    public void setScrollAnimation(boolean scrollAnimation) {
        this.scrollAnimation = scrollAnimation;
    }

    boolean scrollAnimation;

    public boolean isMatchRightScreen() {
        return matchRightScreen;
    }

    public void setMatchRightScreen(boolean matchRightScreen) {
        this.matchRightScreen = matchRightScreen;
    }

    public RecyclerViewPager(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public RecyclerViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
    }

    /**
     * This method initializes the properties for the recyclerView
     */
    private void init() {
        try {
            mRecyclerViewWidth = getWindowSize(getContext()).x;
            if (mRecyclerViewWidth == 0) {
                mRecyclerViewWidth = 100;
            }
            mSnapHorizontalLinearLayoutManager = new SnapHorizontalLinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            setHasFixedSize(true);
            setLayoutManager(mSnapHorizontalLinearLayoutManager);
            setRecycledViewPool(new RecyclerView.RecycledViewPool());
            setOnScrollListener(new SnapRecyclerViewScrollListener());
            //addOnScrollListener(new SnapRecyclerViewScrollListener());
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }
    public static Point getWindowSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        return screenSize;
    }
    public void setAdapterChanged() {
        mScrollToLeft = true;
    }

    /**
     * This method places to center a Holder from the recyclerView elements
     * with smoothScroll Animation
     *
     * @param position
     */
    public void setCenterViewAtPosition(int position) {
        try {
            if (BuildConfig.DEBUG) Log.d(TAG, "setCenterViewAtPosition");
            if (mOldPosition > position) {
                mScrollToLeft = true;
            }
            mPosition = position;
            mSnapHorizontalLinearLayoutManager.scrollToPosition(position);
            mSnapHorizontalLinearLayoutManager.smoothScrollToPosition(this, null, position);
            mOldPosition = position;
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * This method is overriden to trigger the animation after a change to the layout
     * Further documentation can be found android developer site documentation
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            int preFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getPosition() : -1;
            int preLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1)).getPosition() : -1;
            super.onLayout(changed, left, top, right, bottom);
            int postFirstChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(0)).getPosition() : -1;
            int postLastChildPosition = getChildCount() != 0 ? getChildViewHolder(getChildAt(getChildCount() - 1)).getPosition() : -1;

            if (preFirstChildPosition != postFirstChildPosition || preLastChildPosition != postLastChildPosition) {
                centerFirstVisibleHolder();
            }
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }


    private void centerFirstVisibleHolder() {
        try {
            int scrollNeeded = mRecyclerViewWidth / 2 - mRecyclerViewWidth;
            if (mScrollToLeft) {
                mScrollToLeft = false;
                smoothScrollBy(-scrollNeeded, 0);
            } else {
                smoothScrollBy(scrollNeeded, 0);
            }
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * Register a listener callback to get notified about the snapped view
     *
     * @param listener
     */
    public void addSnapListener(SnapRecyclerViewListener listener) {
        mListeners.add(listener);
    }

    public void setScrollType(SwipeType scrollType) {
        mScrollType = scrollType;
    }


    /**
     * Custom OnScrollListener to handle the swiping process with animation
     */
    private class SnapRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

        private boolean mAutoSet = true;
        private int mCenterPivot;

        public SnapRecyclerViewScrollListener() {
            mCenterPivot = mRecyclerViewWidth / 2;
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            try {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (mCenterPivot == 0) {
                    mCenterPivot = recyclerView.getLeft() + recyclerView.getRight();
                }
                if (!mAutoSet) {

                    synchronized (this) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                            View view = linearLayoutManager.findViewByPosition(mPosition);

                            mPosition = INITIAL_POSITION;

                            if (view == null) {
                                view = findCenterView(linearLayoutManager);
                            }
                            int viewCenter = 0;
                            int viewWidth = 0;
                            viewCenter = (view.getLeft() + view.getRight()) / 2;
                            viewWidth = view.getRight() - view.getLeft();
                            if(isMatchRightScreen() && linearLayoutManager.findFirstVisibleItemPosition()==0 && linearLayoutManager.findLastVisibleItemPosition()==0){
                                int dummy = ((view.getLeft()/2)+(view.getLeft()/4));
                                viewCenter = (view.getRight()-dummy);
                                viewWidth = view.getRight() - view.getLeft();
                            }
                            int scrollNeeded = viewCenter - mCenterPivot;

                            if (BuildConfig.DEBUG) Log.d(TAG, "viewCentre = " + viewCenter +
                                    " viewWidth = " + viewWidth + " scrollNeeded = " + scrollNeeded +
                                    " recyclerViewWidth = " + recyclerView.getMeasuredWidth());

                            recyclerView.smoothScrollBy(scrollNeeded, 0);

                            mCenterHolderPosition = recyclerView.getChildPosition(view);

                            if (BuildConfig.DEBUG)
                                Log.d(TAG, "centerHolderPosition = " + mCenterHolderPosition + " mPosition = " + mPosition);

                            if (mScrollType == null) {
                                mScrollType = SwipeType.SWIPE;
                            }

                            onCenterItemSnap(mCenterHolderPosition, recyclerView.getChildViewHolder(view), mScrollType);

                            mScrollType = null;
                            mAutoSet = true;
                        }
                    }
                }
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    mAutoSet = false;
                }
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
        }

        /**
         * This method is used to notify snapped center holder to subscribed listeners
         *
         * @param childLayoutPosition
         * @param centerHolder
         * @param scrollType
         */
        private void onCenterItemSnap(int childLayoutPosition, ViewHolder centerHolder, SwipeType scrollType) {
            try {
                for (SnapRecyclerViewListener listener : mListeners) {
                    listener.onCenterItemSnapped(childLayoutPosition, centerHolder, scrollType);
                }
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            try {
                int childCount = recyclerView.getChildCount();
                int width = recyclerView.getChildAt(0).getWidth();
                int padding = (recyclerView.getWidth() - width) / 2;
                if(isScrollAnimation()){
                    for (int i = 0; i < childCount; i++) {
                        View view = recyclerView.getChildAt(i);

                        float ratio = 0;

                        if (view.getLeft() <= padding) {
                            if (view.getLeft() >= padding - view.getWidth()) {
                                ratio = (padding - view.getLeft()) * 1f / view.getWidth();
                            } else {
                                ratio = 1;
                            }
                            view.setScaleY(1 - ratio * 0.5f);
                            view.setScaleX(1 - ratio * 0.5f);

                        } else {

                            if (view.getLeft() <= recyclerView.getWidth() - padding) {
                                ratio = (recyclerView.getWidth() - padding - view.getLeft()) * 1f / view.getWidth();
                            }
                            view.setScaleY(0.5f + ratio * 0.5f);
                            view.setScaleX(0.5f + ratio * 0.5f);
                        }
                    }
                }
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
        }

        /**
         * This method takes layout manager and finds the right center element of it
         *
         * @param layoutManager
         * @return View
         */
        private View findCenterView(LinearLayoutManager layoutManager) {
            View pre = null;
            int minDistance = 0;
            View view, returnView = null;
            try {
                boolean notFound = true;
                for (int i = layoutManager.findFirstVisibleItemPosition(); i <= layoutManager.findLastVisibleItemPosition() && notFound; i++) {

                    view = layoutManager.findViewByPosition(i);

                    int center = (view.getLeft() + view.getRight()) / 2;

                    int leastDifference = Math.abs(mCenterPivot - center);

                    if (leastDifference <= minDistance || i == layoutManager.findFirstVisibleItemPosition()) {
                        minDistance = leastDifference;
                        returnView = view;
                    } else {
                        notFound = false;
                    }
                }
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
            if(returnView!=null){
                pre = returnView;
            }
            else{
                returnView = pre;
            }
            return returnView;
        }
    }

    @Override
    public void smoothScrollBy(int dx, int dy) {
        super.smoothScrollBy(dx, dy);
        try {
            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        ViewHolder rightHolder = findViewHolderForPosition(mCenterHolderPosition - 1);

                        if (rightHolder != null) {
                            if (Build.VERSION.SDK_INT < 16) {
                                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            onRightItemSnap(mCenterHolderPosition - 1, rightHolder);
                        }
                    }
                    catch (Exception e){
                        e.fillInStackTrace();
                    }
                }
            });

            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    try {
                        ViewHolder leftHolder = findViewHolderForPosition(mCenterHolderPosition + 1);

                        if (leftHolder != null) {
                            if (Build.VERSION.SDK_INT < 16) {
                                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                            onLeftItemSnap(mCenterHolderPosition + 1, leftHolder);
                        }
                    }
                    catch (Exception e){
                        e.fillInStackTrace();
                    }
                }
            });
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * This method is used to notify snapped left holder to subscribed listeners
     *
     * @param childLayoutPosition
     * @param leftHolder
     */
    private void onLeftItemSnap(int childLayoutPosition, ViewHolder leftHolder) {
        try {
            for (SnapRecyclerViewListener listener : mListeners) {
                listener.onLeftItemSnapped(childLayoutPosition, leftHolder);
            }
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * This method is used to notify snapped right holder to subscribed listeners
     *
     * @param childLayoutPosition
     * @param rightHolder
     */
    private void onRightItemSnap(int childLayoutPosition, ViewHolder rightHolder) {
        try {
            for (SnapRecyclerViewListener listener : mListeners) {
                listener.onRightItemSnapped(childLayoutPosition, rightHolder);
            }
        }
        catch (Exception e){
            e.fillInStackTrace();
        }
    }

    /**
     * This is a helper class sets the height and width of the layout manager based on the child MeasureSpec.
     * This class helps a recylerview to function WRAP_CONTENT properly.
     */
    private class SnapHorizontalLinearLayoutManager extends LinearLayoutManager {

        public SnapHorizontalLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        @Override
        public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
            try {
                View view = null;

                if (state.getItemCount() > 0) {
                    view = recycler.getViewForPosition(0);
                }

                int height = 0;
                int width = 0;

                if (view != null) {
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
                    int childWidthSpec = ViewGroup.getChildMeasureSpec(widthSpec,
                            getPaddingLeft() + getPaddingRight(), params.width);
                    int childHeightSpec = ViewGroup.getChildMeasureSpec(heightSpec,
                            getPaddingTop() + getPaddingBottom(), params.height);
                    view.measure(childWidthSpec, childHeightSpec);
                    width = view.getMeasuredWidth() + params.leftMargin + params.rightMargin;
                    height = view.getMeasuredHeight() + params.bottomMargin + params.topMargin;
                    recycler.recycleView(view);
                }

                setMeasuredDimension(width, height);
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
        }
    }

    /**
     * Callback listener for snapping elements in the recyclerview
     */
    public interface SnapRecyclerViewListener {

        void onCenterItemSnapped(int centerHolderPosition, ViewHolder centerHolder, SwipeType scrollType);

        void onLeftItemSnapped(int holderPosition, ViewHolder leftHolder);

        void onRightItemSnapped(int holderPosition, ViewHolder rightHolder);
    }

    public enum SwipeType {
        SWIPE, TAP;
    }
}