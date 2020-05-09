package am.falconry.quote.list


import am.falconry.databinding.QuoteBinding
import am.falconry.domain.Quote
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class QuoteAdapter(
    private val clickListener: QuoteClickListener
) : RecyclerView.Adapter<QuoteAdapter.ViewHolder>() {

    var quotes = listOf<Quote>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = quotes[position]
        holder.bind(item, clickListener)
    }

    override fun getItemCount(): Int = quotes.size

    class ViewHolder private constructor(val binding: QuoteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Quote, clickListener: QuoteClickListener) {
            binding.quote = item
            binding.card.setOnClickListener { clickListener.onClick(item) }
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = QuoteBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class QuoteClickListener(val clickListener: (quoteId: Long) -> Unit) {
    fun onClick(quote: Quote) = clickListener(quote.quoteId)
}
