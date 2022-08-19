package com.vorto.businessnearby.ui.search

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


class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private var token = "Bearer $YELP_TOKEN"
    private var mutableBusinessList = MutableLiveData<List<BusinessModel>>()
    private var mutableIsLoading = MutableLiveData<Boolean>()
    private var mutableSearchTerm = MutableLiveData<String>()
    private var mutableLatitude = MutableLiveData<String>()
    private var mutableLongitude = MutableLiveData<String>()

    val businessList: LiveData<List<BusinessModel>> get() = mutableBusinessList
    val isLoading: LiveData<Boolean> get() = mutableIsLoading
    val searchTerm: LiveData<String> get() = mutableSearchTerm

    init {
        mutableBusinessList.value = arrayListOf()
        mutableSearchTerm.value = ""
        mutableLatitude.value = ""
        mutableLongitude.value = ""
    }

    fun setLatitude(newValue: String){
        mutableLatitude.value = newValue
    }

    fun setLongitude(newValue: String){
        mutableLongitude.value = newValue
    }

    @SuppressLint("CheckResult")
    fun searchBusiness(newValue: String?, newRadius: String){
        if(newValue != null) {
            mutableSearchTerm.value = newValue!!
        }

        mutableIsLoading.value = true
        mutableBusinessList.value = arrayListOf()

        ApiClient.apiEndpoint.searchBusiness(
            token,
            mutableSearchTerm.value!!,
            mutableLatitude.value!!,
            mutableLongitude.value!!,
            "40",
            newRadius)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                if(it.businesses != null) {
                    mutableBusinessList.value = ArrayList(it.businesses)
                }
                mutableIsLoading.value = false

            },{
                Log.e("ERR", it.localizedMessage)
                mutableIsLoading.value = false
            })
    }

    fun resetResult(){
        mutableBusinessList.value = arrayListOf()
        mutableIsLoading.value = false
    }
}