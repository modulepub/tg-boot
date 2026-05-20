package pub.module.dating.biz.mock.support;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * 某个人员文件夹下的照片集合（文件夹名即姓名）。
 */
public record DatingMockPhotoBundle(String displayName, List<Path> imageFiles) {

    private static final List<String> IMAGE_EXT = List.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    public static List<DatingMockPhotoBundle> scanPersonDirs(Path categoryDir) {
        if (!Files.isDirectory(categoryDir)) {
            return List.of();
        }
        List<DatingMockPhotoBundle> list = new ArrayList<>();
        try (Stream<Path> dirs = Files.list(categoryDir).filter(Files::isDirectory).sorted(Comparator.comparing(p -> p.getFileName().toString()))) {
            dirs.forEach(dir -> {
                String name = dir.getFileName().toString();
                List<Path> images = listImages(dir);
                if (!images.isEmpty()) {
                    list.add(new DatingMockPhotoBundle(name, images));
                }
            });
        } catch (Exception e) {
            throw new IllegalStateException("扫描目录失败: " + categoryDir, e);
        }
        return list;
    }

    public static DatingMockPhotoBundle scanSingleDir(Path dir) {
        String name = dir.getFileName().toString();
        return new DatingMockPhotoBundle(name, listImages(dir));
    }

    private static List<Path> listImages(Path dir) {
        try (Stream<Path> stream = Files.walk(dir, 1)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(DatingMockPhotoBundle::isImage)
                    .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                    .toList();
        } catch (Exception e) {
            throw new IllegalStateException("读取图片失败: " + dir, e);
        }
    }

    private static boolean isImage(Path p) {
        String n = p.getFileName().toString().toLowerCase();
        return IMAGE_EXT.stream().anyMatch(n::endsWith);
    }
}
