package com.yotsufe.techresearch.adapters

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.yotsufe.techresearch.R

class CountPagerAdapter(
        private val activity: AppCompatActivity
) : PagerAdapter() {

    override fun getCount(): Int {
        return 20
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val tv = TextView(activity)
        tv.text = "Postion:$position"
        tv.setTextColor(Color.BLACK)
        tv.textSize = 30f
        tv.gravity = Gravity.CENTER
        tv.background = activity.getDrawable(R.drawable.border)
        (container as ViewPager).addView(tv, 0)
        return tv
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

}
