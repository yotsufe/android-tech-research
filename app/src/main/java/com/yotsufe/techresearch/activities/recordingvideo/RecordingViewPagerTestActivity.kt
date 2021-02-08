package com.yotsufe.techresearch.activities.recordingvideo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.adapters.CountPagerAdapter
import com.yotsufe.techresearch.databinding.ActivityRecordingViewPagerTestBinding
import com.yotsufe.techresearch.services.MediaProjectionService

class RecordingViewPagerTestActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    enum class RecordingStatus {
        RECORDING,
        PAUSING,
        STOPPING
    }
    private lateinit var binding: ActivityRecordingViewPagerTestBinding
    private var recordingStatus = RecordingStatus.STOPPING
    private var isRecording = false
    private var mediaProjection: MediaProjection? = null
    private var mediaProjectionBinder: MediaProjectionService.MediaProjectionBinder? = null
    private val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            mediaProjectionBinder = binder as MediaProjectionService.MediaProjectionBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaProjectionBinder = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_view_pager_test)

        binding.btnRecStart.setOnClickListener {
            when (recordingStatus) {
                RecordingStatus.RECORDING -> {
                    // TODO pause機能を追加する
//                    pauseRecording()
                }
                RecordingStatus.PAUSING -> {
                    // TODO resume機能を追加する
//                    resumeRecording()
                }
                RecordingStatus.STOPPING -> {
                    startRecording()
                }
            }
        }

        binding.btnRecStop.setOnClickListener {
            when (recordingStatus) {
                RecordingStatus.STOPPING -> { }
                else -> {
                    stopRecording()
                }
            }
        }

        setViewPager()
    }

    private fun setViewPager() {
        binding.countViewPager.adapter = createAdapter()
        binding.countViewPager.offscreenPageLimit = 1
        binding.countViewPager.addOnPageChangeListener(this)
    }

    private fun createAdapter(): PagerAdapter {
        return CountPagerAdapter(this)
    }

    private fun startRecording() {
        binding.btnRecStart.setImageResource(R.drawable.ic_pause24)
        isRecording = true
        recordingStatus = RecordingStatus.RECORDING
        startShareScreen()
    }

    private fun stopRecording() {
        binding.btnRecStart.setImageResource(R.drawable.ic_baseline_videocam_24)
        isRecording = false
        unbindService(connection)
        val intent = Intent(this, MediaProjectionService::class.java)
        stopService(intent)
    }

    private fun startShareScreen() {
        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1000)
            return
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 1000) {
            return
        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "録画を開始できませんでした。", Toast.LENGTH_SHORT)
                .show()
            binding.btnRecStart.setImageResource(R.drawable.ic_baseline_videocam_24)
            RecordingStatus.STOPPING
            return
        }

        val metrics = resources.displayMetrics
        val intent = Intent(this, MediaProjectionService::class.java).apply {
            putExtra("code", resultCode)
            putExtra("data", data)
            putExtra("height", metrics.heightPixels)
            putExtra("width", metrics.widthPixels)
            putExtra("dpi", metrics.densityDpi)
            putExtra("fileName", "rec_pager_test")
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        mediaProjectionBinder?.stopRecording()
        mediaProjectionBinder?.startRecording(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE -> { }
            ViewPager.SCROLL_STATE_DRAGGING -> { }
            ViewPager.SCROLL_STATE_SETTLING -> { }
        }
    }

}
