/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package am.falconry.client

import am.falconry.database.client.Location
import am.falconry.databinding.ClientLocationBinding
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