package cases;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Created by zhengfan on 2017/3/14 0014.
 */
public class FileSystem {

    @Test
    public void removeSign() throws IOException {
        File apk = new File("c:\\Users\\zhengfan\\Downloads\\cn.opda.a.phonoalbumshoushou_3428_a324a2f64e8397dc7799bb6da3f19d61.apk");
        try (java.nio.file.FileSystem fs = FileSystems.newFileSystem(apk.toPath(), null)) {
            // remove META-INF to reSign apk
            final Path meta = fs.getPath("/META-INF");
            if (Files.exists(meta)) {
                Files.walkFileTree(meta, new SimpleFileVisitor<Path>() {


                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (file.getNameCount() > 2) {
                            return FileVisitResult.SKIP_SIBLINGS;
                        }

                        String fileName = file.getFileName().toString().toUpperCase();
                        if (fileName.endsWith(".RSA") || fileName.endsWith(".SF") || fileName.endsWith(".MF")) {
                            Files.deleteIfExists(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }



                });
            }
            //Files.deleteIfExists(meta);
        }
    }
}
