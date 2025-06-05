import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.epic.databinding.ItemHostBlockBinding
import com.example.epic.db.entity.HostListItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HostBlockListAdapter(private var onItemClicked: ((info: HostListItem) -> Unit)) :
    PagingDataAdapter<HostListItem, HostBlockListAdapter.HostViewHolder>(HostDiffCallback()) {

    inner class HostViewHolder(private val binding: ItemHostBlockBinding) : RecyclerView.ViewHolder(binding.itemBlockRoot) {

        fun bind(item: HostListItem?) {
            item?.let {
                binding.itemBlockRoot.setOnClickListener {
                    onItemClicked(item)
                }
                binding.tvHostName.text = it.id.toString()
                binding.tvDescription.text = it.host
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HostViewHolder {
        val binding = ItemHostBlockBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class HostDiffCallback : DiffUtil.ItemCallback<HostListItem>() {
        override fun areItemsTheSame(oldItem: HostListItem, newItem: HostListItem): Boolean {
            return oldItem.host == newItem.host
        }

        override fun areContentsTheSame(oldItem: HostListItem, newItem: HostListItem): Boolean {
            return oldItem == newItem
        }
    }

    fun submitData(scope: CoroutineScope, data: PagingData<HostListItem>) {
        scope.launch {
            submitData(data)
        }
    }
}