package com.yotsufe.techresearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.databinding.ActivityRecordingVideoTestBinding

class RecordingVideoTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingVideoTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_video_test)

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
    }

    private fun stopRecording() {
    }

    private fun startPlaying() {
    }

    private fun stopPlaying() {
    }

}
