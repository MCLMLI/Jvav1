package xyz.jvav.cli.compile;

import java.io.*;
import java.nio.file.*;
import java.util.jar.*;
import java.util.stream.Stream;

public final class JarPacker {
    private JarPacker() {}

    public static void createJar(Path classesDir, Path outJar, String mainClass) throws IOException {
        Manifest mf = new Manifest();
        Attributes attrs = mf.getMainAttributes();
        attrs.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (mainClass != null && !mainClass.isEmpty()) {
            attrs.put(Attributes.Name.MAIN_CLASS, mainClass);
        }
        Files.createDirectories(outJar.getParent());
        try (OutputStream fos = Files.newOutputStream(outJar);
             JarOutputStream jos = new JarOutputStream(fos, mf)) {
            final int rootLen = classesDir.toAbsolutePath().toString().length() + 1;
            try (Stream<Path> stream = Files.walk(classesDir)) {
                stream.filter(Files::isRegularFile).forEach(p -> {
                    String rel = p.toAbsolutePath().toString().substring(rootLen).replace('\\', '/');
                    try (InputStream in = Files.newInputStream(p)) {
                        JarEntry e = new JarEntry(rel);
                        try {
                            jos.putNextEntry(e);
                            byte[] buf = new byte[8192];
                            int r;
                            while ((r = in.read(buf)) != -1) {
                                jos.write(buf, 0, r);
                            }
                            jos.closeEntry();
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    } catch (IOException ex) {
                        throw new UncheckedIOException(ex);
                    }
                });
            }
        }
    }
}

