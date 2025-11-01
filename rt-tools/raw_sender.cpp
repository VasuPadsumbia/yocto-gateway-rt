#include <arpa/inet.h>
#include <linux/if_packet.h>
#include <net/if.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <sys/mman.h>
#include <unistd.h>
#include <pthread.h>

#include <chrono>
#include <cstring>
#include <iostream>
#include <thread>

using namespace std::chrono;

#pragma pack(push, 1)
struct Payload {
    uint64_t sequence_number;
    uint64_t timestamp;
};
#pragma pack(pop)

static void make_realtime(int prio = 90) {
    mlockall(MCL_CURRENT | MCL_FUTURE);
    struct sched_param sch_params;
    sch_params.sched_priority = prio;
    if (pthread_setschedparam(pthread_self(), SCHED_FIFO, &sch_params) != 0) {
        std::cerr << "Failed to set thread to real-time priority\n";
        exit(EXIT_FAILURE);
    }
}

int main(int argc, char* argv[]) {
    const char* interface_name = (argc > 1) ? argv[1] : "enp3s0";
    make_realtime();

    int sockfd = socket(AF_PACKET, SOCK_RAW, htons(0x88B5));
    if (sockfd < 0) {
        std::cerr << "Socket creation failed\n";
        return EXIT_FAILURE;
    }

    struct ifreq ifr;
    std::strncpy(ifr.ifr_name, interface_name, IFNAMSIZ);
    if (ioctl(sockfd, SIOCGIFINDEX, &ifr) < 0) {
        std::cerr << "Failed to get interface index\n";
        close(sockfd);
        return EXIT_FAILURE;
    }

    struct sockaddr_ll socket_address;
    std::memset(&socket_address, 0, sizeof(socket_address));
    socket_address.sll_family = AF_PACKET;
    socket_address.sll_ifindex = ifr.ifr_ifindex;
    socket_address.sll_halen = 6;
    std::memset(socket_address.sll_addr, 0xff, 6); // Broadcast

    uint8_t buffer[1500];
    std::memset(buffer, 0xff, 6);
    std::memset(buffer + 6, 0x22, 6);
    buffer[12] = 0x88;
    buffer[13] = 0xB5;

    auto period = milliseconds(100);
    auto next_send_time = steady_clock::now() + period;
    uint64_t sequence_number = 0;

    while (true) {
        Payload payload;
        payload.sequence_number = htobe64(sequence_number++);
        payload.timestamp = htobe64(duration_cast<microseconds>(
            steady_clock::now().time_since_epoch()).count());
        std::memcpy(buffer + 14, &payload, sizeof(payload));
        ssize_t sent_size = sendto(sockfd, buffer, sizeof(buffer) + 14, 0,
                                   (struct sockaddr*)&socket_address,
                                   sizeof(socket_address));
        if (sent_size < 0) {
            std::cerr << "Packet send failed\n";
            close(sockfd);
            return EXIT_FAILURE;
        }
        next_send_time += period;
        std::this_thread::sleep_until(next_send_time); // Throttle sending rate
    }

    return 0;
}