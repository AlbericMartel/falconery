package am.falconry.quote.interventiondates

import am.falconry.databinding.InterventionDateBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ClientInterventionZonesAdapter(
    private val cardClickListener: InterventionZoneClickListener
): RecyclerView.Adapter<ClientInterventionZonesAdapter.ViewHolder>() {

    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var data =  listOf<LocalDate>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, dateFormatter, cardClickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: InterventionDateBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: LocalDate, dateFormatter: DateTimeFormatter, cardClickListener: InterventionZoneClickListener) {
            binding.date = item.format(dateFormatter)
            binding.card.setOnClickListener { cardClickListener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InterventionDateBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class InterventionZoneClickListener(val clickListener: (date: LocalDate) -> Unit) {
    fun onClick(date: LocalDate) = clickListener(date)
}