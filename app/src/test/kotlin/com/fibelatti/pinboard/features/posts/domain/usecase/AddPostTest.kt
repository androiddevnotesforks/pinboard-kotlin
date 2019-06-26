package com.fibelatti.pinboard.features.posts.domain.usecase

import com.fibelatti.core.functional.Failure
import com.fibelatti.core.functional.Success
import com.fibelatti.core.functional.exceptionOrNull
import com.fibelatti.core.functional.getOrNull
import com.fibelatti.core.test.extension.callSuspend
import com.fibelatti.core.test.extension.givenSuspend
import com.fibelatti.core.test.extension.mock
import com.fibelatti.core.test.extension.shouldBe
import com.fibelatti.core.test.extension.shouldBeAnInstanceOf
import com.fibelatti.core.test.extension.verifySuspend
import com.fibelatti.pinboard.MockDataProvider.mockUrlTitle
import com.fibelatti.pinboard.MockDataProvider.mockUrlValid
import com.fibelatti.pinboard.core.network.ApiException
import com.fibelatti.pinboard.core.network.InvalidRequestException
import com.fibelatti.pinboard.features.posts.domain.PostsRepository
import com.fibelatti.pinboard.features.posts.domain.model.Post
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyBoolean
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyString
import org.mockito.Mockito.never

class AddPostTest {

    private val mockPostsRepository = mock<PostsRepository>()
    private val mockValidateUrl = mock<ValidateUrl>()

    private val params = AddPost.Params(mockUrlValid, mockUrlTitle)

    private val addPost = AddPost(
        mockPostsRepository,
        mockValidateUrl
    )

    @Test
    fun `GIVEN ValidateUrl fails WHEN AddPost is called THEN Failure is returned`() {
        // GIVEN
        givenSuspend { mockValidateUrl(mockUrlValid) }
            .willReturn(Failure(InvalidRequestException()))

        // WHEN
        val result = callSuspend { addPost(params) }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull()?.shouldBeAnInstanceOf<InvalidRequestException>()
        verifySuspend(mockPostsRepository, never()) {
            add(
                url = anyString(),
                title = anyString(),
                description = anyString(),
                private = anyBoolean(),
                readLater = anyBoolean(),
                tags = anyList()
            )
        }
    }

    @Test
    fun `GIVEN posts repository add fails WHEN AddPost is called THEN Failure is returned`() {
        // GIVEN
        givenSuspend { mockValidateUrl(mockUrlValid) }
            .willReturn(Success(mockUrlValid))
        givenSuspend {
            mockPostsRepository.add(
                params.url,
                params.title,
                params.description,
                params.private,
                params.readLater,
                params.tags
            )
        }.willReturn(Failure(ApiException()))

        // WHEN
        val result = callSuspend { addPost(AddPost.Params(mockUrlValid, mockUrlTitle)) }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull()?.shouldBeAnInstanceOf<ApiException>()

        verifySuspend(mockPostsRepository, never()) { getPost(anyString()) }
    }

    @Test
    fun `GIVEN posts repository add succeeds but get post fails WHEN AddPost is called THEN Failure is returned`() {
        // GIVEN
        givenSuspend { mockValidateUrl(mockUrlValid) }
            .willReturn(Success(mockUrlValid))
        givenSuspend {
            mockPostsRepository.add(
                params.url,
                params.title,
                params.description,
                params.private,
                params.readLater,
                params.tags
            )
        }.willReturn(Success(Unit))
        givenSuspend { mockPostsRepository.getPost(params.url) }
            .willReturn(Failure(ApiException()))

        // WHEN
        val result = callSuspend { addPost(AddPost.Params(mockUrlValid, mockUrlTitle)) }

        // THEN
        result.shouldBeAnInstanceOf<Failure>()
        result.exceptionOrNull()?.shouldBeAnInstanceOf<ApiException>()
    }

    @Test
    fun `GIVEN posts repository add succeeds WHEN AddPost is called THEN Success is returned`() {
        // GIVEN
        val mockPost = mock<Post>()
        givenSuspend { mockValidateUrl(mockUrlValid) }
            .willReturn(Success(mockUrlValid))
        givenSuspend {
            mockPostsRepository.add(
                params.url,
                params.title,
                params.description,
                params.private,
                params.readLater,
                params.tags
            )
        }.willReturn(Success(Unit))
        givenSuspend { mockPostsRepository.getPost(params.url) }
            .willReturn(Success(mockPost))

        // WHEN
        val result = callSuspend { addPost(AddPost.Params(mockUrlValid, mockUrlTitle)) }

        // THEN
        result.shouldBeAnInstanceOf<Success<Post>>()
        result.getOrNull() shouldBe mockPost
    }
}
