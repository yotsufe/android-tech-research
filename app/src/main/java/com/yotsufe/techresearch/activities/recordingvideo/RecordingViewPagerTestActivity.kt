package com.yotsufe.techresearch.activities.recordingvideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityRecordingViewPagerTestBinding

class RecordingViewPagerTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingViewPagerTestBinding
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_view_pager_test)

        binding.btnRecStart.setOnClickListener {
        }

        binding.btnRecStop.setOnClickListener {
        }

//        setViewPager()
    }

}
