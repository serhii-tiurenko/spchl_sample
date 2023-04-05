package com.sergeicnl.myapplication.challenge2.rules

import com.sergeicnl.myapplication.challenge2.contract.State

class StateRule(
    private val allowedTransitions: Set<State>
) {

    fun verifyTransition(nextState: State): Boolean = allowedTransitions.contains(nextState)
}