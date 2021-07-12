package dgut.rpc.router.impl;

import com.alibaba.nacos.api.naming.pojo.Instance;
import dgut.rpc.router.AbstractRouter;
import dgut.rpc.util.StringUtils;
import dgut.rpc.util.UrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
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

    protected static Pattern ARGUMENTS_PATTERN = Pattern.compile("arguments\\[([0-9]+)\\]");

    protected Map<String, MatchPair> whenCondition;

    protected Map<String, MatchPair> thenCondition;

    private boolean enabled;

    @Override
    public List<Instance> route(List<Instance> instants) {
        List<Instance> list = new ArrayList<>();
        return list;
    }

    public void init(String rule) {
        try {
            if (rule == null || rule.trim().length() == 0) {
                throw new IllegalArgumentException("Illegal route rule!");
            }
            rule = rule.replace("consumer.", "")
                    .replace("provider.", "");
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


//    private boolean matchWhen(URL url, Invocation invocation) {
////        return CollectionUtils.isEmptyMap(whenCondition) || matchCondition(whenCondition, url, null, invocation);
//        return true;
//    }

//    private boolean matchThen(URL url, URL param) {
////        return CollectionUtils.isNotEmptyMap(thenCondition) && matchCondition(thenCondition, url, param, null);
//        return true;
//    }

    private boolean matchCondition() {
        return true;
    }

    private static Map<String, MatchPair> parseRule(String rule)
            throws ParseException {
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
            if (StringUtils.isEmpty(separator)) {
                pair = new MatchPair();
                condition.put(content, pair);
            }
            // 新的匹配集合
            else if ("&".equals(separator)) {
                if (condition.get(content) == null) {
                    pair = new MatchPair();
                    condition.put(content, pair);
                } else {
                    pair = condition.get(content);
                }
            }
            // 加入匹配路由
            else if ("=".equals(separator)) {
                if (pair == null) {
                    parseError(rule, separator, matcher, content);
                }

                values = pair.matches;
                values.add(content);
            }
            // 加入不匹配路由
            else if ("!=".equals(separator)) {
                if (pair == null) {
                    parseError(rule, separator, matcher, content);
                }

                values = pair.mismatches;
                values.add(content);
            }
            // 加入匹配集合
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

    private static void parseError(String rule,
                                   String separator,
                                   Matcher matcher,
                                   String content)
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
