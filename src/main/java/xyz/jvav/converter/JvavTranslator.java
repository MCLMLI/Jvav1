package xyz.jvav.converter;

import xyz.jvav.syntax.SyntaxRegistry;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

public final class JvavTranslator {
    private JvavTranslator() {}

    /**
     * 将 .jvav 源文件翻译为 .java 文件。
     * @param jvavFiles 需要翻译的 .jvav 列表
     * @param sourceRoots 源根（用于保持相对路径），可包含文件与目录；仅目录参与相对路径推断
     * @param genSrcDir 输出的生成源码根目录
     * @param encoding 读写文件编码
     * @return 生成的 .java 文件路径列表
     */
    public static List<Path> translateJvavToJava(List<Path> jvavFiles, List<Path> sourceRoots, Path genSrcDir, String encoding) throws IOException {
        Objects.requireNonNull(jvavFiles, "jvavFiles");
        Objects.requireNonNull(sourceRoots, "sourceRoots");
        Objects.requireNonNull(genSrcDir, "genSrcDir");
        if (encoding == null || encoding.trim().isEmpty()) encoding = "UTF-8";
        Charset cs = Charset.forName(encoding);

        SyntaxRegistry reg = SyntaxRegistry.load();
        Map<String, String> map = new LinkedHashMap<>(reg.mappings());
        List<Path> out = new ArrayList<>();

        // 预处理：为通用匹配准备按长度降序的键列表
        List<String> keysByLenDesc = new ArrayList<>(map.keySet());
        keysByLenDesc.sort((a,b) -> Integer.compare(b.length(), a.length()));

        for (Path in : jvavFiles) {
            if (in == null || !Files.exists(in)) continue;
            byte[] bytes = Files.readAllBytes(in);
            // 快速检测是否被 JAR/二进制覆盖
            if (looksLikeBinary(bytes)) {
                throw new IllegalArgumentException("源文件 " + in + " 看起来不是有效的 UTF-8 文本");
            }
            String content = new String(bytes, cs);
            if (containsReplacementOrNull(content)) {
                throw new IllegalArgumentException("源文件 " + in + " 似乎存在编码问题（出现替换符/空字节）。请以 UTF-8 无 BOM 保存，或指定 -encoding 与文件一致。");
            }
            String translated = translateContent(content, map, keysByLenDesc);

            Path target = resolveTargetPath(in, sourceRoots, genSrcDir);
            Files.createDirectories(target.getParent());
            Files.write(target, translated.getBytes(cs));
            out.add(target);
        }
        return out;
    }

    private static boolean looksLikeBinary(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return false;
        // ZIP/JAR 头 PK\u0003\u0004
        if (bytes.length >= 4 && bytes[0] == 'P' && bytes[1] == 'K' && bytes[2] == 3 && bytes[3] == 4) return true;
        return false;
    }

    private static boolean containsReplacementOrNull(String s) {
        if (s == null) return false;
        // \ufffd 为解码替代符；\u0000 为空字节痕迹
        return s.indexOf('\ufffd') >= 0 || s.indexOf('\u0000') >= 0 || s.indexOf('\0') >= 0;
    }

    private static Path resolveTargetPath(Path input, List<Path> roots, Path genRoot) {
        Path abs = input.toAbsolutePath().normalize();
        Path bestRoot = null;
        int bestLen = -1;
        for (Path r0 : roots) {
            if (r0 == null) continue;
            Path r = r0.toAbsolutePath().normalize();
            if (Files.isDirectory(r) && abs.startsWith(r)) {
                int len = r.getNameCount();
                if (len > bestLen) { bestLen = len; bestRoot = r; }
            }
        }
        Path rel;
        if (bestRoot != null) {
            rel = bestRoot.relativize(abs);
        } else {
            rel = input.getFileName();
        }
        String name = rel.toString();
        if (name.endsWith(".jvav")) name = name.substring(0, name.length() - 5) + ".java";
        // 将路径中的 .jvav 文件名替换为 .java，保持子目录
        Path relParent = rel.getParent();
        Path outRel = (relParent != null) ? relParent.resolve(name) : Paths.get(name);
        return genRoot.resolve(outRel).normalize();
    }

    private enum State { CODE, LINE_COMMENT, BLOCK_COMMENT, STR_DQ, STR_SQ }

    private static String translateContent(String s, Map<String,String> map, List<String> keysByLenDesc) {
        // 去除文件级 BOM，防止生成的 .java 开头出现非法字符
        if (s != null && !s.isEmpty() && s.charAt(0) == '\ufeff') {
            s = s.substring(1);
        }
        StringBuilder out = new StringBuilder(s.length() + 64);
        State st = State.CODE;
        int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);
            switch (st) {
                case CODE: {
                    // 注释开始
                    if (c == '/') {
                        if (i + 1 < n) {
                            char c2 = s.charAt(i + 1);
                            if (c2 == '/') { out.append("//"); i += 2; st = State.LINE_COMMENT; break; }
                            if (c2 == '*') { out.append("/*"); i += 2; st = State.BLOCK_COMMENT; break; }
                        }
                    }
                    // 字符串/字符字面量
                    if (c == '"') { out.append(c); i++; st = State.STR_DQ; break; }
                    if (c == '\'') { out.append(c); i++; st = State.STR_SQ; break; }

                    // 标识符：按 Java 标识符边界处理（避免局部替换）
                    if (Character.isJavaIdentifierStart(c)) {
                        int j = i + 1;
                        while (j < n && Character.isJavaIdentifierPart(s.charAt(j))) j++;
                        String ident = s.substring(i, j);
                        String mapped = map.get(ident);
                        if (mapped != null) out.append(mapped); else out.append(ident);
                        i = j; break;
                    }

                    // 非标识符：尝试最长匹配（处理全角符号等）
                    String matched = tryMatch(s, i, keysByLenDesc);
                    if (matched != null) {
                        out.append(map.get(matched));
                        i += matched.length();
                        break;
                    }

                    // 默认逐字输出
                    out.append(c);
                    i++;
                    break;
                }
                case LINE_COMMENT: {
                    out.append(c);
                    i++;
                    if (c == '\n' || c == '\r') st = State.CODE;
                    break;
                }
                case BLOCK_COMMENT: {
                    out.append(c);
                    if (c == '*' && i + 1 < n && s.charAt(i + 1) == '/') { out.append('/'); i += 2; st = State.CODE; }
                    else { i++; }
                    break;
                }
                case STR_DQ: {
                    out.append(c);
                    i++;
                    if (c == '\\' && i < n) { out.append(s.charAt(i)); i++; }
                    else if (c == '"') { st = State.CODE; }
                    break;
                }
                case STR_SQ: {
                    out.append(c);
                    i++;
                    if (c == '\\' && i < n) { out.append(s.charAt(i)); i++; }
                    else if (c == '\'') { st = State.CODE; }
                    break;
                }
            }
        }
        return out.toString();
    }

    private static String tryMatch(String s, int pos, List<String> keysByLenDesc) {
        int n = s.length();
        for (String k : keysByLenDesc) {
            int klen = k.length();
            if (klen <= 0) continue;
            if (pos + klen > n) continue;
            // 如果是标识符型键，确保不在标识符中间（此分支通常不会触发，因为 CODE 分支已处理）
            if (isIdentifierLike(k)) {
                // 起始不能是标识符的一部分
                if (pos > 0 && Character.isJavaIdentifierPart(s.charAt(pos - 1))) continue;
                // 结束也不能连着标识符
                if (pos + klen < n && Character.isJavaIdentifierPart(s.charAt(pos + klen))) continue;
            }
            if (s.regionMatches(pos, k, 0, klen)) return k;
        }
        return null;
    }

    private static boolean isIdentifierLike(String k) {
        if (k.isEmpty()) return false;
        if (!Character.isJavaIdentifierStart(k.charAt(0))) return false;
        for (int i = 1; i < k.length(); i++) if (!Character.isJavaIdentifierPart(k.charAt(i))) return false;
        return true;
    }
}
