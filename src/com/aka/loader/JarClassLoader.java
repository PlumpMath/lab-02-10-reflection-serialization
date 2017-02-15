package com.aka.loader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClassLoader extends ClassLoader {
    private URL jarURL;
    private HashMap<String, Class> classes = new HashMap<>();

    public JarClassLoader(URL jarURL) {
        super(JarClassLoader.class.getClassLoader());
        this.jarURL = jarURL;
    }

    public JarClassLoader() throws MalformedURLException {
        super(JarClassLoader.class.getClassLoader());
        jarURL = new URL("https://github.com/aka-demik/lab-02-10-reflection-serialization/blob/lab-02-15-classloaders/plugins/lab-02-15-animal-class.jar?raw=true");
    }

    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        return findClass(className);
    }

    @Override
    public Class findClass(String className) {
        byte classByte[];
        Class result = null;

        result = classes.get(className);
        if (result != null) {
            return result;
        }

        try {
            return findSystemClass(className);
        } catch (ClassNotFoundException e) {
        }

        try {
            try (InputStream is = jarURL.openStream()) {
                Files.copy(is, Paths.get("tmp.jar"), StandardCopyOption.REPLACE_EXISTING);
            }

            JarFile jar = new JarFile("tmp.jar");
            JarEntry entry = jar.getJarEntry(className + ".class");
            InputStream is = jar.getInputStream(entry);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
            int nextValue = is.read();
            while (-1 != nextValue) {
                byteStream.write(nextValue);
                nextValue = is.read();
            }

            classByte = byteStream.toByteArray();
            result = defineClass(className, classByte, 0, classByte.length, null);
            classes.put(className, result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
