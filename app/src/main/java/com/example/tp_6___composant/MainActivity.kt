package com.example.tp_6___composant

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var workManager: WorkManager

    private lateinit var tvLogStatus: TextView
    private lateinit var tvSaveStatus: TextView
    private lateinit var tvCalcResult: TextView

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Permission de notification accordée.")
            } else {
                Log.w(TAG, "Permission de notification refusée.")
                Toast.makeText(
                    this,
                    "La notification de sauvegarde nécessite la permission de notification.",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        workManager = WorkManager.getInstance(this)

        val btnRunLogWorker = findViewById<Button>(R.id.btnRunLogWorker)
        val btnSaveData = findViewById<Button>(R.id.btnSaveData)
        val btnCalculate = findViewById<Button>(R.id.btnCalculate)

        tvLogStatus = findViewById(R.id.tvLogStatus)
        tvSaveStatus = findViewById(R.id.tvSaveStatus)
        tvCalcResult = findViewById(R.id.tvCalcResult)

        checkNotificationPermission()

        planAndRunLogWorker()

        btnRunLogWorker.setOnClickListener {
            planAndRunLogWorker()
        }

        btnSaveData.setOnClickListener {
            runSaveDataWorker()
        }

        btnCalculate.setOnClickListener {
            runCalculationWorker()
        }
    }

    private fun planAndRunLogWorker() {
        val logWorkRequest = OneTimeWorkRequestBuilder<LogWorker>().build()

        workManager.enqueue(logWorkRequest)
        Log.d(TAG, "Exercice 1 : LogWorker enqueued avec l'ID ${logWorkRequest.id}")

        workManager.getWorkInfoByIdLiveData(logWorkRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    Log.d(TAG, "LogWorker statut : ${workInfo.state}")
                    if (workInfo.state.isFinished) {
                        tvLogStatus.text = getString(R.string.ex1_status_finished, workInfo.state.name)
                    } else {
                        tvLogStatus.text = getString(R.string.ex1_status_running, workInfo.state.name)
                    }
                }
            }
    }

    private fun runSaveDataWorker() {
        val saveRequest = OneTimeWorkRequestBuilder<SaveDataWorker>().build()
        workManager.enqueue(saveRequest)

        Log.d(TAG, "Exercice 2 : SaveDataWorker démarré (ID: ${saveRequest.id})")
        tvSaveStatus.setText(R.string.ex2_status_starting)

        workManager.getWorkInfoByIdLiveData(saveRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    Log.d(TAG, "SaveDataWorker - Changement de statut : ${workInfo.state}")

                    when (workInfo.state) {
                        WorkInfo.State.ENQUEUED -> tvSaveStatus.setText(R.string.ex2_status_enqueued)
                        WorkInfo.State.RUNNING -> tvSaveStatus.setText(R.string.ex2_status_running)
                        WorkInfo.State.SUCCEEDED -> tvSaveStatus.setText(R.string.ex2_status_succeeded)
                        WorkInfo.State.FAILED -> tvSaveStatus.setText(R.string.ex2_status_failed)
                        WorkInfo.State.CANCELLED -> tvSaveStatus.setText(R.string.ex2_status_cancelled)
                        WorkInfo.State.BLOCKED -> tvSaveStatus.setText(R.string.ex2_status_blocked)
                    }
                }
            }
    }

    private fun runCalculationWorker() {
        val calcRequest = OneTimeWorkRequestBuilder<CalculationWorker>().build()
        workManager.enqueue(calcRequest)

        Log.d(TAG, "Exercice 3 : CalculationWorker enqueued (ID: ${calcRequest.id})")
        tvCalcResult.setText(R.string.ex3_calculating)

        workManager.getWorkInfoByIdLiveData(calcRequest.id)
            .observe(this) { workInfo ->
                if (workInfo != null) {
                    Log.d(TAG, "CalculationWorker - Statut : ${workInfo.state}")

                    if (workInfo.state.isFinished) {
                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            val result = workInfo.outputData.getInt(CalculationWorker.KEY_RESULT_SUM, 0)
                            Log.d(TAG, "Résultat reçu depuis CalculationWorker : $result")

                            Toast.makeText(
                                this,
                                getString(R.string.toast_calculation_result, result),
                                Toast.LENGTH_LONG
                            ).show()

                            tvCalcResult.text = getString(R.string.ex3_result_format, result)
                        } else {
                            tvCalcResult.setText(R.string.ex3_error)
                            Toast.makeText(
                                this,
                                getString(R.string.ex3_error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
