package com.wang.tolerant;

/**
 * 容错策略常量
 */
public interface TolerantStrategyKeys {
    /**
     * 静默处理
     */
    String FAIL_SAFE = "failSafe";
    /**
     * 快速失败
     */
    String FAIL_FAST = "failFast";
    /**
     * 故障转移
     */
    String FAIL_OVER = "failOver";
    /**
     * 自动恢复
     */
    String FAIL_BACK = "failBack";
}
