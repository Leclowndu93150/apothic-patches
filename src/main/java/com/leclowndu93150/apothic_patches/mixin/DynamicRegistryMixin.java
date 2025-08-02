package com.leclowndu93150.apothic_patches.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.leclowndu93150.apothic_patches.MaxAmplifierManager;
import dev.shadowsoffire.placebo.reload.DynamicRegistry;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = DynamicRegistry.class, remap = false)
public class DynamicRegistryMixin {
    
    @Inject(method = "apply", at = @At("HEAD"))
    private void captureJsonBeforeCodec(java.util.Map<ResourceLocation, JsonElement> objects, net.minecraft.server.packs.resources.ResourceManager resourceManager, net.minecraft.util.profiling.ProfilerFiller profiler, CallbackInfo ci) {
        boolean hasGemData = objects.entrySet().stream()
            .anyMatch(entry -> entry.getValue() instanceof JsonObject json 
                && json.has("bonuses") 
                && (entry.getKey().getNamespace().equals("apotheosis") || entry.getKey().getNamespace().equals("fallen_gems_affixes")));
        
        if (!hasGemData) {
            return;
        }

        objects.forEach((key, ele) -> {
            if (ele instanceof JsonObject json && json.has("bonuses")) {
                for (var bonusElement : json.getAsJsonArray("bonuses")) {
                    if (bonusElement.isJsonObject()) {
                        JsonObject bonus = bonusElement.getAsJsonObject();
                        String bonusType = bonus.has("type") ? bonus.get("type").getAsString() : "";

                        extractMaxAmplifierFromBonus(bonus, key, bonusType);
                    }
                }
            }
        });
    }
    
    private void extractMaxAmplifierFromBonus(JsonObject bonus, ResourceLocation key, String bonusType) {
        ResourceLocation gemId = new ResourceLocation(key.getNamespace(), key.getPath());

        if (bonus.has("max_amplifier")) {
            int maxAmplifier = bonus.get("max_amplifier").getAsInt();
            System.out.println("[INTERCEPT] Storing max_amplifier: " + maxAmplifier + " for gem ID: " + gemId + " (type: " + bonusType + ")");
            MaxAmplifierManager.storeMaxAmplifier(gemId, maxAmplifier);
            return;
        }

        if (bonus.has("values")) {
            JsonObject values = bonus.getAsJsonObject("values");
            for (var rarityEntry : values.entrySet()) {
                JsonElement rarityElement = rarityEntry.getValue();
                if (rarityElement.isJsonObject()) {
                    JsonObject rarityData = rarityElement.getAsJsonObject();
                    if (rarityData.has("max_amplifier")) {
                        int maxAmplifier = rarityData.get("max_amplifier").getAsInt();
                        System.out.println("[INTERCEPT] Storing max_amplifier: " + maxAmplifier + " for gem ID: " + gemId + " (type: " + bonusType + ")");
                        MaxAmplifierManager.storeMaxAmplifier(gemId, maxAmplifier);
                        return;
                    }
                }
            }
        }

        if (bonus.has("effects")) {
            JsonObject effects = bonus.getAsJsonObject("effects");
            for (var rarityEntry : effects.entrySet()) {
                JsonElement rarityElement = rarityEntry.getValue();
                if (rarityElement.isJsonObject()) {
                    JsonObject rarityData = rarityElement.getAsJsonObject();
                    if (rarityData.has("max_amplifier")) {
                        int maxAmplifier = rarityData.get("max_amplifier").getAsInt();
                        System.out.println("[INTERCEPT] Storing max_amplifier: " + maxAmplifier + " for gem ID: " + gemId + " (type: " + bonusType + ")");
                        MaxAmplifierManager.storeMaxAmplifier(gemId, maxAmplifier);
                        return;
                    }
                }
            }
        }
    }
}