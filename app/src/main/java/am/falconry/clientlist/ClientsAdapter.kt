package am.falconry.clientlist


//import am.falconry.clientlist.ClientsFragment.OnListFragmentInteractionListener
import am.falconry.database.client.Client
import am.falconry.databinding.ClientBinding
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ClientsAdapter(
    val clickListener: ClientClickListener
//    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ClientsAdapter.ViewHolder>() {

    var clients = listOf<Client>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

//    private val mOnClickListener: View.OnClickListener

    init {
//        mOnClickListener = View.OnClickListener { v ->
//            val client = v.tag as Client
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
//            mListener?.onListFragmentInteraction(client)
//        }
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

        fun bind(item: Client, clickListener: ClientClickListener) {
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
    fun onClick(client: Client) = clickListener(client.clientId)
}