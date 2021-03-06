package org.anon.smart.smcore.test.channel.upload;

import org.anon.smart.d2cache.D2CacheConfig;
import org.anon.smart.base.test.testanatomy.BaseStartConfig;
import org.anon.smart.channels.shell.ExternalConfig;
import org.anon.smart.smcore.anatomy.SMCoreConfig;
import org.anon.smart.smcore.channel.server.EventServerConfig;
import org.anon.smart.smcore.channel.server.UploadServerConfig;
import org.anon.utilities.exception.CtxException;

import java.util.Map;

public class TestStartUploadConfig extends BaseStartConfig implements SMCoreConfig {

    protected ExternalConfig[] _channels;

    public TestStartUploadConfig(String[] deploy, String[] tenants, Map<String, String[]> enable, int port ,int uploadPort)
    {
        super(deploy, tenants, enable);
        _channels = new ExternalConfig[2];
        _channels[0] = new EventServerConfig("Test", port, false);
        _channels[1] = new UploadServerConfig("Test", uploadPort, false);
    }

    public ExternalConfig[] startChannels()
            throws CtxException
    {
        return _channels;
    }

    public boolean firstJVM()
    {
        return true;
    }

    public boolean initTenants()
    {
        return false; //will be initialized by smcore
    }

    public D2CacheConfig repository() {
        return null;
    }
}
