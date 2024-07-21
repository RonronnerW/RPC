package com.wang.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author wanglibin
 * @version 1.0
 */
public class SequenceIdUtil {
    private static AtomicInteger sequenceId = new AtomicInteger();
    public static int getSequenceId() {
        return sequenceId.getAndIncrement();
    }
}
