package com.redcom1988.domain.submission.repository

import androidx.paging.Pager
import com.redcom1988.domain.submission.model.Submission

interface SubmissionRepository {

    fun getSubmissionsPager(): Pager<Int, Submission>

    suspend fun fetchRecentSubmissions(limit: Int = 5): List<Submission>

}

