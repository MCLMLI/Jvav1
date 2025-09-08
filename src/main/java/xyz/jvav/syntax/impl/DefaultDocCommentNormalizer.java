package xyz.jvav.syntax.impl;

import xyz.jvav.syntax.DocCommentNormalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * 将 Javadoc 中文标签/更亲民别名 规范化为标准英文标签；
 * 同时处理常见内联标签的中文写法（如 {@链接} -> {@link}）。
 */
public final class DefaultDocCommentNormalizer implements DocCommentNormalizer {
    private static final Map<String,String> LINE_TAGS = new HashMap<>();
    private static final Map<String,String> INLINE_TAGS = new HashMap<>();

    static {
        // 行首标签（以 @ 开头）
        LINE_TAGS.put("@作者", "@author");
        LINE_TAGS.put("@过时", "@deprecated");
        LINE_TAGS.put("@弃用", "@deprecated");
        LINE_TAGS.put("@异常", "@exception");
        LINE_TAGS.put("@抛出异常", "@throws");
        LINE_TAGS.put("@参数", "@param");
        LINE_TAGS.put("@形参", "@param");
        LINE_TAGS.put("@返回", "@return");
        LINE_TAGS.put("@返回值", "@return");
        LINE_TAGS.put("@参见", "@see");
        LINE_TAGS.put("@序列化", "@serial");
        LINE_TAGS.put("@序列化数据", "@serialData");
        LINE_TAGS.put("@序列化字段", "@serialField");
        LINE_TAGS.put("@自版本", "@since");
        LINE_TAGS.put("@起始于", "@since");
        LINE_TAGS.put("@版本", "@version");
        // 英文自保
        LINE_TAGS.put("@author", "@author");
        LINE_TAGS.put("@deprecated", "@deprecated");
        LINE_TAGS.put("@exception", "@exception");
        LINE_TAGS.put("@throws", "@throws");
        LINE_TAGS.put("@param", "@param");
        LINE_TAGS.put("@return", "@return");
        LINE_TAGS.put("@see", "@see");
        LINE_TAGS.put("@serial", "@serial");
        LINE_TAGS.put("@serialData", "@serialData");
        LINE_TAGS.put("@serialField", "@serialField");
        LINE_TAGS.put("@since", "@since");
        LINE_TAGS.put("@version", "@version");

        // 内联标签（以 {@ 开头）
        INLINE_TAGS.put("{@链接", "{@link");
        INLINE_TAGS.put("{@参照", "{@link");
        INLINE_TAGS.put("{@链接文本", "{@linkplain");
        INLINE_TAGS.put("{@纯文本链接", "{@linkplain");
        INLINE_TAGS.put("{@继承文档", "{@inheritDoc");
        INLINE_TAGS.put("{@文档根", "{@docRoot");
        INLINE_TAGS.put("{@常量值", "{@value");
        // 英文自保
        INLINE_TAGS.put("{@link", "{@link");
        INLINE_TAGS.put("{@linkplain", "{@linkplain");
        INLINE_TAGS.put("{@inheritDoc", "{@inheritDoc");
        INLINE_TAGS.put("{@docRoot", "{@docRoot");
        INLINE_TAGS.put("{@value", "{@value");
    }

    @Override
    public String normalize(String body) {
        if (body == null || body.isEmpty()) return body;
        String[] lines = body.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        StringBuilder out = new StringBuilder(body.length() + 32);
        for (String raw : lines) {
            String line = stripLeadingStar(raw);
            // 内联标签替换
            for (Map.Entry<String,String> e : INLINE_TAGS.entrySet()) {
                if (line.contains(e.getKey())) line = line.replace(e.getKey(), e.getValue());
            }
            // 行首 @ 标签替换
            int idxAt = firstAtIndex(line);
            if (idxAt == 0) {
                int sp = spanTagEnd(line, 1);
                String tag = line.substring(0, sp);
                String mapped = LINE_TAGS.get(tag);
                if (mapped != null && !mapped.equals(tag)) {
                    line = mapped + line.substring(sp);
                }
            }
            out.append(line).append('\n');
        }
        // 去掉末尾多余换行的简单策略：交由调用方按行重组时处理；此处直接返回。
        return out.toString().replaceAll("\n$", "");
    }

    private static String stripLeadingStar(String s) {
        int i = 0, n = s.length();
        while (i < n && Character.isWhitespace(s.charAt(i))) i++;
        if (i < n && s.charAt(i) == '*') {
            i++;
            if (i < n && s.charAt(i) == ' ') i++;
        }
        return s.substring(i);
    }

    private static int firstAtIndex(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '@') return i;
            if (!Character.isWhitespace(c) && c != '*') return -1;
        }
        return -1;
    }

    private static int spanTagEnd(String s, int start) {
        int i = start;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) break;
            i++;
        }
        return i;
    }
}

