package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.compose.data.repository.MonitorSelectedStoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor() :
    ViewModel() {
    val selectedStoryDetail = MonitorSelectedStoryRepository.storyDetailViewData
}