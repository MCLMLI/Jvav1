package xyz.jvav.cli.args;

import java.util.*;

public final class Aliases {
    private Aliases() {}

    private static final String[] OPTS_TAKING_VALUE = {
            "-d","-classpath","-cp","-encoding","-source","-target","--release",
            "-processor","-processorpath","-s","-sourcepath","--module-path","--add-modules",
            "-Xlint","-A","-o","-jar","-main",
            "-implicit","-bootclasspath",
            // 中文别名（解析后会转换为标准项）
            "-输出目录","--输出目录","-类路径","--类路径","-依赖路径","-库路径","-依赖","-字符集","-文件编码",
            "-源","--源","-源级别","-语法级别","-语言级别",
            "-目标","--目标","-目标级别","-字节码级别",
            "-发布","--发布","-发布版本","-兼容版本",
            "-主类","--主类","-入口类","-入口",
            "-打包","--打包","-生成jar","-输出jar",
            "-注解处理器","-注解处理器路径","-源码路径","-生成源目录","-模块路径","-添加模块",
            "-gensrc","-生成java源","-导出java源","-导出源码"
    };

    private static final Map<String,String> CN_ALIAS = new HashMap<>();
    static {
        // 类路径
        CN_ALIAS.put("-类路径", "-classpath");
        CN_ALIAS.put("--类路径", "-classpath");
        CN_ALIAS.put("-依赖路径", "-classpath");
        CN_ALIAS.put("-库路径", "-classpath");
        CN_ALIAS.put("-依赖", "-classpath");
        // 输出目录
        CN_ALIAS.put("-输出目录", "-d");
        CN_ALIAS.put("--输出目录", "-d");
        CN_ALIAS.put("-编译输出", "-d");
        CN_ALIAS.put("-类输出", "-d");
        // 编码
        CN_ALIAS.put("-编码", "-encoding");
        CN_ALIAS.put("--编码", "-encoding");
        CN_ALIAS.put("-字符集", "-encoding");
        CN_ALIAS.put("-文件编码", "-encoding");
        // source/target/release
        CN_ALIAS.put("-源", "-source");
        CN_ALIAS.put("--源", "-source");
        CN_ALIAS.put("-源级别", "-source");
        CN_ALIAS.put("-语法级别", "-source");
        CN_ALIAS.put("-语言级别", "-source");
        CN_ALIAS.put("-目标", "-target");
        CN_ALIAS.put("--目标", "-target");
        CN_ALIAS.put("-目标级别", "-target");
        CN_ALIAS.put("-字节码级别", "-target");
        CN_ALIAS.put("-发布", "--release");
        CN_ALIAS.put("--发布", "--release");
        CN_ALIAS.put("-发布版本", "--release");
        CN_ALIAS.put("-兼容版本", "--release");
        // 打包与主类
        CN_ALIAS.put("-主类", "-main");
        CN_ALIAS.put("--主类", "-main");
        CN_ALIAS.put("-入口类", "-main");
        CN_ALIAS.put("-入口", "-main");
        CN_ALIAS.put("-打包", "-jar");
        CN_ALIAS.put("--打包", "-jar");
        CN_ALIAS.put("-生成jar", "-jar");
        CN_ALIAS.put("-输出jar", "-jar");
        // 详细/保持
        CN_ALIAS.put("-详细", "-verbose");
        CN_ALIAS.put("--详细", "-verbose");
        CN_ALIAS.put("-详细日志", "-verbose");
        CN_ALIAS.put("-显示详情", "-verbose");
        CN_ALIAS.put("-保持编译目录", "--keep-classes");
        CN_ALIAS.put("--保持编译目录", "--keep-classes");
        CN_ALIAS.put("-保留中间产物", "--keep-classes");
        CN_ALIAS.put("-保留classes", "--keep-classes");
        // 帮助/版本
        CN_ALIAS.put("-版本", "-version");
        CN_ALIAS.put("--版本", "-version");
        CN_ALIAS.put("-帮助", "-help");
        CN_ALIAS.put("--帮助", "-help");
        // 注解与模块与源码
        CN_ALIAS.put("-注解处理器", "-processor");
        CN_ALIAS.put("-注解处理器路径", "-processorpath");
        CN_ALIAS.put("-源码路径", "-sourcepath");
        CN_ALIAS.put("-生成源目录", "-s");
        CN_ALIAS.put("-模块路径", "--module-path");
        CN_ALIAS.put("-添加模块", "--add-modules");
        // 其他常见
        CN_ALIAS.put("-不警告", "-nowarn");
        CN_ALIAS.put("-关闭警告", "-nowarn");
        CN_ALIAS.put("-弃用警告", "-deprecation");
        CN_ALIAS.put("-显示弃用", "-deprecation");
        CN_ALIAS.put("-调试信息", "-g");
        CN_ALIAS.put("-调试", "-g");
        CN_ALIAS.put("-警告即错误", "-Werror");
        CN_ALIAS.put("-将警告视为错误", "-Werror");
        CN_ALIAS.put("-保留参数名", "-parameters");
        CN_ALIAS.put("-引导类路径", "-bootclasspath");
        CN_ALIAS.put("-隐式编译", "-implicit");
    }

    public static String normalize(String opt) {
        if (opt == null) return null;
        String mapped = CN_ALIAS.get(opt);
        return mapped != null ? mapped : opt;
    }

    public static boolean takesValue(String opt) {
        String n = normalize(opt);
        for (String k : OPTS_TAKING_VALUE) {
            if (normalize(k).equals(n)) return true;
        }
        return false;
    }
}

