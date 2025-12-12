package org.LiangMi.soulstone.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.access.PointSystemAccess;
import org.LiangMi.soulstone.data.PlayerPointData;

import java.util.Map;

public class PointTestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(CommandManager.literal("pointtest")
                .requires(source -> source.hasPermissionLevel(2)) // 需要OP权限
                .executes(context -> showHelp(context))
                .then(CommandManager.literal("help")
                        .executes(context -> showHelp(context))
                )
                .then(CommandManager.literal("addpoints")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 1000))
                                        .executes(context -> addPoints(context,
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount")))
                                )
                        )
                )
                .then(CommandManager.literal("setpoints")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("amount", IntegerArgumentType.integer(0, 1000))
                                        .executes(context -> setPoints(context,
                                                EntityArgumentType.getPlayer(context, "player"),
                                                IntegerArgumentType.getInteger(context, "amount")))
                                )
                        )
                )
                .then(CommandManager.literal("assign")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .then(CommandManager.argument("attribute", StringArgumentType.word())
                                        .then(CommandManager.argument("amount", IntegerArgumentType.integer(1, 100))
                                                .executes(context -> assignPoints(context,
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        StringArgumentType.getString(context, "attribute"),
                                                        IntegerArgumentType.getInteger(context, "amount")))
                                        )
                                )
                        )
                )
                .then(CommandManager.literal("reset")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> resetPoints(context,
                                        EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> showInfo(context,
                                        EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(CommandManager.literal("maxall")
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> maxAllAttributes(context,
                                        EntityArgumentType.getPlayer(context, "player")))
                        )
                )
                .then(CommandManager.literal("listattributes")
                        .executes(context -> listAttributes(context))
                )
        );
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendMessage(Text.literal("§6=== 加点系统测试命令 ==="));
        source.sendMessage(Text.literal("§a/pointtest help §7- 显示此帮助信息"));
        source.sendMessage(Text.literal("§a/pointtest addpoints <玩家> <数量> §7- 给玩家添加加点值"));
        source.sendMessage(Text.literal("§a/pointtest setpoints <玩家> <数量> §7- 设置玩家的加点值"));
        source.sendMessage(Text.literal("§a/pointtest assign <玩家> <属性> <数量> §7- 给玩家分配点到指定属性"));
        source.sendMessage(Text.literal("§a/pointtest reset <玩家> §7- 重置玩家的所有加点"));
        source.sendMessage(Text.literal("§a/pointtest info <玩家> §7- 显示玩家的加点信息"));
        source.sendMessage(Text.literal("§a/pointtest maxall <玩家> §7- 将所有属性加到最大等级"));
        source.sendMessage(Text.literal("§a/pointtest listattributes §7- 列出所有可用属性"));
        source.sendMessage(Text.literal("§7示例: /pointtest addpoints Steve 50"));
        source.sendMessage(Text.literal("§7示例: /pointtest assign Steve health 10"));

        return 1;
    }

    private static int addPoints(CommandContext<ServerCommandSource> context, ServerPlayerEntity target, int amount) {
        ServerCommandSource source = context.getSource();

        PointSystemAccess.addPoints(target, amount);

        source.sendMessage(Text.literal("§a已给 " + target.getName().getString() + " 添加 " + amount + " 点加点值"));
        target.sendMessage(Text.literal("§6你获得了 " + amount + " 点加点值!"), false);

        return 1;
    }

    private static int setPoints(CommandContext<ServerCommandSource> context, ServerPlayerEntity target, int amount) {
        ServerCommandSource source = context.getSource();

        // 先获取当前数据
        PlayerPointData data = PointSystemAccess.getPlayerData(target);
        int currentTotal = data.getTotalPointsEarned();
        int currentAvailable = data.getAvailablePoints();

        // 计算需要添加的点数
        int pointsToAdd = amount - currentTotal;

        if (pointsToAdd > 0) {
            PointSystemAccess.addPoints(target, pointsToAdd);
            source.sendMessage(Text.literal("§a已将 " + target.getName().getString() + " 的加点值设置为 " + amount + " (添加了 " + pointsToAdd + " 点)"));
        } else if (pointsToAdd < 0) {
            // 如果要减少点数，需要先重置
            data.resetPoints();
            PointSystemAccess.setPlayerData(target, data);
            PointSystemAccess.addPoints(target, amount);
            source.sendMessage(Text.literal("§a已将 " + target.getName().getString() + " 的加点值设置为 " + amount + " (重置后重新设置)"));
        } else {
            source.sendMessage(Text.literal("§e" + target.getName().getString() + " 的加点值已经是 " + amount));
        }

        target.sendMessage(Text.literal("§6你的加点值已被设置为 " + amount + "!"), false);

        return 1;
    }

    private static int assignPoints(CommandContext<ServerCommandSource> context, ServerPlayerEntity target,
                                    String attribute, int amount) {
        ServerCommandSource source = context.getSource();

        // 检查属性是否有效
        if (!isValidAttribute(attribute)) {
            source.sendMessage(Text.literal("§c无效的属性: " + attribute));
            source.sendMessage(Text.literal("§c使用 /pointtest listattributes 查看所有可用属性"));
            return 0;
        }

        boolean success = PointSystemAccess.assignPoints(target, attribute, amount);

        if (success) {
            PlayerPointData data = PointSystemAccess.getPlayerData(target);
            int newPoints = data.getAssignedPoints(attribute);
            String attributeName = getAttributeDisplayName(attribute);

            source.sendMessage(Text.literal("§a已给 " + target.getName().getString() + " 的 " + attributeName + " 分配 " + amount + " 点"));
            source.sendMessage(Text.literal("§a当前 " + attributeName + " 等级: " + newPoints));
            target.sendMessage(Text.literal("§6你的 " + attributeName + " 增加了 " + amount + " 点!"), false);
        } else {
            source.sendMessage(Text.literal("§c分配点数失败! 可能原因:"));
            source.sendMessage(Text.literal("§c- 点数不足"));
            source.sendMessage(Text.literal("§c- 已达到最大等级"));
            source.sendMessage(Text.literal("§c- 属性不存在"));
        }

        return success ? 1 : 0;
    }

    private static int resetPoints(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) {
        ServerCommandSource source = context.getSource();

        boolean success = PointSystemAccess.resetPoints(target);

        if (success) {
            source.sendMessage(Text.literal("§a已重置 " + target.getName().getString() + " 的所有加点值"));
            target.sendMessage(Text.literal("§6你的所有加点值已被重置!"), false);
        } else {
            source.sendMessage(Text.literal("§c" + target.getName().getString() + " 没有加点值可以重置"));
        }

        return success ? 1 : 0;
    }

    private static int showInfo(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) {
        ServerCommandSource source = context.getSource();
        PlayerPointData data = PointSystemAccess.getPlayerData(target);

        source.sendMessage(Text.literal("§6=== " + target.getName().getString() + " 的加点信息 ==="));
        source.sendMessage(Text.literal("§a可用加点值: §e" + data.getAvailablePoints()));
        source.sendMessage(Text.literal("§a总获得点数: §e" + data.getTotalPointsEarned()));
        source.sendMessage(Text.literal("§a已分配点数: §e" + data.getAllAssignedPoints().values().stream().mapToInt(Integer::intValue).sum()));

        source.sendMessage(Text.literal("§6已分配的属性:"));
        Map<String, Integer> assignedPoints = data.getAllAssignedPoints();
        boolean hasAssignedPoints = false;

        for (Map.Entry<String, Integer> entry : assignedPoints.entrySet()) {
            if (entry.getValue() > 0) {
                String attributeName = getAttributeDisplayName(entry.getKey());
                source.sendMessage(Text.literal("  §7- §b" + attributeName + ": §e" + entry.getValue() + " 点"));
                hasAssignedPoints = true;
            }
        }

        if (!hasAssignedPoints) {
            source.sendMessage(Text.literal("  §7- §c暂无分配的属性点"));
        }

        return 1;
    }

    private static int maxAllAttributes(CommandContext<ServerCommandSource> context, ServerPlayerEntity target) {
        ServerCommandSource source = context.getSource();
        PlayerPointData data = PointSystemAccess.getPlayerData(target);

        // 计算需要多少点才能加满所有属性
        int totalPointsNeeded = 0;
        for (String attribute : getAllAttributes()) {
            int currentPoints = data.getAssignedPoints(attribute);
            int maxLevel = getMaxLevel(attribute);
            int pointsNeeded = maxLevel - currentPoints;
            if (pointsNeeded > 0) {
                totalPointsNeeded += pointsNeeded;
            }
        }

        // 如果点数不够，先添加足够的点数
        if (data.getAvailablePoints() < totalPointsNeeded) {
            int pointsToAdd = totalPointsNeeded - data.getAvailablePoints();
            PointSystemAccess.addPoints(target, pointsToAdd);
            source.sendMessage(Text.literal("§a已添加 " + pointsToAdd + " 点以便加满所有属性"));
        }

        // 加满所有属性
        int attributesMaxed = 0;
        for (String attribute : getAllAttributes()) {
            int currentPoints = data.getAssignedPoints(attribute);
            int maxLevel = getMaxLevel(attribute);
            int pointsToAdd = maxLevel - currentPoints;

            if (pointsToAdd > 0) {
                PointSystemAccess.assignPoints(target, attribute, pointsToAdd);
                attributesMaxed++;
            }
        }

        source.sendMessage(Text.literal("§a已将 " + target.getName().getString() + " 的 " + attributesMaxed + " 个属性加满"));
        target.sendMessage(Text.literal("§6你的所有属性已被加满!"), false);

        return 1;
    }

    private static int listAttributes(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();

        source.sendMessage(Text.literal("§6=== 所有可用属性 ==="));

        String[] attributes = getAllAttributes();
        String[] attributeNames = getAttributeDisplayNames();
        int[] maxLevels = getMaxLevels();

        for (int i = 0; i < attributes.length; i++) {
            String attr = attributes[i];
            String name = attributeNames[i];
            int maxLevel = maxLevels[i];

            source.sendMessage(Text.literal("  §b" + name + " (§7" + attr + "§b)"));
            source.sendMessage(Text.literal("    §7最大等级: §e" + maxLevel));
            source.sendMessage(Text.literal("    §7效果: §a" + getEffectDescription(attr)));
        }

        return 1;
    }

    // 辅助方法
    private static boolean isValidAttribute(String attribute) {
        for (String attr : getAllAttributes()) {
            if (attr.equals(attribute)) {
                return true;
            }
        }
        return false;
    }

    private static String getAttributeDisplayName(String attribute) {
        String[] attributes = getAllAttributes();
        String[] names = getAttributeDisplayNames();

        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].equals(attribute)) {
                return names[i];
            }
        }
        return attribute;
    }

    private static String[] getAllAttributes() {
        return new String[]{
                "health", "attack", "defense", "speed",
                "mining_speed", "luck", "experience",
                "jump_height", "swim_speed", "fall_resistance",
                "knockback_resistance", "critical_chance", "critical_damage"
        };
    }

    private static String[] getAttributeDisplayNames() {
        return new String[]{
                "生命值", "攻击力", "防御力", "移动速度",
                "挖掘速度", "幸运值", "经验加成",
                "跳跃高度", "游泳速度", "摔落抗性",
                "击退抗性", "暴击几率", "暴击伤害"
        };
    }

    private static int[] getMaxLevels() {
        return new int[]{
                100, 50, 50, 30,
                50, 20, 30,
                20, 20, 20,
                30, 30, 25
        };
    }

    private static String getEffectDescription(String attribute) {
        switch (attribute) {
            case "health": return "每点增加1颗心";
            case "attack": return "每点增加1点攻击伤害";
            case "defense": return "每点增加1点护甲值";
            case "speed": return "每点增加1%移动速度";
            case "mining_speed": return "每点增加10%挖掘速度";
            case "luck": return "每点增加5%幸运值";
            case "experience": return "每点增加10%经验获取";
            case "jump_height": return "每点增加5%跳跃高度";
            case "swim_speed": return "每点增加5%游泳速度";
            case "fall_resistance": return "每点减少5%摔落伤害";
            case "knockback_resistance": return "每点增加3%击退抗性";
            case "critical_chance": return "每点增加1%暴击几率";
            case "critical_damage": return "每点增加2%暴击伤害";
            default: return "未知效果";
        }
    }

    private static int getMaxLevel(String attribute) {
        switch (attribute) {
            case "health": return 100;
            case "attack": return 50;
            case "defense": return 50;
            case "speed": return 30;
            case "mining_speed": return 50;
            case "luck": return 20;
            case "experience": return 30;
            case "jump_height": return 20;
            case "swim_speed": return 20;
            case "fall_resistance": return 20;
            case "knockback_resistance": return 30;
            case "critical_chance": return 30;
            case "critical_damage": return 25;
            default: return 50;
        }
    }
}
