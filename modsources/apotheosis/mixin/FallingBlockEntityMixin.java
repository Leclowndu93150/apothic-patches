package dev.shadowsoffire.apotheosis.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;

import dev.shadowsoffire.apotheosis.util.INBTSensitiveFallingBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

// TODO: Forge PR?
@Mixin(FallingBlockEntity.class)
public abstract class FallingBlockEntityMixin extends Entity {

    public FallingBlockEntityMixin(EntityType<?> pType, Level pLevel) {
        super(pType, pLevel);
    }

    @Nullable
    @Override
    public ItemEntity spawnAtLocation(ItemLike pItem) {
        if (pItem instanceof INBTSensitiveFallingBlock nbtSensitive && ths().blockData != null) {
            return this.ths().spawnAtLocation(nbtSensitive.toStack(this.ths().getBlockState(), this.ths().blockData), 0F);
        }
        return this.ths().spawnAtLocation(pItem, 0);
    }

    private FallingBlockEntity ths() {
        return (FallingBlockEntity) (Object) this;
    }

}
