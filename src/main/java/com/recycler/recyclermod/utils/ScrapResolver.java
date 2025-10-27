package com.recycler.recyclermod.utils;

import com.recycler.recyclermod.config.ConfigScrapTables;
import com.recycler.recyclermod.data.ScrapValuesReloader;
import com.recycler.recyclermod.utils.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public final class ScrapResolver {
    private ScrapResolver() {}

    public static Range getScrapRange(ItemStack stack) {
        if (stack.isEmpty()) return Range.fixed(0);
        var keyOpt = stack.getItemHolder().unwrapKey();
        if (keyOpt.isEmpty()) return Range.fixed(0);
        ResourceLocation id = keyOpt.get().location();

        if (ConfigScrapTables.itemAllow().contains(id)) {
            Range it = ConfigScrapTables.items().get(id);
            if (it != null) return it;

            Range best = Range.fixed(0); int bestMax = 0;
            for (ResourceLocation tagRl : ConfigScrapTables.tagAllow()) {
                var tag = ItemTags.create(tagRl);
                if (stack.is(tag)) {
                    Range tr = ConfigScrapTables.tags().get(tagRl);
                    if (tr != null && tr.max() > bestMax) { best = tr; bestMax = tr.max(); }
                }
            }
            return best;
        }

        Range bestAllow = Range.fixed(0); int bestAllowMax = 0;
        boolean matchedAllow = false;
        for (ResourceLocation tagRl : ConfigScrapTables.tagAllow()) {
            var tag = ItemTags.create(tagRl);
            if (stack.is(tag)) {
                matchedAllow = true;
                Range it = ConfigScrapTables.items().get(id);
                if (it != null) return it;
                Range tr = ConfigScrapTables.tags().get(tagRl);
                if (tr != null && tr.max() > bestAllowMax) { bestAllow = tr; bestAllowMax = tr.max(); }
            }
        }
        if (matchedAllow) return bestAllow;

        Range cfgItem = ConfigScrapTables.items().get(id);
        if (cfgItem != null) return cfgItem;

        Range bestCfg = Range.fixed(0); int bestCfgMax = 0;
        for (Map.Entry<ResourceLocation, Range> e : ConfigScrapTables.tags().entrySet()) {
            var tag = ItemTags.create(e.getKey());
            if (stack.is(tag)) {
                Range r = e.getValue();
                if (r.max() > bestCfgMax) { bestCfg = r; bestCfgMax = r.max(); }
            }
        }
        if (bestCfgMax > 0) return bestCfg;

        Range bestDp = Range.fixed(0); int bestDpMax = 0;
        for (Map.Entry<ResourceLocation, Range> e : ScrapValuesReloader.INSTANCE.getDefaultsByTag().entrySet()) {
            var tag = ItemTags.create(e.getKey());
            if (stack.is(tag)) {
                Range r = e.getValue();
                if (r.max() > bestDpMax) { bestDp = r; bestDpMax = r.max(); }
            }
        }
        return bestDp;
    }
}
