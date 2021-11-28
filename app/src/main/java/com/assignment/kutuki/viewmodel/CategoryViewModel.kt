package com.assignment.kutuki.viewmodel

import android.util.Log
import androidx.lifecycle.*
import com.assignment.kutuki.network.ApiService
import com.assignment.kutuki.pojo.*
import java.net.UnknownHostException

private const val TAG = "CategoryViewModel"
class CategoryViewModel:ViewModel() {
    private val apiService by lazy { ApiService.getInstance() }
    private var liveStatus= MutableLiveData<LoadingStatus>()
    val status get() = liveStatus

    private var mutableListOfData1=MutableLiveData<GetCategoryList>()

    val listOfCategory:LiveData<List<VideoCategoriesDummy>> = Transformations.switchMap(mutableListOfData1,::getAllListOfCategory)

    private fun getAllListOfCategory(getCategoryList: GetCategoryList): LiveData<List<VideoCategoriesDummy>> {
        val _list=MutableLiveData<List<VideoCategoriesDummy>>()
        val list= mutableListOf<VideoCategoriesDummy>()
        getCategoryList.response.videoCategories.keys.forEach {
            getCategoryList.response.videoCategories[it]?.let { it1 ->
                if (it1.name.length==9){
                    list.add(VideoCategoriesDummy("${it1.name.subSequence(0,8)}0${it1.name.substring(8) }",it1.image))
                }else{
                    list.add(it1)
                }
            }
        }
        list.sortBy {
            it.name
        }
        list.forEach {
            Log.d(TAG, "getAllListOfCategory: "+it.name)
        }
        _list.value=list
        return _list
    }



    suspend fun fetchFromNetwork()=try {
        val result=apiService.getCategoryList()
        Log.d(TAG, "fetchFromNetwork 1: $result")
        if (result.isSuccessful){
            val list=result.body()
            Log.d(TAG, "fetchFromNetwork: $list")
            list?.let {
                mutableListOfData1.value=list!!
            }
            liveStatus.value=LoadingStatus.success()
        }else{
            Log.e(TAG, "fetchFromNetwork: 2")
            liveStatus.value=LoadingStatus.error(ErrorCode.NO_DATA)
        }
    }catch (ex: UnknownHostException){
        Log.e(TAG, "fetchFromNetwork: 3")
        liveStatus.value=LoadingStatus.error(ErrorCode.NETWORK_ERROR)
    }catch (ex: Exception){
        Log.e(TAG, "fetchFromNetwork: 5")
        liveStatus.value=LoadingStatus.error(ErrorCode.UNKNOWN_ERROR)
    }




}


