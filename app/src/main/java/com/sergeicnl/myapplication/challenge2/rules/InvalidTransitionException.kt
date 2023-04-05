package com.sergeicnl.myapplication.challenge2.rules

import com.sergeicnl.myapplication.challenge2.contract.State

class InvalidTransitionException(fromState: State, toState: State) :
    IllegalStateException("Invalid transition from ${fromState.javaClass.name} to ${toState.javaClass.name}.")