package com.smoothlight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

public final class SmoothLightClient {

    private SmoothLightClient() {
    }

    public static void init() {
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Post event) -> {
            ClientLevel level = Minecraft.getInstance().level;
            LightTransitions.drop(l -> l instanceof ClientLevel && l != level);
            if (level != null) {
                LightTransitions.tick(level);
            }
        });
    }
}
