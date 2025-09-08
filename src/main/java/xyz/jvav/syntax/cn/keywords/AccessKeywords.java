package xyz.jvav.syntax.cn.keywords;

import java.util.LinkedHashMap;
import java.util.Map;

/** 访问控制/修饰符 相关关键字映射 */
public final class AccessKeywords {
    private AccessKeywords() {}
    public static Map<String,String> map() {
        Map<String,String> m = new LinkedHashMap<>();
        m.put("公共", "public");
        m.put("受保护", "protected");
        m.put("私有", "private");
        m.put("静态", "static");
        m.put("最终", "final");
        m.put("抽象", "abstract");
        m.put("严格fp", "strictfp");
        m.put("本地", "native");
        m.put("短暂", "transient");
        m.put("易变", "volatile");
        return m;
    }
}

