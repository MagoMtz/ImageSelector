package com.mago.imagepicker.extensions

import io.reactivex.rxjava3.core.Observable
import java.util.concurrent.TimeUnit

/**
 * @author by jmartinez
 * @since 12/11/2020.
 */
fun <T> Observable<T>.debounceIf(predicate: (T) -> Boolean, delay: Long, unit: TimeUnit): Observable<T> {
    return this.debounce {
        when {
            predicate(it) -> Observable.timer(delay, unit)
            else -> Observable.empty()
        }
    }
}