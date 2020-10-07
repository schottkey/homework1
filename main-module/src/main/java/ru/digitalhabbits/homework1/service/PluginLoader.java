package ru.digitalhabbits.homework1.service;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import org.slf4j.Logger;
import ru.digitalhabbits.homework1.plugin.PluginInterface;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.slf4j.LoggerFactory.getLogger;

public class PluginLoader {
    private static final Logger logger = getLogger(PluginLoader.class);

    private static final String PLUGIN_EXT = ".jar";
    private static final String PACKAGE_TO_SCAN = "ru.digitalhabbits.homework1.plugin";

    @Nonnull
    @SuppressWarnings("unchecked")
    public List<Class<? extends PluginInterface>> loadPlugins(@Nonnull String pluginDirName) {
        final List<Class<? extends PluginInterface>> plugins = newArrayList();

        final File dir = new File("." + File.separator + pluginDirName);
        final File[] files = dir.listFiles();
        if (files == null) {
            return plugins;
        }

        final URL[] urls = Arrays.stream(files)
                .filter(file -> file.getName().endsWith(PLUGIN_EXT))
                .map(this::getUrlFromFile)
                .filter(Objects::nonNull)
                .toArray(URL[]::new);
        final URLClassLoader loader = URLClassLoader.newInstance(urls);

        final Set<String> classNames = newHashSet();
        try {
            Set<ClassInfo> classes = ClassPath.from(loader)
                    .getTopLevelClasses(PACKAGE_TO_SCAN);
            classNames.addAll(classes
                    .stream()
                    .map(ClassInfo::getName)
                    .collect(Collectors.toSet()));
        } catch (IOException ignored) {
        }

        for (String className : classNames) {
            try {
                Class<?> cls = loader.loadClass(className);
                if (!cls.isInterface() && PluginInterface.class.isAssignableFrom(cls)) {
                    plugins.add((Class<? extends PluginInterface>) cls);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return plugins;
    }

    @Nullable
    private URL getUrlFromFile(@Nonnull File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            return null;
        }
    }

}
