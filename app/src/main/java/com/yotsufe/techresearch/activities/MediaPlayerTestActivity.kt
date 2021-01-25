package com.yotsufe.techresearch.activities

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityMediaPlayerTestBinding

class MediaPlayerTestActivity : AppCompatActivity(), SurfaceHolder.Callback2 {

    private lateinit var binding: ActivityMediaPlayerTestBinding
    private var isPlaying: Boolean = false

    private val directoryPath: String = Environment.getExternalStorageDirectory().absolutePath
    private val fileName1 = "/video_recording1.mp4"
    private val fileName2 = "/video_recording2.mp4"
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_player_test)

        binding.btnPlayController.setOnClickListener {
            onPlay(isPlaying)
        }

    }

    private fun onPlay(isPlaying: Boolean) {
        if (isPlaying) {
            stopPlaying()
        } else {
            startPlaying()
        }
    }

    private fun startPlaying() {
        val url = directoryPath + fileName1

        surfaceHolder = binding.videoView.holder
        surfaceHolder.addCallback(this)
        var mediaPlayer: MediaPlayer? = MediaPlayer.create(applicationContext, Uri.parse(url)).apply {
            Log.d("###", "video: ${videoWidth}, ${videoHeight}")
            setSurface(surfaceHolder.surface)
            setOnPreparedListener {
                val videoRatio = it.videoWidth / it.videoHeight.toFloat()
                val screenWidth = binding.videoView.width
                val screenHeight = binding.videoView.height
                Log.d("###", "view: ${screenWidth}, ${screenHeight}")
                val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
                val layoutParams = binding.videoView.layoutParams

                if (videoRatio > screenProportion) {
                    layoutParams.width = screenWidth
                    layoutParams.height = (screenWidth / videoRatio).toInt()
                } else {
                    layoutParams.width = (videoRatio * screenHeight).toInt()
                    layoutParams.height = screenHeight
                }
                binding.videoView.layoutParams = layoutParams

            }
            start()
        }

    }

    private fun setFitToFillAspectRatio(mp: MediaPlayer?, videoWidth: Int, videoHeight: Int) {
        if (mp != null) {
            Log.d("###", "${mp.videoWidth}, ${mp.videoHeight}")

            val screenWidth = this.windowManager.defaultDisplay.width
            val screenHeight = this.windowManager.defaultDisplay.height
            val videoParams = binding.videoView.layoutParams
            if (videoWidth > videoHeight) {
                videoParams.width = screenWidth
                videoParams.height = screenWidth * videoHeight / videoWidth
            } else {
                videoParams.width = screenHeight * videoWidth / videoHeight
                videoParams.height = screenHeight
            }
            binding.videoView.layoutParams = videoParams
        }
    }

    private fun stopPlaying() {
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {
    }

}
