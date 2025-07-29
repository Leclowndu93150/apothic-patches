package com.leclowndu93150.apothic_patches;

import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtendedEffectData {
    private static final Map<PotionBonus.EffectData, Integer> maxAmplifierMap = new ConcurrentHashMap<>();
    
    public static void storeMaxAmplifier(PotionBonus.EffectData data, int maxAmplifier) {
        if (maxAmplifier != -1) {
            maxAmplifierMap.put(data, maxAmplifier);
        }
    }
    
    public static int getMaxAmplifier(PotionBonus.EffectData data) {
        return maxAmplifierMap.getOrDefault(data, -1);
    }
    
    public static PotionBonus.EffectData createWithMaxAmplifier(int duration, int amplifier, int cooldown, int maxAmplifier) {
        PotionBonus.EffectData data = new PotionBonus.EffectData(duration, amplifier, cooldown);
        storeMaxAmplifier(data, maxAmplifier);
        return data;
    }
}