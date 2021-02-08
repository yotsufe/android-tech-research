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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.googlecode.mp4parser.authoring.Movie
import com.googlecode.mp4parser.authoring.Track
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator
import com.googlecode.mp4parser.authoring.tracks.AppendTrack
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityRecordingVideoTestBinding
import com.yotsufe.techresearch.services.MediaProjectionService
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RecordingVideoTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingVideoTestBinding
    private var isRecording: Boolean = false

    private val directoryPath: String = Environment.getExternalStorageDirectory().absolutePath
    private val fileName1 = "video_recording1"
    private val fileName2 = "video_recording2"

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

//        // ステータスバー非表示
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        // ナビゲーションバー非表示
//        window.decorView.apply {
//            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
//        }

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

        binding.btnGoToViewPager.setOnClickListener {
            startActivity(Intent(this, RecordingViewPagerTestActivity::class.java))
        }


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

    private fun stitchByMP4Parser() {
        val movie1 = MovieCreator.build("${directoryPath}/${fileName1}.mp4")
        val movie2 = MovieCreator.build("${directoryPath}/${fileName2}.mp4")
        val inMovies = arrayOf<Movie>(movie1, movie2)

        val videoTracks = LinkedList<Track>()
        val audioTracks = LinkedList<Track>()
        for (m in inMovies) {
            for (t in m.tracks) {
                if (t.handler == "soun") {
                    audioTracks.add(t)
                }
                if (t.handler == "vide") {
                    videoTracks.add(t)
                }
            }
        }

        val result = Movie()
        if (audioTracks.size > 0) {
            result.addTrack(AppendTrack(audioTracks[0], audioTracks[1]))
        }
        if (videoTracks.size > 0) {
            result.addTrack(AppendTrack(videoTracks[0], videoTracks[1]))
        }

        val out = DefaultMp4Builder().build(result)
        val outputFilePath = "$directoryPath/after_editing.mp4"

        val fos = FileOutputStream(File(outputFilePath))
        out.writeContainer(fos.channel)
        fos.close()
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

}
