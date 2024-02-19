package com.example.mc_homework

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

abstract class BackgroundWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
}