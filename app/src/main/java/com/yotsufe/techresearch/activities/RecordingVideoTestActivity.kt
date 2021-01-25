package com.yotsufe.techresearch.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
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
import java.io.File
import java.io.FileOutputStream
import java.util.*

class RecordingVideoTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingVideoTestBinding
    private var isRecording: Boolean = false

    private val directoryPath: String = Environment.getExternalStorageDirectory().absolutePath
    private val fileName1 = "/video_recording1.mp4"
    private val fileName2 = "/video_recording2.mp4"
    private var mediaRecorder: MediaRecorder? = null

    private var count:Int = 0

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var screenDensity: Int = 0
    private var mediaProjectionCallback: MediaProjection.Callback? = null
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var permissionToRecordAccepted = false

    companion object {
        private const val DISPLAY_WIDTH = 720
        private const val DISPLAY_HEIGHT = 1280

        private const val REQUEST_RECORD_VIDEO_PERMISSION = 200
        private const val MAX_DURATION_MS = 15 * 1000
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

        // ステータスバー非表示
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // ナビゲーションバー非表示
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE
        }

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        binding.btnRecController1.setOnClickListener {
            onRecord(isRecording, fileName1)
        }

        binding.btnRecStop1.setOnClickListener {
            onRecord(isRecording, "")
        }

        binding.btnRecController2.setOnClickListener {
            onRecord(isRecording, fileName2)
        }

        binding.btnRecStop2.setOnClickListener {
            onRecord(isRecording, "")
        }

        binding.btnStitching.setOnClickListener {
            stitchByMP4Parser()
        }

        binding.btnCountUp.setOnClickListener {
            count += 1
            binding.count.text = "$count"
        }

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        screenDensity = metrics.densityDpi
    }

    private fun onRecord(isRecording: Boolean, fileName: String) {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording(fileName)
        }
    }

    private fun startRecording(fileName: String) {
        binding.btnRecController1.setImageResource(R.drawable.ic_stop_24)
        isRecording = true
        initRecorder(fileName)
        startShareScreen()
    }

    private fun stopRecording() {
        binding.btnRecController1.setImageResource(R.drawable.ic_mic_24)
        isRecording = false
        mediaRecorder?.stop()
        mediaRecorder?.reset()
    }

    private fun stitchByMP4Parser() {
        val movie1 = MovieCreator.build(directoryPath + fileName1)
        val movie2 = MovieCreator.build(directoryPath + fileName2)
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

    private fun initRecorder(fileName: String) {
        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setVideoSource(MediaRecorder.VideoSource.SURFACE)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(directoryPath + fileName)
            setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT)
            setVideoEncoder(MediaRecorder.VideoEncoder.H264)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setVideoEncodingBitRate(512 * 1000)
            setVideoFrameRate(30)
            setMaxDuration(MAX_DURATION_MS)
            setOnInfoListener { _, what, _ ->
                when (what) {
                    MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> {
                        Toast.makeText(applicationContext, "最大録音時間に達しました。", Toast.LENGTH_SHORT)
                                .show()
                        stopRecording()
                    }
                    MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED -> {
                        Toast.makeText(applicationContext, "空き領域がなくなりました。", Toast.LENGTH_SHORT)
                                .show()
                        stopRecording()
                    }
                }
            }
            try {
                prepare()
            } catch (e: Exception) {
            }
        }
    }

    private fun startShareScreen() {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager?.createScreenCaptureIntent(), 1000)
            return
        }

        virtualDisplay = createVirtualDisplay()
        try {
            mediaRecorder?.start()
        } catch (e: Exception) {
        }
    }

    private fun stopShareScreen() {
        if (virtualDisplay == null) {
            return
        }
        virtualDisplay?.release()
        destroyMediaProjection()
    }

    private fun createVirtualDisplay(): VirtualDisplay? {
        return mediaProjection?.createVirtualDisplay("test",
                DISPLAY_WIDTH, DISPLAY_HEIGHT, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mediaRecorder?.surface, null, null)
    }

    private fun destroyMediaProjection() {
        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        mediaProjection?.stop()
        mediaProjection = null
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
        mediaProjectionCallback = object : MediaProjection.Callback() {
            override fun onStop() {
                super.onStop()
                stopRecording()
                mediaProjection = null
                stopShareScreen()
            }
        }

        mediaProjection = mediaProjectionManager?.getMediaProjection(resultCode, data!!)
        mediaProjection?.registerCallback(mediaProjectionCallback, null)
        virtualDisplay = createVirtualDisplay()
        mediaRecorder?.start()
    }

}
