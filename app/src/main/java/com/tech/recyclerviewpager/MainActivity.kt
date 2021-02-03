package com.tech.recyclerviewpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycler_view.setAdapter(MyAdapter().apply {
            data.addAll(
                arrayListOf(
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg",
                    "https://i.keaitupian.net/up/05/51/8c/9eb1b9b9650cd9140a51fc51108c5105.jpg"
                )
            )
        })
        recycler_view.setCurrentItem(2, false)

        recycler_view.setOnPageChangeListener(object : RepeatViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                Log.d("xxx", "$position")
            }

        })

    }

    class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
        val data = ArrayList<String>()

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val textView = itemView.findViewById<TextView>(R.id.tv_text)
            val imageView = itemView.findViewById<ImageView>(R.id.iv_image)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_text, parent, false)
            )
        }

        override fun getItemCount(): Int = data.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = position.toString()
            Glide.with(holder.imageView).load(data[position]).into(holder.imageView)
        }
    }
}
