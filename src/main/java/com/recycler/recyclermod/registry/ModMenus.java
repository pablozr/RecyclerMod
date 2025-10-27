package com.recycler.recyclermod.registry;

import com.recycler.recyclermod.RecyclerMod;
import com.recycler.recyclermod.content.menu.RecyclerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, RecyclerMod.MODID);

    public static final RegistryObject<MenuType<RecyclerMenu>> RECYCLER_MENU =
            MENUS.register("recycler", () -> IForgeMenuType.create(RecyclerMenu::new));

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
