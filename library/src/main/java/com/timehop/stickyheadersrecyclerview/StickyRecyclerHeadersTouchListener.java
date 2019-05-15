package com.timehop.stickyheadersrecyclerview;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

public class StickyRecyclerHeadersTouchListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = StickyRecyclerHeadersTouchListener.class.getSimpleName();

    private final GestureDetectorCompat mTapDetector;
    private final RecyclerView mRecyclerView;
    private final StickyRecyclerHeadersDecoration mDecor;
    private OnHeaderClickListener mOnHeaderClickListener;

    public interface OnHeaderClickListener {
        void onHeaderClick(View header, int position, long headerId);
    }

    public StickyRecyclerHeadersTouchListener(final RecyclerView recyclerView,
            final StickyRecyclerHeadersDecoration decor) {
        mTapDetector =
                new GestureDetectorCompat(recyclerView.getContext(), new SingleTapDetector());
        mRecyclerView = recyclerView;
        mDecor = decor;
    }

    public StickyRecyclerHeadersAdapter getAdapter() {
        if (mRecyclerView.getAdapter() instanceof StickyRecyclerHeadersAdapter) {
            return (StickyRecyclerHeadersAdapter) mRecyclerView.getAdapter();
        } else {
            throw new IllegalStateException("A RecyclerView with "
                    + StickyRecyclerHeadersTouchListener.class.getSimpleName()
                    + " requires a "
                    + StickyRecyclerHeadersAdapter.class.getSimpleName());
        }
    }

    public void setOnHeaderClickListener(OnHeaderClickListener listener) {
        mOnHeaderClickListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
        Log.d(TAG, "onInterceptTouchEvent " + event.toString());

        if (this.mOnHeaderClickListener != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int position =
                        mDecor.findHeaderPositionUnder((int) event.getX(), (int) event.getY());
                return position != -1;
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent event) {
        this.mTapDetector.onTouchEvent(event);

        Log.d(TAG, "onTouchEvent " + event.toString());
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }

    private class SingleTapDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            int position = mDecor.findHeaderPositionUnder((int) e.getX(), (int) e.getY());
            if (position != -1) {
                View headerView = mDecor.getHeaderView(mRecyclerView, position);
                long headerId = getAdapter().getHeaderId(position);
                mOnHeaderClickListener.onHeaderClick(headerView, position, headerId);
                mRecyclerView.playSoundEffect(SoundEffectConstants.CLICK);
                headerView.onTouchEvent(e);
                return true;
            }
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return true;
        }
    }
}
