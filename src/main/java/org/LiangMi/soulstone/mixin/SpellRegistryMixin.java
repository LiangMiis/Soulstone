package org.LiangMi.soulstone.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellRegistry;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 混入法术注册表类(SpellRegistry.class)，用于在法术加载后注入自定义逻辑
@Mixin(value = SpellRegistry.class)
public class SpellRegistryMixin {

    /**
     * 注入到法术加载方法的尾部
     * 作用：为法术配置自定义的魔力消耗值
     *
     * @param resourceManager 资源管理器（用于访问法术JSON文件）
     * @param callbackInfo    回调信息对象
     */
    @Inject(at = @At("TAIL"), method = "loadSpells")
    private static void manaCosts(ResourceManager resourceManager, CallbackInfo callbackInfo) {
        // 遍历配置文件中定义的所有法术魔力消耗值
        Soulstone.config.spells.entrySet().iterator().forEachRemaining(entry -> {
            // 获取法术ID对应的法术对象
            Spell spell = SpellRegistry.getSpell(new Identifier(entry.getKey()));

            // 调试输出当前处理法术
            System.out.println(spell);

            // 确保法术存在且可访问
            if (spell != null) {
                // 通过混入接口设置法术的自定义魔力消耗值
                ((SpellcostMixinInterface) spell.cost).setManaCost(entry.getValue());
            }
        });
    }
}
