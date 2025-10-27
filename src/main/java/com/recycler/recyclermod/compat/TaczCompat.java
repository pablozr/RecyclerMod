package com.recycler.recyclermod.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public final class TaczCompat {
    private static final ResourceLocation GUN_RL = new ResourceLocation("tacz", "modern_kinetic_gun");

    private TaczCompat() {}

    public static boolean isTaczGun(ItemStack stack) {
        Item base = ForgeRegistries.ITEMS.getValue(GUN_RL);
        if (base == null || !stack.is(base)) return false;
        return stack.hasTag() && stack.getTag().contains("GunId");
    }
}
