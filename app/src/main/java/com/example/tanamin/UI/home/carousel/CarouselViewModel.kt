package com.example.tanamin.UI.home.carousel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.tanamin.Data.Dummy
import com.example.tanamin.model.Carousel
import kotlinx.coroutines.launch

class CarouselViewModel(
    application: Application
) : AndroidViewModel(application) {


    private val _carouselList: MutableLiveData<List<Carousel>> by lazy {
        MutableLiveData<List<Carousel>>()
    }

    fun fetchAllCarousel(): LiveData<List<Carousel>> {
        viewModelScope.launch {
            _carouselList.value = Dummy.getComments()
        }
        return _carouselList
    }
}