package am.falconry.client

import am.falconry.databinding.InterventionZoneBinding
import am.falconry.domain.InterventionZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientInterventionZonesAdapter(
    private val cardClickListener: InterventionZoneClickListener,
    private val newQuoteClickListener: InterventionZoneClickListener
): RecyclerView.Adapter<ClientInterventionZonesAdapter.ViewHolder>() {

    var data =  listOf<InterventionZone>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, cardClickListener, newQuoteClickListener)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: InterventionZoneBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: InterventionZone, cardClickListener: InterventionZoneClickListener, newQuoteClickListener: InterventionZoneClickListener) {
            binding.interventionZone = item
            binding.card.setOnClickListener { cardClickListener.onClick(item) }
            binding.newQuote.setOnClickListener { newQuoteClickListener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InterventionZoneBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class InterventionZoneClickListener(val clickListener: (interventionZoneId: Long) -> Unit) {
    fun onClick(interventionZone: InterventionZone) = clickListener(interventionZone.interventionZoneId)
}