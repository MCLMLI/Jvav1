package xyz.jvav.syntax.Package;

import xyz.jvav.syntax.SyntaxContributor;

import java.util.Map;

public final class Lang implements SyntaxContributor {
    @Override
    public void contribute(Map<String, String> map) {

        /* ===== 1. 根对象与基本包装 ===== */
        map.put("对象", "Object");

        map.put("类对象", "Class");

        map.put("字符串", "String");

        map.put("字节包装", "Byte");

        map.put("短整型包装", "Short");
        map.put("短整数包装", "Short");

        map.put("整型包装", "Integer");
        map.put("整数包装", "Integer");

        map.put("长整型包装", "Long");
        map.put("长整数包装", "Long");

        map.put("单精度包装", "Float");
        map.put("单精度小数包装", "Float");

        map.put("双精度包装", "Double");
        map.put("双精度小数包装", "Double");

        map.put("字符包装", "Character");
        map.put("单字符包装", "Character");

        map.put("布尔包装", "Boolean");
        map.put("布尔值包装", "Boolean");


        /* ===== 2. 数学与系统 ===== */
        map.put("数学", "Math");

        map.put("严格数学", "StrictMath");

        map.put("系统", "System");

        map.put("运行时", "Runtime");


        /* ===== 3. 线程与进程 ===== */
        map.put("线程", "Thread");

        map.put("可运行", "Runnable");

        map.put("进程", "Process");

        map.put("进程构建器", "ProcessBuilder");


        /* ===== 4. 比较与执行 ===== */
        map.put("可比较", "Comparable");

        map.put("字符序列", "CharSequence");

        map.put("可调用", "Callable");


        /* ===== 5. 反射与加载 ===== */
        map.put("类加载器", "ClassLoader");

        map.put("包信息", "Package");


        /* ===== 6. 常用异常 ===== */
        map.put("类未找到异常", "ClassNotFoundException");

        map.put("非法访问异常", "IllegalAccessException");

        map.put("实例化异常", "InstantiationException");

        map.put("中断异常", "InterruptedException");

        map.put("克隆不支持异常", "CloneNotSupportedException");


        /* ===== 7. 常用运行时异常 ===== */
        map.put("空指针异常", "NullPointerException");

        map.put("数组越界异常", "ArrayIndexOutOfBoundsException");

        map.put("类转换异常", "ClassCastException");

        map.put("算术异常", "ArithmeticException");

        map.put("字符串索引越界异常", "StringIndexOutOfBoundsException");


        /* ===== 8. 常用错误 ===== */
        map.put("内存溢出错误", "OutOfMemoryError");

        map.put("栈溢出错误", "StackOverflowError");

        map.put("无类定义错误", "NoClassDefFoundError");


        /* ===== 9. 字符串构建 ===== */
        map.put("字符串构建器", "StringBuilder");

        map.put("字符串缓冲", "StringBuffer");


        /* ===== 10. 注解 ===== */
        map.put("@覆盖", "@Override");

        map.put("@已过时", "@Deprecated");

        map.put("@抑制警告", "@SuppressWarnings");
        
        map.put("@函数式接口", "@FunctionalInterface");

        map.put("@安全可变参数", "@SafeVarargs");


        /* ===== 11. 工具小类 ===== */
        map.put("空占位", "Void");

    }
}
