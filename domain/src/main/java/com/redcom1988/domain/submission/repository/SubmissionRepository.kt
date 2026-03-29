package com.redcom1988.domain.submission.repository

import androidx.paging.Pager
import com.redcom1988.domain.submission.model.Submission
import java.io.File

interface SubmissionRepository {
    fun getSubmissionsPager(): Pager<Int, Submission>
    suspend fun fetchRecentSubmissions(limit: Int): List<Submission>
    suspend fun uploadSubmission(imageFiles: List<File>): Submission
}

