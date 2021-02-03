package com.tech.recyclerviewpager

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 *  create by Myking
 *  date : 2021/1/8 17:21
 *  description :
 */
class RepeatViewPager @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attributeSet, defStyle) {

    private val recyclerView: RecyclerView = RecyclerViewImp(context)
    private val pagerSnapHelper = PagerSnapHelper()
    private val layoutManager = RepeatLayoutManager()

    private var onPageChangeListener: OnPageChangeListener? = null

    private var currentItem = 0
    private var scrollState: Int = RecyclerView.SCROLL_STATE_IDLE

    var isUserInputEnabled = true

    init {
        addView(
            recyclerView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        recyclerView.layoutManager = layoutManager
        pagerSnapHelper.attachToRecyclerView(recyclerView)
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        recyclerView.adapter = adapter
        currentItem = 0
    }

    fun setCurrentItem(item: Int, smoothScroll: Boolean = true) {
        val adapter: RecyclerView.Adapter<*> = getAdapter() ?: return
        if (adapter.itemCount <= 0) {
            return
        }
        var item = Math.max(item, 0)
        item = Math.min(item, adapter.itemCount - 1)

        if (item == currentItem && scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            return
        }
        if (item == currentItem && smoothScroll) {
            return
        }

        var previousItem = currentItem
        currentItem = item
        if (!smoothScroll) {
            recyclerView.post { recyclerView.scrollToPosition(item) }
            return
        }

        if (Math.abs(item - previousItem) > 3) {
            recyclerView.scrollToPosition(if (item > previousItem) item - 3 else item + 3)
            recyclerView.post {
                recyclerView.smoothScrollToPosition(item)
            }
        } else {
            recyclerView.post {
                recyclerView.smoothScrollToPosition(item)
            }
        }
    }

    fun getAdapter(): RecyclerView.Adapter<*>? {
        return recyclerView.adapter
    }

    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener
    }

    interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }

    private inner class RecyclerViewImp @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
    ) : RecyclerView(context, attributeSet, defStyle) {

        override fun onScrollStateChanged(state: Int) {
            super.onScrollStateChanged(state)
            this@RepeatViewPager.scrollState = state
            if (state == SCROLL_STATE_IDLE) {
                val snapView: View? = pagerSnapHelper.findSnapView(layoutManager)
                Log.d("xxx", "${snapView}")
                if (snapView == null) {
                    return  // nothing we can do
                }
                val snapPosition: Int = this@RepeatViewPager.layoutManager.getPosition(snapView)
                if (snapPosition != currentItem) {
                    currentItem = snapPosition
                    onPageChangeListener?.onPageSelected(snapPosition)
                }
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouchEvent(e: MotionEvent?): Boolean {
            return isUserInputEnabled && super.onTouchEvent(e)
        }

        override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
            return isUserInputEnabled && super.onInterceptTouchEvent(e)
        }
    }
}