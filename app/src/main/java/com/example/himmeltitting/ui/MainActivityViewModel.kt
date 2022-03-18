package com.example.himmeltitting.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.himmeltitting.main_data_source.MainDataSource
import com.example.himmeltitting.main_data_source.Observation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel: ViewModel() {
    private val mainDataSource = MainDataSource()



    private val observationList: MutableLiveData<MutableList<Observation>> by lazy {
        MutableLiveData<MutableList<Observation>>().also {
            loadObservationsInit()
        }
    }

    fun getObservations(): LiveData<MutableList<Observation>?> {
        return observationList
    }

    fun loadObservations(latitude: Double, longitude: Double, radius: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            mainDataSource.getObservation(latitude, longitude, radius).also {
                observationList.postValue(it as MutableList<Observation>?)
            }
        }
    }

    fun loadObservationsInit() {

        viewModelScope.launch(Dispatchers.IO) {
            mainDataSource.getObservation(59.9139, 10.7522454, 30).also {
                observationList.postValue(it as MutableList<Observation>?)
            }
        }
    }
}