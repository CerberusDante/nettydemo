package icu.cerberus.nettydemo.nio.buffer.uitl;

import lombok.extern.slf4j.Slf4j;

import java.nio.Buffer;

@Slf4j
public class BufferUtil {

    public static void  print(Buffer buffer){
        log.info("++-----------------------------------------------------------------++");

        log.info("---------      position({}" +
                "" +
                "), limit({}), position({})        ---------", buffer.position(),  buffer.limit(), buffer.capacity());

    }

}
