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

using namespace std::chrono;

#pragma pack(push, 1)
struct Payload {
    uint64_t sequence_number;
    uint64_t timestamp;
};
#pragma pack(pop)

static void make_realtime(int prio = 80) {
    mlockall(MCL_CURRENT | MCL_FUTURE);
    struct sched_param sch_params;
    sch_params.sched_priority = prio;
    if (pthread_setschedparam(pthread_self(), SCHED_FIFO, &sch_params) != 0) {
        std::cerr << "Failed to set thread to real-time priority\n";
        exit(EXIT_FAILURE);
    }
}

int main(int argc, char* argv[]) {
    if (argc != 2) {
        std::cerr << "Usage: " << argv[0] << " <ifname>\n";
        return EXIT_FAILURE;
    }

    const char* interface_name = argv[1];
    make_realtime();

    int sockfd = socket(AF_PACKET, SOCK_RAW, htons(0x88B5));
    if (sockfd < 0) {
        perror("socket");
        return EXIT_FAILURE;
    }

    struct ifreq ifr{};
    std::strncpy(ifr.ifr_name, interface_name, IFNAMSIZ);
    if (ioctl(sockfd, SIOCGIFINDEX, &ifr) < 0) {
        perror("SIOCGIFINDEX");
        close(sockfd);
        return EXIT_FAILURE;
    }

    struct sockaddr_ll sll{};
    std::memset(&sll, 0, sizeof(sll));
    sll.sll_family = AF_PACKET;
    sll.sll_ifindex = ifr.ifr_ifindex;
    sll.sll_protocol = htons(0x88B5);

    if (bind(sockfd, (struct sockaddr*)&sll, sizeof(sll)) < 0) {
        perror("bind");
        close(sockfd);
        return EXIT_FAILURE;
    }

    uint64_t expected_sequence = 0;
    std::cout << "Listening on interface: " << interface_name << "\n";

    uint8_t buffer[2048];
    uint64_t prev_timestamp = 0;
    while (true) {
        ssize_t num_bytes = recvfrom(sockfd, buffer, sizeof(buffer), 0, nullptr, nullptr);
        if (num_bytes < 0) {
            perror("recvfrom");
            break;
        }

        if (num_bytes < 14 + sizeof(Payload)) {
            std::cerr << "Received packet too small\n";
            continue;
        }

        Payload* payload;
        std::memcpy(&payload, buffer + 14, sizeof(Payload));
        uint64_t curr_timestamp = duration_cast<nanoseconds>(steady_clock::now().time_since_epoch()).count();
        uint64_t delta = prev_timestamp ? (curr_timestamp - prev_timestamp) / 1e6 : 0.0;
        double latency_ms = (static_cast<double>(curr_timestamp - payload->timestamp)) / 1e6;
        prev_timestamp = curr_timestamp;
        if (payload->sequence_number != expected_sequence) {
            std::cerr << "Packet loss detected. Expected: " << expected_sequence
                      << ", Received: " << payload->sequence_number << "\n";
            expected_sequence = payload->sequence_number + 1;
        } else {
            expected_sequence++;
        }
        std::cout << "Seq: " << payload->sequence_number
                  << ", Latency: " << latency_ms << " ms"
                  << ", Interval: " << delta << " ms\n";
    }
    return 0;
}