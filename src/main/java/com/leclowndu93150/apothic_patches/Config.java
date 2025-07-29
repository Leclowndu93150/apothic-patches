package com.leclowndu93150.apothic_patches;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ApothicPatches.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue DEFAULT_AMPLIFIER_CAP = BUILDER
            .comment("Default maximum amplifier for gem effects when stacking is enabled (-1 = no limit)")
            .defineInRange("defaultAmplifierCap", 255, -1, 255);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int defaultAmplifierCap;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        defaultAmplifierCap = DEFAULT_AMPLIFIER_CAP.get();
    }
}