package com.recycler.recyclermod.utils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class RecyclerTags {
    public static final TagKey<Item> GUNS  = TagKey.create(Registries.ITEM, new ResourceLocation("recycler", "recyclables/guns"));
    public static final TagKey<Item> TOOLS = TagKey.create(Registries.ITEM, new ResourceLocation("recycler", "recyclables/tools"));
    public static final TagKey<Item> ARMOR = TagKey.create(Registries.ITEM, new ResourceLocation("recycler", "recyclables/armor"));
    private RecyclerTags() {}
}