package de.johni0702.minecraft.view.impl;

import de.johni0702.minecraft.view.impl.client.ClientWorldsManagerImpl;

public final class ClientViewAPIImpl {
    public static final ClientViewAPIImpl INSTANCE = new ClientViewAPIImpl();

    public ClientWorldsManagerImpl getViewManagerImpl() {
        throw new RuntimeException();
    }
}
