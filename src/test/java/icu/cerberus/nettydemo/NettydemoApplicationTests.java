package icu.cerberus.nettydemo;

import icu.cerberus.nettydemo.discard.DiscardServer;
import icu.cerberus.nettydemo.echo.EchoServer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NettydemoApplicationTests {

    @Test
    void contextLoads() throws Exception {
        int port = 8000;
        new EchoServer(port).run();
    }

}
