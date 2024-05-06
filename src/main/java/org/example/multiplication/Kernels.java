package org.example.multiplication;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import org.example.Main;
import org.example.utils.Pair;
import org.example.utils.Round;

public class Kernels {


    public static Pair<Kernel, Range> getNaiveKernel(float[] a, float[] b, float[] c,
                                                     int m, int k, int n,
                                                     Device device) {
        Kernel kernel = new Kernel() {
            @Override
            public void run() {
                int col = getGlobalId(0);
                int row = getGlobalId(1);
                if (row >= m || col >= n) {
                    return;
                }
                float acc = 0.F;
                for (int i = 0; i < k; ++i) {
                    acc += a[row * k + i] * b[i * n + col];
                }
                c[row * n + col] = acc;
            }
        };
        int workgroupSize = 16;
        Range range = Range.create2D(device, Round.fit(n, workgroupSize), Round.fit(m, workgroupSize), workgroupSize, workgroupSize);
        return new Pair<>(kernel, range);
    }

    public static Pair<Kernel, Range> getLocalMemKernel(float[] a, float[] b, float[] c,
                                                        int m, int k, int n,
                                                        Device device) {
        int tileSize = 16;
        Kernel kernel = new Kernel() {
            @Local
            float[][] tileA = new float[tileSize][tileSize + 1];
            @Local
            float[][] tileB = new float[tileSize][tileSize + 1];

            @Override
            public void run() {
                int col = getGlobalId(0);
                int row = getGlobalId(1);
                int localCol = getLocalId(0);
                int localRow = getLocalId(1);

                float acc = 0.F;
                int numTiles = k / tileSize;
                for (int t = 0; t <= numTiles; ++t) {
                    int tiledRow = tileSize * t + localCol;
                    int tiledCol = tileSize * t + localRow;

                    int ind = row * k + tiledRow;
                    tileA[localRow][localCol] = (ind < m * k) ? a[ind] : 0;
                    ind = tiledCol * n + col;
                    tileB[localRow][localCol] = (ind < k * n) ? b[ind] : 0;
                    localBarrier();

                    for (int i = 0; i < tileSize; ++i) {
                        acc += tileA[localRow][i] * tileB[i][localCol];
                    }
                    localBarrier();
                }
                if (row < m && col < n) {
                    c[row * n + col] = acc;
                }
            }
        };
        Range range = Range.create2D(device, Round.fit(n, tileSize), Round.fit(m, tileSize), tileSize, tileSize);
        return new Pair<>(kernel, range);
    }

    public static Pair<Kernel, Range> getWPTOptKernel(float[] a, float[] b, float[] c,
                                                      int m, int k, int n,
                                                      Device device) {
        int tileSize = 16;
        int wpt = 8;
        int rts = tileSize / wpt;
        Kernel kernel = new Kernel() {
            @Local
            float[][] tileA = new float[tileSize][tileSize + 1];
            @Local
            float[][] tileB = new float[tileSize][tileSize + 1];

            @Override
            public void run() {
                int localCol = getLocalId(0);
                int localRow = getLocalId(1);
                int col = tileSize * getGroupId(0) + localCol;
                int row = tileSize * getGroupId(1) + localRow;

                float acc1 = 0.F;
                float acc2 = 0.F;
                float acc3 = 0.F;
                float acc4 = 0.F;
                float acc5 = 0.F;
                float acc6 = 0.F;
                float acc7 = 0.F;
                float acc8 = 0.F;

                int numTiles = k / tileSize;
                for (int t = 0; t <= numTiles; ++t) {

                    for (int w = 0; w < wpt; ++w) {
                        int tiledRow = tileSize * t + localCol;
                        int tiledCol = tileSize * t + localRow;
                        int ind = (row + w * rts) * k + tiledRow;
                        tileA[localRow + w * rts][localCol] = (ind < m * k) ? a[ind] : 0;
                        ind = (tiledCol + w * rts) * n + col;
                        tileB[localRow + w * rts][localCol] = (ind < k * n) ? b[ind] : 0;
                    }
                    localBarrier();

                    for (int i = 0; i < tileSize; ++i) {
                        acc1 += tileA[localRow + 0 * rts][i] * tileB[i][localCol];
                        acc2 += tileA[localRow + 1 * rts][i] * tileB[i][localCol];
                        acc3 += tileA[localRow + 2 * rts][i] * tileB[i][localCol];
                        acc4 += tileA[localRow + 3 * rts][i] * tileB[i][localCol];
                        acc5 += tileA[localRow + 4 * rts][i] * tileB[i][localCol];
                        acc6 += tileA[localRow + 5 * rts][i] * tileB[i][localCol];
                        acc7 += tileA[localRow + 6 * rts][i] * tileB[i][localCol];
                        acc8 += tileA[localRow + 7 * rts][i] * tileB[i][localCol];
                    }
                    localBarrier();
                }
                if (col < n) {
                    int ind = (row + 0 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc1;
                    }
                    ind = (row + 1 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc2;
                    }
                    ind = (row + 2 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc3;
                    }
                    ind = (row + 3 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc4;
                    }
                    ind = (row + 4 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc5;
                    }
                    ind = (row + 5 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc6;
                    }
                    ind = (row + 6 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc7;
                    }
                    ind = (row + 7 * rts) * n + col;
                    if ((ind - col) / n < m) {
                        c[ind] = acc8;
                    }
                }
            }
        };
        Range range = Range.create2D(device, Round.fit(n, tileSize), Round.roundUp(Round.fit(m, tileSize), wpt), tileSize, tileSize / wpt);
        return new Pair<>(kernel, range);
    }
}
