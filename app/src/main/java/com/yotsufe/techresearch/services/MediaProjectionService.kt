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
import android.os.Build
import android.os.Environment
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MediaProjectionService : Service() {

    private var data: Intent? = null
    private var code = Activity.RESULT_OK
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var projectionManager: MediaProjectionManager
    private lateinit var projection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var fileName: String
    private var height = 2800
    private var width = 1400
    private var dpi = 1000

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        data = intent?.getParcelableExtra("data")
        code = intent?.getIntExtra("code", Activity.RESULT_OK) ?: Activity.RESULT_OK

        height = intent?.getIntExtra("height", 1000)?: 1000
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

        startRec()

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setOutputFile(getFilePath())
            } else {
                setOutputFile(getFilePath())
            }
            prepare()
        }

        virtualDisplay = projection.createVirtualDisplay(
                "recode",
                width,
                height,
                dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder.surface,
                null,
                null
        )

        mediaRecorder.start()
    }

    private fun stopRec() {
        mediaRecorder.stop()
        mediaRecorder.release()
        virtualDisplay.release()
        projection.stop()
    }

    private fun getFilePath(): String {
        // TODO 適切なストレージを指定する
        val scopedStoragePath = Environment.getExternalStorageDirectory()
        Log.d("###", scopedStoragePath!!.absolutePath)
        Log.d("###", "${scopedStoragePath.path}/${fileName}.mp4")
        return "${scopedStoragePath.path}/${fileName}.mp4"
    }

}
