package com.wire.android.feature.auth.registration.personal.usecase

import com.wire.android.UnitTest
import com.wire.android.any
import com.wire.android.core.exception.Conflict
import com.wire.android.core.exception.DatabaseFailure
import com.wire.android.core.exception.Failure
import com.wire.android.core.exception.Forbidden
import com.wire.android.core.exception.NotFound
import com.wire.android.core.functional.Either
import com.wire.android.feature.auth.registration.RegistrationRepository
import com.wire.android.framework.functional.assertLeft
import com.wire.android.framework.functional.assertRight
import com.wire.android.shared.user.User
import com.wire.android.shared.user.UserRepository
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class RegisterPersonalAccountUseCaseTest : UnitTest() {

    @Mock
    private lateinit var registrationRepository: RegistrationRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var user: User

    private lateinit var useCase: RegisterPersonalAccountUseCase

    @Before
    fun setUp() {
        useCase = RegisterPersonalAccountUseCase(registrationRepository, userRepository)
    }

    @Test
    fun `given run is called, when registrationRepository and userRepository return success, then returns success`() {
        runBlocking {
            mockRepositoryResponse(Either.Right(user))
            `when`(userRepository.save(user)).thenReturn(Either.Right(Unit))

            val result = useCase.run(params)

            verify(registrationRepository).registerPersonalAccount(TEST_NAME, TEST_EMAIL, TEST_PASSWORD, TEST_ACTIVATION_CODE)
            verify(userRepository).save(user)
            result.assertRight()
        }
    }

    @Test
    fun `given run is called, when registrationRepository returns Forbidden, then directly returns UnauthorizedEmail`() {
        runBlocking {
            mockRepositoryResponse(Either.Left(Forbidden))

            val result = useCase.run(params)

            result.assertLeft {
                assertThat(it).isEqualTo(UnauthorizedEmail)
            }
            verifyNoInteractions(userRepository)
        }
    }

    @Test
    fun `given run is called, when registrationRepository returns NotFound, then directly returns InvalidEmailActivationCode`() {
        runBlocking {
            mockRepositoryResponse(Either.Left(NotFound))

            val result = useCase.run(params)

            result.assertLeft {
                assertThat(it).isEqualTo(InvalidEmailActivationCode)
            }
            verifyNoInteractions(userRepository)
        }
    }

    @Test
    fun `given run is called, when registrationRepository returns Conflict, then directly returns EmailInUse`() {
        runBlocking {
            mockRepositoryResponse(Either.Left(Conflict))

            val result = useCase.run(params)

            result.assertLeft {
                assertThat(it).isEqualTo(EmailInUse)
            }
            verifyNoInteractions(userRepository)
        }
    }

    @Test
    fun `given run is called, when registrationRepository returns any other failure, then directly returns that failure`() {
        runBlocking {
            val failure = mock(Failure::class.java)
            mockRepositoryResponse(Either.Left(failure))

            val result = useCase.run(params)

            result.assertLeft {
                assertThat(it).isEqualTo(failure)
            }
            verifyNoInteractions(userRepository)
        }
    }

    @Test
    fun `given run is called, when registrationRepository returns success but userRepository fails, then returns that failure`() {
        runBlocking {
            mockRepositoryResponse(Either.Right(user))
            val failure = DatabaseFailure()
            `when`(userRepository.save(user)).thenReturn(Either.Left(failure))

            val result = useCase.run(params)

            result.assertLeft {
                assertThat(it).isEqualTo(failure)
            }
        }
    }

    private suspend fun mockRepositoryResponse(response: Either<Failure, User>) {
        `when`(registrationRepository.registerPersonalAccount(any(), any(), any(), any())).thenReturn(response)
    }

    companion object {
        private const val TEST_NAME = "name"
        private const val TEST_EMAIL = "email"
        private const val TEST_PASSWORD = "password"
        private const val TEST_ACTIVATION_CODE = "123456"

        private val params = RegisterPersonalAccountParams(TEST_NAME, TEST_EMAIL, TEST_PASSWORD, TEST_ACTIVATION_CODE)
    }
}
