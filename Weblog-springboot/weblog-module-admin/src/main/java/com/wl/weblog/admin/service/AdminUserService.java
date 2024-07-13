package com.wl.weblog.admin.service;

import com.wl.weblog.admin.model.vo.user.UpdateAdminUserPasswordReqVO;
import com.wl.weblog.common.utils.Response;

public interface AdminUserService {
    /**
     * 修改密码
     * @param updateAdminUserPasswordReqVO
     * @return
     */
    Response updatePassword(UpdateAdminUserPasswordReqVO updateAdminUserPasswordReqVO) ;

    /**
     * 获取当前登录用户信息
     * @param username
     * @return
     */
    Response findUserInfo();
}
