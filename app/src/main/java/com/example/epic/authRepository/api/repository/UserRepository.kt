import com.example.epic.authRepository.api.model.response.UpdateUserResponse
import com.example.epic.authRepository.api.model.response.UserInfo
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun deleteUser(authToken: String, refreshToken: String): Flow<Unit>
    fun updateUser(
        authToken: String,
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<UpdateUserResponse>
    fun getUserInfo(): Flow<UserInfo>
}
