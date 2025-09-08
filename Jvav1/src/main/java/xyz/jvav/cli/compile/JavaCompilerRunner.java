package xyz.jvav.cli.compile;

import xyz.jvav.cli.args.Aliases;
import xyz.jvav.cli.config.Config;

import javax.tools.*;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class JavaCompilerRunner {
    private JavaCompilerRunner() {}

    public static boolean compile(List<Path> sources, Path classesDir, Config cfg) throws java.io.IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            System.err.println("[jvavc] 未找到系统 JavaCompiler，请使用 JDK 运行（不是 JRE）");
            return false;
        }
        StandardJavaFileManager fm = compiler.getStandardFileManager(null, null, Charset.forName(cfg.encoding));
        fm.setLocation(StandardLocation.CLASS_OUTPUT, java.util.Collections.singletonList(classesDir.toFile()));

        Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromFiles(
                sources.stream().map(Path::toFile).collect(Collectors.toList())
        );

        List<String> options = new ArrayList<>();
        if (cfg.classpath != null && !cfg.classpath.trim().isEmpty()) {
            options.addAll(Arrays.asList("-classpath", cfg.classpath));
        }
        options.addAll(Arrays.asList("-encoding", cfg.encoding));

        int runtimeMajor = getRuntimeMajor();
        if (runtimeMajor >= 9 && cfg.release != null) {
            options.addAll(Arrays.asList("--release", String.valueOf(cfg.release)));
        } else {
            if (cfg.sourceLevel != null) options.addAll(Arrays.asList("-source", String.valueOf(cfg.sourceLevel)));
            if (cfg.targetLevel != null) options.addAll(Arrays.asList("-target", String.valueOf(cfg.targetLevel)));
        }
        if (cfg.verbose) options.add("-verbose");

        for (int i = 0; i < cfg.extraOptions.size(); i++) {
            String opt = cfg.extraOptions.get(i);
            String n = Aliases.normalize(opt);
            if ("-d".equals(n) || "-classpath".equals(n) || "-cp".equals(n) || "-encoding".equals(n) ||
                    "-source".equals(n) || "-target".equals(n) || "--release".equals(n) || "-verbose".equals(n)) {
                if (Aliases.takesValue(n)) i++; // 跳过值
                continue;
            }
            options.add(opt);
        }

        DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(
                new PrintWriter(System.out, true), fm, diags, options, null, units
        );
        boolean ok = task.call();
        for (Diagnostic<?> d : diags.getDiagnostics()) {
            System.err.println(formatDiag(d));
        }
        fm.close();
        return ok;
    }

    private static String formatDiag(Diagnostic<?> d) {
        String src = d.getSource() != null ? d.getSource().toString() : "<unknown>";
        return String.format("%s:%d:%d: %s: %s", src, d.getLineNumber(), d.getColumnNumber(), d.getKind(), d.getMessage(Locale.getDefault()));
    }

    public static int getRuntimeMajor() {
        String v = System.getProperty("java.specification.version", "8");
        if (v.contains(".")) {
            String[] ps = v.split("\\.");
            if (ps.length >= 2) {
                try { return Integer.parseInt(ps[1]); } catch (Exception ignored) {}
            }
        } else {
            try { return Integer.parseInt(v); } catch (Exception ignored) {}
        }
        return 8;
    }
}

