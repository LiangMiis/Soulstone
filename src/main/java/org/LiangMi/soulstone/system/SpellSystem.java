package org.LiangMi.soulstone.system;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.casting.SpellCast;
import net.spell_power.api.SpellPower;
import org.LiangMi.soulstone.Soulstone;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.data.PlayerSpellData;
import org.LiangMi.soulstone.registry.EntityRegistry;
import org.LiangMi.soulstone.spell.SpellLv1;
import org.LiangMi.soulstone.util.HelperMethods;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellSystem {
    public static void spellKeyManager(PlayerEntity player,String KeyType){
        boolean spell_success = false; // 标记技能是否成功释放
        String spell = "none"; // 存储当前使用的技能名称
        if(KeyType.contains("arcane_bolt")){
            spell_success = SpellLv1.signatureWizardArcaneBolt(player);
            spell = "ArcaneBolt";
        }
        if(KeyType.contains("sacred_orb")){
            spell_success = SpellLv1.signatureClericSacredOrb(player);
            spell = "SacredOrb";
        }
        if(KeyType.contains("ghostwalk")){
            spell_success = SpellLv1.ghostwalk(player);
            spell = "GhostWalk";
        }
        if(KeyType.contains("meteor_shower")){
            spell_success = SpellLv1.signatureWizardMeteorShower(player);
            spell = "MeteorShower";
        }
        // 向客户端返回冷却时间（仅在服务端执行）
        if (!player.getWorld().isClient) {
            SpellSystem.signatureSpellCooldownManager(spell, spell_success, player);
            System.out.println("Using ability: " + spell);
        }
    }

    public static void signatureSpellCooldownManager(String spell, boolean useSuccess, PlayerEntity player) {
        int minimumCD = 5 * 1000;
        int useDelay = (int) 0.5 * 1000;
        int cooldown = 500; // 默认冷却时间
        double sendCooldown; // 最终发送的冷却时间
        String type = ""; // 技能类型描述
        String cooldownType = "none"; // 冷却时间类型
        switch (spell){
            case "SacredOrb" ->{
                cooldown = 10 * 1000;
                type = "magic, arcane";
                cooldownType = "SpellKey1";
            }
        }
        // 冷却时间计算：考虑法术急速的影响
        sendCooldown = cooldown;
        // 确保冷却时间不低于最小值，如果技能释放失败则使用最小使用间隔
        if (sendCooldown < (minimumCD) && useSuccess) sendCooldown = minimumCD;
        if (!useSuccess) sendCooldown = useDelay;
        // 调试信息输出
//        System.out.println("Ability type: " + type);
//        System.out.println(cooldownType);
//        System.out.println(cooldown);
//        System.out.println(sendCooldown);
        sendCooldownPacket((ServerPlayerEntity) player, (int) sendCooldown, cooldownType);
    }
    public static void updatePlayerSpell(ServerPlayerEntity player) {
        if (player == null || player.getWorld().isClient) return;

        PlayerSpellData spellData = SpellAccess.getPlayerData(player);

    }
    /**
     * 施放自动瞄准的法术（无目标锁定）
     * @param player 玩家实体
     * @param spellIdentifier 法术标识符
     */
    public static void castSpellEngineDumbFire(PlayerEntity player, String spellIdentifier) {
        // 施放指向玩家视线方向的法术
        SpellCast.Action action = SpellCast.Action.RELEASE;
        Identifier spellID      = new Identifier(spellIdentifier);
        List<Entity> list       = new ArrayList<Entity>();

        SpellHelper.performSpell(
                player.getWorld(),
                player,
                spellID,
                list,
                action,
                20);
    }

    /**
     * 施放法术引擎中的法术
     * @param player 玩家实体
     * @param spellIdentifier 法术标识符
     */
    public static void castSpellEngine(PlayerEntity player, String spellIdentifier) {
        ItemStack itemStack     = player.getMainHandStack();
        Identifier spellID      = new Identifier(spellIdentifier);

        SpellHelper.attemptCasting(player, itemStack, spellID, false);
    }

    /**
     * 间接目标施放法术
     * @param player 玩家实体
     * @param spellIdentifier 法术标识符
     * @param range 施法范围
     * @param target 目标实体（可为空）
     * @param blockpos 目标方块位置（可为空）
     */
    public static void castSpellEngineIndirectTarget(PlayerEntity player, String spellIdentifier, int range, @Nullable Entity target, @Nullable BlockPos blockpos) {
        // 如果目标为空但有方块位置，在方块位置生成目标实体
        if (target == null && blockpos != null) {
            target = EntityRegistry.SPELL_TARGET_ENTITY.spawn( (ServerWorld) player.getWorld(),
                    blockpos,
                    SpawnReason.TRIGGERED);
        } else if (target == null && blockpos == null) {
            // 如果目标和方块位置都为空，获取玩家正在看的方块
            blockpos = HelperMethods.getBlockLookingAt(player, range);
            if (blockpos != null) {
                target = EntityRegistry.SPELL_TARGET_ENTITY.spawn((ServerWorld) player.getWorld(),
                        blockpos,
                        SpawnReason.TRIGGERED);
            }
        }

        // 如果存在目标，施放指向该目标的法术
        if (target != null) {
            ItemStack itemStack     = player.getMainHandStack();
            Hand hand               = player.getActiveHand();
            SpellCast.Action action = SpellCast.Action.RELEASE;
            Identifier spellID      = new Identifier(spellIdentifier);
            List<Entity> list       = new ArrayList<Entity>();
            list.add(target);

            SpellHelper.performSpell(
                    player.getWorld(),
                    player,
                    spellID,
                    list,
                    action,
                    1);
        }
    }

    /**
     * 施放范围效果法术
     * @param player 玩家实体
     * @param spellIdentifier 法术标识符
     * @param radius 作用半径
     * @param chance 命中几率
     * @param singleTarget 是否只针对单个目标
     * @param ignorePassive 是否忽略被动生物
     * @return 是否成功施放法术
     */
    public static boolean castSpellEngineAOE(PlayerEntity player, String spellIdentifier, int radius, int chance, boolean singleTarget, boolean ignorePassive) {
        // 施放影响范围内多个目标的法术
        ItemStack itemStack     = player.getMainHandStack();
        Hand hand               = player.getActiveHand();
        SpellCast.Action action = SpellCast.Action.RELEASE;
        Identifier spellID      = new Identifier(spellIdentifier);
        List<Entity> list       = new ArrayList<Entity>();

        // 创建搜索范围框
        Box box = HelperMethods.createBox(player, radius);
        // 遍历范围内的所有实体
        for (Entity entities : player.getWorld().getOtherEntities(player, box, EntityPredicates.VALID_LIVING_ENTITY)) {
            if (entities != null) {
                // 如果忽略被动生物且当前实体是被动生物，则跳过
                if (entities instanceof PassiveEntity && ignorePassive)
                    continue;
                // 检查是否为友军伤害
                if ((entities instanceof LivingEntity le) && HelperMethods.checkFriendlyFire(le, player)) {
                    // 根据几率决定是否将实体加入目标列表
                    if (player.getRandom().nextInt(100) < chance)
                        list.add(le);
                    // 如果只针对单个目标，找到第一个目标后退出循环
                    if (singleTarget)
                        break;
                }
            }
        }

        // 如果找到目标，施放法术
        if (!list.isEmpty()) {
            SpellHelper.performSpell(
                    player.getWorld(),
                    player,
                    spellID,
                    list,
                    action,
                    20);

            return true;
        }
        return false;
    }
    @Environment(EnvType.CLIENT)
    public static void sendKeybindPacket(String string){
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(string);
        ClientPlayNetworking.send(new Identifier(Soulstone.ID,"spell"),buf);
    }
    public static void sendCooldownPacket(ServerPlayerEntity player, int cooldown, String cooldownType) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(cooldown);
        buf.writeString(cooldownType);
        ServerPlayNetworking.send(player,new Identifier(Soulstone.ID,"cooldown"),buf);
    }
}
