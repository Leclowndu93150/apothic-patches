package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.Config;
import com.leclowndu93150.apothic_patches.MaxAmplifierManager;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import net.kayn.fallen_gems_affixes.adventure.socket.gem.bonus.MultiEffectBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MultiEffectBonus.class, remap = false)
public class MultiEffectBonusMixin {

    @Redirect(method = "applySingleEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), remap = true)
    private boolean applyMultiEffectWithAmplifierLimit(LivingEntity target, MobEffectInstance newEffect, ItemStack gemStack) {
        if (newEffect == null) {
            return false;
        }

        MobEffect effect = newEffect.getEffect();
        MobEffectInstance existingEffect = target.getEffect(effect);

        ResourceLocation gemId = GemItem.getGem(gemStack).getId();
        int maxAmp = MaxAmplifierManager.getMaxAmplifier(gemId);
        
        if (maxAmp == -1) {
            maxAmp = Config.defaultAmplifierCap;
        }
        
        int newAmplifier = newEffect.getAmplifier();
        if (maxAmp >= 0 && newAmplifier > maxAmp) {
            newAmplifier = maxAmp;
        }

        MobEffectInstance cappedEffect = new MobEffectInstance(
            effect,
            newEffect.getDuration(),
            newAmplifier,
            newEffect.isAmbient(),
            newEffect.isVisible(),
            newEffect.showIcon()
        );
        
        return target.addEffect(cappedEffect);
    }
}