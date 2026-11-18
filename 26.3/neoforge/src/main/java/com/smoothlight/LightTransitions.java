package com.smoothlight;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;

public final class LightTransitions {
    private static final int FADE_STEP = 2;
    private static final int MAX_ACTIVE_PER_LEVEL = 256;

    private static final int MASK_5 = 0x1F;
    private static final int PRESENT_BIT = 1 << 20;

    private static final ConcurrentHashMap<Object, LevelState> LEVELS = new ConcurrentHashMap<>();
    private static volatile boolean active;

    private static final class LevelState {
        final Long2IntOpenHashMap entries = new Long2IntOpenHashMap(64);
        final LongArrayList pendingChecks = new LongArrayList(64);
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        volatile boolean hasEntries;

        LevelState() {
            entries.defaultReturnValue(0);
        }
    }

    private LightTransitions() {
    }

    private static int pack(int curEmission, int targetEmission, int curOpacity, int targetOpacity) {
        return PRESENT_BIT
                | (curEmission & MASK_5)
                | ((targetEmission & MASK_5) << 5)
                | ((curOpacity & MASK_5) << 10)
                | ((targetOpacity & MASK_5) << 15);
    }

    private static int curEmission(int packed) {
        return packed & MASK_5;
    }

    private static int targetEmission(int packed) {
        return (packed >>> 5) & MASK_5;
    }

    private static int curOpacity(int packed) {
        return (packed >>> 10) & MASK_5;
    }

    private static int targetOpacity(int packed) {
        return (packed >>> 15) & MASK_5;
    }

    public static void onBlockChanged(Level level, BlockPos pos, BlockState oldState, BlockState newState) {
        if (level == null || oldState == newState) {
            return;
        }
        int newEmission = newState.getLightEmission();
        int newOpacity = Math.max(1, newState.getLightDampening());
        int oldEmission = oldState.getLightEmission();
        int oldOpacity = Math.max(1, oldState.getLightDampening());
        long key = pos.asLong();

        LevelState state = LEVELS.get(level);
        if (state == null && oldEmission == newEmission && oldOpacity == newOpacity) {
            return;
        }
        if (state == null) {
            state = LEVELS.computeIfAbsent(level, l -> new LevelState());
        }

        synchronized (state) {
            int prev = state.entries.get(key);
            int fromEmission = prev != 0 ? curEmission(prev) : oldEmission;
            int fromOpacity = prev != 0 ? curOpacity(prev) : oldOpacity;
            if (fromEmission == newEmission && fromOpacity == newOpacity) {
                if (prev != 0) {
                    state.entries.remove(key);
                    if (state.entries.isEmpty()) {
                        state.hasEntries = false;
                        recomputeActive();
                    }
                }
                return;
            }
            if (prev == 0 && state.entries.size() >= MAX_ACTIVE_PER_LEVEL) {
                return;
            }
            state.entries.put(key, pack(fromEmission, newEmission, fromOpacity, newOpacity));
            state.hasEntries = true;
            active = true;
        }
    }

    public static int emission(Object level, long pos, int actual) {
        int packed = getPacked(level, pos);
        return packed != 0 ? curEmission(packed) : actual;
    }

    public static int opacity(Object level, long pos, int actual) {
        int packed = getPacked(level, pos);
        return packed != 0 ? curOpacity(packed) : actual;
    }

    public static boolean overridesShape(Object level, long pos) {
        return getPacked(level, pos) != 0;
    }

    private static int getPacked(Object level, long pos) {
        if (!active || level == null) {
            return 0;
        }
        LevelState state = LEVELS.get(level);
        if (state == null || !state.hasEntries) {
            return 0;
        }
        synchronized (state) {
            return state.entries.get(pos);
        }
    }

    public static void tick(Level level) {
        if (!active || level == null) {
            return;
        }
        LevelState state = LEVELS.get(level);
        if (state == null || !state.hasEntries) {
            return;
        }

        LongArrayList checks = state.pendingChecks;
        checks.clear();

        synchronized (state) {
            if (state.entries.isEmpty()) {
                state.hasEntries = false;
                recomputeActive();
                return;
            }
            ObjectIterator<Long2IntMap.Entry> it = state.entries.long2IntEntrySet().fastIterator();
            while (it.hasNext()) {
                Long2IntMap.Entry en = it.next();
                int p = en.getIntValue();
                int ce = curEmission(p);
                int te = targetEmission(p);
                int co = curOpacity(p);
                int to = targetOpacity(p);
                int ne = approach(ce, te, FADE_STEP);
                int no = approach(co, to, FADE_STEP);
                checks.add(en.getLongKey());
                if (ne == te && no == to) {
                    it.remove();
                } else {
                    en.setValue(pack(ne, te, no, to));
                }
            }
            if (state.entries.isEmpty()) {
                state.hasEntries = false;
                recomputeActive();
            }
        }

        if (!checks.isEmpty()) {
            LevelLightEngine light = level.getChunkSource().getLightEngine();
            BlockPos.MutableBlockPos mpos = state.mutablePos;
            for (int i = 0, len = checks.size(); i < len; i++) {
                mpos.set(checks.getLong(i));
                light.checkBlock(mpos);
            }
            checks.clear();
        }
    }

    public static void drop(Predicate<Object> levelFilter) {
        if (LEVELS.isEmpty()) {
            return;
        }
        LEVELS.keySet().removeIf(levelFilter);
        recomputeActive();
    }

    private static void recomputeActive() {
        for (LevelState state : LEVELS.values()) {
            if (state.hasEntries) {
                active = true;
                return;
            }
        }
        active = false;
    }

    private static int approach(int cur, int target, int step) {
        if (cur < target) {
            return Math.min(cur + step, target);
        }
        if (cur > target) {
            return Math.max(cur - step, target);
        }
        return cur;
    }
}
