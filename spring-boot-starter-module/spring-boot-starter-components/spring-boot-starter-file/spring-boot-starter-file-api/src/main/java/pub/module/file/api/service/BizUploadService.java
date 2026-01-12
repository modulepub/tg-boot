package pub.module.file.api.service;


import java.io.File;


public interface BizUploadService {
    int uploadByFragment(
            File file,
            String filePath,
            String sliceFileMd5,
            Integer sliceIndex,
            Integer totalPieces
            );


    String upload(File file,String biz);
    String upload(byte[] bytes, String fileName,String biz);
}
