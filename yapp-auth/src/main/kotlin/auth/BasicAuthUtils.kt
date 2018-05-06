package auth

import io.ktor.auth.Principal
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.UserPasswordCredential
import io.ktor.util.encodeBase64
import io.ktor.util.getDigestFunction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

object BasicAuthUtils {
    private val passwordSalt = System.getenv("PASSWORD_SALT") ?: "this_is_default_password_salt"
    private val digestFunction = getDigestFunction("SHA-256", passwordSalt)

    fun isUsernameAlreadyTaken(name: String): Boolean {
        return transaction {
            !UserEntity.find { UsersTable.username eq name }.empty()
        }
    }

    fun authenticateByPassword(credential: UserPasswordCredential): Principal? {
        val hashedPassword = hashAndEncodePassword(credential.password)
        val userId: Int? = transaction {
            val find =
                UserEntity.find { (UsersTable.username eq credential.name) and (UsersTable.password eq hashedPassword) }
            if (find.empty()) {
                null
            } else {
                find.first().id.value
            }
        }
        return if (userId == null) {
            null
        } else {
            UserIdPrincipal(userId.toString())
        }
    }

    fun hashAndEncodePassword(password: String): String {
        return encodeBase64(digestFunction.invoke(password))
    }
}
