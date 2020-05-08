package am.falconry.quote

import am.falconry.databinding.QuoteInterventionZoneBinding
import am.falconry.domain.QuoteInterventionZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuoteInterventionZonesAdapter : RecyclerView.Adapter<QuoteInterventionZonesAdapter.ViewHolder>() {

    var data = listOf<QuoteInterventionZone>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: QuoteInterventionZoneBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuoteInterventionZone) {
            binding.quoteInterventionZone = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = QuoteInterventionZoneBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}