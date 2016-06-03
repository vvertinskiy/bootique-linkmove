package com.nhl.bootique.linkmove;

import com.nhl.link.move.runtime.LmRuntimeBuilder;
import com.nhl.link.move.runtime.extractor.model.ClasspathExtractorModelLoader;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;

public class LinkMoveExtractorModelProvider {

    private static final String CLASS_PATH_PROTOCOL = "classpath:";
    private static final String FILE_PATH_PROTOCOL = "file:";

    public static void setupExtractorModels(LmRuntimeBuilder runtimeBuilder, String extractorsPath) {
        if (!extractorsPath.contains(":")) {
            runtimeBuilder.extractorModelsRoot(extractorsPath);
        } else {
            if (extractorsPath.startsWith(CLASS_PATH_PROTOCOL)) {
                final String path = formatClassPath(extractorsPath.substring(CLASS_PATH_PROTOCOL.length()));
                runtimeBuilder.extractorModelLoader(new ClasspathExtractorModelLoader() {
                    @Override
                    protected Reader getXmlSource(String name) throws IOException {
                        return super.getXmlSource(path + name);
                    }
                });
            } else if (extractorsPath.startsWith(FILE_PATH_PROTOCOL)) {
                try {
                    URI fileUri = new URI(extractorsPath);
                    File rootDir = new File(fileUri);
                    runtimeBuilder.extractorModelsRoot(rootDir);
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Incorrect file URI: \"" + extractorsPath + "\"");
                }
            } else {
                throw new IllegalArgumentException("Unsupported protocol is used in the value '" + extractorsPath + "' for property 'extractorDirs'");
            }
        }
    }

    private static String formatClassPath(String classPath) {
        if (classPath == null) {
            return "";
        }
        if (!classPath.isEmpty()) {
            classPath = classPath.trim();
            if (!classPath.endsWith("/")) {
                classPath += "/";
            }
        }
        return classPath;
    }
}