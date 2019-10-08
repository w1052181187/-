package com.bibinet.biunion.project.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 *
 */

public class RadioLayout extends LinearLayout {
    private OnRadioLayoutOnClickListener onRadioLayoutOnClickListener;
    private int selectPos = -1;
    public RadioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public void setSelectItem(int pos){
        selectPos = pos;
        setSelectState(this);
        return;
    }

    private void setSelectState(ViewGroup v) {
        // TODO Auto-generated method stub
        for(int i=0 ; i<v.getChildCount() ; i++){
            View object = v.getChildAt(i);
            if(selectPos == i){
                object.setSelected(true);
            }else{
                object.setSelected(false);
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // TODO Auto-generated method stub
        //套用原来的布局操作
        super.onLayout(changed, l, t, r, b);
        //循环对里面所有的1级子view进行点击事件操作
        for(int i=0 ; i<getChildCount() ; i++){
            View v = getChildAt(i);
            final int finalI = i;
            v.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = finalI;
                    setSelectItem(pos);
                    if(onRadioLayoutOnClickListener!=null){
                        onRadioLayoutOnClickListener.onClickPos(pos);
                    }
                }
            });
        }
    }


    public void setOnRadioLayoutOnClickListener(OnRadioLayoutOnClickListener onRadioLayoutOnClickListener) {
        this.onRadioLayoutOnClickListener = onRadioLayoutOnClickListener;
    }



    public interface OnRadioLayoutOnClickListener{
        void onClickPos(int pos);
    }
}
