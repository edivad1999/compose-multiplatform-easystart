package com.edivad99.common.di

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class RealmTransactionContext(override val di: DI) : DIAware {
    val conf: RealmConfiguration by instance()

}

internal suspend inline fun <R> RealmTransactionContext.transaction(
    action: suspend Realm.() -> R

): R {
    val realm = Realm.open(conf)
    return try {
       realm.action()
    } finally {
        realm.close()
    }
}
