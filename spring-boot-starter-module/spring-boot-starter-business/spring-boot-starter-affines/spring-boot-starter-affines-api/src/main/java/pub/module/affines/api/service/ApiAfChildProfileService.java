package pub.module.affines.api.service;

import pub.module.affines.api.service.dto.AfChildProfileDTO;

import java.util.List;

public interface ApiAfChildProfileService {

    List<AfChildProfileDTO> listByParentUserCode(String parentUserCode);

    AfChildProfileDTO getDetailByCode(String afChildProfileCode);

    AfChildProfileDTO saveProfile(String parentUserCode, AfChildProfileDTO dto);

    AfChildProfileDTO updateProfile(String parentUserCode, AfChildProfileDTO dto);

    void deleteProfile(String parentUserCode, String afChildProfileCode);
}
