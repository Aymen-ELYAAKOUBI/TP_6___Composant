package com.example.tp_6___composant

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class LogWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val TAG = "LogWorker"
    }

    override fun doWork(): Result {
        Log.d(TAG, "Bonjour depuis WorkManager !")
        return Result.success()
    }
}
