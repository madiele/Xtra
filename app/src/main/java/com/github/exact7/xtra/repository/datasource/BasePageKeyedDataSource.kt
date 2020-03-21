package com.github.exact7.xtra.repository.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.github.exact7.xtra.repository.LoadingState
import com.github.exact7.xtra.util.nullIfEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BasePageKeyedDataSource<T>(private val coroutineScope: CoroutineScope) : PageKeyedDataSource<String, T>(), PagingDataSource {

    protected val tag: String = javaClass.simpleName
    private var retry: (() -> Any)? = null

    override val loadingState = MutableLiveData<LoadingState>()
    override val pagingState = MutableLiveData<LoadingState>()

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, T>) {

    }

    protected fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, T>, request: suspend () -> Pair<List<T>, String>) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "Loading data. Size: " + params.requestedLoadSize)
                loadingState.postValue(LoadingState.LOADING)
                val data = request()
                callback.onResult(data.first, 0, data.first.size, null, data.second.nullIfEmpty())
                Log.d(tag, "Successfully loaded data")
                loadingState.postValue(LoadingState.LOADED)
                retry = null
            } catch (e: Exception) {
                Log.e(tag, "Error loading data", e)
                e.printStackTrace()
                retry = { loadInitial(params, callback) }
                loadingState.postValue(LoadingState.FAILED)
            }
        }
    }

    protected fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, T>, request: suspend () -> Pair<List<T>, String>) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                Log.d(tag, "Loading data. Size: " + params.requestedLoadSize)
                pagingState.postValue(LoadingState.LOADING)
                val data = request()
                callback.onResult(data.first, data.second)
                Log.d(tag, "Successfully loaded data")
                pagingState.postValue(LoadingState.LOADED)
                retry = null
            } catch (e: Exception) {
                Log.e(tag, "Error loading data", e)
                e.printStackTrace()
                retry = { loadAfter(params, callback) }
                pagingState.postValue(LoadingState.FAILED)
            }
        }
    }

    override fun retry() {
        retry?.let {
            coroutineScope.launch(Dispatchers.IO) {
                it.invoke()
            }
            retry = null
        }
    }
}