package com.yotsufe.techresearch

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.databinding.ActivityRecordingTestBinding
import com.yotsufe.techresearch.models.Base64Encoder
import kotlinx.android.synthetic.main.activity_recording_test.*
import java.io.File
import java.io.IOException

class RecordingTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingTestBinding
    private var recorder: MediaRecorder? = null
    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
        private const val MAX_DURATION_MS = 5 * 1000
    }
    private var player: MediaPlayer? = null
    private var filePath: String = Environment.getExternalStorageDirectory()
        .absolutePath + "/test_recording.mp3"
    private var isPlaying = false
    private var isRecording = false

    private var permissionToRecordAccepted = false

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_test)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
        binding.btnRecController.setOnClickListener {
            onRecord(isRecording)
            isRecording = !isRecording
        }

        binding.btnAudioController.setOnClickListener {
            recorder?.stop()
            if (File(filePath).exists()) {
                onPlay(isPlaying)
            }
        }

        binding.btnDelete.apply {
            visibility = if (File(filePath).exists()) {
                View.VISIBLE
            } else {
                View.INVISIBLE
            }
            setOnClickListener {
                File(filePath).delete()
                visibility = if (File(filePath).exists()) {
                    View.VISIBLE
                } else {
                    View.INVISIBLE
                }
            }
        }
    }

    private fun onRecord(isRecording: Boolean) {
        if (isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun onPlay(isPlaying: Boolean) {
        if (isPlaying) {
            stopPlaying()
        } else {
            startPlaying()
        }
    }

    private fun startRecording() {
        binding.btnRecController.setImageResource(R.drawable.ic_stop_24)
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setOutputFile(filePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setMaxDuration(MAX_DURATION_MS)
            setOnInfoListener { mediaRecorder, what, extra ->
                when (what) {
                    MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED -> {
                        Toast.makeText(applicationContext, "最大録音時間に達しました。", Toast.LENGTH_SHORT)
                            .show()
                        stopRecording()
                    }
                    MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED -> {
                        Toast.makeText(applicationContext, "空き領域がなくなりました。", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            try {
                prepare()
            } catch (e: IOException) {
                Log.e("###", e.message ?: "")
            }
            start()
        }
    }

    private fun stopRecording() {
        binding.btnRecController.setImageResource(R.drawable.ic_mic_24)
        recorder?.apply {
            stop()
            release()
        }
        recorder = null
        btnDelete.visibility = if (File(filePath).exists()) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }

    }

    private fun startPlaying() {
        binding.btnAudioController.setImageResource(R.drawable.ic_stop_24)
        if (player != null) {
            replay()
            return
        }
        player = MediaPlayer().apply {
            try {
                setDataSource(filePath)
                setOnCompletionListener {
                    binding.btnAudioController.setImageResource(R.drawable.ic_play_arrow_24)
                    binding.currentTime.stop()
                    this@RecordingTestActivity.isPlaying = false
                    player = null
                    Log.d("###", Base64Encoder.encorde(filePath))
                }

                prepare()

                binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
                        if (player != null) {
                            player?.seekTo(binding.seekBar.progress * 1000)
                            binding.currentTime.base =
                                SystemClock.elapsedRealtime() - player!!.currentPosition
                            if (isPlaying) {
                                binding.currentTime.start()
                                player!!.start()
                            }
                        }
                    }
                })

                binding.currentTime.setOnChronometerTickListener {
                    binding.seekBar.progress = (this.currentPosition / this.duration.toFloat() * 100).toInt()
                }
                binding.currentTime.base = SystemClock.elapsedRealtime()
                binding.currentTime.start()
                start()
                this@RecordingTestActivity.isPlaying = true
            } catch (e: IOException) {
                Log.e("###", "prepare() failed")
            }
        }
    }

    private fun replay() {
        Log.d("###", "replay()")
        binding.btnAudioController.setImageResource(R.drawable.ic_stop_24)
        binding.currentTime.base =
            SystemClock.elapsedRealtime() - player!!.currentPosition
        binding.currentTime.start()
        player?.start()
        isPlaying = true
    }

    private fun stopPlaying() {
        Log.d("###", "stopPlaying()")
        binding.btnAudioController.setImageResource(R.drawable.ic_play_arrow_24)
        binding.currentTime.stop()
        player?.pause()
        isPlaying = false
    }

    override fun onPause() {
        super.onPause()

        stopPlaying()
    }
}
