package xyz.jvav.syntax;

/**
 * 文档注释规范器：将 Javadoc 中文标签/内联标签规范为标准英文形式；
 * 实现可按需扩展更“亲民”的别名。
 */
public interface DocCommentNormalizer {
    /**
     * 传入不含注释起止标记的主体内容，返回规范化后的主体文本。
     */
    String normalize(String body);
}
