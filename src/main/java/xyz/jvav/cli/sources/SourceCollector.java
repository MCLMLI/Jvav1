package xyz.jvav.cli.sources;

import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public final class SourceCollector {
    private SourceCollector() {}

    public static final class Sources {
        public final List<Path> javaFiles;
        public final List<Path> jvavFiles;
        public Sources(List<Path> javaFiles, List<Path> jvavFiles) {
            this.javaFiles = javaFiles;
            this.jvavFiles = jvavFiles;
        }
    }

    public static Sources collect(List<Path> inputs) {
        List<Path> javaFiles = new ArrayList<>();
        List<Path> jvavFiles = new ArrayList<>();
        for (Path s : inputs) {
            if (s == null || !Files.exists(s)) continue;
            if (Files.isDirectory(s)) {
                try (Stream<Path> stream = walkSafe(s)) {
                    stream.forEach(p -> {
                        String n = p.toString();
                        if (n.endsWith(".java")) javaFiles.add(p);
                        else if (n.endsWith(".jvav")) jvavFiles.add(p);
                    });
                } catch (Exception ignored) {}
            } else {
                String n = s.toString();
                if (n.endsWith(".java")) javaFiles.add(s);
                else if (n.endsWith(".jvav")) jvavFiles.add(s);
            }
        }
        return new Sources(javaFiles, jvavFiles);
    }

    private static Stream<Path> walkSafe(Path dir) {
        try { return Files.walk(dir); } catch (Exception e) { return Stream.empty(); }
    }
}

