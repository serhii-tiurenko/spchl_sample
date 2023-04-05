package com.sergeicnl.myapplication.challenge2.contract

typealias StateProcessor = (State) -> Unit

interface StateMachine {

    var stateProcessor: StateProcessor?

    fun start()
    
    fun reset()
}