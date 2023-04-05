package com.sergeicnl.myapplication

import com.sergeicnl.myapplication.challenge1.SampleService
import com.sergeicnl.myapplication.challenge1.SampleServiceOperation
import com.sergeicnl.myapplication.challenge2.MachineValidator
import com.sergeicnl.myapplication.challenge2.contract.State
import com.sergeicnl.myapplication.challenge2.contract.StateMachine
import com.sergeicnl.myapplication.challenge2.contract.StateProcessor
import com.sergeicnl.myapplication.challenge2.contract.States
import com.sergeicnl.myapplication.challenge2.rules.StateRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.SocketTimeoutException

fun main() {
    challenge1Run()

    challenge2Run()
}

fun challenge1Run() {
    val operation = SampleServiceOperation(ConnectionFailedService(3), "{}")
    runBlocking {
        // We need to wait for this anyway, that's why it is blocking here.
        operation.execute()
    }
}

class ConnectionFailedService(retries: Int) : SampleService {

    private var awaitRetries = retries

    override fun postClientData(json: String) {
        if (awaitRetries != 0) {
            awaitRetries--
            throw SocketTimeoutException("Cannot connect to a service.")
        }
    }
}

fun challenge2Run() {
    // In task description I did not see a requirement to have a valid transition between multiple states, like:
    // Transition from C to D only possible if state before C is A.
    // Otherwise StateRule implementation would be more complex and I would need a separate
    // "TransitionHistory" data class in validator to store a piece of transition history.
    val validator = MachineValidator(
        mapOf(
            States.A to StateRule(setOf(States.A, States.B)),
            States.B to StateRule(setOf(States.C)),
            States.C to StateRule(setOf(States.D)),
            States.D to StateRule(setOf(States.E)),
            States.E to StateRule(setOf(States.E))
        )
    )

    validator.validate(
        SequentialTestMachine(
            listOf(States.A, States.A, States.B, States.C, States.D, States.E, States.E)
        )
    )

    validator.validate(
        SequentialTestMachine(
            listOf(States.A, States.C, States.B)
        )
    )

    validator.validate(
        SequentialTestMachine(
            listOf(States.A, States.B, States.C, States.D, States.E, States.A)
        )
    )
}

class SequentialTestMachine(private val states: List<State>) : StateMachine {

    private var reset = false
    override var stateProcessor: StateProcessor? = null

    override fun start() {
        val iterator = states.iterator()
        while (!reset) {
            if (!iterator.hasNext()) {
                stateProcessor?.invoke(States.FINISH)
                return
            }

            stateProcessor?.invoke(iterator.next())
        }
    }

    override fun reset() {
        reset = true
    }
}