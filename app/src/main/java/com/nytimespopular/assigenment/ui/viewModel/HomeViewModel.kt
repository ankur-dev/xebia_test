package com.nytimespopular.assigenment.ui.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.nytimespopular.assigenment.R
import com.nytimespopular.assigenment.base.BaseViewModel
import com.nytimespopular.assigenment.data.model.ResultsItem
import com.nytimespopular.assigenment.data.network.EventTask
import com.nytimespopular.assigenment.data.network.NetworkHelper
import com.nytimespopular.assigenment.data.network.Status
import com.nytimespopular.assigenment.data.network.Task
import com.nytimespopular.assigenment.data.repository.ArticleRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel (
    private var repository: ArticleRepository,
    private var networkHelper: NetworkHelper,
    var context: Application): BaseViewModel()

{
    var articleListResponse = MutableLiveData<EventTask<Any>>()
    var selectedArticleItem:ResultsItem?=null

    fun getArticleList(dayValue: Int) {
        val apkiKey = context.getString(R.string.nyt_api_key)
        addToDisposable(repository.getArticleList(dayValue,apkiKey)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe {
                articleListResponse.postValue(EventTask.loading(Task.GET_ARTICLE_LIST))
            }
            .subscribe({
                if (it.status.equals("OK")) {
                    val data = it.results as ArrayList<ResultsItem>
                    articleListResponse.postValue(EventTask.success(data, Task.GET_ARTICLE_LIST))

                } else {
                 articleListResponse.postValue(
                        EventTask.error(
                            context.resources.getString(R.string.error_server),
                            Status.ERROR,
                            Task.GET_ARTICLE_LIST
                        )
                    )
                }
            }) {
                if (networkHelper.isNetworkConnected()) {
                    articleListResponse.postValue(
                        EventTask.error(
                            context.resources.getString(R.string.error_server),
                            Status.ERROR,
                            Task.GET_ARTICLE_LIST
                        )
                    )

                } else {
                    articleListResponse.postValue(
                        EventTask.error(
                            context.resources.getString(R.string.no_internet),
                            Status.ERROR,
                            Task.GET_ARTICLE_LIST
                        )
                    )

                }
            })
    }

}