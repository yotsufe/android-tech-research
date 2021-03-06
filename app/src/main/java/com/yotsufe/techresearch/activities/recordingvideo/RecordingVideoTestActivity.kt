package com.yotsufe.techresearch.activities.recordingvideo

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityRecordingVideoTestBinding
import com.yotsufe.techresearch.models.MovieEditor
import com.yotsufe.techresearch.services.MediaProjectionService
import java.io.File
import kotlin.collections.ArrayList

class RecordingVideoTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingVideoTestBinding
    private var isRecording: Boolean = false

    private val directoryPath: String = Environment.getExternalStorageDirectory().absolutePath
    private val fileName1 = "rec_pager_test_0.mp4"
    private val fileName2 = "rec_pager_test_1.mp4"

    private var count: Int = 0

    private var mediaProjection: MediaProjection? = null
    private var screenDensity: Int = 0
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var permissionToRecordAccepted = false
    private var mediaProjectionBinder: MediaProjectionService.MediaProjectionBinder? = null
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mediaProjectionBinder = binder as MediaProjectionService.MediaProjectionBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaProjectionBinder = null
        }
    }


    companion object {
        private const val REQUEST_RECORD_VIDEO_PERMISSION = 200
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_VIDEO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_video_test)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        binding.btnRecController1.setOnClickListener {
            onRecord(isRecording)
        }

        binding.btnRecStop1.setOnClickListener {
            onRecord(isRecording)
        }

        binding.btnRecController2.setOnClickListener {
            onRecord(isRecording)
        }

        binding.btnRecStop2.setOnClickListener {
            onRecord(isRecording)
        }

        binding.btnCountUp.setOnClickListener {
            count += 1
            binding.count.text = "$count"
        }

        binding.btnServiceFun.setOnClickListener {
            mediaProjectionBinder?.showToast()
        }

        binding.btnStitching.setOnClickListener {
            stitchByMP4Parser()
        }

        binding.btnGetFiles.setOnClickListener {
            getMovieFiles()
        }

        binding.btnGoToViewPager.setOnClickListener {
            startActivity(Intent(this, RecordingViewPagerTestActivity::class.java))
        }

        binding.spinnerFiles1.adapter = createSpinner()
        binding.spinnerFiles2.adapter = createSpinner()

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        screenDensity = metrics.densityDpi
    }

    private fun onRecord(isRecording: Boolean) {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        binding.btnRecController1.setImageResource(R.drawable.ic_stop_24)
        isRecording = true
        startShareScreen()
    }

    private fun stopRecording() {
        binding.btnRecController1.setImageResource(R.drawable.ic_baseline_videocam_24)
        isRecording = false
        unbindService(connection)
        val intent = Intent(this, MediaProjectionService::class.java)
        stopService(intent)
    }

    private fun startShareScreen() {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager?.createScreenCaptureIntent(), 1000)
            return
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 1000) {
            return
        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Screen Cast Permission Denied", Toast.LENGTH_SHORT)
                    .show()
            return
        }

        val metrics = resources.displayMetrics
        val intent = Intent(this, MediaProjectionService::class.java).apply {
            putExtra("code", resultCode)
            putExtra("data", data)
            putExtra("height", metrics.heightPixels)
            putExtra("width", metrics.widthPixels)
            putExtra("dpi", metrics.densityDpi)
            putExtra("fileName", fileName1)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun stitchByMP4Parser() {
        MovieEditor.append(directoryPath, fileName1, fileName2)
    }

    private fun getMovieFiles(): ArrayList<File> {
        val files = File(Environment.getExternalStorageDirectory().path).listFiles()
        val movieFiles = ArrayList<File>()
        if (files != null) {
            for (file in files) {
                if (file.name.endsWith(".mp4"))
                    movieFiles.add(file)
            }
        }
        return movieFiles
    }

    private fun createSpinner(): ArrayAdapter<File> {
        return ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, getMovieFiles())
    }

}
