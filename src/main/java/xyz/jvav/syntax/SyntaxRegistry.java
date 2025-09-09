package xyz.jvav.syntax;

import java.util.*;

/**
 * 使用 ServiceLoader 发现并加载语法映射构造器（映射表）。
 * 仅依赖 META-INF/services 注册，JAR 内稳定可用。
 */
public final class SyntaxRegistry {
    private final Map<String, String> cn2en = new LinkedHashMap<>();

    private SyntaxRegistry() {}

    public static SyntaxRegistry load() {
        SyntaxRegistry reg = new SyntaxRegistry();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) cl = SyntaxRegistry.class.getClassLoader();
        ServiceLoader<SyntaxContributor> loader = ServiceLoader.load(SyntaxContributor.class, cl);
        for (SyntaxContributor c : loader) {
            try { c.contribute(reg.cn2en); } catch (Throwable ignored) {}
        }
        return reg;
    }

    public Map<String, String> mappings() { return Collections.unmodifiableMap(cn2en); }
}
