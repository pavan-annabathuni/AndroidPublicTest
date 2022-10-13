package com.waycool.data.utils

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class NetworkCall<T>{
    lateinit var call: Call<T>

    fun makeCall(call:Call<T>): MutableLiveData<NetworkResource<T>> {
        this.call = call
        val callBackKt = CallBackKt<T>()
        callBackKt.result.value = NetworkResource.loading(null)
        this.call.clone().enqueue(callBackKt)
        return callBackKt.result
    }

    class CallBackKt<T>: Callback<T> {
        var result: MutableLiveData<NetworkResource<T>> = MutableLiveData()

        override fun onFailure(call: Call<T>, t: Throwable) {
            result.value = NetworkResource.error(t.toString())
            t.printStackTrace()
        }

        override fun onResponse(call: Call<T>, response: Response<T>) {
            if(response.isSuccessful)
                result.value = NetworkResource.success(response.body())
            else{
                result.value = NetworkResource.error(response.message())
            }
        }
    }

    fun cancel(){
        if(::call.isInitialized){
            call.cancel()
        }
    }
}