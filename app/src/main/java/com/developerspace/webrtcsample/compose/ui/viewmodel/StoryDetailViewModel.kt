package com.developerspace.webrtcsample.compose.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.developerspace.webrtcsample.compose.data.repository.MonitorSelectedStoryRepository
import com.developerspace.webrtcsample.compose.data.repository.RemoteStoriesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoryDetailViewModel @Inject constructor(
    private val remoteStoriesRepository: RemoteStoriesRepository
) :
    ViewModel() {
    val selectedStoryDetail = MonitorSelectedStoryRepository.storyDetailViewData
    fun deleteMyStory() = remoteStoriesRepository.deleteMyStory()
}