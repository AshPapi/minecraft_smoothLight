package com.smoothlight;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod(SmoothLight.MOD_ID)
public class SmoothLight {
    public static final String MOD_ID = "smoothlight";

    public SmoothLight() {
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ServerTickEvent event) -> {
            if (event.phase != TickEvent.Phase.END) {
                return;
            }
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                return;
            }
            for (ServerLevel level : server.getAllLevels()) {
                LightTransitions.tick(level);
            }
        });
        MinecraftForge.EVENT_BUS.addListener((ServerStoppedEvent event) ->
                LightTransitions.drop(level -> level instanceof ServerLevel));
        if (FMLEnvironment.dist.isClient()) {
            SmoothLightClient.init();
        }
    }
}
