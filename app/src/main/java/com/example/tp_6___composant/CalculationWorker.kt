package com.example.tp_6___composant

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class CalculationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val TAG = "CalculationWorker"
        const val KEY_RESULT_SUM = "RESULT_SUM"
        const val KEY_MAX_NUMBER = "MAX_NUMBER"
    }

    override fun doWork(): Result {
        val maxNumber = inputData.getInt(KEY_MAX_NUMBER, 100)
        Log.d(TAG, "Début du calcul de la somme de 1 à $maxNumber...")

        var sum = 0
        for (i in 1..maxNumber) {
            sum += i
        }

        Log.d(TAG, "Calcul terminé. Résultat = $sum")

        val outputData = Data.Builder()
            .putInt(KEY_RESULT_SUM, sum)
            .build()

        return Result.success(outputData)
    }
}
