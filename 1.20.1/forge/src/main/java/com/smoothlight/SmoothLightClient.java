package com.smoothlight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;

public final class SmoothLightClient {

    private SmoothLightClient() {
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent event) -> {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            ClientLevel level = Minecraft.getInstance().level;
            LightTransitions.drop(l -> l instanceof ClientLevel && l != level);
            if (level != null) {
                LightTransitions.tick(level);
            }
        });
    }
}
