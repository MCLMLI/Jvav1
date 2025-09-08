package xyz.jvav.syntax;

import xyz.jvav.syntax.impl.DefaultChineseSyntax;
import xyz.jvav.syntax.impl.DefaultDocCommentNormalizer;

/**
 * 语法定义注册中心：集中管理当前生效的关键字/符号/文档注释规范器。
 */
public final class SyntaxRegistry {
    private static volatile KeywordProvider keywordProvider = new DefaultChineseSyntax();
    private static volatile PunctuationProvider punctuationProvider = new DefaultChineseSyntax();
    private static volatile DocCommentNormalizer docCommentNormalizer = new DefaultDocCommentNormalizer();

    private SyntaxRegistry() {}

    public static KeywordProvider keywords() { return keywordProvider; }
    public static PunctuationProvider punctuation() { return punctuationProvider; }
    public static DocCommentNormalizer doc() { return docCommentNormalizer; }

    // 允许在需要时替换（例如将来支持多语言/方言切换）
    public static void setKeywordProvider(KeywordProvider kp) { if (kp != null) keywordProvider = kp; }
    public static void setPunctuationProvider(PunctuationProvider pp) { if (pp != null) punctuationProvider = pp; }
    public static void setDocCommentNormalizer(DocCommentNormalizer n) { if (n != null) docCommentNormalizer = n; }
}
