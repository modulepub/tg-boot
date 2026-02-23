package pub.module.file.biz.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import pub.module.file.biz.BizFileAutoConfiguration;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实现本地文件分片上传
 * @author panzhen
 */
@Slf4j
@Service
public class LocalUploadService {

    @Resource
    BizFileAutoConfiguration bizFileAutoConfiguration;
    private final Map<String, Integer> lock = new HashMap<>();

    /**
     *
     * @param file 上传文件
     * @param md5 上传文件的唯一标识，建议是md5
     * @param sliceIndex 上传文件的分片序号
     * @param totalPieces 上传文件的分片总数
     * @param filePath  上传文件的文件名
     */
    @SneakyThrows
    public Integer uploadByFragment(File file, String md5, Integer sliceIndex, Integer totalPieces, String filePath) {
        String fileName = FileUtil.getName(filePath);
        String storePath = bizFileAutoConfiguration.getPath();
        String temPath = FileUtil.getTmpDirPath() +File.separator +"file"+ File.separator + md5;
        String extPath =filePath.replace(fileName,"");
        if(StrUtil.isNotEmpty(extPath)){
            temPath = temPath +File.separator + extPath;
            storePath = storePath +File.separator + extPath;
        }
        File tempDir = FileUtil.mkdir(temPath);
        File storeDir = FileUtil.mkdir(storePath);

        String tempFileNamePath = tempDir.getAbsolutePath() + File.separator + fileName + "_" + sliceIndex + ".part";
        //将分片存储到临时文件夹中
        //TODO 这里可以采用检查是否存在分片文件来判断是否已经上传过了，以实现秒传和断点续传功能,需要配合见擦汗分片文件大小来判断 file.getSize()
       FileUtil.writeBytes(FileUtil.readBytes(file), tempFileNamePath);
        File[] tempFiles = tempDir.listFiles();
        one:
        if (totalPieces.equals(Objects.requireNonNull(tempFiles).length)) {
            //需要校验一下,表示已有异步程序正在合并了;如果是分布式这个校验可以加入redis的分布式锁来完成
            if (lock.get(md5) != null) {
                break one;
            }
            lock.put(md5, tempFiles.length);
            FileOutputStream fileOutputStream = new FileOutputStream(storeDir.getAbsolutePath()+File.separator + fileName);
            //这里如果分片很多的情况下，可以采用多线程来执行
            for (int i = 0; i < totalPieces; i++) {
                //读取分片数据，进行分片合并
                FileInputStream fileInputStream = new FileInputStream(tempDir.getAbsolutePath() + "\\" + fileName + "_" + i + ".part");
                byte[] buf = new byte[1024 * 8];//8MB
                int length;
                while ((length = fileInputStream.read(buf)) != -1) {//读取fis文件输入字节流里面的数据
                    fileOutputStream.write(buf, 0, length);//通过fos文件输出字节流写出去
                }
                fileInputStream.close();
            }
            fileOutputStream.flush();
            fileOutputStream.close();
            lock.remove(md5);
            FileUtil.clean(tempDir);
           tempDir.deleteOnExit();
            return -1;
        }
        //通过返回成功的分片值，来验证分片是否有丢失
        return sliceIndex;
    }
}
