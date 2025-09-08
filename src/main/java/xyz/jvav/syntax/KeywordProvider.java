package xyz.jvav.syntax;

import java.util.Map;

/**
 * 关键字提供者：定义中文 -> Java 关键字的映射。
 */
public interface KeywordProvider {
    Map<String, String> getKeywordMap();
}

