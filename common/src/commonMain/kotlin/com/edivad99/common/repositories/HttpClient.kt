package com.edivad99.common.repositories

import com.edivad99.common.di.RealmTransactionContext
import com.edivad99.common.di.transaction
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import java.util.*

@Serializable
data class LoginRequestData(val username: String, val password: String)

@Serializable
data class AuthTokenResponseData(val jwt: String, val expAt: Long) : RealmObject
class CommonRepository(override val di: DI) : DIAware {

    val transactionContext by instance<RealmTransactionContext>()


    val client = HttpClient(CIO) {
        val json: Json by instance()
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
        install(Auth) {
            bearer {

                loadTokens {
                    // Load tokens from a local storage and return them as the 'BearerTokens' instance

                    val token =  transactionContext.transaction {
                        query(AuthTokenResponseData::class).find().firstOrNull()
                    }
                    BearerTokens(token?.jwt ?:"", "")
                }

                refreshTokens {
                    val token = loginApi()?.also {
                        transactionContext.transaction {
                            write {
                                val oldTokens= query(AuthTokenResponseData::class).find()
                                delete(oldTokens)
                                copyToRealm(it)
                            }

                        }
                    }


                    BearerTokens(token?.jwt ?: "", "")
                }

            }
        }

    }

    private suspend fun RefreshTokensParams.loginApi() = runCatching {
        client.post(Endpoints.loginEndpoint) {
            markAsRefreshTokenRequest()
            contentType(ContentType.Application.Json)
            val encoder = Base64.getEncoder()
            setBody(
                LoginRequestData(
                    username = encoder.encodeToString("admin".toByteArray()),
                    password = encoder.encodeToString("password".toByteArray())
                )
            )
            markAsRefreshTokenRequest()
        }.body<AuthTokenResponseData>()
    }.getOrNull()

}

object Endpoints {

    val loginEndpoint = "/api/login"

    val exampleEndpoint = "https://ktor.io/docs/"
}
