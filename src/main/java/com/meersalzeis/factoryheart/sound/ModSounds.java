package com.meersalzeis.factoryheart.sound;

import com.meersalzeis.factoryheart.FHModMain;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, FHModMain.MOD_ID);

    public static final Supplier<SoundEvent> HEART_BEATING = registerSoundEvent("heart_beat");
    public static final Supplier<SoundEvent> CONDENSER_WIRR = registerSoundEvent("condenser_wirr");
    public static final Supplier<SoundEvent> EXTRACTOR_WIRR = registerSoundEvent("extractor_wirr");
    public static final Supplier<SoundEvent> BLAZER_CRACKLING = registerSoundEvent("blazer_crackling");
    public static final Supplier<SoundEvent> TEST_FAULTY = registerSoundEvent("test_faulty");
    public static final Supplier<SoundEvent> TEST_SUCCESS = registerSoundEvent("test_success");
    public static final Supplier<SoundEvent> WRAPPER_WINDING = registerSoundEvent("wrapper_winding");


    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(FHModMain.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}