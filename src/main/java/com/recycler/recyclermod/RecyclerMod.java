package com.recycler.recyclermod;

import com.recycler.recyclermod.config.RecyclerConfig;
import com.recycler.recyclermod.data.ScrapValuesReloader;
import com.recycler.recyclermod.registry.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RecyclerMod.MODID)
public final class RecyclerMod {
    public static final String MODID = "recycler";

    public RecyclerMod(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        ModItems.register(bus);
        ModTabs.register(bus);
        ModBlocks.register(bus);
        ModBlocksEntities.register(bus);
        ModMenus.register(bus);
        ModSounds.SOUNDS.register(bus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RecyclerConfig.SPEC, "recycler-common.toml");

        MinecraftForge.EVENT_BUS.addListener(ScrapValuesReloader::onReload);
    }
}
