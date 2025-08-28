package org.LiangMi.soulstone.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.LiangMi.soulstone.client.hud.bar.FoodBar;
import org.LiangMi.soulstone.client.hud.bar.HealthBar;
import org.LiangMi.soulstone.client.hud.bar.ManaBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class GuiMixin {
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V"),method = "render")
    public void disableStatusBars(InGameHud instance, DrawContext context){
        // Disable rendering of all survival elements
    }
    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/gui/DrawContext;)V"), method = "render")
    private void disableMountBars(InGameHud instance, DrawContext context) {
        // Disable rendering of all survival elements
    }

    private static final HealthBar healthBar = new HealthBar();
    private static final ManaBar manaBar = new ManaBar();
    private static final FoodBar foodBar = new FoodBar();

    @Inject(method = "render", at = @At(value = "HEAD"))
    public void renderBars(DrawContext context, float tickDelta, CallbackInfo ci) {
        HealthBar.isUseSeparateIconsIDEA(true);
        healthBar.render(context, tickDelta);
        ManaBar.isUseSeparateIconsIDEA(true);
        manaBar.render(context, tickDelta);
        FoodBar.isUseSeparateIconsIDEA(true);
        foodBar.render(context,tickDelta);
    }
}
