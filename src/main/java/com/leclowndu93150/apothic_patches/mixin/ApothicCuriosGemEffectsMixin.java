package com.leclowndu93150.apothic_patches.mixin;

import com.google.common.util.concurrent.AtomicDouble;
import daripher.apothiccurios.ApothicCuriosMod;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketedGems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.event.level.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mixin(value = ApothicCuriosMod.class, remap = false)
public class ApothicCuriosGemEffectsMixin {

    @Inject(method = "applyCurioShieldBlockAffixes", at = @At("TAIL"), remap = false)
    private void applyCurioGemShieldEffects(ShieldBlockEvent event, CallbackInfo ci) {
        AtomicReference<Float> blocked = new AtomicReference<>(event.getBlockedDamage());
        LivingEntity entity = event.getEntity();
        DamageSource damageSource = event.getDamageSource();

        ApothicCuriosAccessor.invokeGetEquippedCurios(entity).forEach(curio -> {
            SocketedGems gems = SocketHelper.getGems(curio);
            blocked.set(gems.onShieldBlock(entity, damageSource, blocked.get()));
        });
        
        if (blocked.get() != event.getOriginalBlockedDamage()) {
            event.setBlockedDamage(blocked.get());
        }
    }

    @Inject(method = "applyCurioBlockBreakAffixes", at = @At("TAIL"), remap = false)
    private void applyCurioGemBlockBreakEffects(BlockEvent.BreakEvent event, CallbackInfo ci) {
        Player player = event.getPlayer();
        LevelAccessor level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = event.getState();

        apothic_patches$getEquippedCurios(player).forEach(curio -> {
            SocketedGems gems = SocketHelper.getGems(curio);
            gems.onBlockBreak(player, level, pos, state);
        });
    }

    @Inject(method = "applyCurioArrowAffixes", at = @At("TAIL"), remap = false)
    private void applyCurioGemArrowEffects(EntityJoinLevelEvent event, CallbackInfo ci) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (arrow.getPersistentData().getBoolean("apoth.generated")) return;
        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity living)) return;

        apothic_patches$getEquippedCurios(living).forEach(curio -> {
            SocketedGems gems = SocketHelper.getGems(curio);
            gems.onArrowFired(living, arrow);
        });
    }

    @Inject(method = "applyCurioDamageAffixes", at = @At("TAIL"), remap = false)
    private void applyCurioGemDamageEffects(LivingHurtEvent event, CallbackInfo ci) {
        DamageSource source = event.getSource();
        LivingEntity entity = event.getEntity();
        AtomicDouble amount = new AtomicDouble(event.getAmount());

        ApothicCuriosAccessor.invokeGetEquippedCurios(entity).forEach(curio -> {
            SocketedGems gems = SocketHelper.getGems(curio);
            amount.set(gems.onHurt(source, entity, amount.floatValue()));
        });
        
        event.setAmount(amount.floatValue());
    }

    @Inject(method = "applyCurioDamageAffixes", at = @At("HEAD"), remap = false)
    private void applyCurioGemPostAttackEffects(LivingHurtEvent event, CallbackInfo ci) {
        DamageSource source = event.getSource();
        LivingEntity target = event.getEntity();
        
        if (source.getEntity() instanceof LivingEntity attacker) {
            apothic_patches$getEquippedCurios(attacker).forEach(curio -> {
                SocketedGems gems = SocketHelper.getGems(curio);
                gems.doPostAttack(attacker, target);
            });
        }

        apothic_patches$getEquippedCurios(target).forEach(curio -> {
            SocketedGems gems = SocketHelper.getGems(curio);
            gems.doPostHurt(target, source.getEntity());
        });
    }

    @Unique
    private static List<ItemStack> apothic_patches$getEquippedCurios(LivingEntity entity) {
        List<ItemStack> curios = new ArrayList();
        CuriosApi.getCuriosInventory(entity).map(ICuriosItemHandler::getEquippedCurios).ifPresent((i) -> {
            for(int slot = 0; slot < i.getSlots(); ++slot) {
                curios.add(i.getStackInSlot(slot));
            }

        });
        return curios;
    }
}