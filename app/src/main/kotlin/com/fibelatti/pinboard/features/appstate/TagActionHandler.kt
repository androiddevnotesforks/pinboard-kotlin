package com.fibelatti.pinboard.features.appstate

import com.fibelatti.pinboard.core.android.ConnectivityInfoProvider
import com.fibelatti.pinboard.features.user.domain.UserRepository
import javax.inject.Inject

class TagActionHandler @Inject constructor(
    private val userRepository: UserRepository,
    private val connectivityInfoProvider: ConnectivityInfoProvider
) : ActionHandler<TagAction>() {

    override suspend fun runAction(action: TagAction, currentContent: Content): Content {
        return when (action) {
            is RefreshTags -> refresh(currentContent)
            is SetTags -> setTags(action, currentContent)
            is PostsForTag -> postsForTag(action)
        }
    }

    private fun refresh(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<TagListContent>(currentContent) {
            it.copy(
                shouldLoad = connectivityInfoProvider.isConnected(),
                isConnected = connectivityInfoProvider.isConnected()
            )
        }
    }

    private fun setTags(action: SetTags, currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<TagListContent>(currentContent) {
            it.copy(
                tags = action.tags,
                shouldLoad = false
            )
        }
    }

    private fun postsForTag(action: PostsForTag): Content {
        return PostListContent(
            category = All,
            posts = null,
            showDescription = userRepository.showDescriptionInLists,
            sortType = NewestFirst,
            searchParameters = SearchParameters(tags = listOf(action.tag)),
            shouldLoad = ShouldLoadFirstPage,
            isConnected = connectivityInfoProvider.isConnected()
        )
    }
}
