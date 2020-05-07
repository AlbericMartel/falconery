package am.falconry.quote

import am.falconry.databinding.QuoteInterventionZoneBinding
import am.falconry.domain.QuoteInterventionZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuoteInterventionZonesAdapter(
    private val trappingClickListener: QuoteInterventionZoneOptionClickListener,
    private val scaringClickListener: QuoteInterventionZoneOptionClickListener
) : RecyclerView.Adapter<QuoteInterventionZonesAdapter.ViewHolder>() {

    var data = listOf<QuoteInterventionZone>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, trappingClickListener, scaringClickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: QuoteInterventionZoneBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuoteInterventionZone, trappingClickListener: QuoteInterventionZoneOptionClickListener, scaringClickListener: QuoteInterventionZoneOptionClickListener) {
            binding.quoteInterventionZone = item
            binding.trapping.setOnCheckedChangeListener { _, isChecked ->
                trappingClickListener.onClick(item, isChecked)
            }
            binding.scaring.setOnCheckedChangeListener { _, isChecked ->
                scaringClickListener.onClick(item, isChecked)
            }
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

class QuoteInterventionZoneOptionClickListener(val clickListener: (interventionZoneId: Long, checked: Boolean) -> Unit) {
    fun onClick(quoteInterventionZone: QuoteInterventionZone, checked: Boolean) = clickListener(quoteInterventionZone.interventionZoneId, checked)
}