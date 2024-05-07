FROM ubuntu:22.04

RUN apt-get update && apt-get install -yq clang cmake bash git cpio lsb-core ocl-icd-libopencl1 wget libnuma-dev gdb libx11-dev &&\
  apt-get clean
RUN mkdir neo &&\
  cd neo &&\
  wget https://github.com/intel/intel-graphics-compiler/releases/download/igc-1.0.16510.2/intel-igc-core_1.0.16510.2_amd64.deb &&\
  wget https://github.com/intel/intel-graphics-compiler/releases/download/igc-1.0.16510.2/intel-igc-opencl_1.0.16510.2_amd64.deb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/intel-level-zero-gpu-dbgsym_1.3.29138.7_amd64.ddeb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/intel-level-zero-gpu_1.3.29138.7_amd64.deb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/intel-opencl-icd-dbgsym_24.13.29138.7_amd64.ddeb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/intel-opencl-icd_24.13.29138.7_amd64.deb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/libigdgmm12_22.3.18_amd64.deb &&\
  wget https://github.com/intel/compute-runtime/releases/download/24.13.29138.7/ww13.sum &&\
  sha256sum -c ww13.sum &&\
  dpkg -i *.deb && cd .. && rm -r neo

RUN cd home && mkdir opencl_installer && cd opencl_installer &&\
 wget http://registrationcenter-download.intel.com/akdlm/irc_nas/vcp/15532/l_opencl_p_18.1.0.015.tgz && tar -xzf l_opencl_p_18.1.0.015.tgz &&\
 rm l_opencl_p_18.1.0.015.tgz && cd ../..

RUN mkdir amd_driver_installer && cd amd_driver_installer && wget https://repo.radeon.com/amdgpu-install/23.40.2/ubuntu/jammy/amdgpu-install_6.0.60002-1_all.deb &&\
 dpkg -i *.deb && cd .. && rm -r amd_driver_installer

RUN apt-get -yq install kmod clinfo && apt-get clean
ARG nvidia_binary_version="470.57.02"
ARG nvidia_binary="NVIDIA-Linux-x86_64-${nvidia_binary_version}.run"
RUN wget -q https://us.download.nvidia.com/XFree86/Linux-x86_64/${nvidia_binary_version}/${nvidia_binary} &&\
chmod +x ${nvidia_binary} &&\
./${nvidia_binary} --accept-license --ui=none --no-kernel-module --no-questions &&\
rm -rf ${nvidia_binary}

RUN rm /bin/sh && ln -s /bin/bash /bin/sh
RUN apt-get -yq install unzip zip curl && apt-get clean && curl -s "https://get.sdkman.io" | bash 
RUN bash -c "source /root/.sdkman/bin/sdkman-init.sh && sdk install java && sdk install gradle 8.5 && sdk install kotlin" 

ENTRYPOINT ["/bin/bash"]

