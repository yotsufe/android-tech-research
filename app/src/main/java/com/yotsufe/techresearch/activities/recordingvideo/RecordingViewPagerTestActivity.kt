package com.yotsufe.techresearch.activities.recordingvideo

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.adapters.CountPagerAdapter
import com.yotsufe.techresearch.databinding.ActivityRecordingViewPagerTestBinding
import com.yotsufe.techresearch.models.MovieEditor
import com.yotsufe.techresearch.services.MediaProjectionService
import kotlinx.coroutines.*

class RecordingViewPagerTestActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

    enum class RecordingStatus {
        RECORDING,
        PAUSING,
        STOPPING
    }
    private lateinit var binding: ActivityRecordingViewPagerTestBinding
    private var recordingStatus = RecordingStatus.STOPPING
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
    private lateinit var job: Job
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_view_pager_test)

        binding.btnRecStart.setOnClickListener {
            when (recordingStatus) {
                RecordingStatus.RECORDING -> {
                    pauseRecording()
                }
                RecordingStatus.PAUSING -> {
                    resumeRecording()
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

        binding.leftTapArea.setOnClickListener {
            goToLeftPage()
        }

        binding.rightTapArea.setOnClickListener {
            goToRightPage()
        }

        setButtonsVisibility(recordingStatus)
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

    private fun goToLeftPage() {
        binding.countViewPager.setCurrentItem(currentPage - 1, true)
    }

    private fun goToRightPage() {
        Log.d("###1", "goToRightPage")
        mediaProjectionBinder?.stopRecording()
        mediaProjectionBinder?.startRecording(currentPage + 1)
        appendMovie(currentPage)

        runBlocking {
            Thread.sleep(1000)
        }
        binding.countViewPager.setCurrentItem(currentPage + 1, true)
    }

    private fun setButtonsVisibility(recordingStatus: RecordingStatus) {
        when (recordingStatus) {
            RecordingStatus.PAUSING -> {
                binding.btnRecStart.setImageResource(R.drawable.ic_baseline_videocam_24)
                binding.btnRecStop.visibility = View.VISIBLE
                binding.btnRecRetake.visibility = View.VISIBLE
            }
            RecordingStatus.RECORDING -> {
                binding.btnRecStart.setImageResource(R.drawable.ic_pause24)
                binding.btnRecStop.visibility = View.VISIBLE
                binding.btnRecRetake.visibility = View.GONE
            }
            RecordingStatus.STOPPING -> {
                binding.btnRecStart.setImageResource(R.drawable.ic_baseline_videocam_24)
                binding.btnRecStop.visibility = View.GONE
                binding.btnRecRetake.visibility = View.GONE
            }
        }
    }

    private fun startRecording() {
        recordingStatus = RecordingStatus.RECORDING
        setButtonsVisibility(recordingStatus)
        startShareScreen()
    }

    private fun stopRecording() {
        setButtonsVisibility(recordingStatus)
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

    private fun pauseRecording() {
        Log.d("###", "push pause")
        mediaProjectionBinder?.pauseRecording()
        recordingStatus = RecordingStatus.PAUSING
        setButtonsVisibility(recordingStatus)
    }

    private fun resumeRecording() {
        Log.d("###", "push resume")
        mediaProjectionBinder?.resumeRecording()
        recordingStatus = RecordingStatus.PAUSING
        setButtonsVisibility(recordingStatus)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != 1000) {
            return
        }
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "録画を開始できませんでした。", Toast.LENGTH_SHORT)
                .show()
            recordingStatus = RecordingStatus.STOPPING
            setButtonsVisibility(recordingStatus)
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
        currentPage = position
    }

    override fun onPageScrollStateChanged(state: Int) {
        when (state) {
            ViewPager.SCROLL_STATE_IDLE -> {
                Log.d("###10", "SCROLL_STATE_IDLE")
            }
            ViewPager.SCROLL_STATE_DRAGGING -> {
                Log.d("###", "SCROLL_STATE_DRAGGING")
            }
            ViewPager.SCROLL_STATE_SETTLING -> {
                Log.d("###9", "SCROLL_STATE_SETTLING")
            }
        }
    }

    private fun appendMovie(currentPosition: Int) {
        job = GlobalScope.launch(Dispatchers.IO) {
            if (currentPosition == 1) {
                MovieEditor.append(
                        Environment.getExternalStorageDirectory().path,
                        "rec_pager_test_0.mp4",
                        "rec_pager_test_1.mp4"
                )
            } else if (currentPosition > 1) {
                MovieEditor.append(
                        Environment.getExternalStorageDirectory().path,
                        "rec_pager_test_full.mp4",
                        "rec_pager_test_${currentPage}.mp4"
                )
            }
        }
    }

}
