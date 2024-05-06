package org.example;


import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import org.example.multiplication.CPUMultiplier;
import org.example.multiplication.GPUMultiplier;
import org.example.multiplication.Multiplier;

import java.util.Arrays;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int m = 2;
        int k = 2;
        int n = 2;

        float[] firstMat = new float[m * k];
        float[] secondMat = new float[n * k];

        Random r = new Random();
        for (int i = 0; i < m * k; i++) {
            firstMat[i] = r.nextFloat();
        }
        for (int i = 0; i < n * k; i++) {
            secondMat[i] = r.nextFloat();
        }

        Multiplier gpuMultiplier = new GPUMultiplier(OpenCLDevice.listDevices(Device.TYPE.GPU).getFirst(), firstMat, secondMat, m, k, n);
        gpuMultiplier.calculate();

        Multiplier cpuMultiplier = new CPUMultiplier(firstMat, secondMat, m, k, n);
        cpuMultiplier.calculate();

        float[] resGPU = gpuMultiplier.getResultMat();
        float[] resCPU = cpuMultiplier.getResultMat();
        for (int i = 0; i < m * n; ++i) {
            if (Math.abs(resGPU[i] - resCPU[i]) > 0.01F) {
                System.out.println("Error at index " + i);
            }
        }
    }
}