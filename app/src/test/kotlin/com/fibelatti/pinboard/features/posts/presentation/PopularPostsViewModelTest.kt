package com.fibelatti.pinboard.features.posts.presentation

import com.fibelatti.core.functional.Failure
import com.fibelatti.core.functional.Success
import com.fibelatti.pinboard.BaseViewModelTest
import com.fibelatti.pinboard.MockDataProvider.createPost
import com.fibelatti.pinboard.features.appstate.AppStateRepository
import com.fibelatti.pinboard.features.appstate.PostSaved
import com.fibelatti.pinboard.features.appstate.SetPopularPosts
import com.fibelatti.pinboard.features.posts.domain.EditAfterSharing
import com.fibelatti.pinboard.features.posts.domain.model.Post
import com.fibelatti.pinboard.features.posts.domain.usecase.AddPost
import com.fibelatti.pinboard.features.posts.domain.usecase.GetPopularPosts
import com.fibelatti.pinboard.features.tags.domain.model.Tag
import com.fibelatti.pinboard.features.user.domain.UserRepository
import com.fibelatti.pinboard.isEmpty
import com.fibelatti.pinboard.randomBoolean
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class PopularPostsViewModelTest : BaseViewModelTest() {

    private val mockAppStateRepository = mockk<AppStateRepository>(relaxed = true)
    private val mockUserRepository = mockk<UserRepository>(relaxed = true)
    private val mockGetPopularPosts = mockk<GetPopularPosts>()
    private val mockAddPost = mockk<AddPost>()

    private val popularPostsViewModel = PopularPostsViewModel(
        mockAppStateRepository,
        mockUserRepository,
        mockGetPopularPosts,
        mockAddPost
    )

    @Test
    fun `WHEN getPosts fails THEN error should receive a value`() {
        // GIVEN
        val error = Exception()
        coEvery { mockGetPopularPosts.invoke() } returns Failure(error)

        // WHEN
        popularPostsViewModel.getPosts()

        // THEN
        runBlocking {
            assertThat(popularPostsViewModel.error.first()).isEqualTo(error)
        }
        coVerify(exactly = 0) { mockAppStateRepository.runAction(any()) }
    }

    @Test
    fun `WHEN getPosts succeeds THEN AppStateRepository should run SetPopularPosts`() {
        // GIVEN
        val mockPosts = mockk<List<Post>>()
        coEvery { mockGetPopularPosts() } returns Success(mockPosts)

        // WHEN
        popularPostsViewModel.getPosts()

        // THEN
        coVerify { mockAppStateRepository.runAction(SetPopularPosts(mockPosts)) }
        runBlocking {
            assertThat(popularPostsViewModel.error.isEmpty()).isTrue()
        }
    }

    @Test
    fun `WHEN saveLink is called AND getEditAfterSharing is BeforeSaving THEN PostSaved action is run AND AddPost is not called`() {
        // GIVEN
        val post = createPost()
        every { mockUserRepository.editAfterSharing } returns EditAfterSharing.BeforeSaving

        // WHEN
        popularPostsViewModel.saveLink(post)

        // THEN
        coVerify { mockAppStateRepository.runAction(PostSaved(post.copy(tags = emptyList()))) }
        runBlocking {
            assertThat(popularPostsViewModel.loading.isEmpty()).isTrue()
        }
        coVerify(exactly = 0) { mockAddPost.invoke(any()) }
    }

    @Test
    fun `WHEN saveLink is called AND add post fails THEN error should receive a value`() {
        // GIVEN
        val post = createPost()
        val error = Exception()
        coEvery { mockAddPost(any()) } returns Failure(error)
        every { mockUserRepository.editAfterSharing } returns mockk()

        // WHEN
        popularPostsViewModel.saveLink(post)

        // THEN
        runBlocking {
            assertThat(popularPostsViewModel.loading.first()).isEqualTo(false)
            assertThat(popularPostsViewModel.error.first()).isEqualTo(error)
            assertThat(popularPostsViewModel.saved.isEmpty()).isTrue()
        }
        coVerify(exactly = 0) { mockAppStateRepository.runAction(any<PostSaved>()) }
    }

    @Test
    fun `WHEN saveLink is called AND add post is successful THEN saved should receive a value and PostSave should be run`() {
        // GIVEN
        val post = createPost(
            tags = null
        )
        val randomBoolean = randomBoolean()
        val mockTags = mockk<List<Tag>>()
        val params = AddPost.Params(
            url = post.url,
            title = post.title,
            description = post.description,
            tags = mockTags,
            private = randomBoolean,
            readLater = randomBoolean,
        )
        every { mockUserRepository.defaultPrivate } returns randomBoolean
        every { mockUserRepository.defaultReadLater } returns randomBoolean
        every { mockUserRepository.defaultTags } returns mockTags
        coEvery { mockAddPost(params) } returns Success(post)

        // WHEN
        popularPostsViewModel.saveLink(post)

        // THEN
        runBlocking {
            assertThat(popularPostsViewModel.loading.first()).isEqualTo(false)
            assertThat(popularPostsViewModel.saved.first()).isEqualTo(Unit)
            assertThat(popularPostsViewModel.error.isEmpty()).isTrue()
        }
        coVerify { mockAppStateRepository.runAction(PostSaved(post)) }
    }
}
