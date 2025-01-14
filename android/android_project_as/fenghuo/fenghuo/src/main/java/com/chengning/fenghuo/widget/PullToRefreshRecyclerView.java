/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.chengning.fenghuo.widget; 
import java.lang.reflect.Field;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.AttributeSet;
import android.view.View;

import com.chengning.fenghuo.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class PullToRefreshRecyclerView extends PullToRefreshBase<LoadMoreRecyclerView> {


	public PullToRefreshRecyclerView(Context context) {
		super(context);
		init();
	} 

	public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PullToRefreshRecyclerView(Context context, Mode mode) {
		super(context, mode);
		init();
	}

	public PullToRefreshRecyclerView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
		init();
	}

	private void init() {
	}

	public void setAdapter(Adapter adapter){
		getRefreshableView().setAdapter(adapter);
	}
	
	@Override
	public final Orientation getPullToRefreshScrollDirection() {
		return Orientation.VERTICAL;
	}
	
	@Override  
	protected LoadMoreRecyclerView createRefreshableView(Context context, AttributeSet attrs) {  
		LoadMoreRecyclerView recyclerView = new LoadMoreRecyclerView(context, attrs);  
		
		//设置id（必须，值可随意）避免java.lang.IllegalArgumentException: Wrong state class,This usually happens when two views of different type have the same id in the same hierarchy. 
		recyclerView.setId(R.id.id_load_more_recyclerView);
	    return recyclerView;  
	} 

	
	@Override
	protected boolean isReadyForPullStart() {
//		int[] scrollconsumed = getNestedScrollConsumed();
//		if(scrollconsumed[1] != 0){
//			return false;
//		}
//	    if (getRefreshableView().getChildCount() <= 0)
//	        return true;
//		int firstVisiblePosition = ((RecyclerView) getRefreshableView()).getChildAdapterPosition(getRefreshableView().getChildAt(0));
//	    if (firstVisiblePosition == 0)
//	        return getRefreshableView().getChildAt(0).getTop() == getRefreshableView().getPaddingTop();
//	    else
//	        return false;
		View view = getRefreshableView().getChildAt(0);  
		  
        if (view != null) {  
            return view.getTop() >= getRefreshableView().getTop();  
        }  
        return false; 
	
	}
	
	@Override
	protected boolean isReadyForPullEnd() {
//		int[] scrollconsumed = getNestedScrollConsumed();
//		if(scrollconsumed[1] != 0){
//			return false;
//		}
//		if(mRefreshableView.getChildCount() <= 0){
//			return true;
//		}
//		int lastVisiblePosition = mRefreshableView.getChildAdapterPosition(mRefreshableView.getChildAt(mRefreshableView.getChildCount() -1));
//	    if (lastVisiblePosition >= mRefreshableView.getAdapter().getItemCount()-1) {
//	        return mRefreshableView.getChildAt(mRefreshableView.getChildCount() - 1).getBottom() <= mRefreshableView.getBottom();
//	    }
//	    return false;
		
		View view = getRefreshableView().getChildAt(getRefreshableView().getChildCount() - 1);
        if (null != view) {
            return getRefreshableView().getBottom() >= view.getBottom();
        }
        return false;
		
//	    return isLastItemVisible(); 
	}
	
	private boolean isLastItemVisible() {  
        final RecyclerView.Adapter adapter = getRefreshableView().getAdapter();  
  
        if (null == adapter) {  
            return true;  
        } else {  
            LinearLayoutManager layoutManager = (LinearLayoutManager) getRefreshableView().getLayoutManager();  
            final int lastItemPosition = getRefreshableView().getAdapter().getItemCount() -1;  
            final int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();  
  
  
            /** 
             * This check should really just be: lastVisiblePosition == 
             * lastItemPosition, but PtRListView internally uses a FooterView 
             * which messes the positions up. For me we'll just subtract one to 
             * account for it and rely on the inner condition which checks 
             * getBottom(). 
             */  
            if (lastVisiblePosition >= lastItemPosition - 1) {  
                final int childIndex = lastVisiblePosition - layoutManager.findFirstVisibleItemPosition();  
                final View lastVisibleChild = getRefreshableView().getChildAt(childIndex);  
                if (lastVisibleChild != null) {  
                    return lastVisibleChild.getBottom() <= getRefreshableView().getBottom();  
                }  
            }  
        }  
  
        return false;  
    } 
	    
	private int[] getNestedScrollConsumed(){
		int[] consumed = new int[2];
		try {
			Field field = RecyclerView.class.getDeclaredField("mScrollConsumed");
		if(field != null){
			field.setAccessible(true);
			consumed = (int[]) field.get(getRefreshableView());
			return consumed;
		}
		return consumed;
		
	} catch (NoSuchFieldException e) {
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return consumed;
		
	}
	
}
