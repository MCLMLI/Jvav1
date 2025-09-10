package xyz.jvav.cli.args;

import xyz.jvav.cli.config.Config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ArgsParser {
    private ArgsParser() {}

    public static Config parse(String[] rawArgs) {
        if (rawArgs == null || rawArgs.length == 0) return null;
        List<String> args = expandAtFiles(rawArgs);

        Config c = new Config();
        for (int i = 0; i < args.size(); i++) {
            String a0 = args.get(i);
            if (a0 == null) continue;

            String a0trim = a0.trim();
            if ("-".equals(a0trim) || "—".equals(a0trim) || "/".equals(a0trim)) {
                if (i + 1 < args.size()) {
                    String nxt = args.get(i + 1);
                    if (nxt != null) {
                        String n = nxt.trim().toLowerCase(Locale.ROOT);
                        if ("help".equals(n) || "?".equals(n) || "帮助".equals(n)) {
                            return null; // 显示 usage
                        }
                    }
                }
            }
            if ("/?".equals(a0trim) || "-?".equals(a0trim) || "--help".equalsIgnoreCase(a0trim) || "help".equalsIgnoreCase(a0trim)) {
                return null;
            }

            // -J* 直接标记忽略提示
            if (a0.startsWith("-J")) { c.seenJFlags = true; continue; }

            String a = Aliases.normalize(a0);
            if (!a.startsWith("-")) { c.sources.add(Paths.get(a)); continue; }

            String valueInline = null;
            int eq = a.indexOf('=');
            if (eq > 0) { valueInline = a.substring(eq + 1); a = a.substring(0, eq); }

            switch (a) {
                case "-h":
                case "--help":
                case "-help":
                    return null;
                case "-version":
                    c.printVersion = true; break;
                case "--keep-classes":
                    c.keepClassesDir = true; break;
                case "-v":
                case "-verbose":
                    c.verbose = true; break;
                case "-nowarn":
                case "-deprecation":
                case "-Werror":
                case "-parameters":
                    c.extraOptions.add(a); break;
                case "-main": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.mainClass = v; break; }
                case "-jar":
                case "-o": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.outputJar = Paths.get(v); break; }
                case "-d": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.classesDir = Paths.get(v); break; }
                case "-classpath":
                case "-cp": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.classpath = v; break; }
                case "-encoding": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.encoding = v; break; }
                case "-gensrc": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.genSrcDir = Paths.get(v); break; }
                case "--release": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.release = parseIntOrNull(v); break; }
                case "-source": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.sourceLevel = parseIntOrNull(v); break; }
                case "-target": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.targetLevel = parseIntOrNull(v); break; }
                case "-Xlint": {
                    String v = peekValue(args, i, valueInline);
                    if (v != null && !v.startsWith("-")) { i++; c.extraOptions.add("-Xlint:" + v); }
                    else { c.extraOptions.add("-Xlint"); }
                    break; }
                case "-g": {
                    String v = peekValue(args, i, valueInline);
                    if (v != null && !v.startsWith("-")) { i++; c.extraOptions.add("-g:" + v); }
                    else { c.extraOptions.add("-g"); }
                    break; }
                case "-proc": {
                    String v = takeValue(args, ++i, valueInline); if (v == null) return null; c.extraOptions.add("-proc:" + v); break; }
                default: {
                    if (Aliases.takesValue(a)) {
                        String v = takeValue(args, ++i, valueInline); if (v == null) return null;
                        c.extraOptions.add(a); c.extraOptions.add(v);
                    } else {
                        c.extraOptions.add(a);
                    }
                }
            }
        }
        if (c.encoding == null) c.encoding = "UTF-8";
        if (c.release == null) c.release = 8;
        if (c.sourceLevel == null) c.sourceLevel = 8;
        if (c.targetLevel == null) c.targetLevel = 8;
        return (c.printVersion || !c.sources.isEmpty()) ? c : null;
    }

    private static String takeValue(List<String> args, int idx, String inline) {
        if (inline != null) return inline;
        if (idx < args.size()) return args.get(idx);
        return null;
    }

    private static String peekValue(List<String> args, int i, String inline) {
        if (inline != null) return inline;
        if (i + 1 < args.size()) return args.get(i + 1);
        return null;
    }

    private static List<String> expandAtFiles(String[] args) {
        List<String> out = new ArrayList<>();
        for (String a : args) {
            if (a != null && a.startsWith("@") && a.length() > 1) {
                Path f = Paths.get(a.substring(1));
                if (Files.exists(f)) {
                    try {
                        String content = new String(Files.readAllBytes(f), StandardCharsets.UTF_8);
                        tokenizeArgfile(content, out);
                        continue;
                    } catch (IOException ignored) {}
                }
            }
            out.add(a);
        }
        return out;
    }

    private static void tokenizeArgfile(String content, List<String> out) {
        boolean inQuote = false; char quote = '"';
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (inQuote) {
                if (c == quote) { inQuote = false; }
                else if (c == '\\' && i + 1 < content.length()) { i++; sb.append(content.charAt(i)); }
                else { sb.append(c); }
            } else {
                if (c == '"' || c == '\'' ) { inQuote = true; quote = c; }
                else if (Character.isWhitespace(c)) { if (sb.length() > 0) { out.add(sb.toString()); sb.setLength(0);} }
                else { sb.append(c); }
            }
        }
        if (sb.length() > 0) out.add(sb.toString());
    }

    private static Integer parseIntOrNull(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }
}

