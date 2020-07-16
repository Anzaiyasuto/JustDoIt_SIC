package sic.example.justdoit

import android.graphics.Color
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.testnotificationmanagerrepeat.R
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import java.util.*

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>) :
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true) {
    private var listener1: ((Long?) -> Unit)? = null
    private var listener2: ((Long?) -> Unit)? = null

    fun setOnItemClickListener(listener: (Long?) -> Unit) {
        this.listener1 = listener
    }
    fun setOnItemLongClickListener(listener: (Long?) -> Unit){
        this.listener2 = listener
    }

    init {
        setHasStableIds(true)
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var day: TextView = itemView.findViewById(R.id.tvDay)
        var title: TextView = itemView.findViewById(R.id.tvTaskName)
        var time: TextView = itemView.findViewById(R.id.tvTime)
        var progress: TextView = itemView.findViewById(R.id.tvTaskProgress)
        var limit: TextView = itemView.findViewById(R.id.tvLimitDay)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val infrater = LayoutInflater.from(parent.context)
        val view = infrater.inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val schedule: Schedule? = getItem(position)
        holder.day.text = DateFormat.format("yyyy/MM/dd ", schedule?.time)
        holder.time.text = DateFormat.format("HH:mm ", schedule?.time)
        holder.title.text = schedule?.title
        val str = String.format(Locale.US, "%d%%", schedule?.progressDate)
        holder.progress.text = str
        val c = Calendar.getInstance()
        if (schedule != null) {
            c.time = schedule.time
        }
        val timeMillis1 = c.timeInMillis
        val currentTimeMillis = System.currentTimeMillis()
        var diff = timeMillis1 - currentTimeMillis
        diff /= 1000
        diff /= 60
        diff /= 60
        diff /= 24
        if (diff > 0) {
            val str1 = "残り $diff 日"
            holder.limit.text = str1
        } else {
            val str2 = "JUST DO IT !!!"
            holder.limit.text = str2
            holder.limit.setTextColor(Color.RED)
        }
        holder.itemView.setOnClickListener {
            listener1?.invoke(schedule?.id)
        }
        holder.itemView.setOnLongClickListener{
            listener2?.invoke(schedule?.id)
            true
        }

    }
    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }
}