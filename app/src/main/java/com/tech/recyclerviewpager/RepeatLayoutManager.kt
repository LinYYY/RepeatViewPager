package com.tech.recyclerviewpager

import android.graphics.PointF
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider


/**
 *  create by Myking
 *  date : 2021/1/8 17:04
 *  description :
 */
class RepeatLayoutManager : RecyclerView.LayoutManager(), ScrollVectorProvider {

    private var pendingPosition: Int = RecyclerView.NO_POSITION

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (itemCount <= 0) {
            return
        }
        if (state.isPreLayout) {
            return
        }
        layoutChunk(recycler, state)
    }

    private fun layoutChunk(recycler: Recycler, state: RecyclerView.State) {

        if (pendingPosition != RecyclerView.NO_POSITION) {
            if (state.itemCount == 0) {
                removeAndRecycleAllViews(recycler)
                return
            }
        }

        var currentPosition = 0

        //当childCount != 0时，证明是已经填充过View的
        if (childCount != 0) {
            currentPosition = getPosition(getChildAt(0)!!)
        }

        if (pendingPosition != RecyclerView.NO_POSITION) {
            currentPosition = pendingPosition
        }

        //将所有Item分离至scrap
        detachAndScrapAttachedViews(recycler)

        var totalSpace = width - paddingRight

        var left = 0
        var top = 0
        var right = 0
        var bottom = 0
        //模仿LinearLayoutManager的写法，当可用距离足够和要填充的itemView的position在合法范围内才填充View
        while (totalSpace > 0 && currentPosition < state.itemCount) {
            val view = recycler.getViewForPosition(currentPosition % itemCount)
            addView(view)
            measureChild(view, 0, 0)

            right = left + getDecoratedMeasuredWidth(view)
            bottom = top + getDecoratedMeasuredHeight(view)
            layoutDecorated(view, left, top, right, bottom)

            currentPosition++
            left += getDecoratedMeasuredWidth(view)

            totalSpace -= getDecoratedMeasuredWidth(view)
        }

    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        pendingPosition = RecyclerView.NO_POSITION
        Log.d("xxx", "childCount :$childCount itemCount:$itemCount")
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: Recycler,
        state: RecyclerView.State?
    ): Int {
        fillHorizontal(recycler, dx > 0)
        offsetChildrenHorizontal(-dx)
        //dx<0就是手指从左滑向右，所以要回收后面的children dx>0就是手指从右滑向左，所以要回收前面的children
        recyclerChildView(dx > 0, recycler)
        return dx
    }

    override fun scrollToPosition(position: Int) {
        if (position < 0 || position >= itemCount) return
        pendingPosition = position
        requestLayout()
    }

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView, state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller = LinearSmoothScroller(recyclerView.context)

        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    /*
     *横向填充
     */
    private fun fillHorizontal(recycler: Recycler, fillEnd: Boolean) {
        if (childCount == 0) return
        if (fillEnd) {
            //填充尾部
            var anchorView = getChildAt(childCount - 1)!!
            val anchorPosition = getPosition(anchorView)
            while (anchorView.right < width - paddingRight) {
                var position = (anchorPosition + 1) % itemCount
                if (position < 0) position += itemCount
                val scrapItem = recycler.getViewForPosition(position)
                addView(scrapItem)
                measureChildWithMargins(scrapItem, 0, 0)
                val left = anchorView.right
                val top = paddingTop
                val right = left + getDecoratedMeasuredWidth(scrapItem)
                val bottom = top + getDecoratedMeasuredHeight(scrapItem)
                layoutDecorated(scrapItem, left, top, right, bottom)
                anchorView = scrapItem
            }
        } else {
            //填充首部
            var anchorView = getChildAt(0)!!
            val anchorPosition = getPosition(anchorView)
            while (anchorView.left > paddingLeft) {
                var position = (anchorPosition - 1) % itemCount
                if (position < 0) position += itemCount
                val scrapItem = recycler.getViewForPosition(position)
                addView(scrapItem, 0)
                measureChildWithMargins(scrapItem, 0, 0)
                val right = anchorView.left
                val top = paddingTop
                val left = right - getDecoratedMeasuredWidth(scrapItem)
                val bottom = top + getDecoratedMeasuredHeight(scrapItem)
                layoutDecorated(
                    scrapItem, left, top,
                    right, bottom
                )
                anchorView = scrapItem
            }
        }
        return
    }


    /**
     * 回收界面不可见的view
     */
    private fun recyclerChildView(fillEnd: Boolean, recycler: Recycler) {
        //要回收View的集合，暂存
        val recycleViews = hashSetOf<View>()

        if (fillEnd) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)!!
                val right = getDecoratedRight(child)
                //itemView的right<0就是要超出屏幕要回收View
                if (right >= 0) break
                recycleViews.add(child)
            }
        } else {
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)!!
                val left = getDecoratedLeft(child)
                //itemView的left>recyclerView.width就是要超出屏幕要回收View
                if (left <= width) break
                recycleViews.add(child)
            }
        }

        //真正把View移除掉
        for (view in recycleViews) {
            removeAndRecycleView(view, recycler)
        }
        recycleViews.clear()
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = getPosition(getChildAt(0)!!)
        val direction = if (targetPosition < firstChildPos) -1 else 1
        return PointF(direction.toFloat(), 0f)
    }
}