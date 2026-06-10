package com.smoothlight.mixin;

import com.smoothlight.LightEngineLevelAccess;
import com.smoothlight.LightTransitions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LightEngine.class)
public abstract class LightEngineMixin implements LightEngineLevelAccess {

    @Shadow
    @Final
    protected LightChunkGetter chunkSource;

    @Override
    public Object smoothlight$level() {
        return this.chunkSource.getLevel();
    }

    @Inject(method = "getOpacity", at = @At("RETURN"), cancellable = true)
    private void smoothlight$opacity(BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        int actual = cir.getReturnValueI();
        int value = LightTransitions.opacity(smoothlight$level(), pos.asLong(), actual);
        if (value != actual) {
            cir.setReturnValue(value);
        }
    }

    @Inject(method = "getOcclusionShape(Lnet/minecraft/world/level/block/state/BlockState;JLnet/minecraft/core/Direction;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
            at = @At("HEAD"), cancellable = true)
    private void smoothlight$occlusionShape(BlockState state, long pos, Direction dir,
                                            CallbackInfoReturnable<VoxelShape> cir) {
        if (LightTransitions.overridesShape(smoothlight$level(), pos)) {
            cir.setReturnValue(Shapes.empty());
        }
    }
}
