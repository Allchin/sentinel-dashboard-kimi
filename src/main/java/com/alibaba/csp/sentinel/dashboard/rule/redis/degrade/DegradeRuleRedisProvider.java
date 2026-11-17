package com.alibaba.csp.sentinel.dashboard.rule.redis.degrade;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.redis.RedisUtil;
import com.alibaba.csp.sentinel.dashboard.rule.redis.RuleKeys;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component("degradeRuleRedisProvider")
public class DegradeRuleRedisProvider implements DynamicRuleProvider<List<DegradeRuleEntity>> {

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private RuleKeys ruleKeys;

    @Override
    public List<DegradeRuleEntity> getRules(String appName) throws Exception {
        String rules = redisUtil.getString(ruleKeys.ruleKey(appName, RuleKeys.TYPE_DEGRADE));
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSONObject.parseArray(rules, DegradeRuleEntity.class);
    }

}