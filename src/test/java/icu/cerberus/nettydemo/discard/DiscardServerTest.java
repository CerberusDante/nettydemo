package icu.cerberus.nettydemo.discard;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DiscardServerTest {

    @Test
    void run() {
        DiscardServer server = new DiscardServer(65111);
        try {
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}