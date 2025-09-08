package xyz.jvav.converter;

import xyz.jvav.syntax.SyntaxRegistry;

import java.util.Map;

/**
 * 将包含中文关键字/全角符号的 .jvav 源码重写为标准 Java 源码。
 * 跳过字符串/字符与普通注释，仅在代码区替换；对文档注释做中文标签规范化。
 */
final class CnKeywordRewriter {
    private CnKeywordRewriter() {}

    static String rewrite(String src) {
        Map<String,String> KW = SyntaxRegistry.keywords().getKeywordMap();
        Map<Character,Character> FW = SyntaxRegistry.punctuation().getFullwidthMap();

        StringBuilder out = new StringBuilder(src.length() + 64);
        int n = src.length();
        int i = 0;
        final int DEFAULT = 0, LINE = 1, BLOCK = 2, DOC = 3, STR = 4, CH = 5;
        int state = DEFAULT;
        while (i < n) {
            char c = src.charAt(i);
            switch (state) {
                case 0: { // DEFAULT
                    if (c == '/') {
                        if (i + 1 < n) {
                            char c2 = src.charAt(i + 1);
                            if (c2 == '/') { out.append("//"); i += 2; state = LINE; break; }
                            if (c2 == '*') {
                                if (i + 2 < n && src.charAt(i + 2) == '*') { out.append("/**"); i += 3; state = DOC; break; }
                                out.append("/*"); i += 2; state = BLOCK; break;
                            }
                        }
                        out.append(c); i++; break;
                    } else if (c == '"') { out.append(c); i++; state = STR; break; }
                    else if (c == '\'') { out.append(c); i++; state = CH; break; }
                    else if (Character.isLetter(c) || isCJK(c)) {
                        int j = i + 1;
                        while (j < n) {
                            char cj = src.charAt(j);
                            if (Character.isLetterOrDigit(cj) || cj == '_' || isCJK(cj)) j++; else break;
                        }
                        String word = src.substring(i, j);
                        String rep = KW.get(word);
                        out.append(rep != null ? rep : word);
                        i = j;
                        break;
                    } else {
                        Character mapped = FW.get(c);
                        out.append(mapped != null ? mapped : c);
                        i++;
                        break;
                    }
                }
                case 1: { // LINE
                    out.append(c); i++;
                    if (c == '\n' || c == '\r') state = DEFAULT;
                    break;
                }
                case 2: { // BLOCK
                    out.append(c); i++;
                    if (c == '*' && i < n && src.charAt(i) == '/') { out.append('/'); i++; state = DEFAULT; }
                    break;
                }
                case 3: { // DOC
                    int start = i;
                    int end = src.indexOf("*/", i);
                    if (end < 0) { out.append(src.substring(start)); i = n; break; }
                    String body = src.substring(start, end);
                    String norm = SyntaxRegistry.doc().normalize(body);
                    out.append('\n');
                    String[] lines = norm.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
                    for (String line : lines) { out.append(" * ").append(line).append('\n'); }
                    out.append(" ");
                    out.append("*/");
                    i = end + 2; state = DEFAULT; break;
                }
                case 4: { // STR
                    out.append(c); i++;
                    if (c == '\\' && i < n) { out.append(src.charAt(i)); i++; }
                    else if (c == '"') state = DEFAULT;
                    break;
                }
                case 5: { // CHAR
                    out.append(c); i++;
                    if (c == '\\' && i < n) { out.append(src.charAt(i)); i++; }
                    else if (c == '\'') state = DEFAULT;
                    break;
                }
            }
        }
        return out.toString();
    }

    private static boolean isCJK(char c) {
        Character.UnicodeBlock b = Character.UnicodeBlock.of(c);
        return b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || b == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || b == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || b == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || b == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || b == Character.UnicodeBlock.GENERAL_PUNCTUATION;
    }
}

