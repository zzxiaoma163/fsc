package com.fsc.fscmonitor;

import com.fsc.fscmonitor.core.FileOper;
import com.fsc.fscmonitor.core.MonitorServer;
import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;


public class FileMonitor {

    private static Logger logger = LoggerFactory.getLogger(FileMonitor.class);

    private final WatchService watcher;

    private final Map<WatchKey, Path> keys;

    private final Map<Path, Path> dirkeys;

    private int deltype = PropertiesUtils.getIntValue(Content.ONDELETE);

    FileMonitor(Path dir) throws IOException {
        this.watcher = FileSystems.getDefault().newWatchService();
        this.keys = new HashMap<WatchKey, Path>();
        this.dirkeys = new HashMap<Path, Path>();
        walkAndRegisterDirectories(dir);
    }

    private void registerDirectory(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE,
                ENTRY_MODIFY);
        keys.put(key, dir);
        dirkeys.put(dir, dir);
    }

    private void walkAndRegisterDirectories(final Path start)
            throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    void processEvents() throws IOException {
        for (; ; ) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }
            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                @SuppressWarnings("rawtypes")
                WatchEvent.Kind kind = event.kind();
                @SuppressWarnings("unchecked")
                Path name = ((WatchEvent<Path>) event).context();
                Path child = dir.resolve(name);
                logger.info(System.currentTimeMillis() + "---"
                        + event.kind().name() + "---" + child);

                if (kind == ENTRY_CREATE) {
                    if (Files.isDirectory(child)) {
                        walkAndRegisterDirectories(child);
                    }
                }
                if (kind == ENTRY_MODIFY) {
                    Path dirchild = dirkeys.get(child);
                    if (dirchild == null) {
                        FileOper.write(System.currentTimeMillis() + "---"
                                + event.kind().name() + "---" + child + "\r\n");
                    }
                }
                if (kind == ENTRY_DELETE) {
                    if(deltype == 1) {
                        Path dirchild = dirkeys.get(child);
                        if (dirchild == null) {
                            FileOper.write(System.currentTimeMillis() + "---"
                                    + event.kind().name() + "---" + child + "\r\n");
                        } else {
                            dirkeys.remove(child);
                        }
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        MonitorServer ms = new MonitorServer();
        ms.start();
        Path dir = Paths.get(PropertiesUtils.getStringValue(Content.MONITOR_ADDRESS));
        new FileMonitor(dir).processEvents();

    }
}
