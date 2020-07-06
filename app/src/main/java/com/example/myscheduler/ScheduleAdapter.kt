package com.example.myscheduler

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmQuery
import io.realm.RealmRecyclerViewAdapter

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>) :
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {
    private var listener1: ((Long?) -> Unit)? = null
    private var listener2: ((Long?) -> Unit)? = null
    fun setOnItemClickListener(listener: (Long?) -> Unit) {
        this.listener1 = listener
    }
    /*
    fun setOnItemLongClickListener(listener: (Long?) -> Unit){
        this.listener2 = listener
    }
    */
    init {
        setHasStableIds(true)
    }
    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell) {
        val day: TextView = cell.findViewById(android.R.id.text1)
        //val time: TextView = cell.findViewById(android.R.id.text2)
        val title: TextView = cell.findViewById(android.R.id.text2)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val infrater = LayoutInflater.from(parent.context)
        val view = infrater.inflate(
            android.R.layout.simple_list_item_2,
            parent, false
        )
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule: Schedule? = getItem(position)
        holder.day.text = DateFormat.format("yyyy/MM/dd ", schedule?.day)
        //holder.time.text = DateFormat.format("HH:mm ", schedule?.time)
        holder.title.text = schedule?.title
        holder.itemView.setOnClickListener {
            listener1?.invoke(schedule?.id)
        }
    }
    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }
}