package com.recycler.recyclermod.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.recycler.recyclermod.utils.Range;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;

import java.util.HashMap;
import java.util.Map;

public class ScrapValuesReloader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new Gson();
    public static final ScrapValuesReloader INSTANCE = new ScrapValuesReloader();

    private final Map<ResourceLocation, Range> defaultsByTag = new HashMap<>();
    private final Map<ResourceLocation, Range> overridesByItem = new HashMap<>();

    private ScrapValuesReloader() {
        super(GSON, "recycler");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager mgr, ProfilerFiller profiler) {
        defaultsByTag.clear();
        overridesByItem.clear();

        ResourceLocation key = new ResourceLocation("recycler", "scrap_values");
        JsonElement rootEl = map.get(key);
        if (rootEl == null || !rootEl.isJsonObject()) return;
        JsonObject root = rootEl.getAsJsonObject();

        JsonObject defs = GsonHelper.getAsJsonObject(root, "defaults_by_tag", new JsonObject());
        for (var e : defs.entrySet()) {
            defaultsByTag.put(new ResourceLocation(e.getKey()), parseRange(e.getValue()));
        }
        JsonObject ovrs = GsonHelper.getAsJsonObject(root, "overrides_by_item", new JsonObject());
        for (var e : ovrs.entrySet()) {
            overridesByItem.put(new ResourceLocation(e.getKey()), parseRange(e.getValue()));
        }
    }

    private static Range parseRange(JsonElement el) {
        if (el == null || el.isJsonNull()) return Range.fixed(0);
        if (el.isJsonPrimitive()) return Range.fixed(el.getAsInt());
        JsonObject obj = el.getAsJsonObject();
        int min = obj.has("min") ? obj.get("min").getAsInt() : 0;
        int max = obj.has("max") ? obj.get("max").getAsInt() : min;
        return new Range(min, max);
    }

    public Map<ResourceLocation, Range> getDefaultsByTag() { return defaultsByTag; }
    public Range getOverride(ResourceLocation itemId) { return overridesByItem.get(itemId); }

    public static void onReload(AddReloadListenerEvent e) { e.addListener(INSTANCE); }
}
