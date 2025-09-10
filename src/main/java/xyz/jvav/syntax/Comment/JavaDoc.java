package xyz.jvav.syntax.Comment;

import xyz.jvav.syntax.SyntaxContributor;

import java.util.Map;

public final class JavaDoc implements SyntaxContributor {
    @Override
    public void contribute(Map<String, String> map) {

        map.put("@作者", "@author");

        map.put("@过时", "@deprecated");
        map.put("@已过时", "@deprecated");
        map.put("@过期", "@deprecated");
        map.put("@已过期", "@deprecated");

        map.put("@异常", "@exception");
        map.put("@抛出异常", "@exception");

        map.put("@参数", "@param");
        map.put("@方法参数", "@param");

        map.put("@返回", "@return");
        map.put("@返回值", "@return");
        map.put("@返回值类型", "@return");

        map.put("@参见", "@see");
        map.put("@参考", "@see");

        map.put("@序列化", "@serial");
        map.put("@序列化属性", "@serial");

        map.put("@序列化数据", "@serialData");

        map.put("@序列化组件", "@serialField");

        map.put("@引入", "@since");
        map.put("@引入变化", "@since");
        map.put("@引入特定变化", "@since");

        map.put("@版本", "@version");
        map.put("@类版本", "@version");

        map.put("{@根目录路径", "{@docRoot");
        map.put("{@根路径", "{@docRoot");
        map.put("{@根目录", "{@docRoot");
        map.put("{@根", "{@docRoot");

        map.put("{@父类注释", "{@inheritDoc");
        map.put("{@继承父类注释", "{@inheritDoc");
        map.put("{@继承注释", "{@inheritDoc");

        map.put("{@链接", "{@link");
        map.put("{@插入链接", "{@link");

        map.put("{@文本链接", "{@linkplain");
        map.put("{@插入文本链接", "{@linkplain");
        map.put("{@纯文本链接", "{@linkplain");
        map.put("{@插入纯文本链接", "{@linkplain");

        map.put("{@值", "{@value}");
        map.put("{@常量值", "{@value}");

    }
}
