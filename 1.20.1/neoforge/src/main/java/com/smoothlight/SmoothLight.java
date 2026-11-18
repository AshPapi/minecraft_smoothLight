package com.smoothlight;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod(SmoothLight.MOD_ID)
public class SmoothLight {
    public static final String MOD_ID = "smoothlight";

    public SmoothLight() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            MinecraftServer server = event.getServer();
            if (server == null) {
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                LightTransitions.tick(level);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) ->
                LightTransitions.drop(level -> level instanceof ServerLevel));

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SmoothLightClient::init);
    }
}

