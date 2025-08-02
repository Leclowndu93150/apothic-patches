package com.leclowndu93150.apothic_patches.mixin;

import com.leclowndu93150.apothic_patches.Config;
import dev.shadowsoffire.apotheosis.adventure.loot.LootCategory;
import net.kayn.fallen_gems_affixes.event.PermanentEffectHandler;
import net.kayn.fallen_gems_affixes.util.EquipmentSlotUtil;
import net.kayn.fallen_gems_affixes.util.EquipmentSlotWrapper;
import net.kayn.fallen_gems_affixes.util.ProtectedMobEffectMap;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = PermanentEffectHandler.class, remap = false)
public class PermanentEffectBonusMixin {

    /**
     * @author ApothicPatches
     * @reason Apply amplifier limits to permanent effects in tick handler
     */
    @Overwrite
    private void onTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            ProtectedMobEffectMap<?> cached =
                PermanentEffectHandlerAccessor.getTickEventProtectedMapWrapper().get(player.getUUID());

            try {
                for(ItemStack equipment : player.getAllSlots()) {
                    for(EquipmentSlot slot : LootCategory.forItem(equipment).getSlots()) {
                        EquipmentSlotWrapper slotWrapper =
                            EquipmentSlotUtil.getVanillaWrapper(slot);
                        cached.initOperation(slotWrapper);
                        java.util.Set<MobEffect> effects = cached.getEffectsFromCache(slotWrapper);
                        if (effects != null) {
                            PermanentEffectHandler.checkGemBonus(equipment, (bonus, rarity) -> {
                                MobEffect effect = bonus.getEffect();
                                if (!player.getActiveEffectsMap().containsKey(effect) && effects.contains(effect)) {
                                    int amplifier = bonus.getAmplifier(rarity);
                                    int maxAmp = Config.defaultAmplifierCap;
                                    if (maxAmp >= 0 && amplifier > maxAmp) {
                                        amplifier = maxAmp;
                                    }
                                    
                                    player.addEffect(new MobEffectInstance(effect, -1, amplifier));
                                    cached.addPermanentEffect(slotWrapper, effect, amplifier, true);
                                }
                            });
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cached.finalizeOperation();
            }
        }
    }

    @Redirect(method = "onEquipByTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), remap = true)
    private boolean capAmplifierInOnEquipByTick(LivingEntity entity, MobEffectInstance effect) {
        return apothic_patches$applyWithAmplifierCap(entity, effect);
    }

    @Redirect(method = "onEquipDefault", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), remap = true)
    private boolean capAmplifierInOnEquipDefault(LivingEntity entity, MobEffectInstance effect) {
        return apothic_patches$applyWithAmplifierCap(entity, effect);
    }

    @Redirect(method = "addPermanentEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z"), remap = true)
    private boolean capAmplifierInAddPermanentEffect(LivingEntity entity, MobEffectInstance effect) {
        return apothic_patches$applyWithAmplifierCap(entity, effect);
    }

    @Unique
    private boolean apothic_patches$applyWithAmplifierCap(LivingEntity target, MobEffectInstance effect) {
        if (effect == null) {
            return false;
        }

        int newAmplifier = effect.getAmplifier();
        int maxAmp = Config.defaultAmplifierCap;
        
        if (maxAmp >= 0 && newAmplifier > maxAmp) {
            newAmplifier = maxAmp;
        }
        
        MobEffectInstance cappedEffect = new MobEffectInstance(
            effect.getEffect(),
            effect.getDuration(),
            newAmplifier,
            effect.isAmbient(),
            effect.isVisible(),
            effect.showIcon()
        );
        
        return target.addEffect(cappedEffect);
    }
}