package org.example.multiplication;

import lombok.Getter;

@Getter
public abstract class Multiplier {
    protected float[] firstMat;
    protected float[] secondMat;
    protected float[] resultMat;

    public abstract void calculate();
}
