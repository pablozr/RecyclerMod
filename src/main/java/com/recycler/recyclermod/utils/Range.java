package com.recycler.recyclermod.utils;

import net.minecraft.util.RandomSource;

public record Range(int min, int max) {
    public int sample(RandomSource rand) {
        int lo = Math.max(0, min);
        int hi = Math.max(lo, max);
        if (hi == lo) return lo;
        return lo + rand.nextInt((hi - lo) + 1);
    }

    public static Range fixed(int v) { return new Range(v, v); }
}