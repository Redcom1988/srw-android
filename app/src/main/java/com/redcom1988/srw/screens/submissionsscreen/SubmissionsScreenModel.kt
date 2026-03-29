package com.redcom1988.srw.screens.submissionsscreen

import androidx.paging.PagingData
import androidx.paging.cachedIn
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.redcom1988.core.util.inject
import com.redcom1988.domain.submission.interactor.GetSubmissions
import com.redcom1988.domain.submission.model.Submission
import kotlinx.coroutines.flow.Flow

class SubmissionsScreenModel(
    private val getSubmissions: GetSubmissions = inject()
) : ScreenModel {

    val submissionsPagingData: Flow<PagingData<Submission>> = getSubmissions()
        .cachedIn(screenModelScope)

}

