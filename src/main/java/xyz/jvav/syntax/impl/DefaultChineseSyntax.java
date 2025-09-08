package xyz.jvav.syntax.impl;

import xyz.jvav.syntax.KeywordProvider;
import xyz.jvav.syntax.PunctuationProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认中文语法：关键字与全角符号映射。
 */
public final class DefaultChineseSyntax implements KeywordProvider, PunctuationProvider {
    private static final Map<String, String> KEYWORDS;
    private static final Map<Character, Character> FULLWIDTH;

    static {
        Map<String,String> kw = new HashMap<>();
        // 访问控制与类型
        kw.put("公共", "public");
        kw.put("受保护", "protected");
        kw.put("私有", "private");
        kw.put("类", "class");
        kw.put("接口", "interface");
        kw.put("枚举", "enum");
        kw.put("抽象", "abstract");
        kw.put("最终", "final");
        kw.put("静态", "static");
        kw.put("严格fp", "strictfp");
        kw.put("本地", "native");
        kw.put("短暂", "transient");
        kw.put("易变", "volatile");
        // 继承实现
        kw.put("继承", "extends");
        kw.put("实现", "implements");
        // 流程控制
        kw.put("如果", "if");
        kw.put("否则", "else");
        kw.put("选择", "switch");
        kw.put("情况", "case");
        kw.put("默认", "default");
        kw.put("当", "while");
        kw.put("做", "do");
        kw.put("对于", "for");
        kw.put("继续", "continue");
        kw.put("跳出", "break");
        kw.put("返回", "return");
        kw.put("断言", "assert");
        kw.put("属于", "instanceof");
        // 异常
        kw.put("尝试", "try");
        kw.put("捕获", "catch");
        kw.put("最终块", "finally");
        kw.put("抛出", "throw");
        kw.put("抛出异常", "throws");
        // 其他
        kw.put("包", "package");
        kw.put("导入", "import");
        kw.put("新建", "new");
        kw.put("本", "this");
        kw.put("父类", "super");
        kw.put("空返回", "void");
        kw.put("空", "null");
        kw.put("真", "true");
        kw.put("假", "false");
        KEYWORDS = Collections.unmodifiableMap(kw);

        Map<Character,Character> fw = new HashMap<>();
        fw.put('（', '('); fw.put('）', ')');
        fw.put('｛', '{'); fw.put('｝', '}');
        fw.put('［', '['); fw.put('］', ']');
        fw.put('，', ','); fw.put('；', ';');
        fw.put('＜', '<'); fw.put('＞', '>');
        fw.put('＝', '='); fw.put('＋', '+');
        fw.put('－', '-'); fw.put('＊', '*');
        fw.put('／', '/'); fw.put('｜', '|');
        fw.put('＆', '&'); fw.put('！', '!');
        fw.put('？', '?'); fw.put('：', ':');
        fw.put('．', '.');
        FULLWIDTH = Collections.unmodifiableMap(fw);
    }

    @Override
    public Map<String, String> getKeywordMap() { return KEYWORDS; }

    @Override
    public Map<Character, Character> getFullwidthMap() { return FULLWIDTH; }
}

