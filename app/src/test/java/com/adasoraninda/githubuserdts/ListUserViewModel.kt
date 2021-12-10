package com.adasoraninda.githubuserdts

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.adasoraninda.githubuserdts.data.domain.User
import com.adasoraninda.githubuserdts.data.domain.toDomain
import com.adasoraninda.githubuserdts.data.response.SearchResponse
import com.adasoraninda.githubuserdts.data.response.UserResponse
import com.adasoraninda.githubuserdts.network.GitHubUserService
import com.adasoraninda.githubuserdts.viewmodel.ListUserViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import retrofit2.Response
import retrofit2.mock.Calls

val mockData = listOf(
    UserResponse(
        id = 1,
        username = "ada1",
    ),
    UserResponse(
        id = 2,
        username = "ada2",
    ),
    UserResponse(
        id = 3,
        username = "ada3",
    ),
)

class ListUserViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val service = mock(GitHubUserService::class.java)
    private lateinit var viewModel: ListUserViewModel

    @Before
    fun setup() {
        `when`(service.findUsers("")).thenReturn(Calls.response(SearchResponse()))
        viewModel = ListUserViewModel(service)
    }

    @Test
    fun testRandomChar() {
        // arrange
        val expectedSize = 1

        // act
        val randomChar = viewModel.randomChar()

        // assert
        Assert.assertNotNull(randomChar)
        Assert.assertEquals(expectedSize, randomChar.length)
    }

    @Test
    fun testFindUsersSuccess() {
        //arrange
        val query = ""
        val expectedListData = mockData.map(UserResponse::toDomain)

        val refreshData = MutableLiveData(true)
        val actualListData = viewModel.users.testObserver()
        val shimmerData = viewModel.shimmer.testObserver()

        val response = Response.success(SearchResponse(users = mockData))

        `when`(service.findUsers(query)).thenReturn(Calls.response(response))

        // act
        viewModel.findUsers(query, refreshData)

        // assert
        verify(service).findUsers(query)
        Assert.assertEquals(false, refreshData.value)
        Assert.assertEquals(false, shimmerData.observedValue)
        Assert.assertEquals(expectedListData[0].id, actualListData.observedValue?.get(0)?.id)
        Assert.assertEquals(expectedListData.size, actualListData.observedValue?.size)
    }

    @Test
    fun testFindUsersError() {
        // arrange
        val query = ""
        val expectedListData = emptyList<User>()

        val loadingData = MutableLiveData(true)
        val actualListData = viewModel.users.testObserver()
        val actualTextErrorData = viewModel.error.testObserver()
        val shimmerData = viewModel.shimmer.testObserver()

        val response = Response.success(SearchResponse(users = emptyList()))

        `when`(service.findUsers(query)).thenReturn(Calls.response(response))

        // act
        viewModel.findUsers(query, loadingData)

        // assert
        verify(service).findUsers(query)
        Assert.assertEquals(false, loadingData.value)
        Assert.assertEquals(false, shimmerData.observedValue)
        Assert.assertEquals(true, actualTextErrorData.observedValue)
        Assert.assertEquals(expectedListData.size, actualListData.observedValue?.size)
    }

    @Test
    fun testQuerySubmitSuccess() {
        // arrange
        val query = ""
        val expectedListData = mockData.map(UserResponse::toDomain)

        val actualListData = viewModel.users.testObserver()
        val loadingData = viewModel.loading.testObserver()
        val shimmerData = viewModel.shimmer.testObserver()

        val response = Response.success(SearchResponse(users = mockData))

        `when`(service.findUsers(query)).thenReturn(Calls.response(response))

        // act
        viewModel.onQuerySubmit(query)

        // assert
        verify(service).findUsers(query)
        Assert.assertEquals(false, loadingData.observedValue)
        Assert.assertEquals(false, shimmerData.observedValue)
        Assert.assertEquals(expectedListData.size, actualListData.observedValue?.size)
    }

    @Test
    fun testOnItemClick() {
        // arrange
        val username = "ada"
        val actualUsernameData = viewModel.username.testObserver()

        // act
        viewModel.onItemClick(username)

        // assert
        val event = actualUsernameData.observedValue
        val actualUsername = event?.getContent()

        Assert.assertNotNull(event)
        Assert.assertNotNull(actualUsername)
        Assert.assertEquals(username, actualUsername)
        Assert.assertNull(event?.getContent())
    }

}