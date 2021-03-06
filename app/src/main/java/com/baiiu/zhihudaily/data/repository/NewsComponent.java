package com.baiiu.zhihudaily.data.repository;

import com.baiiu.zhihudaily.di.component.AppComponent;
import com.baiiu.zhihudaily.di.scope.PerActivity;
import com.baiiu.zhihudaily.newsDetail.NewsDetailFragment;
import com.baiiu.zhihudaily.newsList.NewsListFragment;
import dagger.Component;

/**
 * author: baiiu
 * date: on 16/6/14 16:54
 * description:
 */
@PerActivity
@Component(
        dependencies = AppComponent.class

)
public interface NewsComponent {

    void inject(NewsDetailFragment newsDetailFragment);

    void inject(NewsListFragment newsListActivity);


}
