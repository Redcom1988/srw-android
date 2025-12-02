package com.redcom1988.domain.submission.interactor

import androidx.paging.PagingData
import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.repository.SubmissionRepository
import kotlinx.coroutines.flow.Flow

class GetSubmissions(
    private val submissionRepository: SubmissionRepository
) {

    operator fun invoke(): Flow<PagingData<Submission>> {
        return submissionRepository.getSubmissionsPager().flow
    }

}