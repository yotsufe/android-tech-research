package com.yotsufe.techresearch.activities.recordingvideo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.adapters.CountPagerAdapter
import com.yotsufe.techresearch.databinding.ActivityRecordingViewPagerTestBinding

class RecordingViewPagerTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecordingViewPagerTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recording_view_pager_test)

        binding.btnRecStart.setOnClickListener {
        }

        binding.btnRecStop.setOnClickListener {
        }

        setViewPager()
    }

    private fun setViewPager() {
        binding.countViewPager.adapter = createAdapter()
        binding.countViewPager.offscreenPageLimit = 1
    }

    private fun createAdapter(): PagerAdapter {
        return CountPagerAdapter(this)
    }

}
