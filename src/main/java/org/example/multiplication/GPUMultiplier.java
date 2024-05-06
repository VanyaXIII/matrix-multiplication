package org.example.multiplication;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import org.example.utils.Pair;

public class GPUMultiplier extends Multiplier {

    private final Pair<Kernel, Range> program;

    public GPUMultiplier(float[] firstMat, float[] secondMat, int m, int k, int n) {
        this(OpenCLDevice.listDevices(Device.TYPE.GPU).getFirst(), firstMat, secondMat, m, k, n);
    }

    public GPUMultiplier(Device device, float[] firstMat, float[] secondMat, int m, int k, int n) {
        this.firstMat = firstMat;
        this.secondMat = secondMat;
        this.resultMat = new float[m * n];
        this.program = Kernels.getWPTOptKernel(firstMat, secondMat, resultMat, m, k, n, device);
    }

    @Override
    public void calculate() {
        program.getFirst().execute(program.getSecond());
    }
}
