package com.redcom1988.domain.submission.interactor

import com.redcom1988.domain.submission.model.Submission
import com.redcom1988.domain.submission.repository.SubmissionRepository
import java.io.File

class UploadSubmission(
    private val submissionRepository: SubmissionRepository
) {

    suspend fun await(imageFiles: List<File>): Result {
        return try {
            val submission = submissionRepository.uploadSubmission(imageFiles)
            Result.Success(submission)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    sealed interface Result {
        data class Success(val submissions: Submission) : Result
        data class Error(val error: Throwable) : Result
    }
}