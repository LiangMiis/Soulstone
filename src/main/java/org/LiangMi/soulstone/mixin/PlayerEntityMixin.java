package org.LiangMi.soulstone.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.LiangMi.soulstone.access.AnchorManagerAccess;
import org.LiangMi.soulstone.access.PlayerAccess;
import org.LiangMi.soulstone.manager.AnchorManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements AnchorManagerAccess, PlayerAccess {

    private AnchorManager anchorManager = new AnchorManager();

    @Override
    public AnchorManager getAnchorManager(){
        return  this.anchorManager;
    }


    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromTagMixin(NbtCompound tag, CallbackInfo info) {
        // 从NBT中读取口渴数据
        this.anchorManager.readNbt(tag);
    }
    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToTagMixin(NbtCompound tag, CallbackInfo info) {
        // 向NBT中写入口渴数据
        this.anchorManager.writeNbt(tag);
    }

}
