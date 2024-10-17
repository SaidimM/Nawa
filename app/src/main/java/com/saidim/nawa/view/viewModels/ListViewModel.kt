package com.saidim.nawa.view.viewModels

import androidx.lifecycle.ViewModel
import com.saidim.nawa.ServiceLocator

class ListViewModel: ViewModel() {
    private val repository = ServiceLocator.getRepository()
    fun getMusics() = repository.getMusicList()
}