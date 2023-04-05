package com.sergeicnl.myapplication.challenge1

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SampleServiceOperation(
    private val service: SampleService,
    private val payload: String
) {

    suspend fun execute() {
        withContext(Dispatchers.IO) {
            doRequest(RETRY_COUNT)
        }
    }

    private suspend fun doRequest(retries: Int) {
        try {
            service.postClientData(payload)
        } catch (exc: IOException) {
            // Stacktrace will not be pretty, but this is just for example. Otherwise iterative
            // solution is cleaner or "Result<>" data type can be used to deliver request outcome explicitly.
            if (retries == 0) {
                throw exc
            }

            delay(Random.nextLong(MIN_TIMEOUT, MAX_TIMEOUT))
            doRequest(retries - 1)
        }
    }

    companion object {
        // This values can be either hardcoded in app or delivered via some config on app startup.
        // Regardless, they should be retrieved SRE or backend engineers to understand what is the
        // average expected service outage time.
        private val MIN_TIMEOUT = TimeUnit.MINUTES.toMillis(1)
        private val MAX_TIMEOUT = TimeUnit.MINUTES.toMillis(5)
        private const val RETRY_COUNT = 5
    }
}