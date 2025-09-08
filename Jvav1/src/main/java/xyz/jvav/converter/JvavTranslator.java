package xyz.jvav.converter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 将 .jvav 源文件翻译为标准 .java 文件的入口。
 * 规则：
 *  - 读取每个 .jvav（指定编码），用 CnKeywordRewriter 重写为 Java 源。
 *  - 在 genSrcDir 下按相对路径落盘，扩展名改为 .java。
 *  - 相对路径以最匹配的源根（sources 中最长前缀）计算；若未匹配则仅保留文件名。
 */
public final class JvavTranslator {
    private JvavTranslator() {}

    public static List<Path> translateJvavToJava(List<Path> jvavFiles, List<Path> sourceRoots, Path genSrcDir, String encoding) throws IOException {
        List<Path> out = new ArrayList<>();
        Charset cs = Charset.forName(encoding != null ? encoding : "UTF-8");
        for (Path jf : jvavFiles) {
            Path rel = relativizeAgainstRoots(jf.toAbsolutePath().normalize(), sourceRoots);
            if (rel == null) rel = jf.getFileName();
            String name = rel.getFileName().toString();
            int dot = name.lastIndexOf('.');
            String base = dot > 0 ? name.substring(0, dot) : name;
            Path leaf = (rel.getParent() == null) ? Paths.get(base + ".java") : rel.getParent().resolve(base + ".java");
            Path target = genSrcDir.resolve(leaf);
            Files.createDirectories(target.getParent());
            byte[] bytes = Files.readAllBytes(jf);
            String src = new String(bytes, cs);
            String java = CnKeywordRewriter.rewrite(src);
            Files.write(target, java.getBytes(cs));
            out.add(target);
        }
        return out;
    }

    private static Path relativizeAgainstRoots(Path file, List<Path> roots) {
        if (roots == null || roots.isEmpty()) return null;
        Path bestRoot = null;
        for (Path r : roots) {
            if (r == null) continue;
            Path rn = r.toAbsolutePath().normalize();
            if (file.startsWith(rn)) {
                if (bestRoot == null || rn.getNameCount() > bestRoot.getNameCount()) bestRoot = rn;
            }
        }
        return bestRoot != null ? bestRoot.relativize(file) : null;
    }
}

