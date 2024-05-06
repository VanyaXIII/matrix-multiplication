package org.example.multiplication;

public class CPUMultiplier extends Multiplier {

    private final int m;
    private final int n;

    public CPUMultiplier(float[] firstMat, float[] secondMat, int m, int k, int n) {
        this.firstMat = firstMat;
        this.secondMat = secondMat;
        this.resultMat = new float[m * n];
        this.m = m;
        this.n = n;
    }

    @Override
    public void calculate() {
        int k = firstMat.length / m;
        for (int row = 0; row < m; ++row) {
            for (int col = 0; col < n; ++col) {
                resultMat[row * n + col] = 0.F;
                for (int i = 0; i < k; ++i) {
                    resultMat[row * n + col] += firstMat[row * k + i] * secondMat[i * n + col];
                }
            }
        }
    }
}
