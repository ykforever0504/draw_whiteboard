package edu.unimelb.common.constant;

import edu.unimelb.service.impl.RemoteUserServiceImpl;

/**
 * Global registration of implementation classes
 *
 * @author Zhuoya Zhou 1366573
 * @since 2024/5/13
 */
public class RegistryConstant {

    /**
     * Remote user logical operation implementation class
     */
    public static final String REMOTE_USER_SERVICE = RemoteUserServiceImpl.class.getName();

}
