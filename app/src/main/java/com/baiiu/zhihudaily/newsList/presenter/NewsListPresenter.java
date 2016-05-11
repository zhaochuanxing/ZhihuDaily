package com.baiiu.zhihudaily.newsList.presenter;

import com.baiiu.zhihudaily.data.newsListData.Daily;
import com.baiiu.zhihudaily.data.newsListData.source.INewsListDataSource;
import com.baiiu.zhihudaily.data.newsListData.source.NewsListRepository;
import com.baiiu.zhihudaily.newsList.NewsListContract;
import com.baiiu.zhihudaily.util.CommonUtil;
import com.baiiu.zhihudaily.util.UIUtil;
import com.baiiu.zhihudaily.util.net.util.HttpNetUtil;

/**
 * auther: baiiu
 * time: 16/5/10 10 22:42
 * description:
 */

public class NewsListPresenter implements NewsListContract.Presenter {
  private NewsListContract.View mNewsListView;
  private final NewsListRepository newsListRepository;

  //很明显,使用构造函数注入依赖
  public NewsListPresenter(NewsListContract.View newsListView) {
    //Presenter绑定View
    this.mNewsListView = newsListView;

    //View绑定Presenter,也可以写在Activity中
    //newsListView.setPresenter(this);

    //持有 NewsList数据操作类,硬编码注入
    newsListRepository = NewsListRepository.instance();
  }

  @Override public void start() {
    mNewsListView.showLoadingPage();
    //第一次,从本地加载
    loadNewsList(false, false);
  }

  @Override public void loadNewsList(final boolean fromRemote, final boolean loadMore) {

    //设置是否从远端拉取数据
    newsListRepository.refreshNewsList(fromRemote);

    newsListRepository.loadNewsList("", loadMore, new INewsListDataSource.LoadNewsListCallback() {
      @Override public void onSuccess(Daily daily) {
        if (daily == null) {
          return;
        }

        if (!loadMore) {
          mNewsListView.showLoadingIndicator(false);
        }

        if (fromRemote) {
          //从远端返回的数据
          if (CommonUtil.isEmpty(daily.stories)) {
            if (mNewsListView.isDataEmpty()) {
              mNewsListView.showEmptyPage();
            } else {
              mNewsListView.showErrorInfo("拉取数据为空");
            }
          } else {
            mNewsListView.showNews(daily, !loadMore);
          }

          mNewsListView.bindFooter(daily.stories, false);
        } else {
          //本地的数据
          if (loadMore) {
            mNewsListView.showNews(daily, false);
            mNewsListView.bindFooter(daily.stories, true);
          } else {

            if (CommonUtil.isEmpty(daily.stories)) {
              //本地都没有缓存
              mNewsListView.showLoadingIndicator(false);
              onRefresh();
            } else {
              //本地有,展示数据
              mNewsListView.showNews(daily, true);
              mNewsListView.bindFooter(daily.stories, true);

              UIUtil.postDelayed(new Runnable() {
                @Override public void run() {
                  mNewsListView.showLoadingIndicator(true);
                  onRefresh();
                }
              }, 1000);
            }
          }
        }
      }

      @Override public void onFailure() {
        mNewsListView.showLoadingIndicator(false);
        mNewsListView.showErrorInfo("网络错误");

        if (mNewsListView.isDataEmpty()) {
          //历史数据都没有
          mNewsListView.showErrorPage();
        } else {
          mNewsListView.bindFooter(null, false);
        }
      }
    });
  }

  @Override public void loadMore() {
    if (HttpNetUtil.isConnected()) {
      loadNewsList(true, true);
    } else {
      loadNewsList(false, true);
    }
  }

  @Override public void onRefresh() {
    if (HttpNetUtil.isConnected()) {
      //刷新数据时从网络拉取
      loadNewsList(true, false);
    } else {
      mNewsListView.showLoadingIndicator(false);
      mNewsListView.showErrorInfo("网络未连接");

      if (mNewsListView.isDataEmpty()) {
        mNewsListView.showErrorPage();
      }
    }
  }
}