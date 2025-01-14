/*
 * Copyright 2011 woozzu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.woozzu.android.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class IndexableExpandableListView extends ExpandableListView {
	
	private boolean mIsFastScrollEnabled = false;
	private boolean mIsIndexScrollerAlwaysVisible = false;
	private IndexScroller_ExpandableListView mScroller = null;
	private GestureDetector mGestureDetector = null;

	public IndexableExpandableListView(Context context) {
		super(context);
	}

	public IndexableExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexableExpandableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null)
				mScroller = new IndexScroller_ExpandableListView(getContext(), this);
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}
	
    public void setIndexScrollerAlwaysVisible(final boolean alwaysShow) {
		mIsIndexScrollerAlwaysVisible = alwaysShow;
		if (mScroller != null){
			mScroller.showAlways();
		}
    }
    
    public void setIndexScrollerIsVisible(boolean b){
		if (mScroller != null){
			if(b)
				mScroller.show();
			else{
				mScroller.hide();
			}
		}
    }

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		// Overlay index bar
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;
		
		if (mGestureDetector == null) {
			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

				@Override
				public boolean onFling(MotionEvent e1, MotionEvent e2,
						float velocityX, float velocityY) {
					// If fling happens, index bar shows
					mScroller.show();
					return super.onFling(e1, e2, velocityX, velocityY);
				}
				
			});
		}
		mGestureDetector.onTouchEvent(ev);
		
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_DOWN){
			if (mScroller != null && mScroller.onTouchEvent(ev))
				return true;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}

}
