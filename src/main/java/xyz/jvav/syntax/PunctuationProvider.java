package xyz.jvav.syntax;

import java.util.Map;

/**
 * 全角符号提供者：定义常见全角 -> 半角的映射，仅在代码区替换。
 */
public interface PunctuationProvider {
    Map<Character, Character> getFullwidthMap();
}

