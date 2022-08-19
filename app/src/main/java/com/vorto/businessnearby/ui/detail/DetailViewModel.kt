package com.vorto.businessnearby.ui.detail

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vorto.businessnearby.model.BusinessModel
import com.vorto.businessnearby.util.Constant.Companion.YELP_TOKEN
import com.vorto.businessnearby.webservice.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private var token = "Bearer $YELP_TOKEN"
    private var mutableBusinessDetail = MutableLiveData<BusinessModel>()
    private var mutableIsLoading = MutableLiveData<Boolean>()

    val businessDetail: LiveData<BusinessModel> get() = mutableBusinessDetail
    val isLoading: LiveData<Boolean> get() = mutableIsLoading

    @SuppressLint("CheckResult")
    fun getBusinessDetail(id: String){
        mutableIsLoading.value = true

        ApiClient.apiEndpoint.businessDetail(
            token,
            id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it!= null) {
                    mutableBusinessDetail.value = it
                }
                mutableIsLoading.value = false

            },{
                Log.e("ERR", it.localizedMessage)
                mutableIsLoading.value = false
            })
    }
}