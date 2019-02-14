package com.lfork.phonelimitadvanced.limitcore

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters

class TestWorker(context : Context, params : WorkerParameters)
    : Worker(context, params) {

    override fun doWork(): Result {

        // Do the work here--in this case, compress the stored images.
        // In this example no parameters are passed; the task is
        // assumed to be "compress the whole library."
        val intent = Intent(applicationContext,LimitService::class.java)
        applicationContext.startService(intent)
        // Indicate success or failure with your return value:
        return Result.success()

        // (Returning Result.retry() tells WorkManager to try this task again
        // later; Result.failure() says not to try again.)

    }

}