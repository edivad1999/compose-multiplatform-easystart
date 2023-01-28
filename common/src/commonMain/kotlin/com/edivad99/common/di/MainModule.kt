package com.edivad99.common.di

import com.edivad99.common.repositories.CommonRepository
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton


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

}
