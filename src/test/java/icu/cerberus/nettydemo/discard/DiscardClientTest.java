package icu.cerberus.nettydemo.discard;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DiscardClientTest {

    @Test
    void run() {
        DiscardClient client = new DiscardClient(65111);
        client.run();
    }


}