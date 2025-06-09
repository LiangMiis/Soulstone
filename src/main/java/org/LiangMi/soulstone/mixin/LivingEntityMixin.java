package org.LiangMi.soulstone.mixin;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.spell_engine.api.spell.Spell;
import net.spell_power.api.enchantment.SpellPowerEnchanting;
import org.LiangMi.soulstone.api.ManaInstance;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.util.ManaValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public class LivingEntityMixin implements ManaInterface {

    private static final TrackedData<Float> CURRENTMANA;

    public List<ManaInstance> manaInstances = new ArrayList<ManaInstance>(List.of());
    public int timefull = 0;

    static{
        CURRENTMANA = DataTracker.registerData(PlayerEntity.class, TrackedDataHandlerRegistry.FLOAT);

    }

    @Override
    public double getMaxMana() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        return 50+player.getAttributeValue(ManaValue.MANA);
    }
    public double getManaRegen(){
        PlayerEntity living = (PlayerEntity) (Object) this;

        return living.getAttributeValue(ManaValue.MANAREGEN)/20;
    }
    @Override
    public List<ManaInstance> getManaInstances() {
        return this.manaInstances;
    }
    @Inject(at = @At("HEAD"), method = "tick")
    public void tickMana(CallbackInfo callbackInfo) {
        LivingEntity living = (LivingEntity) (Object) this;

        if(living.getDataTracker().get(CURRENTMANA)< getMaxMana()){
            living.getDataTracker().set(CURRENTMANA,(float)Math.min(living.getDataTracker().get(CURRENTMANA)+getManaRegen(),getMaxMana()));
            timefull =0;
        }
        else{
            living.getDataTracker().set(CURRENTMANA,(float)getMaxMana());
            timefull++;
        }
        for(ManaInstance instance : this.getManaInstances()){
            instance.tick();
        }
        if(!this.getManaInstances().isEmpty()) {
            this.getManaInstances().removeIf(manaInstance -> manaInstance.remainingduration <= 0);
        }
    }
    @Inject(at = @At("TAIL"), method = "initDataTracker")
    protected void initDataTrackerMana(CallbackInfo callbackInfo) {
        LivingEntity living = (LivingEntity) (Object) this;
        living.getDataTracker().startTracking(CURRENTMANA, 0F);
    }
    @Inject(method = "createPlayerAttributes", at = @At("RETURN"))
    private static void addAttributesextraspellattributes_RETURN(final CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
        info.getReturnValue().add(ManaValue.MANA);
        info.getReturnValue().add(ManaValue.MANAREGEN);
        info.getReturnValue().add(ManaValue.MANACOST);


    }
    public @Nullable Spell lastSpell;

    public Spell getLastSpell() {
        return lastSpell;
    }

    public void setLastSpell(Spell lastSpell) {
        this.lastSpell = lastSpell;
    }
    @Override
    public double getMana() {
        LivingEntity living = (LivingEntity) (Object) this;

        return living.getDataTracker().get(CURRENTMANA);
    }

    @Override
    public double spendMana(double toadd) {
        LivingEntity living = (LivingEntity) (Object) this;
        if(living instanceof PlayerEntity player) {
            this.getManaInstances().add(new ManaInstance(player, 80, -toadd));
        }
        living.getDataTracker().set(CURRENTMANA,(float)(living.getDataTracker().get(CURRENTMANA)+toadd));
        return living.getDataTracker().get(CURRENTMANA);
    }

    @Override
    public int getTimeFull() {
        return timefull;
    }
}
