package com.room.util

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerItemClickListener(
    context: Context,
    private val onItemClickListener: OnItemClickListener? = null,
    private val onRecyclerViewItemClickListener: OnRecyclerViewItemClickListener? = null
) : RecyclerView.OnItemTouchListener {

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnRecyclerViewItemClickListener {
        fun onItemClick(parentView: View, childView: View, position: Int)
    }

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean = true
        })

    constructor(context: Context, listener: OnItemClickListener) : this(context, listener, null)

    constructor(context: Context, listener: OnRecyclerViewItemClickListener) : this(
        context,
        null,
        listener
    )

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && gestureDetector.onTouchEvent(e)) {
            when {
                onItemClickListener != null -> {
                    onItemClickListener.onItemClick(
                        childView,
                        rv.getChildAdapterPosition(childView)
                    )
                }

                onRecyclerViewItemClickListener != null -> {
                    onRecyclerViewItemClickListener.onItemClick(
                        rv,
                        childView,
                        rv.getChildAdapterPosition(childView)
                    )
                }
            }
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}