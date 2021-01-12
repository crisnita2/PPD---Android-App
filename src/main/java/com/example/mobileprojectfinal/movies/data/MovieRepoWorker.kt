package com.example.mobileprojectfinal.movies.data

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MovieRepoWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        when (inputData.getString("operation")) {
            "save" -> MovieRepoHelper.save()
            "update" -> MovieRepoHelper.update()
            "delete" -> MovieRepoHelper.delete()
            else -> return Result.failure()
        }
        return Result.success()
    }
}