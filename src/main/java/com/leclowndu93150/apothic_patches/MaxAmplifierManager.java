package com.leclowndu93150.apothic_patches;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = "apothic_patches", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MaxAmplifierManager {
    
    private static final Map<ResourceLocation, Integer> MAX_AMPLIFIERS = new HashMap<>();

    public static void storeMaxAmplifier(ResourceLocation gemId, int maxAmplifier) {
        MAX_AMPLIFIERS.put(gemId, maxAmplifier);
    }
    
    public static int getMaxAmplifier(ResourceLocation gemId) {
        int maxAmplifier =  MAX_AMPLIFIERS.getOrDefault(gemId, -1);
        System.out.println("[DEBUG] Max amplifier for gem ID " + gemId + ": " + maxAmplifier);
        return maxAmplifier - 1; //starts at 0
    }
    
    public static Map<ResourceLocation, Integer> getAllMaxAmplifiers() {
        return new HashMap<>(MAX_AMPLIFIERS);
    }
}