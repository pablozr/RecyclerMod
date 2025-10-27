package com.recycler.recyclermod.client.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.recycler.recyclermod.registry.ModSounds;

public class RecyclerLoopSound extends AbstractTickableSoundInstance {
    private final BlockPos pos;
    private volatile boolean shouldStop = false;

    public RecyclerLoopSound(BlockPos pos) {
        super(ModSounds.RECYCLER_LOOP.get(), SoundSource.BLOCKS, RandomSource.create());
        this.pos = pos.immutable();
        this.looping = true;
        this.relative = false;
        this.attenuation = SoundInstance.Attenuation.LINEAR;
        this.volume = 0.7f;
        this.pitch = 1.0f;
        this.x = pos.getX() + 0.5;
        this.y = pos.getY() + 0.5;
        this.z = pos.getZ() + 0.5;
    }

    public void requestStop() {
        this.shouldStop = true;
    }

    @Override
    public void tick() {
        var mc = Minecraft.getInstance();
        if (mc.level == null) { this.stop(); return; }
        if (this.shouldStop) { this.stop(); return; }
        var state = mc.level.getBlockState(pos);
        boolean lit = state.hasProperty(BlockStateProperties.LIT) && state.getValue(BlockStateProperties.LIT);
        if (!lit) { this.stop(); }
    }
}
