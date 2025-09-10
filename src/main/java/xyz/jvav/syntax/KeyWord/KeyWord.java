package xyz.jvav.syntax.KeyWord;

import xyz.jvav.syntax.SyntaxContributor;

import java.util.Map;

public final class KeyWord implements SyntaxContributor {
    @Override
    public void contribute(Map<String, String> map) {

        /* 1. Access Modifiers 访问修饰符 */
        map.put("公共", "public");
        map.put("公开", "public");

        map.put("保护", "protected");
        map.put("受保护", "protected");
        map.put("被保护", "protected");

        map.put("私有", "private");


        /* 2. Class-related 类/接口/抽象类 */
        map.put("类", "class");

        map.put("接口", "interface");

        map.put("抽象", "abstract");

        map.put("继承", "extends");

        map.put("实现", "implements");

        map.put("新", "new");
        map.put("新建", "new");

        map.put("父类引用", "super");

        map.put("这个", "this");
        map.put("这个对象", "this");
        map.put("当前对象", "this");

        map.put("实例判断", "instanceof");


        /* 3. Primitive Types 基本数据类型 */
        map.put("空", "void");
        map.put("空类型", "void");

        map.put("字节", "byte");

        map.put("短整型", "short");
        map.put("短整数", "short");

        map.put("整型", "int");
        map.put("整数", "short");

        map.put("长整型", "long");
        map.put("长整数", "long");

        map.put("单精度", "float");
        map.put("单精度小数", "float");
        map.put("单精度浮点数", "float");

        map.put("双精度", "double");
        map.put("双精度小数", "double");
        map.put("双精度浮点数", "double");

        map.put("字符", "char");
        map.put("单字符", "char");

        map.put("布尔", "boolean");
        map.put("布尔值", "boolean");


        /* 4. Value Literals 值字面量 */
        map.put("空值", "null");

        map.put("真", "true");
        map.put("是", "true");

        map.put("假", "false");
        map.put("否", "false");


        /* 5. Control Flow 控制流 */
        map.put("如果", "if");

        map.put("否则", "else");

        map.put("匹配", "switch");
        map.put("情景", "switch");

        map.put("分支", "case");
        map.put("情况", "case");

        map.put("默认", "default");

        map.put("循环", "while");

        map.put("先循环", "do");

        map.put("遍历", "for");

        map.put("跳出", "break");
        map.put("结束", "break");
        map.put("终结", "break");

        map.put("继续", "continue");

        map.put("返回", "return");


        /* 6. Exception Handling 异常处理 */
        map.put("尝试", "try");

        map.put("捕获", "catch");
        map.put("捕捉", "catch");

        map.put("最后", "finally");

        map.put("抛出", "throw");
        map.put("抛出异常", "throw");

        map.put("抛出类型", "throws");
        map.put("抛出异常类型", "throws");

        /* 7. Concurrency 并发 */
        map.put("重量同步", "synchronized");
        map.put("重量锁", "synchronized");
        map.put("重量级锁", "synchronized");

        map.put("轻量同步", "volatile");
        map.put("轻量锁", "volatile");
        map.put("轻量级锁", "volatile");

        /* 8. Memory/Serialization 内存/序列化 */
        map.put("反序列化", "transient");
        map.put("防序列化", "transient");

        /* 9. Native Interface 本地接口 */
        map.put("本地", "native");

        /* 10. Assertion 断言 */
        map.put("断言", "assert");

        /* 11. Package 包 */
        map.put("包", "package");

        map.put("导入", "import");
        map.put("引入", "import");

        /* 12. Final 最终修饰符 */
        map.put("最终", "final");
        map.put("锁定", "final");

        /* 13. Static 静态修饰符 */
        map.put("静态", "static");

        /* 14. Strict FP 严格浮点 */
        map.put("严格浮点", "strictfp");

        /* 15. Enum 枚举 */
        map.put("枚举", "enum");
    }
}
