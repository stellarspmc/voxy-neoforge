package me.cortex.voxy.common.config.storage.other;

import me.cortex.voxy.common.config.storage.StorageConfig;

public abstract class DelegateStorageConfig extends StorageConfig {
    public StorageConfig delegate;
}
