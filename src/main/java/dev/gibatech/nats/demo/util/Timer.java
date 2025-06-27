package dev.gibatech.nats.demo.util;

public class Timer {
    private long start;
    private long end;

    public void start() {
        start = System.nanoTime();
    }

    public void stop() {
        end = System.nanoTime();
    }

    public double getSeconds() {
        return (end - start) / 1_000_000_000.0;
    }

    public double stopAndGetSeconds() {
        stop();
        return getSeconds();
    }
}