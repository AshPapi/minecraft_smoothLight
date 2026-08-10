package com.smoothlight;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(SmoothLight.MOD_ID)
public class SmoothLight {
    public static final String MOD_ID = "smoothlight";

    public SmoothLight() {
        NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post event) -> {
            for (ServerLevel level : event.getServer().getAllLevels()) {
                LightTransitions.tick(level);
            }
        });
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent event) ->
                LightTransitions.drop(level -> level instanceof ServerLevel));
        if (FMLEnvironment.dist.isClient()) {
            ClientInit.init();
        }
    }

    private static class ClientInit {
        private static void init() {
            SmoothLightClient.init();
        }
    }
}

