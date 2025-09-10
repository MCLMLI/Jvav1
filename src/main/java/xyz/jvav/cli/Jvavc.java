package xyz.jvav.cli;

import xyz.jvav.cli.args.ArgsParser;
import xyz.jvav.cli.config.Config;
import xyz.jvav.cli.sources.SourceCollector;
import xyz.jvav.cli.compile.JavaCompilerRunner;
import xyz.jvav.cli.compile.JarPacker;
import xyz.jvav.cli.util.IOUtils;
import xyz.jvav.converter.JvavTranslator;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * jvavc：编译 Java/.jvav 并可选打包为 JAR（最低 Java 版本 8）。
 * 说明：.jvav 可写标准 Java 或 jvav 中文（可混用）；会先翻译为 .java 再统一交给 javac。
 */
public final class Jvavc {
    private static final int EXIT_OK = 0;
    private static final int EXIT_ARG_ERROR = 2;
    private static final int EXIT_COMPILE_ERROR = 3;
    private static final int EXIT_IO_ERROR = 4;

    private Jvavc() {}

    private static int run(Config cfg) throws IOException {
        if (cfg.sources.isEmpty()) {
            System.err.println("[jvavc] 未提供任何 --source");
            return EXIT_ARG_ERROR;
        }

        // 参数防呆：-jar/-o 必须是 .jar，且不得指向 .jvav 或与源文件冲突
        if (cfg.outputJar != null) {
            Path jar = cfg.outputJar.toAbsolutePath().normalize();
            String name = jar.getFileName() != null ? jar.getFileName().toString().toLowerCase(Locale.ROOT) : "";
            if (!name.endsWith(".jar")) {
                System.err.println("[jvavc] 错误：-jar/-打包 后应为 .jar 文件路径。例如：-jar build/hello.jar");
                return EXIT_ARG_ERROR;
            }
            for (Path srcArg : cfg.sources) {
                if (srcArg != null && Files.isRegularFile(srcArg)) {
                    Path sp = srcArg.toAbsolutePath().normalize();
                    if (sp.equals(jar)) {
                        System.err.println("[jvavc] 错误：输出 JAR 与源文件相同，疑似误把 -jar 指向了 .jvav 源。请改用：-jar <输出.jar> <源...>");
                        return EXIT_ARG_ERROR;
                    }
                }
            }
        }

        SourceCollector.Sources sc = SourceCollector.collect(cfg.sources);
        List<Path> javaFiles = new ArrayList<>(sc.javaFiles);
        List<Path> jvavFiles = new ArrayList<>(sc.jvavFiles);

        if (javaFiles.isEmpty() && jvavFiles.isEmpty()) {
            System.err.println("[jvavc] 未发现任何 .java 或 .jvav 文件");
            return EXIT_ARG_ERROR;
        }
        if (cfg.verbose) {
            System.out.println("[jvavc] 收集到 Java 源: " + javaFiles.size() + ", jvav 源: " + jvavFiles.size());
        }

        Path genSrcDir = null;
        if (!jvavFiles.isEmpty()) {
            genSrcDir = (cfg.genSrcDir != null) ? cfg.genSrcDir : Files.createTempDirectory("jvavc-gensrc-");
            if (cfg.verbose) System.out.println("[jvavc] 生成源目录: " + genSrcDir.toAbsolutePath());
            try {
                List<Path> generated = JvavTranslator.translateJvavToJava(jvavFiles, cfg.sources, genSrcDir, cfg.encoding);
                if (cfg.verbose) System.out.println("[jvavc] 生成 Java 源文件数量: " + generated.size());
                javaFiles.addAll(generated);
            } catch (IllegalArgumentException iae) {
                System.err.println("[jvavc] 错误：" + iae.getMessage());
                if (genSrcDir != null && cfg.genSrcDir == null) IOUtils.deleteRecursively(genSrcDir);
                return EXIT_ARG_ERROR;
            }
        }

        Path classesDir = cfg.classesDir != null ? cfg.classesDir : Files.createTempDirectory("jvavc-classes-");
        if (cfg.verbose) {
            System.out.println("[jvavc] 编译输出目录: " + classesDir.toAbsolutePath());
        }
        Files.createDirectories(classesDir);

        boolean compiled = JavaCompilerRunner.compile(javaFiles, classesDir, cfg);
        if (!compiled) {
            System.err.println("[jvavc] 编译失败");
            if (genSrcDir != null && cfg.genSrcDir == null) IOUtils.deleteRecursively(genSrcDir);
            return EXIT_COMPILE_ERROR;
        }

        if (cfg.outputJar != null) {
            if (cfg.verbose) {
                System.out.println("[jvavc] 生成 JAR: " + cfg.outputJar.toAbsolutePath());
            }
            JarPacker.createJar(classesDir, cfg.outputJar, cfg.mainClass);
        } else if (cfg.verbose) {
            System.out.println("[jvavc] 未指定 -jar/-o，已完成编译，跳过打包");
        }

        if (!cfg.keepClassesDir && (cfg.classesDir == null) && cfg.outputJar != null) {
            IOUtils.deleteRecursively(classesDir);
        }
        if (genSrcDir != null && cfg.genSrcDir == null) IOUtils.deleteRecursively(genSrcDir);
        if (cfg.verbose) System.out.println("[jvavc] 完成");
        return EXIT_OK;
    }

    private static void usage() {
        System.out.println(
            "jvavc1 - jvav1 / java8 编译与打包工具\n" +
            "\n" +
            "用法\n" +
            "  jvavc1 [options] <sources...>\n" +
            "  jvavc1 @argsfile\n" +
            "\n" +
            "说明\n" +
            "  <sources...> 可以是 .jvav 文件 或 .java 文件 或 目录（目录将递归搜索 .jvav / .java）。需要使用 JDK 运行（非 JRE）。\n" +
            "  JDK 9+ 环境优先使用 --release 8 生成 Java 8 兼容字节码；JDK 8 回退到 -source/-target 8。\n" +
            "\n" +
            "常用选项（中英等价，均可用）\n" +
            "  -d <dir>                      | -输出目录 <dir> | -编译输出 <dir>\n" +
            "  -cp/-classpath <path>         | -类路径/-依赖路径/-库路径/-依赖 <path>\n" +
            "  -encoding <enc>               | -编码/-字符集/-文件编码 <enc>（默认 UTF-8）\n" +
            "  -source <n>  | -源级别 <n>    ; -target <n>  | -目标级别 <n>\n" +
            "  --release <n> | -发布版本 <n>  （仅 JDK 9+ 生效）\n" +
            "  -nowarn | -不警告   ; -deprecation | -弃用警告   ; -Werror | -警告即错误\n" +
            "  -g[[:parts]] | -调试信息      ; -parameters | -保留参数名\n" +
            "\n" +
            "打包相关\n" +
            "  -jar <file> | -o <file>       | -打包/-生成jar/-输出jar <file>  生成可运行 JAR\n" +
            "  -main <FQCN>                  | -主类/-入口类/-入口 <FQCN>      写入清单 Main-Class\n" +
            "  --keep-classes                | -保持编译目录/保留中间产物     保留临时 classes 目录\n" +
            "  -gensrc <dir>                 | -生成java源/-导出java源 <dir>  导出 .jvav 翻译后的 .java\n" +
            "\n" +
            "注解处理 / 模块 / 源码路径\n" +
            "  -processor <list>             | -注解处理器 <list>（逗号分隔）\n" +
            "  -processorpath <path>         | -注解处理器路径 <path>\n" +
            "  -sourcepath <path>            | -源码路径 <path>\n" +
            "  -s <dir>                      | -生成源目录 <dir>\n" +
            "  --module-path <path>          | -模块路径 <path>\n" +
            "  --add-modules <mods>          | -添加模块 <mods>（逗号分隔）\n" +
            "  -implicit {none|class}        | -隐式编译 {none|class}\n" +
            "  -bootclasspath <path>         | -引导类路径 <path>\n" +
            "  -X* / -A* 等非常用选项将原样透传给 javac。\n" +
            "\n" +
            "兼容\n" +
            "  • 支持 @argsfile 参数文件（UTF-8，支持引号与空白分隔）。\n" +
            "  • -J* 选项属于 JVM 参数，jvavc 会忽略（请加到 java 命令上）。\n" +
            "  • 最低 JavaJDK 版本要求为 8\n" +
            "\n" +
            "退出码\n" +
            "  0 成功；2 参数错误；3 编译失败；4 I/O 异常。\n" +
            "\n" +
            "注意\n" +
            "  • 必须使用 JDK 运行（非 JRE）。\n" +
            "  • Windows 类路径分隔符 ';'，Linux/Mac 为 ':'.\n"
        );
    }

    public static void main(String[] args) {
        try {
            Config cfg = ArgsParser.parse(args);
            if (cfg == null) { usage(); System.exit(EXIT_ARG_ERROR); return; }
            if (cfg.seenJFlags) {
                System.err.println("[jvavc] 提示: -J* 选项由 JVM 处理，已忽略。");
            }
            if (cfg.printVersion) { System.out.println("jvavc version 1.0.0-SNAPSHOT"); System.exit(EXIT_OK); return; }
            int code = run(cfg);
            System.exit(code);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            System.exit(EXIT_IO_ERROR);
        }
    }
}
