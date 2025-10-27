package com.recycler.recyclermod.config;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigEvents {
    @SubscribeEvent
    public static void onLoad(ModConfigEvent.Loading e)  { ConfigScrapTables.onLoadOrReload(e.getConfig()); }
    @SubscribeEvent
    public static void onReload(ModConfigEvent.Reloading e) { ConfigScrapTables.onLoadOrReload(e.getConfig()); }
}
