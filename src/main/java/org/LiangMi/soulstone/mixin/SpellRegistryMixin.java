package org.LiangMi.soulstone.mixin;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellRegistry;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// 混入（Mixin）法术注册表类，用于在加载法术时修改法术的魔力消耗
@Mixin(SpellRegistry.class) // 指定要混入的类
public class SpellRegistryMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellRegistryMixin.class);

    // 注入到loadSpells方法的尾部，用于在加载所有法术后设置自定义魔力消耗
    @Inject(at = @At("TAIL"), method = "loadSpells")
    private static void manaCosts(ResourceManager resourceManager, CallbackInfo callbackInfo) {
        // 遍历配置文件中定义的所有法术和对应的魔力消耗
        Soulstone.config.spells.entrySet().iterator().forEachRemaining(entry -> {
            // 根据法术标识符获取法术对象
            Spell spell = SpellRegistry.getSpell(new Identifier(entry.getKey()));

            // 如果法术存在且不为空
            if (spell != null) {
                try {
                    // 通过混入接口设置法术的魔力消耗
                    ((SpellcostMixinInterface) spell.cost).setManaCost(entry.getValue());
                    // 记录成功设置的法术信息（可选，用于调试）
                    LOGGER.info("成功设置法术 {} 的魔力消耗为: {}", entry.getKey(), entry.getValue());
                } catch (ClassCastException e) {
                    // 处理类型转换异常，记录错误日志
                    LOGGER.error("法术 {} 的cost对象不支持SpellcostMixinInterface接口", entry.getKey(), e);
                }
            } else {
                // 记录未找到法术的警告
                LOGGER.warn("未找到标识符为 {} 的法术", entry.getKey());
            }
        });
    }
}
