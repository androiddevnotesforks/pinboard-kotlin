package com.fibelatti.pinboard.features.tags.presentation

import android.view.View
import com.fibelatti.core.android.base.BaseAdapter
import com.fibelatti.pinboard.R
import com.fibelatti.pinboard.databinding.ListItemTagBinding
import com.fibelatti.pinboard.features.tags.domain.model.Tag
import javax.inject.Inject

class TagsAdapter @Inject constructor() : BaseAdapter<Tag>(hasFilter = true) {

    var onItemClicked: ((Tag) -> Unit)? = null

    override fun getLayoutRes(): Int = R.layout.list_item_tag

    override fun View.bindView(item: Tag, viewHolder: ViewHolder) {
        val binding = ListItemTagBinding.bind(this)

        binding.textViewTagName.text = item.name
        binding.textViewPostCount.text = context.resources.getQuantityString(
            R.plurals.posts_quantity,
            item.posts,
            item.posts
        )

        setOnClickListener { onItemClicked?.invoke(item) }
    }

    override fun filterCriteria(query: String, item: Tag): Boolean = item.name.startsWith(query, ignoreCase = true)
}
