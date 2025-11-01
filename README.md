# Yocto-Gateway-RT (PREEMPT_RT Raspberry Pi Gateway)

Embedded Linux Gateway based on Yocto Project with PREEMPT_RT for real-time applications on Raspberry Pi Zero 2W.
Implements:
- Real-time Linux kernel (PREEMPT_RT 6.6.x)
- Custom Yocto layers for gateway functionalities (Walnscar)
- Auto Wi-Fi + SSH
- Deterministic networking stack (Raw Ethernet)
- Single-file KAS deployment
- Support for Raspberry Pi Zero 2W

---
## Structure
- `meta-gateway/`: Custom Yocto layer for gateway functionalities.
- `rt-tools/`: host side C++ latency tests.
- `kas/firmware.yml`: KAS configuration file for building the image.
- `.github/workflows/`: CI/CD workflows for automated builds.

---
## Prerequisites
- A Linux host system (Ubuntu 20.04 or later recommended).
- KAS tool installed (https://github.com/siemens/kas)

## Building the Image
```bash
# Requirements installation
sudo apt-get update
sudo apt-get install -y git wget kas docker.io
sudo usermod -aG docker $USER
newgrp docker

# Clone the repository
git clone https://github.com/yourusername/yocto-gateway-rt.git
cd yocto-gateway-rt
# Build the image using KAS
kas build kas/firmware.yml

# For Docker
./kas-container.sh

```