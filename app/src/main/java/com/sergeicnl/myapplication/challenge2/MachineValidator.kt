package com.sergeicnl.myapplication.challenge2

import com.sergeicnl.myapplication.challenge2.contract.State
import com.sergeicnl.myapplication.challenge2.contract.StateMachine
import com.sergeicnl.myapplication.challenge2.contract.States
import com.sergeicnl.myapplication.challenge2.rules.InvalidTransitionException
import com.sergeicnl.myapplication.challenge2.rules.StateRule

class MachineValidator(private val rules: Map<State, StateRule>) {

    private var activeState: State = States.NULL

    fun validate(stateMachine: StateMachine) {
        stateMachine.stateProcessor = processor@{ state ->
            if (state == States.FINISH) {
                onValidationFinished()
                finalizeValidation(stateMachine)
                return@processor
            }

            if (activeState == States.NULL || isValidTransition(activeState, state)) {
                activeState = state
            } else {
                finalizeValidation(stateMachine)
                return@processor
            }
        }

        stateMachine.start()
    }

    private fun isValidTransition(
        fromState: State,
        toState: State
    ): Boolean {
        rules[fromState]?.let { rule ->
            if (!rule.verifyTransition(toState)) {
                onInvalidTransition(fromState, toState)
                return false
            }
        } ?: run {
            onNoRuleForState(fromState)
            return false
        }

        return true
    }

    private fun onInvalidTransition(fromState: State, toState: State) {
        println(
            "State machine validation failed, no transitions allowed from " +
                    "$fromState to $toState"
        )
    }

    private fun onNoRuleForState(state: State) {
        println("State machine validation failed, no transition rules exists for state $state")
    }

    private fun onValidationFinished() {
        println("OK")
    }

    private fun finalizeValidation(stateMachine: StateMachine) {
        stateMachine.reset()
        activeState = States.NULL
    }
}