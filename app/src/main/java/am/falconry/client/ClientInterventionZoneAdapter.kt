package am.falconry.client

import am.falconry.databinding.ClientInterventionZoneBinding
import am.falconry.domain.InterventionZone
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientInterventionZoneAdapter: RecyclerView.Adapter<ClientInterventionZoneAdapter.ViewHolder>() {

    var data =  listOf<InterventionZone>()
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

    class ViewHolder private constructor(val binding: ClientInterventionZoneBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: InterventionZone) {
            binding.clientInterventionZone = item
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