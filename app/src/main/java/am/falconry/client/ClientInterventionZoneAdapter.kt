package am.falconry.client

import am.falconry.databinding.ClientInterventionZoneBinding
import am.falconry.domain.InterventionZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientInterventionZoneAdapter(
    private val trappingClickListener: InterventionZoneOptionClickListener,
    private val scaringClickListener: InterventionZoneOptionClickListener
) : RecyclerView.Adapter<ClientInterventionZoneAdapter.ViewHolder>() {

    var data =  listOf<InterventionZone>()
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

    class ViewHolder private constructor(val binding: ClientInterventionZoneBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: InterventionZone, trappingClickListener: InterventionZoneOptionClickListener, scaringClickListener: InterventionZoneOptionClickListener) {
            binding.clientInterventionZone = item
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
                val binding = ClientInterventionZoneBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class InterventionZoneOptionClickListener(val clickListener: (interventionZoneId: Long, checked: Boolean) -> Unit) {
    fun onClick(interventionZone: InterventionZone, checked: Boolean) = clickListener(interventionZone.interventionZoneId, checked)
}