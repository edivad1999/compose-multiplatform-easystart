package com.edivad99.common.di

import com.edivad99.common.repositories.AuthTokenResponseData
import com.edivad99.common.repositories.CommonRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance


object DiModules {


    val http
        get() = DI.Module("http") {
            bindSingleton {
                CommonRepository(di)
            }
        }
    val serialization
        get() = DI.Module("serialization") {
            bindSingleton {
                Json {
                    prettyPrint = true
                    isLenient = true
                }
            }
        }
    val database
        get() = DI.Module("database") {
            bindSingleton {
                RealmConfiguration.create(schema = setOf(AuthTokenResponseData::class))
            }
            bindProvider {
                RealmTransactionContext(di)
            }
        }

}
