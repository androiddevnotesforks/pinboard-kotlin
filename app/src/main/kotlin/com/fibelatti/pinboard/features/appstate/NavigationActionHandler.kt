package com.fibelatti.pinboard.features.appstate

import androidx.annotation.VisibleForTesting
import com.fibelatti.core.extension.orFalse
import com.fibelatti.core.functional.Either
import com.fibelatti.core.functional.catching
import com.fibelatti.pinboard.core.android.ConnectivityInfoProvider
import com.fibelatti.pinboard.core.di.IoScope
import com.fibelatti.pinboard.features.posts.domain.PostsRepository
import com.fibelatti.pinboard.features.posts.domain.PreferredDetailsView
import com.fibelatti.pinboard.features.posts.domain.model.Post
import com.fibelatti.pinboard.features.user.domain.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NavigationActionHandler @Inject constructor(
    private val userRepository: UserRepository,
    private val postsRepository: PostsRepository,
    private val connectivityInfoProvider: ConnectivityInfoProvider,
    @IoScope private val markAsReadRequestScope: CoroutineScope
) : ActionHandler<NavigationAction>() {

    override suspend fun runAction(action: NavigationAction, currentContent: Content): Content {
        return when (action) {
            is NavigateBack -> navigateBack(currentContent)
            is ViewCategory -> viewCategory(action)
            is ViewPost -> viewPost(action, currentContent)
            is ViewSearch -> viewSearch(currentContent)
            is AddPost -> viewAddPost(currentContent)
            is ViewTags -> viewTags(currentContent)
            is ViewNotes -> viewNotes(currentContent)
            is ViewNote -> viewNote(action, currentContent)
            is ViewPopular -> viewPopular(currentContent)
            is ViewPreferences -> viewPreferences(currentContent)
        }
    }

    private fun navigateBack(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<ContentWithHistory>(currentContent) {
            if (currentContent is UserPreferencesContent) {
                currentContent.previousContent.copy(
                    showDescription = userRepository.showDescriptionInLists
                )
            } else {
                it.previousContent
            }
        }
    }

    private fun viewCategory(action: ViewCategory): Content {
        return PostListContent(
            category = action,
            posts = null,
            showDescription = userRepository.showDescriptionInLists,
            sortType = NewestFirst,
            searchParameters = SearchParameters(),
            shouldLoad = ShouldLoadFirstPage,
            isConnected = connectivityInfoProvider.isConnected()
        )
    }

    private fun viewPost(action: ViewPost, currentContent: Content): Content {
        val preferredDetailsView = userRepository.preferredDetailsView

        return when (currentContent) {
            is PostListContent -> {
                when (preferredDetailsView) {
                    is PreferredDetailsView.InAppBrowser -> {
                        val shouldLoad: ShouldLoad = markAsRead(action.post)
                        PostDetailContent(
                            action.post,
                            previousContent = currentContent.copy(shouldLoad = shouldLoad)
                        )
                    }
                    is PreferredDetailsView.ExternalBrowser -> {
                        val shouldLoad: ShouldLoad = markAsRead(action.post)
                        ExternalBrowserContent(
                            action.post,
                            previousContent = currentContent.copy(shouldLoad = shouldLoad)
                        )
                    }
                    PreferredDetailsView.Edit -> {
                        EditPostContent(
                            post = action.post,
                            previousContent = currentContent
                        )
                    }
                }
            }
            is PopularPostsContent -> {
                if (preferredDetailsView is PreferredDetailsView.ExternalBrowser) {
                    ExternalBrowserContent(action.post, previousContent = currentContent)
                } else {
                    PopularPostDetailContent(action.post, previousContent = currentContent)
                }
            }
            else -> currentContent
        }
    }

    @VisibleForTesting
    fun markAsRead(post: Post): ShouldLoad {
        return if (post.readLater && userRepository.markAsReadOnOpen) {
            markAsReadRequestScope.launch {
                catching {
                    postsRepository.add(
                        url = post.url,
                        title = post.title,
                        description = post.description,
                        private = post.private,
                        readLater = false,
                        tags = post.tags,
                        replace = true
                    )
                }
            }
            ShouldLoadFirstPage
        } else {
            Loaded
        }
    }

    private fun viewSearch(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            SearchContent(it.searchParameters, shouldLoadTags = true, previousContent = it)
        }
    }

    private fun viewAddPost(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            AddPostContent(
                defaultPrivate = userRepository.defaultPrivate.orFalse(),
                defaultReadLater = userRepository.defaultReadLater.orFalse(),
                defaultTags = userRepository.defaultTags,
                previousContent = it
            )
        }
    }

    private fun viewTags(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            TagListContent(
                tags = emptyList(),
                shouldLoad = connectivityInfoProvider.isConnected(),
                isConnected = connectivityInfoProvider.isConnected(),
                previousContent = it
            )
        }
    }

    private fun viewNotes(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            NoteListContent(
                notes = emptyList(),
                shouldLoad = connectivityInfoProvider.isConnected(),
                isConnected = connectivityInfoProvider.isConnected(),
                previousContent = it
            )
        }
    }

    private fun viewNote(action: ViewNote, currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<NoteListContent>(currentContent) {
            NoteDetailContent(
                id = action.id,
                note = Either.Left(connectivityInfoProvider.isConnected()),
                isConnected = connectivityInfoProvider.isConnected(),
                previousContent = it
            )
        }
    }

    private fun viewPopular(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            PopularPostsContent(
                posts = emptyList(),
                shouldLoad = connectivityInfoProvider.isConnected(),
                isConnected = connectivityInfoProvider.isConnected(),
                previousContent = it
            )
        }
    }

    private fun viewPreferences(currentContent: Content): Content {
        return runOnlyForCurrentContentOfType<PostListContent>(currentContent) {
            UserPreferencesContent(
                periodicSync = userRepository.periodicSync,
                appearance = userRepository.appearance,
                preferredDateFormat = userRepository.preferredDateFormat,
                preferredDetailsView = userRepository.preferredDetailsView,
                autoFillDescription = userRepository.autoFillDescription,
                showDescriptionInLists = userRepository.showDescriptionInLists,
                defaultPrivate = userRepository.defaultPrivate.orFalse(),
                defaultReadLater = userRepository.defaultReadLater.orFalse(),
                editAfterSharing = userRepository.editAfterSharing,
                defaultTags = userRepository.defaultTags,
                previousContent = it,
            )
        }
    }
}
