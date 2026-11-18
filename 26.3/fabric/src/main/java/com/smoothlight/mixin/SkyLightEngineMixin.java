package com.smoothlight.mixin;

import com.smoothlight.LightEngineLevelAccess;
import com.smoothlight.LightTransitions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.SkyLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyLightEngine.class)
public abstract class SkyLightEngineMixin {

    @Shadow
    @Final
    private BlockPos.MutableBlockPos mutablePos;

    @Unique
    private long smoothlight$fromNode;

    @Inject(method = "propagateIncrease", at = @At("HEAD"))
    private void smoothlight$captureFromNode(long fromNode, long increaseData, int fromLevel, CallbackInfo ci) {
        this.smoothlight$fromNode = fromNode;
    }

    @Redirect(
            method = "propagateIncrease",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;getOpacity(Lnet/minecraft/world/level/block/state/BlockState;)I"
            )
    )
    private int smoothlight$opacity(SkyLightEngine instance, BlockState state) {
        int actual = ((LightEngineAccessor) (Object) instance).smoothlight$callGetOpacity(state);
        return LightTransitions.opacity(
                ((LightEngineLevelAccess) this).smoothlight$level(),
                this.mutablePos.asLong(),
                actual
        );
    }

    @Redirect(
            method = "propagateIncrease",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/SkyLightEngine;shapeOccludes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"
            )
    )
    private boolean smoothlight$shapeOccludes(SkyLightEngine instance, BlockState fromState, BlockState toState, Direction dir) {
        Object level = ((LightEngineLevelAccess) this).smoothlight$level();
        long toNode = BlockPos.offset(this.smoothlight$fromNode, dir);
        if (LightTransitions.overridesShape(level, this.smoothlight$fromNode) || LightTransitions.overridesShape(level, toNode)) {
            return false;
        }
        return ((LightEngineAccessor) (Object) instance).smoothlight$callShapeOccludes(fromState, toState, dir);
    }
}
