package org.example.multiplication;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.device.OpenCLDevice;
import lombok.Getter;
import org.example.utils.Pair;

public class GPUMultiplier extends Multiplier {

    private final Pair<Kernel, Range> program;
    @Getter
    private final Device device;

    private static Device getDefaultDevice() {
        if (!OpenCLDevice.listDevices(Device.TYPE.GPU).isEmpty()) {
            return OpenCLDevice.listDevices(Device.TYPE.GPU).getFirst();
        }
        if (!OpenCLDevice.listDevices(Device.TYPE.CPU).isEmpty()) {
            return OpenCLDevice.listDevices(Device.TYPE.CPU).getFirst();
        }
        return null;
    }


    public GPUMultiplier(float[] firstMat, float[] secondMat, int m, int k, int n) {
        this(getDefaultDevice(), firstMat, secondMat, m, k, n, KernelType.NAIVE);
    }

    public GPUMultiplier(float[] firstMat, float[] secondMat, int m, int k, int n, KernelType kernelType) {
        this(getDefaultDevice(), firstMat, secondMat, m, k, n, kernelType);
    }

    public GPUMultiplier(Device device, float[] firstMat, float[] secondMat, int m, int k, int n) {
        this(device, firstMat, secondMat, m, k, n, KernelType.NAIVE);
    }

    public GPUMultiplier(Device device, float[] firstMat, float[] secondMat, int m, int k, int n, KernelType kernelType) {
        if (device == null) {
            throw new IllegalArgumentException("No devices available");
        }
        this.device = device;
        this.firstMat = firstMat;
        this.secondMat = secondMat;
        this.resultMat = new float[m * n];
        switch (kernelType) {
            case WITH_WPT_OPTIMIZATION:
                this.program = Kernels.getWPTOptKernel(firstMat, secondMat, resultMat, m, k, n, device);
                break;
            case WITH_LOCAL_MEM_OPTIMIZATION:
                this.program = Kernels.getLocalMemKernel(firstMat, secondMat, resultMat, m, k, n, device);
                break;
            default:
                this.program = Kernels.getNaiveKernel(firstMat, secondMat, resultMat, m, k, n, device);
                break;
        }
    }

    @Override
    public void calculate() {
        program.getFirst().execute(program.getSecond());
    }
}
