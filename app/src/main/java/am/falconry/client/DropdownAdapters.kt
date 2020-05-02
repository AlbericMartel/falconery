package am.falconry.client

import am.falconry.domain.Client
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ClientAdapter(context: Context, var resource: Int, objects: List<Client>) : ArrayAdapter<Client>(context, resource, objects) {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(resource, parent, false)
        val text = view as TextView?

        text?.text = getItem(position)?.name

        return view
    }


}