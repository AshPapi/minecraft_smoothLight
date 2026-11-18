package com.smoothlight.mixin;

import com.smoothlight.LightEngineLevelAccess;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin implements LightEngineLevelAccess {

    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Override
    public Object smoothlight$level() {
        return this.chunkSource.getLevel();
    }
}
