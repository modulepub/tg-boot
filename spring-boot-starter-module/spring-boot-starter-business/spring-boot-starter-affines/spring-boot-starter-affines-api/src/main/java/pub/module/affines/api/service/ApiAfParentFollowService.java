package pub.module.affines.api.service;

import pub.module.affines.api.service.dto.AfParentFollowDTO;

import java.util.List;

public interface ApiAfParentFollowService {

    void follow(String followerUserCode, String targetChildProfileCode);

    void unfollow(String followerUserCode, String targetChildProfileCode);

    boolean isFollowing(String followerUserCode, String targetChildProfileCode);

    List<AfParentFollowDTO> listMyFollows(String followerUserCode);
}
