package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.Config;
import com.leclowndu93150.apothic_patches.ExtendedEffectData;
import dev.shadowsoffire.apotheosis.adventure.affix.Affix;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.bonus.PotionBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = PotionBonus.class, remap = false)
public class PotionBonusMixin {
    
    @Shadow @Final protected MobEffect effect;
    @Shadow @Final protected boolean stackOnReapply;
    @Shadow @Final protected java.util.Map<LootRarity, PotionBonus.EffectData> values;
    
    @Shadow
    protected int getCooldown(LootRarity rarity) {
        throw new AssertionError();
    }
    
    
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void applyEffect(ItemStack gemStack, LivingEntity target, LootRarity rarity) {
        int cooldown = this.getCooldown(rarity);
        if (cooldown == 0 || !Affix.isOnCooldown(((GemBonusAccessor)this).invokeGetCooldownId(gemStack), cooldown, target)) {
            PotionBonus.EffectData data = this.values.get(rarity);
            MobEffectInstance inst = target.getEffect(this.effect);

            if (this.stackOnReapply && inst != null) {
                int maxAmplifier = ExtendedEffectData.getMaxAmplifier(data);
                System.out.println("Max Amplifier: " + maxAmplifier);
                if (maxAmplifier == -1) {
                    maxAmplifier = Config.defaultAmplifierCap;
                }

                int newAmplifier = inst.getAmplifier() + 1 + data.amplifier();

                if (maxAmplifier >= 0 && newAmplifier > maxAmplifier) {
                    newAmplifier = maxAmplifier;
                }

                MobEffectInstance newInst = new MobEffectInstance(this.effect, Math.max(inst.getDuration(), data.duration()), newAmplifier);
                target.addEffect(newInst);
            } else {
                target.addEffect(data.build(this.effect));
            }

            Affix.startCooldown(((GemBonusAccessor)this).invokeGetCooldownId(gemStack), target);
        }
    }
}