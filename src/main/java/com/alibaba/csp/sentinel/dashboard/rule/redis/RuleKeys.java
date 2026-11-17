package com.alibaba.csp.sentinel.dashboard.rule.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 规则 Redis key / channel 构造器，与 kimigoshop-tms 客户端约定对齐：
 * <pre>
 *   ruleKey = sentinel:rules:{app-name}:{group}:{flow|degrade|param-flow}
 *   channel = sentinel:channel:{app-name}:{group}:{flow|degrade|param-flow}
 * </pre>
 * 其中 {app-name} 为控制台页面上的应用名（即 TMS 的 spring.application.name），
 * {group} 由配置 {@code sentinel.rule.group} 指定（默认 default），用于按实例角色隔离规则。
 * <p>
 * 注意：system / authority / gateway / cluster 等规则仍沿用 RuleConsts 的旧 key，
 * TMS 客户端只消费 flow / degrade / param-flow 三类。
 */
@Component
public class RuleKeys {

    /** TMS 支持的规则类型（与 kimigoshop-tms SentinelRulePublisher.RULE_TYPES 一致）。 */
    public static final String TYPE_FLOW = "flow";
    public static final String TYPE_DEGRADE = "degrade";
    public static final String TYPE_PARAM_FLOW = "param-flow";

    @Value("${sentinel.rule.group:default}")
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /** 规则 key：sentinel:rules:{app}:{group}:{type} */
    public String ruleKey(String app, String type) {
        return "sentinel:rules:" + app + ":" + group + ":" + type;
    }

    /** 通知 channel：sentinel:channel:{app}:{group}:{type} */
    public String channel(String app, String type) {
        return "sentinel:channel:" + app + ":" + group + ":" + type;
    }
}
