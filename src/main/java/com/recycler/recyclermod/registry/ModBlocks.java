package com.recycler.recyclermod.registry;

import com.recycler.recyclermod.RecyclerMod;
import com.recycler.recyclermod.content.block.RecyclerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RecyclerMod.MODID);

    public static final RegistryObject<Block> RECYCLER =
            BLOCKS.register("recycler", () -> new RecyclerBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5F, 6.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)
                            .noOcclusion()
                            .lightLevel(state -> state.getValue(RecyclerBlock.LIT) ? 13 : 0)
            ));

    public static void register(IEventBus bus) { BLOCKS.register(bus); }
}
