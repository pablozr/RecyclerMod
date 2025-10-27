package com.recycler.recyclermod.registry;

import com.recycler.recyclermod.RecyclerMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RecyclerMod.MODID);

    public static final RegistryObject<CreativeModeTab> RECYCLER_TAB =
            TABS.register("recycler_tab", () -> CreativeModeTab.builder()
                    .icon(() -> ModItems.RECYCLER.get().getDefaultInstance())
                    .title(Component.translatable("Recycler"))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.SCRAP.get());
                        output.accept(ModItems.RECYCLER.get());
                    })
                    .build());

    public static void register(IEventBus bus) { TABS.register(bus); }
}
