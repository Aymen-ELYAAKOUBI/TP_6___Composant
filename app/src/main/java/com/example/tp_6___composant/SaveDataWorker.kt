package com.example.tp_6___composant

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class SaveDataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val TAG = "SaveDataWorker"
        const val CHANNEL_ID = "save_data_channel"
        const val NOTIFICATION_ID = 1001
    }

    override fun doWork(): Result {
        Log.d(TAG, "Début de la sauvegarde des données...")

        return try {
            Thread.sleep(3000)

            val successMessage = applicationContext.getString(R.string.save_success_notification)
            showNotification(successMessage)

            Log.d(TAG, "Sauvegarde terminée avec succès.")
            Result.success()
        } catch (e: InterruptedException) {
            Log.e(TAG, "Erreur lors de la sauvegarde : ${e.message}", e)
            Result.failure()
        }
    }

    private fun showNotification(message: String) {
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Sauvegarde de données",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Notifications liées à la sauvegarde des données"
        }
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("WorkManager")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
