package mohamednasser.projects.webserver;

import mohamednasser.projects.http.ContentType;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

class ResourceInitializer extends SimpleFileVisitor<Path> {

    private final Map<Path, Map<String, Object>> resources;

    private final Path webroot;

    ResourceInitializer(Path webroot) {
        this.webroot = webroot;
        this.resources = new HashMap<>();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        Map<String, Object> metadata = new HashMap<>();
        String name = file.getFileName().toString();
        metadata.put("name", name);
        metadata.put("size", attrs.size());
        metadata.put("Content-Type", ContentType.inferContentType(name));
        resources.put(webroot.relativize(file.toAbsolutePath()), metadata);
        return FileVisitResult.CONTINUE;
    }

    Map<Path, Map<String, Object>> initialize() throws IOException {
        Files.walkFileTree(webroot, this);
        return resources;
    }
}
