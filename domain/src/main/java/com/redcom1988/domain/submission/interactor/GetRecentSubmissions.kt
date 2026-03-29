package com.redcom1988.domain.submission.interactor

import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.repository.SubmissionRepository

class GetRecentSubmissions(
    private val submissionRepository: SubmissionRepository
) {
    suspend fun await(limit: Int = 5): Result {
        return try {
            val submissions = submissionRepository.fetchRecentSubmissions(limit)
            Result.Success(submissions)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val submissions: List<Submission>) : Result
        data class Error(val error: Throwable) : Result
    }
}

