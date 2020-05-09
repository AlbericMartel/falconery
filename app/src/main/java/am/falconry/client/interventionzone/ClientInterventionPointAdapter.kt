package am.falconry.client.interventionzone

import am.falconry.databinding.ClientInterventionPointBinding
import am.falconry.domain.InterventionPoint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientInterventionPointAdapter : RecyclerView.Adapter<ClientInterventionPointAdapter.ViewHolder>() {

    var data =  listOf<InterventionPoint>()
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

    class ViewHolder private constructor(val binding: ClientInterventionPointBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: InterventionPoint) {
            binding.clientInterventionPoint = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ClientInterventionPointBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}