package com.recycler.recyclermod.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class RecyclerConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_ALLOW;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TAG_ALLOW;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_OVERRIDES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> TAG_DEFAULTS;

    static {
        BUILDER.push("scrap");

        ITEM_ALLOW = BUILDER
                .comment("Itens recicláveis (ID completo). Ex.: \"minecraft:stick\"")
                .defineList("item_allow", List.of(), o -> o instanceof String);

        TAG_ALLOW = BUILDER
                .comment("Tags cujos itens são recicláveis (ID de tag). Ex.: \"recycler:recyclables/guns\"")
                .defineList("tag_allow", List.of(), o -> o instanceof String);

        ITEM_OVERRIDES = BUILDER
                .comment("Quantidades por item: \"<ns:item>=<n>\" ou \"<ns:item>=<lo..hi>\"")
                .defineList("item_overrides",
                        List.of("minecraft:diamond_sword=25..35"),
                        o -> o instanceof String);

        TAG_DEFAULTS = BUILDER
                .comment("Quantidades por tag: \"<ns:tag>=<n>\" ou \"<ns:tag>=<lo..hi>\"")
                .defineList("tag_defaults",
                        List.of("recycler:recyclables/guns=50..120", "recycler:recyclables/tools=10..15"),
                        o -> o instanceof String);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private RecyclerConfig() {}
}

