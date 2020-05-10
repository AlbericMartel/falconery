package am.falconry.quote.interventions

import am.falconry.databinding.QuoteInterventionBinding
import am.falconry.domain.QuoteIntervention
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuoteInterventionsAdapter : RecyclerView.Adapter<QuoteInterventionsAdapter.ViewHolder>() {

    var data = listOf<QuoteIntervention>()
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

    class ViewHolder private constructor(val binding: QuoteInterventionBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuoteIntervention) {
            binding.intervention = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = QuoteInterventionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}