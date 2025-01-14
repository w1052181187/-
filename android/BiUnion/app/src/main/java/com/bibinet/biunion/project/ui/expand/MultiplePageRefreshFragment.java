package com.bibinet.biunion.project.ui.expand;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.andview.refreshview.XRefreshView;
import com.andview.refreshview.listener.OnBottomLoadMoreTime;
import com.andview.refreshview.listener.OnTopRefreshTime;
import com.bibinet.biunion.R;
import com.bibinet.biunion.project.models.PageModel;
import com.bibinet.biunion.project.net.expand.MultiplePageRefreshEmit;
import com.bibinet.biunion.project.ui.custom.HomeHeaderView;
import com.bibinet.biunion.project.ui.custom.RefreshHeaderView;
import com.bibinet.biunion.project.ui.dialog.WaitDialog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by bibinet on 2017-6-7.
 */

public abstract class MultiplePageRefreshFragment<T> extends TitleFragment implements MultiplePageRefreshEmit<T>,
        XRefreshView.XRefreshViewListener, OnTopRefreshTime, OnBottomLoadMoreTime{

    private View errorV;
    private FrameLayout mainV;

    private View notDataV;

    private LinearLayoutManager linearLayoutManager;
    private XRefreshView xRefreshView;
    private RecyclerView recyclerView;

    private List<T> dataList;

    private PageActivityAdapter adapter;

    private boolean isLoadMore = false;

    private int lastvisibleitem = 1;
    private int pageNumb = 1;
    private WaitDialog waitDialog;

    public void setTop(boolean top) {
        isTop = top;
    }

    private boolean isTop = true;

    //滚动监听
    private RecyclerView.OnScrollListener listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (adapter != null) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastvisibleitem + 1 == adapter.getItemCount()) {
                    if (adapter.getCurrentState() == PageActivityAdapter.LOAD_ACCESS) {
                        pageNumb++;
                        nextPageLoad(pageNumb);
                        isLoadMore = true;
                        adapter.changeMoreStatus(PageActivityAdapter.LOADING_MORE);
                    }
                }
            }
            isTop = !recyclerView.canScrollVertically(-1);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastvisibleitem = linearLayoutManager.findLastVisibleItemPosition();
        }
    };

    @Override
    protected View getLayoutView() {
        View parentView = super.getLayoutView();
        mainV = (FrameLayout) parentView.findViewById(R.id.act_page_main);
        errorV = parentView.findViewById(R.id.act_page_error);

        View view = createView(getPageLayoutId());
        xRefreshView = (XRefreshView) view.findViewById(getXRefreshLayoutId());
        recyclerView = (RecyclerView) view.findViewById(getRecyclerViewId());
        notDataV = view.findViewById(getNotDataViewId());
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnScrollListener(listener);
        xRefreshView.setPullRefreshEnable(true);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(false);
        RefreshHeaderView hv = getHeadView();
        if (hv != null) {
            xRefreshView.setCustomHeaderView(hv);
        }
        xRefreshView.setXRefreshViewListener(this);
        xRefreshView.setOnTopRefreshTime(this);
        xRefreshView.setOnBottomLoadMoreTime(this);
        mainV.addView(view);
        return parentView;
    }

    @Override
    protected void onTitleViewCreated(View view, Bundle savedInstanceState) {
        waitDialog = new WaitDialog(getActivity());

        onPageCreate(view, savedInstanceState);

        dataList = new ArrayList<T>();
        adapter = getPageAdapter(dataList);

        errorV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xRefreshView.startRefresh();
            }
        });
        adapter.changeMoreStatus(PageActivityAdapter.LOADING_INIT);


    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        initRefresh();
    }

    @Override
    protected final int getTitleLayoutId() {
        return R.layout.activity_multiple_page_refresh;
    }

    protected abstract int getXRefreshLayoutId();

    protected void initRefresh() {
//        adapter.changeMoreStatus(PageActivityAdapter.LOADING_MORE);
        pageNumb = 1;
        initLoad(pageNumb);
        waitDialog.open();
        isLoadMore = false;
    }

    protected abstract int getNotDataViewId();

    protected abstract PageActivityAdapter getPageAdapter(List<T> dataList);

    protected abstract void initLoad(int pageNumb);

    protected abstract void onPageCreate(View view, Bundle savedInstanceState);

    protected abstract int getRecyclerViewId();

    protected abstract int getPageLayoutId();

    protected abstract void nextPageLoad(int pageNumb);

    //刷新
    @Override
    public void onRefresh() {
        adapter.changeMoreStatus(PageActivityAdapter.LOADING_INIT);
        pageNumb = 1;
        refreshLoad(pageNumb);
        if (errorV.getVisibility() == View.VISIBLE) {
            errorV.setVisibility(View.GONE);
        }
        isLoadMore = false;
    }

    @Override
    public void onRefresh(boolean isPullDown) {

    }

    @Override
    public void onLoadMore(boolean isSilence) {

    }

    @Override
    public void onRelease(float direction) {

    }

    @Override
    public void onHeaderMove(double headerMovePercent, int offsetY) {

    }

    @Override
    public void loadMoreSuccess(List<T> newDataList, PageModel.PageChildModel pageChildModel) {
        waitDialog.close();
        errorV.setVisibility(View.GONE);
        if (xRefreshView != null && getHeadView() != null) {
            getHeadView().stopRefresh(xRefreshView);
        }

        if (pageChildModel == null) {
            if (isLoadMore) {
                dataList.addAll(newDataList);
                if (newDataList.size() <= 0) {
                    Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_NODATA);
                } else {
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_ACCESS);
                }
                adapter.notifyDataSetChanged();
            } else {
                dataList.clear();
                dataList.addAll(newDataList);
                adapter = getPageAdapter(dataList);
                recyclerView.setAdapter(adapter);
                if (dataList.size() <= 0) {
                    notDataV.setVisibility(View.VISIBLE);
                } else {
                    notDataV.setVisibility(View.GONE);
                }
                int size = dataList.size();
                if (size < 8) {
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_NODATA);
                } else {
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_ACCESS);
                }
            }
        } else {
            if (isLoadMore) {
                //总页面 == 当前访问到的页面 没有更多了
                if (pageChildModel.getTotalPage() >= pageNumb) {
                    Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_NODATA);
                } else {
                    //设置还可以继续请求下一页
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_ACCESS);
                }
                //刷新适配器
                adapter.notifyDataSetChanged();
            } else {
                dataList.clear();
                dataList.addAll(newDataList);
                adapter = getPageAdapter(dataList);
                recyclerView.setAdapter(adapter);
                if (dataList.size() <= 0) {
                    notDataV.setVisibility(View.VISIBLE);
                } else {
                    notDataV.setVisibility(View.GONE);
                }
                if (pageChildModel.getTotalPage() >= pageNumb) {
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_NODATA);
                } else {
                    adapter.changeMoreStatus(PageActivityAdapter.LOAD_ACCESS);
                }
            }
        }
    }

    @Override
    public void loadMoreFail(String s) {
        waitDialog.close();
        if (xRefreshView != null && getHeadView() != null) {
            getHeadView().stopRefresh(xRefreshView);
        }
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        //判断列表数量 为0展示错误图
        if (dataList.size() <= 0) {
            errorV.setVisibility(View.VISIBLE);
            notDataV.setVisibility(View.GONE);
        } else {
            errorV.setVisibility(View.GONE);
        }
    }


    protected abstract void refreshLoad(int pageNumb);

    protected List<T> getDataList() {
        return dataList;
    }

    protected PageActivityAdapter getAdapter() {
        return adapter;
    }

    public boolean isTop() {
        return isTop;
    }

    private HomeHeaderView headerView;

    protected RefreshHeaderView getHeadView() {
        if (headerView == null) {
            headerView = HomeHeaderView.getInstance(getActivity()).getLayoutView();
            headerView.init();
            headerView.setSecondary(false);
        }
        return headerView;
    }

    @Override
    public boolean isBottom() {
        return false;
    }

    public View getErrorV() {
        return errorV;
    }
}
