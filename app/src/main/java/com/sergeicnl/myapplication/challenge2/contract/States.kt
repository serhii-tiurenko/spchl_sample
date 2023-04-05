package com.sergeicnl.myapplication.challenge2.contract

// In this example, State interface is a bit redundant. But I am assuming that it is just a sample
// and code should work for other state machine implementations.
enum class States : State {
    NULL,
    A,
    B,
    C,
    D,
    E,

    // In task description, it is not clear what is the goal of validation. i.e. how many transitions to
    // validate and when to finish. So adding terminator.
    FINISH
}