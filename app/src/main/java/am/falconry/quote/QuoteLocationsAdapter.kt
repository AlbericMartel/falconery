package am.falconry.quote

import am.falconry.databinding.QuoteLocationBinding
import am.falconry.domain.QuoteLocation
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuoteLocationsAdapter(
    private val trappingClickListener: QuoteLocationOptionClickListener,
    private val scaringClickListener: QuoteLocationOptionClickListener
) : RecyclerView.Adapter<QuoteLocationsAdapter.ViewHolder>() {

    var data = listOf<QuoteLocation>()
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

    class ViewHolder private constructor(val binding: QuoteLocationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: QuoteLocation, trappingClickListener: QuoteLocationOptionClickListener, scaringClickListener: QuoteLocationOptionClickListener) {
            binding.quoteLocation = item
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
                val binding = QuoteLocationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class QuoteLocationOptionClickListener(val clickListener: (locationId: Long, checked: Boolean) -> Unit) {
    fun onClick(quoteLocation: QuoteLocation, checked: Boolean) = clickListener(quoteLocation.locationId, checked)
}