package com.smoothlight.client;

import com.smoothlight.LightTransitions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.multiplayer.ClientLevel;

public class SmoothLightClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientLevel level = client.level;
            LightTransitions.drop(l -> l instanceof ClientLevel && l != level);
            if (level != null) {
                LightTransitions.tick(level);
            }
        });
    }
}
