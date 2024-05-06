package org.example.utils;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Pair<T, U> {
    private T first;
    private U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
}
