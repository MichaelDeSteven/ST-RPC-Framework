package dgut.rpc.router.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.client.naming.utils.NetUtils;
import dgut.rpc.router.AbstractRouter;
import dgut.rpc.util.CollectionUtils;
import dgut.rpc.util.StringUtils;
import dgut.rpc.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description: ConditionRouter
 * @author: Steven
 * @time: 2021/7/4 23:21
 */
public class ConditionRouter extends AbstractRouter {

    public static final String NAME = "condition";

    private static final Logger logger = LoggerFactory.getLogger(ConditionRouter.class);

    protected static final Pattern ROUTE_PATTERN = Pattern.compile("([&!=,]*)\\s*([^&!=,\\s]+)");

    protected Map<String, MatchPair> whenCondition;

    protected Map<String, MatchPair> thenCondition;

    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public List<Instance> route(List<Instance> instants) {
        if (!enabled || CollectionUtils.isEmpty(instants)) {
            return instants;
        }
        try {
            // 不满足条件则提前返回
            if (!matchWhen(whenCondition)) {
                return instants;
            }

            List<Instance> result = new ArrayList<>();
            if (thenCondition == null) {
                logger.warn("当前服务消费方在黑名单中. 服务消费方: " + NetUtils.localIP());
                return result;
            }
            for (Instance instance : instants) {
                if (matchThen(thenCondition, instance)) {
                    result.add(instance);
                }
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (Throwable t) {
            logger.error("执行条件路由发生异常: " + "cause: " + t.getMessage());
        }
        return instants;
    }

    public void init(String rule) {
        try {
            if (rule == null || rule.trim().length() == 0) {
                throw new IllegalArgumentException("Illegal route rule!");
            }
            int i = rule.indexOf("=>");
            String whenRule = i < 0 ? null : rule.substring(0, i).trim();
            String thenRule = i < 0 ? rule.trim() : rule.substring(i + 2).trim();
            // 获取路由规则，如果服务消费方满足when，那么路由匹配到服务提供方then集合中
            Map<String, MatchPair> when = StringUtils.isBlank(whenRule) ||
                    "true".equals(whenRule) ? new HashMap<>() : parseRule(whenRule);
            Map<String, MatchPair> then = StringUtils.isBlank(thenRule) ||
                    "false".equals(thenRule) ? null : parseRule(thenRule);
            this.whenCondition = when;
            this.thenCondition = then;
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private boolean matchWhen(Map<String, MatchPair> condition) {
        Instance instance = new Instance();
        instance.setIp(NetUtils.localIP());
        return CollectionUtils.isEmptyMap(condition) || matchCondition(condition, instance);
    }

    private boolean matchThen(Map<String, MatchPair> condition, Instance instance) {
        return !CollectionUtils.isEmptyMap(condition) && matchCondition(condition, instance);
    }

    private boolean matchCondition(Map<String, MatchPair> condition, Instance instance) {
        boolean result = false;
        String ip = instance.getIp();
        Map<String, String> metadata = instance.getMetadata();
        for (Map.Entry<String, MatchPair> matchPair : condition.entrySet()) {
            String key = matchPair.getKey();
            String value = metadata.get(key);
            if (value != null) {
                if (!matchPair.getValue().isMatch(ip)) {
                    return false;
                } else {
                    result = true;
                }
            } else {
                return false;
            }
        }
        return result;
    }

    private static Map<String, MatchPair> parseRule(String rule) throws ParseException {
        Map<String, MatchPair> condition = new HashMap<>();
        if (StringUtils.isBlank(rule)) {
            return condition;
        }
        // 同时保存了匹配和不匹配的路由条件
        MatchPair pair = null;
        Set<String> values = null;
        final Matcher matcher = ROUTE_PATTERN.matcher(rule);
        while (matcher.find()) {
            String separator = matcher.group(1);
            String content = matcher.group(2);
            // 开始
            if (StringUtils.isEmpty(separator)) {
                pair = new MatchPair();
                condition.put(content, pair);
            }
            // &条件
            else if ("&".equals(separator)) {
                if (condition.get(content) == null) {
                    pair = new MatchPair();
                    condition.put(content, pair);
                } else {
                    pair = condition.get(content);
                }
            }
            // 加入匹配路由集合
            else if ("=".equals(separator)) {
                if (pair == null) {
                    parseError(rule, separator, matcher, content);
                }

                values = pair.matches;
                values.add(content);
            }
            // 加入不匹配路由集合
            else if ("!=".equals(separator)) {
                if (pair == null) {
                    parseError(rule, separator, matcher, content);
                }

                values = pair.mismatches;
                values.add(content);
            }
            // 加入集合
            else if (",".equals(separator)) {
                if (values == null || values.isEmpty()) {
                    parseError(rule, separator, matcher, content);
                }
                values.add(content);
            } else {
                parseError(rule, separator, matcher, content);
            }
        }
        return condition;
    }

    private static void parseError(String rule, String separator, Matcher matcher, String content)
            throws ParseException {
        throw new ParseException("Illegal route rule \"" + rule
                + "\", The error char '" + separator + "' at index "
                + matcher.start() + " before \"" + content + "\".", matcher.start());
    }

    public static final class MatchPair {
        final Set<String> matches = new HashSet<>();
        final Set<String> mismatches = new HashSet<>();

        private boolean isMatch(String value) {
            // 匹配集合不为空且不匹配集合为空，那么如果url在匹配集合则返回true
            if (!matches.isEmpty() && mismatches.isEmpty()) {
                for (String match : matches) {
                    if (UrlUtils.isMatchGlobPattern(match, value)) {
                        return true;
                    }
                }
                return false;
            }
            // 匹配集合为空且不匹配集合不为空，那么如果url在集合中则返回false
            if (!mismatches.isEmpty() && matches.isEmpty()) {
                for (String mismatch : mismatches) {
                    if (UrlUtils.isMatchGlobPattern(mismatch, value)) {
                        return false;
                    }
                }
                return true;
            }
            // 如果两个集合都不为空，那么优先使用不匹配集合进行匹配
            if (!matches.isEmpty() && !mismatches.isEmpty()) {
                for (String mismatch : mismatches) {
                    if (UrlUtils.isMatchGlobPattern(mismatch, value)) {
                        return false;
                    }
                }
                for (String match : matches) {
                    if (UrlUtils.isMatchGlobPattern(match, value)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
}
