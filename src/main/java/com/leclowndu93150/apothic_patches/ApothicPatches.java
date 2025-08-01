package com.leclowndu93150.apothic_patches;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ApothicPatches.MODID)
public class ApothicPatches {
    public static final String MODID = "apothic_patches";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ApothicPatches() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        MinecraftForge.EVENT_BUS.register(this);
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "apothic_patches/apothic_patches-common.toml");

    }
}