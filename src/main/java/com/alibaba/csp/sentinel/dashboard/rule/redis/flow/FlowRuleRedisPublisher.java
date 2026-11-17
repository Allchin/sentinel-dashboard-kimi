package com.alibaba.csp.sentinel.dashboard.rule.redis.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.redis.RedisUtil;
import com.alibaba.csp.sentinel.dashboard.rule.redis.RuleKeys;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component("flowRuleRedisPublisher")
public class FlowRuleRedisPublisher implements DynamicRulePublisher<List<FlowRuleEntity>> {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RuleKeys ruleKeys;

    @Override
    public void publish(String app, List<FlowRuleEntity> rules) throws Exception {
        if (rules == null) {
            return;
        }
        String strs = JSON.toJSONString(rules);
        redisUtil.setString(ruleKeys.ruleKey(app, RuleKeys.TYPE_FLOW), strs,
                ruleKeys.channel(app, RuleKeys.TYPE_FLOW));
    }
}