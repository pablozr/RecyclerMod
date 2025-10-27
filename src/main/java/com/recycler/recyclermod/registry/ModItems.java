package com.recycler.recyclermod.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ModItems {
    public static final String MODID = "recycler";

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> SCRAP = ITEMS.register("scrap", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RECYCLER = ITEMS.register("recycler", () -> new BlockItem(ModBlocks.RECYCLER.get(), new Item.Properties().stacksTo(1)));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}