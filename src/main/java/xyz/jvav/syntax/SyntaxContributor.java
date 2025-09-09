package xyz.jvav.syntax;

import java.util.Map;

/** 语法映射构造器接口（通过 ServiceLoader 发现） */
public interface SyntaxContributor {
    /** 向 map 注册 “中文 => 英文”的映射（N 中文 -> 1 英文可多次 put） */
    void contribute(Map<String, String> map);
}

