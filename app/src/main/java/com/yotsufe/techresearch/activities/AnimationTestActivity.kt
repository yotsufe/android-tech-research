package com.yotsufe.techresearch.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.databinding.DataBindingUtil
import com.yotsufe.techresearch.R
import com.yotsufe.techresearch.databinding.ActivityAnimationTestBinding
import kotlin.math.hypot

class AnimationTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimationTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_animation_test
        )

        binding.btnAnimationOne.setOnClickListener {
            startAnimationOne(it)
            startAnimationTwo(binding.triangle)
        }

    }

    private fun startAnimationOne(view: View) {
        val cx = view.width / 2
        val cy = view.height / 2
        val finalRadius = hypot(cx.toDouble(), cy.toDouble()).toFloat()

        // create the animator for this view (the start radius is zero)
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, finalRadius / 2, finalRadius)
        anim.duration = 2500
        anim.start()
    }

    private fun startAnimationTwo(view: View) {
        val rotateAnimation = RotateAnimation(0.0f, 360.0f,
        Animation.RELATIVE_TO_SELF, 0.5f,
        Animation.RELATIVE_TO_SELF, 0.6f)

        // animation時間 msec
        rotateAnimation.duration = 3000
        // 繰り返し回数
        rotateAnimation.repeatCount = 0
        // animationが終わったそのまま表示にする
        rotateAnimation.fillAfter = true
        view.startAnimation(rotateAnimation)
    }
}