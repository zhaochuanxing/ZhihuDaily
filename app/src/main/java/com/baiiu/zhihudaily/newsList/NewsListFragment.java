package com.baiiu.zhihudaily.newsList;

import android.support.v7.widget.DefaultItemAnimator;
import android.view.View;
import com.baiiu.tsnackbar.Prompt;
import com.baiiu.tsnackbar.TSnackbar;
import com.baiiu.zhihudaily.R;
import com.baiiu.zhihudaily.base.list.fragment.BaseListFragment;
import com.baiiu.zhihudaily.base.list.fragment.BaseRefreshLoadMoreAdapter;
import com.baiiu.zhihudaily.data.bean.Story;
import com.baiiu.zhihudaily.data.repository.DaggerNewsComponent;
import com.baiiu.zhihudaily.data.util.CommonUtil;
import com.baiiu.zhihudaily.data.util.PreferenceUtil;
import com.baiiu.zhihudaily.newsList.holder.NewsViewHolder;
import com.baiiu.zhihudaily.util.Constant;
import com.baiiu.zhihudaily.util.DoubleClickListener;
import com.baiiu.zhihudaily.util.UIUtil;
import com.baiiu.zhihudaily.util.router.Navigator;
import com.baiiu.zhihudaily.view.fastscroll.FastScrollLinearLayoutManager;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

/**
 * auther: baiiu
 * time: 17/2/19 19 14:38
 * description:
 */

public class NewsListFragment extends BaseListFragment<Story, NewsListPresenter> implements NewsListContract.IView, View.OnClickListener {

    //@Inject protected NewsListPresenter mPresenter;

    public static NewsListFragment instance() {
        return new NewsListFragment();
    }

    @Override protected void initOnCreate() {
        DaggerNewsComponent.builder()
                .appComponent(UIUtil.getAppComponent())
                .build()
                .inject(this);
    }

    @Override protected void initRecyclerView() {
        if (PreferenceUtil.instance()
                .get(Constant.UI_MODE, true)) {
            mRefreshLayout.setColorSchemeColors(UIUtil.getColor(R.color.colorPrimary_Day));
        } else {
            mRefreshLayout.setColorSchemeColors(UIUtil.getColor(R.color.colorPrimary_Night));
        }

        mRecyclerView.setLayoutManager(new FastScrollLinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration((DailyNewsAdapter) mAdapter);
        mRecyclerView.addItemDecoration(headersDecor);

        getActivity().findViewById(R.id.fab)
                .setOnClickListener(v -> mRecyclerView.smoothScrollToPosition(0));

        View view = ((NewsListActivity) getActivity()).mToolbar;
        if (view != null) {
            view.setOnClickListener(new DoubleClickListener() {
                @Override public void onDoubleClick(View v) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            });
        }
    }

    @Override protected BaseRefreshLoadMoreAdapter<Story> provideAdapter() {
        return new DailyNewsAdapter(mContext, this);
    }

    @Override public void onClick(View v) {
        long id = 0;
        switch (v.getId()) {
            case R.id.item_news:
                NewsViewHolder holder = (NewsViewHolder) v.getTag();
                mPresenter.openNewsDetail(holder);
                break;
            case R.id.item_topic_news:
                id = (long) v.getTag(R.id.item_topic_news);
                Navigator.INSTANCE.navigatorToDetail(mContext, id);
                break;
        }
    }

    @Override public boolean isDataEmpty() {
        return CommonUtil.isEmpty(mAdapter.getList());
    }

    @Override public void showSuccessInfo(String info) {
        TSnackbar.make(mRefreshLayout, info, Prompt.SUCCESS)
                .show();
    }

    @Override public void showErrorInfo(String info) {
        TSnackbar.make(mRefreshLayout, info, Prompt.ERROR)
                .show();
    }

    @Override public void showNewsDetail(Story story) {
        Navigator.INSTANCE.navigatorToDetail(mContext, story);
    }

    @Override public void showNewsReaded(int position, boolean isRead) {
        mAdapter.notifyItemChanged(position, isRead);
    }

}
