package pub.module.file.biz.constants;


import lombok.Getter;

@Getter
public enum UploadTypeEnum {

    MINIO("minio","minio"),
    LOCAL("local","local"),
    ALI_OSS("aliOss","aliOss"),
    ;
    private final String code;
    private final String text;

    UploadTypeEnum(String code, String text){
        this.code = code;
        this.text = text;
    }

}
