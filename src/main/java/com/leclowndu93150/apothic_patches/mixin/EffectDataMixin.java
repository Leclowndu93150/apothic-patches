package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.ExtendedEffectData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import dev.shadowsoffire.placebo.codec.PlaceboCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PotionBonus.EffectData.class, remap = false)
public class EffectDataMixin {

    @Shadow
    @Mutable
    private static Codec<PotionBonus.EffectData> CODEC;

    @Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void replaceCodec(CallbackInfo ci) {
        CODEC = RecordCodecBuilder.create(inst -> inst
            .group(
                Codec.INT.fieldOf("duration").forGetter(PotionBonus.EffectData::duration),
                Codec.INT.fieldOf("amplifier").forGetter(PotionBonus.EffectData::amplifier),
                PlaceboCodecs.nullableField(Codec.INT, "cooldown", 0).forGetter(PotionBonus.EffectData::cooldown),
                PlaceboCodecs.nullableField(Codec.INT, "max_amplifier", -1).forGetter(data -> ExtendedEffectData.getMaxAmplifier(data)))
            .apply(inst, ExtendedEffectData::createWithMaxAmplifier));
    }
}