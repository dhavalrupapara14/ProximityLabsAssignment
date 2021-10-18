package com.proximity.labs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proximity.labs.models.AirQuality
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    val loadingLiveData: MutableLiveData<Boolean> by lazy {
        MutableLiveData()
    }

    val airQualityList: MutableLiveData<List<AirQuality>?> by lazy {
        MutableLiveData()
    }

    private val jsonAdapter: JsonAdapter<List<AirQuality>> by lazy {
        Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(
            Types.newParameterizedType(
                List::class.java,
                AirQuality::class.java
            )
        )
    }

    private val originalMap: HashMap<String?, AirQuality> = hashMapOf()

    fun subscribeToSocketEvents() {
        loadingLiveData.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.startSocket().consumeEach {
                    if (it.exception == null) {
                        if (it.connectionOpen) {
                            loadingLiveData.postValue(false)
                        } else {
                            it.text?.let { json ->
                                jsonAdapter.fromJson(json)?.let { list ->
                                    for (item in list) {
                                        originalMap[item.city] = item
                                    }

                                    airQualityList.postValue(originalMap.values.toList())
                                }
                            }
                        }
                    } else {
                        onSocketError(it.exception!!)
                    }
                }
            } catch (ex: java.lang.Exception) {
                onSocketError(ex)
            }
        }
    }

    private fun onSocketError(ex: Throwable) {
        println("Error occurred : ${ex.message}")
    }

    override fun onCleared() {
        repository.closeSocket()
        super.onCleared()
    }
}