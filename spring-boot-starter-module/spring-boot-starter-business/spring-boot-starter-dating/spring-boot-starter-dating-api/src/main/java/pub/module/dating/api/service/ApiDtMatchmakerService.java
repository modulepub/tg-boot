package pub.module.dating.api.service;


/**
 * Api 红娘信息 Service
 *
 * @author tg
 * 2026-03-22 13:32:44
 */
public interface ApiDtMatchmakerService  {

    /**
     * 是否已登记为红娘（按 system userCode）。
     */
    boolean isMatchmakerByUserCode(String userCode);

}
