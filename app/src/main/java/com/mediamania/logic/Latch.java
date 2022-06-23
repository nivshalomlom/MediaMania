package com.mediamania.logic;

import java.util.function.Consumer;

/**
 * A simple observable latch
 */
public class Latch {

    private Consumer<Boolean> stateChangedListener;
    private boolean state;

    /**
     * Creates a new latch
     * @param initialState The initial latch state
     */
    public Latch(boolean initialState) {
        this.stateChangedListener = null;
        this.state = initialState;
    }

    /**
     * @return The current latch state
     */
    public boolean getState() { return this.state; }

    /**
     * Sets the latch state
     * @param value The new state value
     */
    public void setState(boolean value) {
        this.state = value;

        if (this.stateChangedListener != null)
            this.stateChangedListener.accept(this.state);
    }

    /**
     * Hooks up a observer to the latch state
     * @param stateChangedListener The method to run on state changed
     */
    public void setStateChangedListener(Consumer<Boolean> stateChangedListener) { this.stateChangedListener = stateChangedListener; }

}
