package com.recycler.recyclermod.client;

import com.recycler.recyclermod.RecyclerMod;
import com.recycler.recyclermod.registry.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RecyclerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientInit {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() ->
                MenuScreens.register(ModMenus.RECYCLER_MENU.get(), com.recycler.recyclermod.client.screen.RecyclerScreen::new)
        );
    }
}

