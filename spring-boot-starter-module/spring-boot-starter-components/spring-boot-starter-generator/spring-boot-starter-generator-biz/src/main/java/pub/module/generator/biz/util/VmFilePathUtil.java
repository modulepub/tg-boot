package pub.module.generator.biz.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class VmFilePathUtil {

    /**
     * 获取resources/vm目录下所有文件（含子目录）的相对路径（相对resources目录）
     * @return 相对路径列表，如：vm/test.vm、vm/subDir/demo.vm
     */
    public static List<String> getAllVmFileRelativePaths() {
        // 1. 资源匹配模式：vm/** 递归匹配所有子目录文件
        String resourcePattern = "classpath:vm/**";
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<String> relativePathList = new ArrayList<>();

        try {
            // 2. 解析所有匹配的资源
            Resource[] resources = resolver.getResources(resourcePattern);
            System.out.println("匹配到的资源总数：" + resources.length);

            if (resources.length == 0) {
                System.out.println("未匹配到vm目录资源，请检查目录结构和Maven配置");
                return relativePathList;
            }

            // 3. 遍历资源，转换为相对路径
            for (Resource resource : resources) {
                // 过滤目录，只保留文件（核心：避免目录路径混入）
                if (!resource.isFile()) {
                    continue;
                }

                // 4. 获取资源URI并处理特殊字符
                URI resourceUri = resource.getURI();
                String resourcePath = resourceUri.getPath();

                // 5. 截取相对resources目录的路径（兼容两种环境）
                String relativePath = null;
                // 开发环境：路径格式如 file:/D:/project/target/classes/vm/test.vm
                String classesFlag = "classes/";
                // Jar包环境：路径格式如 jar:file:/D:/project/demo.jar!/BOOT-INF/classes!/vm/subDir/demo.vm
                String jarClassesFlag = "classes!/";

                if (resourcePath.contains(classesFlag)) {
                    // 处理开发环境路径
                    int index = resourcePath.lastIndexOf(classesFlag);
                    relativePath = resourcePath.substring(index + classesFlag.length());
                } else if (resourcePath.contains(jarClassesFlag)) {
                    // 处理Jar 包环境路径
                    int index = resourcePath.lastIndexOf(jarClassesFlag);
                    relativePath = resourcePath.substring(index + jarClassesFlag.length());
                }

                // 6. 路径格式化（统一分隔符、过滤无效路径）
                if (StringUtils.hasLength(relativePath)) {
                    // 统一路径分隔符为 /（兼容Windows的\）
                    relativePath = relativePath.replace(File.separator, "/");
                    // 去除路径开头可能存在的 /
                    if (relativePath.startsWith("/")) {
                        relativePath = relativePath.substring(1);
                    }
                    // 确保路径不以 / 结尾（排除目录）
                    if (!relativePath.endsWith("/") && StringUtils.hasLength(relativePath)) {
                        relativePathList.add(relativePath);
                        System.out.println("解析成功：" + relativePath);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("获取vm文件路径失败：" + e.getMessage());
        }

        return relativePathList;
    }

    // 测试方法（直接运行即可）
    public static void main(String[] args) {
        List<String> vmPaths = getAllVmFileRelativePaths();
        System.out.println("========== 最终结果 ==========");
        System.out.println("获取到的vm文件数量：" + vmPaths.size());
        for (String path : vmPaths) {
            System.out.println(path);
        }
    }
}