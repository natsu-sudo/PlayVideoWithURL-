package com.assignment.kutuki.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.assignment.kutuki.network.ApiService
import com.assignment.kutuki.pojo.ErrorCode
import com.assignment.kutuki.pojo.GetVideoList
import com.assignment.kutuki.pojo.LoadingStatus
import com.assignment.kutuki.pojo.VideoDetail
import java.net.UnknownHostException


private const val TAG = "VideoListViewModel"
class VideoListViewModel:ViewModel() {
    private var mutableListOfData= MutableLiveData<GetVideoList>()
    private var selectedMutableCategory=MutableLiveData<String>()
    val listOfVideo: LiveData<List<VideoDetail>> = Transformations.switchMap(mutableListOfData,::getList)

    fun setCateGoryValue(name:String){
        selectedMutableCategory.value=name
    }


    private fun getList(getVideoList: GetVideoList): LiveData<List<VideoDetail>> {
        val _list=MutableLiveData<List<VideoDetail>>()
        val list= mutableListOf<VideoDetail>()
        getVideoList.response.videos.keys.forEach {key->
            val categoryArray= getVideoList.response.videos[key]?.categories?.split(',')
            if (checkForValue(categoryArray!!,selectedMutableCategory.value!!)){
                list.add(getVideoList.response.videos[key]!!)
            }
        }
        list.forEach {
            Log.d(TAG, "getList: "+it.categories)
        }
        _list.value=list
        return _list
    }

    private fun checkForValue(categoryArray: List<String>, value: String): Boolean {
        categoryArray.forEach {
            if (it == value){
                return true
            }
        }
        return false
    }


    private val apiService by lazy { ApiService.getInstance() }


    suspend fun fetchFromNetwork()=try {
        val result=apiService.getVideosList()
        if (result.isSuccessful){
            val list=result.body()
            list?.let {
                mutableListOfData.value=list!!
            }
            LoadingStatus.success()
        }else{
            LoadingStatus.error(ErrorCode.NO_DATA)
        }
    }catch (ex: UnknownHostException){
        LoadingStatus.error(ErrorCode.NETWORK_ERROR)
    }catch (ex: Exception){
        LoadingStatus.error(ErrorCode.UNKNOWN_ERROR)
    }
}