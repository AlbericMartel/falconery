package am.falconry.clientlist


import am.falconry.database.client.ClientEntity
import am.falconry.databinding.ClientBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientsAdapter(
    private val clickListener: ClientClickListener
) : RecyclerView.Adapter<ClientsAdapter.ViewHolder>() {

    var clients = listOf<ClientEntity>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = clients[position]
        holder.bind(item, clickListener)
    }

    override fun getItemCount(): Int = clients.size

    class ViewHolder private constructor(val binding: ClientBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: ClientEntity, clickListener: ClientClickListener) {
            binding.client = item
            binding.card.setOnClickListener { clickListener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ClientBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class ClientClickListener(val clickListener: (clientId: Long) -> Unit) {
    fun onClick(client: ClientEntity) = clickListener(client.clientId)
}
