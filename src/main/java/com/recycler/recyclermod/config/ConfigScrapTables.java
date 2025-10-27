package com.recycler.recyclermod.config;

import com.recycler.recyclermod.utils.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;

public final class ConfigScrapTables {
    private static Map<ResourceLocation, Range> TAG_TABLE   = Map.of();
    private static Map<ResourceLocation, Range> ITEM_TABLE  = Map.of();
    private static Set<ResourceLocation>       ITEM_ALLOW   = Set.of();
    private static Set<ResourceLocation>       TAG_ALLOW    = Set.of();

    public static void onLoadOrReload(ModConfig cfg) { rebuild(); }

    public static void rebuild() {
        Map<ResourceLocation, Range> tags  = new HashMap<>();
        Map<ResourceLocation, Range> items = new HashMap<>();
        Set<ResourceLocation> itemAllow    = new HashSet<>();
        Set<ResourceLocation> tagAllow     = new HashSet<>();

        parsePairs(RecyclerConfig.TAG_DEFAULTS.get(), tags);
        parsePairs(RecyclerConfig.ITEM_OVERRIDES.get(), items);
        parseKeys(RecyclerConfig.ITEM_ALLOW.get(), itemAllow);
        parseKeys(RecyclerConfig.TAG_ALLOW.get(), tagAllow);

        TAG_TABLE  = Map.copyOf(tags);
        ITEM_TABLE = Map.copyOf(items);
        ITEM_ALLOW = Set.copyOf(itemAllow);
        TAG_ALLOW  = Set.copyOf(tagAllow);
    }

    public static Map<ResourceLocation, Range> tags()  { return TAG_TABLE; }
    public static Map<ResourceLocation, Range> items() { return ITEM_TABLE; }
    public static Set<ResourceLocation> itemAllow()    { return ITEM_ALLOW; }
    public static Set<ResourceLocation> tagAllow()     { return TAG_ALLOW; }

    private static void parsePairs(List<? extends String> lines, Map<ResourceLocation, Range> out) {
        if (lines == null) return;
        for (String s : lines) {
            if (s == null) continue;
            String line = s.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            int eq = line.indexOf('=');
            if (eq <= 0 || eq == line.length()-1) continue;
            String key = line.substring(0, eq).trim();
            String val = line.substring(eq+1).trim();
            ResourceLocation rl;
            try { rl = new ResourceLocation(key); } catch (Exception ex) { continue; }
            out.put(rl, parseRange(val));
        }
    }

    private static void parseKeys(List<? extends String> lines, Set<ResourceLocation> out) {
        if (lines == null) return;
        for (String s : lines) {
            if (s == null) continue;
            String key = s.trim();
            if (key.isEmpty() || key.startsWith("#")) continue;
            try { out.add(new ResourceLocation(key)); } catch (Exception ignored) {}
        }
    }

    private static Range parseRange(String text) {
        if (text.contains("..")) {
            String[] p = text.split("\\.\\.");
            try {
                int lo = Integer.parseInt(p[0].trim());
                int hi = Integer.parseInt(p[1].trim());
                if (hi < lo) hi = lo;
                return new Range(lo, hi);
            } catch (Exception ignored) { return new Range(0,0); }
        } else {
            try {
                int v = Integer.parseInt(text.trim());
                return new Range(v, v);
            } catch (Exception ignored) { return new Range(0,0); }
        }
    }

    private ConfigScrapTables() {}
}
