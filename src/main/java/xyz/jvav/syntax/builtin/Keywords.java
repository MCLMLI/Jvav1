package xyz.jvav.syntax.builtin;

import xyz.jvav.syntax.SyntaxContributor;

import java.util.Map;

/**
 * 内置关键字与常用标识映射（示例）。
 * 可在 syntax 目录下新增更多实现类扩展映射。
 */
public final class Keywords implements SyntaxContributor {
    @Override
    public void contribute(Map<String, String> map) {
        // 语法关键字（N -> 1，可多次 put）
        map.put("包", "package");
        map.put("导入", "import");
        map.put("载入", "import");

        map.put("类", "class");
        map.put("类型", "class");

        map.put("接口", "interface");
        map.put("枚举", "enum");

        map.put("公共", "public");
        map.put("公有", "public");
        map.put("受保护", "protected");
        map.put("私有", "private");

        map.put("静态", "static");
        map.put("最终", "final");
        map.put("抽象", "abstract");
        map.put("同步", "synchronized");
        map.put("瞬态", "transient");
        map.put("易失", "volatile");
        map.put("严格fp", "strictfp");
        map.put("默认", "default");

        map.put("空", "void");
        map.put("返回", "return");

        map.put("如果", "if");
        map.put("否则", "else");
        map.put("开关", "switch");
        map.put("情况", "case");
        map.put("默认情况", "default");

        map.put("当", "while");
        map.put("做", "do");
        map.put("为", "for");
        map.put("增强为", "for"); // 语义一致（增强 for）

        map.put("跳出", "break");
        map.put("继续", "continue");

        map.put("尝试", "try");
        map.put("捕获", "catch");
        map.put("最终块", "finally");
        map.put("抛出", "throw");
        map.put("抛出声明", "throws");
        map.put("断言", "assert");

        map.put("新建", "new");
        map.put("新", "new");
        map.put("此", "this");
        map.put("父类", "super");
        map.put("实例于", "instanceof");
        map.put("空值", "null");
        map.put("真", "true");
        map.put("假", "false");

        // 常用类/成员（演示）
        map.put("系统", "System");
        map.put("输出", "out");
        map.put("错误输出", "err");
        map.put("输入", "in");
        map.put("打印行", "println");
        map.put("打印", "println");
        map.put("打印格式", "printf");
    }
}

