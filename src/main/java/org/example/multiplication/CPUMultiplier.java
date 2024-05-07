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

    private float[] getTransposedMat() {
        int k = secondMat.length / n;
        float[] res = new float[secondMat.length];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < n; j++) {
                res[j * k + i] = secondMat[i * n + j];
            }
        }
        return res;
    }

    @Override
    public void calculate() {
        int k = firstMat.length / m;
        float[] transposed = getTransposedMat();
        for (int row = 0; row < m; ++row) {
            for (int col = 0; col < n; ++col) {
                resultMat[row * n + col] = 0.F;
                for (int i = 0; i < k; ++i) {
                    resultMat[row * n + col] += firstMat[row * k + i] * transposed[col * k + i];
                }
            }
        }
    }
}
