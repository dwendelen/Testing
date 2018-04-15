package com.github.dwendelen.testing.component.impl.report;

import com.github.dwendelen.testing.component.impl.component.Component;

public class UnusedDepedency {
    private Component from;
    private Component to;

    public UnusedDepedency(Component from, Component to) {
        this.from = from;
        this.to = to;
    }

    public Component getFrom() {
        return from;
    }

    public Component getTo() {
        return to;
    }
}
