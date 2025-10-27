package com.recycler.recyclermod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class ModSounds {
    public static final String MODID = "recycler";
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final RegistryObject<SoundEvent> RECYCLER_LOOP =
            SOUNDS.register("recycler_loop",
                    () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MODID, "recycler_loop")));

}
