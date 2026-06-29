package pub.module.affines.api.service;

import pub.module.affines.api.service.dto.AfChildProfileViewDTO;

import java.util.List;

public interface ApiAfChildProfileViewService {

    void recordView(String viewerUserCode, String afChildProfileCode);

    List<AfChildProfileViewDTO> listMyViews(String viewerUserCode);

    List<AfChildProfileViewDTO> listViewsByProfileCode(String afChildProfileCode);
}
