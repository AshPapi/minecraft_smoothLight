package com.smoothlight.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightEngine.class)
public interface LightEngineAccessor {

    @Invoker("getOpacity")
    int smoothlight$callGetOpacity(BlockState state);

    @Invoker("shapeOccludes")
    boolean smoothlight$callShapeOccludes(BlockState fromState, BlockState toState, Direction direction);
}
