package com.recycler.recyclermod.registry;

import com.recycler.recyclermod.RecyclerMod;
import com.recycler.recyclermod.content.blockentity.RecyclerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import oshi.hardware.common.AbstractUsbDevice;

public class ModBlocksEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RecyclerMod.MODID);

    public static final RegistryObject<BlockEntityType<RecyclerBlockEntity>> RECYCLER =
            BLOCK_ENTITIES.register("recycler", () -> BlockEntityType.Builder.of(RecyclerBlockEntity::new, ModBlocks.RECYCLER.get()).build(null));

    public static void register(IEventBus bus){
        BLOCK_ENTITIES.register(bus);
    }
}
