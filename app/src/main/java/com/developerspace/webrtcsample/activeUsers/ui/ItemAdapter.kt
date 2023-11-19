package com.developerspace.webrtcsample.activeUsers.ui

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.developerspace.webrtcsample.ChatMainActivity
import com.developerspace.webrtcsample.R
import com.developerspace.webrtcsample.model.User
import java.util.*

class ItemAdapter(private val itemList: MutableList<User>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]

        loadWithGlide(holder.imageView, item.photoUrl!!, true)
        holder.textView.text = item.userName
        val context = holder.textView.context

//        if (AppPref(context).getLong(item.packageName) != 0L) {
//            val textToDisplay = context.getString(R.string.already_scheduled) +
//                    AppScheduleHelper.showTime(
//                        AppPref(context).getLong(item.packageName)
//                    )
//            holder.statusView.apply {
//                text = textToDisplay
//                setTextColor(context.resources.getColor(R.color.statusAlreadyScheduled))
//                setTypeface(null, Typeface.BOLD)
//            }
//        }

        holder.parentView.setOnClickListener {
            val intent = Intent(context, ChatMainActivity::class.java)
            intent.putExtra("receiverUserID", itemList[position].userID)
            context.startActivity(intent)
        }
    }

    private fun loadWithGlide(view: ImageView, url: String, isCircular: Boolean = true) {
        Glide.with(view.context).load(url).into(view)
        var requestBuilder = Glide.with(view.context).load(url)
        if (isCircular) {
            requestBuilder = requestBuilder.transform(CircleCrop())
        }
        requestBuilder.into(view)
    }

}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var parentView: LinearLayout = itemView.rootView as LinearLayout
    var textView: TextView = itemView.findViewById(R.id.textView) as TextView
    var imageView: ImageView = itemView.findViewById(R.id.imageView) as ImageView
    var statusView: TextView = itemView.findViewById(R.id.status) as TextView
}