package pub.module.cms.api.service;


import pub.module.cms.curd.entity.CmsChannel;

public interface BizCmsService {
    /**
     * 设置树结构
     *
     * @param cmsChannel 栏目对象
     */
    void setTree(CmsChannel cmsChannel);

    /**
     * 获取树结构
     *
     * @param ccCode 栏目编码
     */
    CmsChannel getTree(String ccCode);

    /**
     * 发布栏目
     *
     * @param ccCode 栏目编码
     */
    void publishCmsChannel(String ccCode);

    /**
     * 撤销发布
     *
     * @param ccCode 栏目编码
     */
    void cancelPublishCmsChannel(String ccCode);

    /**
     * 撤销发布文档
     *
     * @param cdCode 文档编码
     */
    void cancelPublishCmsDocument(String cdCode);
    /**
     * 发布文档
     *
     * @param cdCode 文档编码
     */
    void publishCmsDocument(String cdCode);
}
