package com.wang.tolerant;

/**
 * 容错策略常量
 */
public interface TolerantStrategyKeys {
    String FAIL_SAFE = "failSafe";
    String FAIL_FAST = "failFast";
    String FAIL_OVER = "failOver";
    String FAIL_BACK = "failBack";
}
