package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.Config;
import com.leclowndu93150.apothic_patches.MaxAmplifierManager;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.affix.effect.PotionAffix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemItem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = PotionBonus.class, remap = false)
public class PotionBonusMixin {

    @Shadow @Final protected MobEffect effect;
    @Shadow @Final protected boolean stackOnReapply;
    @Shadow @Final protected Map<LootRarity, PotionBonus.EffectData> values;
    @Shadow @Final protected PotionAffix.Target target;


    @Inject(method = "applyEffect", at = @At("HEAD"), cancellable = true, remap = false)
    private void applyEffectWithMaxAmplifier(ItemStack gemStack, LivingEntity target, LootRarity rarity, CallbackInfo ci) {
        int cooldown = ((PotionBonusAccessor)this).invokeGetCooldown(rarity);
        if (cooldown == 0 || !Affix.isOnCooldown(((GemBonusAccessor) this).invokeGetCooldownId(gemStack), cooldown, target)) {
            PotionBonus.EffectData data = this.values.get(rarity);
            MobEffectInstance inst = target.getEffect(this.effect);

            if (this.stackOnReapply && inst != null) {
                ResourceLocation gemId = GemItem.getGem(gemStack).getId();
                int maxAmp = MaxAmplifierManager.getMaxAmplifier(gemId);

                if (maxAmp == -1) {
                    maxAmp = Config.defaultAmplifierCap;
                }

                int newAmplifier = inst.getAmplifier() + 1 + data.amplifier();

                if (maxAmp >= 0 && newAmplifier > maxAmp) {
                    newAmplifier = maxAmp;
                }

                MobEffectInstance newInst = new MobEffectInstance(this.effect, Math.max(inst.getDuration(), data.duration()), newAmplifier);
                target.addEffect(newInst);
            } else {
                target.addEffect(data.build(this.effect));
            }

            Affix.startCooldown(((GemBonusAccessor) this).invokeGetCooldownId(gemStack), target);
        }
        ci.cancel();
    }
}