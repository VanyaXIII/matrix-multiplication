package org.example.utils;

public class Round {

    public static int roundUp(int first, int second) {
        return (first - 1 + second) / second;
    }

    public static int fit(int first, int second) {
        return roundUp(first, second) * second;
    }
}
