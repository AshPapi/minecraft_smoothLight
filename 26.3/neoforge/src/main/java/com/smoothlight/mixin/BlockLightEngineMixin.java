package com.smoothlight.mixin;

import com.smoothlight.LightEngineLevelAccess;
import com.smoothlight.LightTransitions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockLightEngine.class)
public abstract class BlockLightEngineMixin {

    @Shadow
    @Final
    private BlockPos.MutableBlockPos mutablePos;

    @Unique
    private long smoothlight$fromNode;

    @Inject(method = "getEmission", at = @At("RETURN"), cancellable = true)
    private void smoothlight$emission(long pos, BlockState state, CallbackInfoReturnable<Integer> cir) {
        int actual = cir.getReturnValueI();
        int value = LightTransitions.emission(((LightEngineLevelAccess) this).smoothlight$level(), pos, actual);
        if (value != actual) {
            cir.setReturnValue(value);
        }
    }

    @Inject(method = "propagateIncrease", at = @At("HEAD"))
    private void smoothlight$captureFromNode(long fromNode, long increaseData, int fromLevel, CallbackInfo ci) {
        this.smoothlight$fromNode = fromNode;
    }

    @Redirect(
            method = "propagateIncrease",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;getOpacity(Lnet/minecraft/world/level/block/state/BlockState;)I"
            )
    )
    private int smoothlight$opacity(BlockLightEngine instance, BlockState state) {
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
                    target = "Lnet/minecraft/world/level/lighting/BlockLightEngine;shapeOccludes(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"
            )
    )
    private boolean smoothlight$shapeOccludes(BlockLightEngine instance, BlockState fromState, BlockState toState, Direction dir) {
        Object level = ((LightEngineLevelAccess) this).smoothlight$level();
        long toNode = BlockPos.offset(this.smoothlight$fromNode, dir);
        if (LightTransitions.overridesShape(level, this.smoothlight$fromNode) || LightTransitions.overridesShape(level, toNode)) {
            return false;
        }
        return ((LightEngineAccessor) (Object) instance).smoothlight$callShapeOccludes(fromState, toState, dir);
    }
}
