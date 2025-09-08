package xyz.jvav.cli.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Config {
    public List<Path> sources = new ArrayList<>();
    public Path outputJar;
    public String classpath;
    public String mainClass;
    public String encoding;
    public Integer release;
    public Integer sourceLevel;
    public Integer targetLevel;
    public Path classesDir;
    public Path genSrcDir; // 导出生成的 Java 源
    public boolean keepClassesDir = false;
    public boolean verbose = false;
    public boolean printVersion = false;
    public boolean seenJFlags = false;
    public List<String> extraOptions = new ArrayList<>();
}

