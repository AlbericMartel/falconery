package am.falconry.client

import am.falconry.databinding.ClientLocationBinding
import am.falconry.domain.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientLocationAdapter(
    private val trappingClickListener: LocationOptionClickListener,
    private val scaringClickListener: LocationOptionClickListener
) : RecyclerView.Adapter<ClientLocationAdapter.ViewHolder>() {

    var data =  listOf<Location>()
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

    class ViewHolder private constructor(val binding: ClientLocationBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Location, trappingClickListener: LocationOptionClickListener, scaringClickListener: LocationOptionClickListener) {
            binding.clientLocation = item
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
                val binding = ClientLocationBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class LocationOptionClickListener(val clickListener: (locationId: Long, checked: Boolean) -> Unit) {
    fun onClick(location: Location, checked: Boolean) = clickListener(location.locationId, checked)
}