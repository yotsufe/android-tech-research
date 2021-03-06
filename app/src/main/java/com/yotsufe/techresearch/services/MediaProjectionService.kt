package com.yotsufe.techresearch.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {

    private var data: Intent? = null
    private var code = Activity.RESULT_OK
    private var mediaRecorder: MediaRecorder? = null
    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var projection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var fileName: String
    private var height = 2800
    private var width = 1400
    private var dpi = 1000
    private val binder = MediaProjectionBinder()

    inner class MediaProjectionBinder : Binder() {
        fun showToast() {
            Toast.makeText(applicationContext, "testBinder", Toast.LENGTH_LONG)
                    .show()
        }

        fun startRecording(currentPage: Int) {
            startMediaRecorder(currentPage)
        }

        fun stopRecording() {
            stopMediaRecorder()
        }

        fun pauseRecording() {
            pauseMediaRecorder()
        }

        fun resumeRecording() {
            resumeMediaRecorder()
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        data = intent?.getParcelableExtra("data")
        code = intent?.getIntExtra("code", Activity.RESULT_OK) ?: Activity.RESULT_OK

        height = intent?.getIntExtra("height", 1000) ?: 1000
        width = intent?.getIntExtra("width", 1000) ?: 1000
        dpi = intent?.getIntExtra("dpi", 1000) ?: 1000
        fileName = intent?.getStringExtra("fileName") ?: "temp"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelID = "rec_notify"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelID) == null) {
                val channel =
                        NotificationChannel(channelID, "録画サービス起動中通知", NotificationManager.IMPORTANCE_HIGH)
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, channelID)
                .setContentText("録画中です。")
                .setContentTitle("画面録画")
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(1, notification)
        }

        countDownAnimation()

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRec()
    }

    private fun startRec() {
        if (data == null) {
            return
        }
        projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        projection = projectionManager.getMediaProjection(code, data!!)
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncodingBitRate(1080 * 10000)
            setVideoFrameRate(30)
            setVideoSize(width, height)
            setAudioSamplingRate(44100)
            setOutputFile(getFilePath())
            prepare()
        }

        virtualDisplay = projection.createVirtualDisplay(
                "recode",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface,
                null,
                null
        )

        mediaRecorder?.start()
    }

    private fun stopRec() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
        virtualDisplay.release()
        projection.stop()
    }

    private fun startMediaRecorder(currentPage: Int) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncodingBitRate(1080 * 10000)
            setVideoFrameRate(30)
            setVideoSize(width, height)
            setAudioSamplingRate(44100)
            setOutputFile(getFilePath(currentPage))
            prepare()
        }

        virtualDisplay = projection.createVirtualDisplay(
                "recode",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface,
                null,
                null
        )

        mediaRecorder?.start()
    }

    private fun stopMediaRecorder() {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaRecorder = null
    }

    private fun pauseMediaRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.pause()
        }
    }

    private fun resumeMediaRecorder() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mediaRecorder?.resume()
        }
    }

    private fun getFilePath(currentPage: Int = 0): String {
        // TODO 適切なストレージを指定する
        val scopedStoragePath = Environment.getExternalStorageDirectory()
        Log.d("###", "${scopedStoragePath.path}/${fileName}_${currentPage}.mp4")
        return "${scopedStoragePath.path}/${fileName}_${currentPage}.mp4"
    }

    private fun countDownAnimation() {
        object : CountDownTimer(1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("###", "onTick: $millisUntilFinished")
            }

            override fun onFinish() {
                Toast.makeText(applicationContext, "Rec Start", Toast.LENGTH_SHORT)
                        .show()
                startRec()
            }
        }.start()
    }
}
