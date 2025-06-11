package org.LiangMi.soulstone.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.client.gui.SpellTooltip;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;
import net.spell_power.api.SpellPower;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.api.SpellcostMixinInterface;
import org.LiangMi.soulstone.registry.ManaRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.spell_engine.internals.SpellHelper.impactTargetingMode;

@Mixin(SpellTooltip.class)
public class SpellTooltipMixin {
    private static float getManaCost(PlayerEntity player, Spell spell, SpellHelper.ImpactContext context, float coeff, int proj){
        float mult = (float) ( + player.getAttributeValue(ManaRegistry.MANACOST) * 0.01F);

        return mult* context.total()*(((SpellcostMixinInterface)spell.cost).calculateManaCost() ? (float) Math.max( 20 , 40  * coeff * proj ) :
                ((SpellcostMixinInterface)spell.cost).getManaCost());
    }

    private static boolean matches(String subject, String nullableRegex) {
        if (subject == null) {
            return false;
        }
        if (nullableRegex == null || nullableRegex.isEmpty()) {
            return false;
        }
        Pattern pattern = Pattern.compile(nullableRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(subject);
        return matcher.find();
    }
    @ModifyReturnValue(at = @At("TAIL"), method = "spellInfo")
    private static List<Text> spellInfoMana(List<Text> tooltip, Identifier spellId, PlayerEntity player, ItemStack itemStack, boolean details) {
        try {
            Spell spell = SpellRegistry.getSpell(spellId);
            float channelMultiplier = 1.0F;
            boolean needsArrow = false;
            if (spell.cost != null && spell.cost.item_id != null) {
                needsArrow = spell.cost.item_id.contains("arrow");
            }
            if (!matches(spellId.toString(), Soulstone.config.blacklist_spell_casting_regex) &&!(needsArrow) && spell.release != null && spell.cost != null && spell.cost.item_id != null) {
                SpellHelper.ImpactContext context = new SpellHelper.ImpactContext(channelMultiplier, 1.0F, (Vec3d) null, SpellPower.getSpellPower(spell.school, player), impactTargetingMode(spell));

                float coeff = 0;
                int proj = 1;
                if(spell.impact != null && ((SpellcostMixinInterface) spell.cost).calculateManaCost() ==true) {

                    for (Spell.Impact impact : spell.impact) {
                        if (impact.action != null && impact.action.damage != null) {
                            coeff += impact.action.damage.spell_power_coefficient;
                        }
                    }
                    if (spell.impact != null && spell.impact.length > 0) {
                        coeff /= spell.impact.length;
                    }
                    if (spell.release.target != null && spell.release.target.projectile != null) {
                        proj += spell.release.target.projectile.launch_properties.extra_launch_count;
                    }
                }
                MutableText text = Text.literal(" ").append(Text.translatable("rpgmana.manacost").append(Text.literal(": ").append(String.valueOf((int) Math.round(getManaCost(player, spell, context, coeff, proj))))));
                if (SpellRegistry.getSpell(spellId).cast.channel_ticks > 0) {
                    text.append(Text.translatable("rpgmana.persecond"));
                }
                tooltip.add(text.formatted(Formatting.AQUA));
            }
        } catch (Exception ignore) {

        }
        return tooltip;
    }
}
