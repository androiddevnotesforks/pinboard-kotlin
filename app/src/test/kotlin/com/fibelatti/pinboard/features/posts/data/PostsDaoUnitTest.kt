package com.fibelatti.pinboard.features.posts.data

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

internal class PostsDaoUnitTest {

    @Test
    fun `GIVEN term is blank WHEN preFormatTerm is called THEN empty string is returned`() {
        assertThat(PostsDao.preFormatTerm(" ")).isEmpty()
    }

    @Test
    fun `WHEN preFormatTerm is called THEN term formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTerm("term")).isEqualTo("term*")
    }

    @Test
    fun `GIVEN tem has trailing spaces WHEN preFormatTerm is called THEN term formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTerm("term ")).isEqualTo("term*")
    }

    @Test
    fun `WHEN preFormatTerm is called with more than one word THEN term formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTerm("two terms")).isEqualTo("two* NEAR terms*")
    }

    @Test
    fun `GIVEN term contained special characters WHEN preFormatTerm is called THEN term formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTerm("term (13)")).isEqualTo("term* NEAR 13*")
    }

    @Test
    fun `GIVEN term contained a double quote WHEN preFormatTerm is called THEN term formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTerm("term\"")).isEqualTo("term*")
    }

    @Test
    fun `WHEN preFormatTag is called THEN tag formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTag("tag")).isEqualTo("\"tag*\"")
    }

    @Test
    fun `GIVEN tag contained a double quote WHEN preFormatTag is called THEN tag formatted for the query is returned`() {
        assertThat(PostsDao.preFormatTag("tag\"")).isEqualTo("\"tag*\"")
    }
}
