package org.LiangMi.soulstone.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.LiangMi.soulstone.manager.GameStageManager;

import java.util.Collection;
import java.util.Map;


public class StageCommand {


    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {


        dispatcher.register(CommandManager.literal("gamestage")
                .requires(source -> source.hasPermissionLevel(2)) // 需要OP权限
                .then(CommandManager.literal("get")
                        .executes(context -> getStage(context, context.getSource().getPlayer()))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> getStage(context, EntityArgumentType.getPlayer(context, "player")))))

                .then(CommandManager.literal("set")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer())
                                .suggests((context, builder) -> {
                                    // 提供等级的自动补全
                                    String[] levelStrings = GameStageManager.getAllLevelStrings();
                                    for (String levelStr : levelStrings) {
                                        builder.suggest(levelStr);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> setLevel(context, context.getSource().getPlayer(), IntegerArgumentType.getInteger(context, "level")))
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(context -> setLevelForPlayers(context, EntityArgumentType.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "level"))))))

                .then(CommandManager.literal("unlock")
                        .then(CommandManager.argument("level", IntegerArgumentType.integer())
                                .suggests((context, builder) -> {
                                    String[] levelStrings = GameStageManager.getAllLevelStrings();
                                    for (String levelStr : levelStrings) {
                                        builder.suggest(levelStr);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> unlockLevel(context, context.getSource().getPlayer(), IntegerArgumentType.getInteger(context, "level")))
                                .then(CommandManager.argument("player", EntityArgumentType.players())
                                        .executes(context -> unlockLevelForPlayers(context, EntityArgumentType.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "level"))))))

                .then(CommandManager.literal("unlockall")
                        .executes(context -> unlockAllLevels(context, context.getSource().getPlayer()))
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(context -> unlockAllLevelsForPlayers(context, EntityArgumentType.getPlayers(context, "player")))))

                .then(CommandManager.literal("list")
                        .executes(context -> listLevels(context, context.getSource().getPlayer()))
                        .then(CommandManager.argument("player", EntityArgumentType.player())
                                .executes(context -> listLevels(context, EntityArgumentType.getPlayer(context, "player")))))

                .then(CommandManager.literal("reset")
                        .executes(context -> resetLevel(context, context.getSource().getPlayer()))
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                                .executes(context -> resetLevelForPlayers(context, EntityArgumentType.getPlayers(context, "player")))))

                .then(CommandManager.literal("help")
                        .executes(StageCommand::showHelp))

                .then(CommandManager.literal("info")
                        .executes(StageCommand::showLevelInfo))
        );
    }

    private static int getStage(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        int currentLevel = GameStageManager.getCurrentLevel(player);
        String levelDisplayName = GameStageManager.getCurrentLevelName(player);

        Text message = Text.literal("玩家 ")
                .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                .append(" 的当前等级: ")
                .append(Text.literal("Lv." + currentLevel).styled(style -> style.withColor(Formatting.AQUA).withBold(true)))
                .append(" (")
                .append(Text.literal(levelDisplayName).styled(style -> style.withColor(Formatting.GREEN)))
                .append(")");

        context.getSource().sendFeedback(() -> message, false);
        return 1;
    }

    private static int setLevel(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, int level) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        boolean success = GameStageManager.setCurrentLevel(player, level);

        if (success) {
            final int finalLevel = level;
            context.getSource().sendFeedback(() ->
                    Text.literal("已将玩家 ")
                            .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                            .append(" 的等级设置为: ")
                            .append(Text.literal("Lv." + finalLevel).styled(style -> style.withColor(Formatting.AQUA).withBold(true)))
                            .append(" (")
                            .append(Text.literal(GameStageManager.getLevelName(finalLevel)).styled(style -> style.withColor(Formatting.GREEN)))
                            .append(")"), false);
            return 1;
        } else {
            if (!GameStageManager.isValidLevel(level)) {
                context.getSource().sendError(Text.literal("无效等级: " + level + "，可用等级: 20, 30, 40, 50").styled(style -> style.withColor(Formatting.RED)));
            } else {
                context.getSource().sendError(Text.literal("无法设置等级: 等级 " + level + " 未解锁").styled(style -> style.withColor(Formatting.RED)));
            }
            return 0;
        }
    }

    private static int setLevelForPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players, int level) throws CommandSyntaxException {
        if (!GameStageManager.isValidLevel(level)) {
            context.getSource().sendError(Text.literal("无效等级: " + level + "，可用等级: 20, 30, 40, 50").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        int count = 0;

        for (ServerPlayerEntity player : players) {
            boolean success = GameStageManager.setCurrentLevel(player, level);
            if (success) {
                count++;
            }
        }

        final int finalCount = count;
        final int finalLevel = level;
        context.getSource().sendFeedback(() ->
                Text.literal("已为 " + finalCount + " 名玩家设置等级为: Lv." + finalLevel).styled(style -> style.withColor(Formatting.GREEN)), false);

        return count;
    }

    private static int unlockLevel(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, int level) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }


        if (!GameStageManager.isValidLevel(level)) {
            context.getSource().sendError(Text.literal("无效等级: " + level + "，可用等级: 20, 30, 40, 50").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        boolean success = GameStageManager.unlockLevel(player, level);

        if (success) {
            final int finalLevel = level;
            context.getSource().sendFeedback(() ->
                    Text.literal("已为玩家 ")
                            .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                            .append(" 解锁等级: Lv." + finalLevel).styled(style -> style.withColor(Formatting.GREEN)), false);
            return 1;
        } else {
            // 可能已经解锁了
            if (GameStageManager.hasLevelUnlocked(player, level)) {
                final int finalLevel = level;
                context.getSource().sendFeedback(() ->
                        Text.literal("玩家 ")
                                .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                                .append(" 已经解锁了等级: Lv." + finalLevel).styled(style -> style.withColor(Formatting.YELLOW)), false);
                return 1;
            } else {
                context.getSource().sendError(Text.literal("无法解锁等级: Lv." + level).styled(style -> style.withColor(Formatting.RED)));
                return 0;
            }
        }
    }

    private static int unlockLevelForPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players, int level) throws CommandSyntaxException {
        if (!GameStageManager.isValidLevel(level)) {
            context.getSource().sendError(Text.literal("无效等级: " + level + "，可用等级: 20, 30, 40, 50").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        int count = 0;

        for (ServerPlayerEntity player : players) {
            boolean success = GameStageManager.unlockLevel(player, level);
            if (success) {
                count++;
            } else if (GameStageManager.hasLevelUnlocked(player, level)) {
                count++; // 已经解锁的也算成功
            }
        }

        final int finalCount = count;
        final int finalLevel = level;
        context.getSource().sendFeedback(() ->
                Text.literal("已为 " + finalCount + " 名玩家解锁等级: Lv." + finalLevel).styled(style -> style.withColor(Formatting.GREEN)), false);

        return count;
    }

    private static int unlockAllLevels(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        GameStageManager.unlockAllLevels(player);
        context.getSource().sendFeedback(() ->
                Text.literal("已为玩家 ")
                        .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                        .append(" 解锁所有等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        return 1;
    }

    private static int unlockAllLevelsForPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        int count = 0;

        for (ServerPlayerEntity player : players) {
            GameStageManager.unlockAllLevels(player);
            count++;
        }

        final int finalCount = count;
        context.getSource().sendFeedback(() ->
                Text.literal("已为 " + finalCount + " 名玩家解锁所有等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        return count;
    }

    private static int listLevels(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        Map<Integer, Boolean> unlockedLevels = GameStageManager.getUnlockedLevels(player);
        int currentLevel = GameStageManager.getCurrentLevel(player);

        // 发送标题
        context.getSource().sendFeedback(() ->
                Text.literal("=== ").styled(style -> style.withColor(Formatting.GRAY))
                        .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                        .append(Text.literal(" 的等级状态 ===").styled(style -> style.withColor(Formatting.GRAY))), false);

        // 列出所有等级
        for (int level : GameStageManager.getAllLevels()) {
            boolean unlocked = unlockedLevels.getOrDefault(level, false);
            boolean isCurrent = level == currentLevel;

            Formatting levelColor = unlocked ? Formatting.GREEN : Formatting.RED;
            MutableText levelText = Text.literal("  " + (isCurrent ? "▶ " : "  "))
                    .append(Text.literal("Lv." + level).styled(style -> style.withColor(levelColor).withBold(isCurrent)))
                    .append(": ")
                    .append(Text.literal(GameStageManager.getLevelName(level)));

            if (isCurrent) {
                levelText = levelText.copy().styled(style -> style.withBold(true));
            }

            // 添加点击事件来设置等级（如果已解锁）
            if (unlocked && !isCurrent) {
                final int finalLevel = level;
                Style clickableStyle = Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/gamestage set " + level + " " + player.getName().getString()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("点击设置为当前等级")));
                levelText = levelText.copy().setStyle(clickableStyle);
            }

            MutableText finalLevelText = levelText;
            context.getSource().sendFeedback(() -> finalLevelText, false);
        }

        return 1;
    }

    private static int addProgress(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String type, int amount) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        GameStageManager.addPlayerProgress(player, type, amount);

        int newValue = GameStageManager.getPlayerProgress(player, type);
        final int finalNewValue = newValue;
        final String finalType = type;
        context.getSource().sendFeedback(() ->
                Text.literal("已为玩家 ")
                        .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                        .append(" 增加进度: " + finalType + " +" + amount + " = " + finalNewValue).styled(style -> style.withColor(Formatting.GREEN)), false);

        return 1;
    }

    private static int getProgress(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, String type) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        int value = GameStageManager.getPlayerProgress(player, type);
        final int finalValue = value;
        final String finalType = type;
        context.getSource().sendFeedback(() ->
                Text.literal("玩家 ")
                        .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                        .append(" 的进度 " + finalType + ": " + finalValue).styled(style -> style.withColor(Formatting.GREEN)), false);

        return 1;
    }

    private static int resetLevel(CommandContext<ServerCommandSource> context, ServerPlayerEntity player) throws CommandSyntaxException {
        if (player == null) {
            context.getSource().sendError(Text.literal("玩家不存在或不在线").styled(style -> style.withColor(Formatting.RED)));
            return 0;
        }

        GameStageManager.resetLevel(player);
        context.getSource().sendFeedback(() ->
                Text.literal("已重置玩家 ")
                        .append(Text.literal(player.getName().getString()).styled(style -> style.withColor(Formatting.GOLD)))
                        .append(" 的等级为 Lv.20").styled(style -> style.withColor(Formatting.GREEN)), false);

        return 1;
    }

    private static int resetLevelForPlayers(CommandContext<ServerCommandSource> context, Collection<ServerPlayerEntity> players) throws CommandSyntaxException {
        int count = 0;

        for (ServerPlayerEntity player : players) {
            GameStageManager.resetLevel(player);
            count++;
        }

        final int finalCount = count;
        context.getSource().sendFeedback(() ->
                Text.literal("已重置 " + finalCount + " 名玩家的等级为 Lv.20").styled(style -> style.withColor(Formatting.GREEN)), false);

        return count;
    }

    private static int showHelp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource player = context.getSource();
        if (player==null) return 0;
        // 显示帮助信息
        context.getSource().sendFeedback(() ->
                Text.literal("=== 游戏等级命令帮助 ===").styled(style -> style.withColor(Formatting.GOLD).withBold(true)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage get [玩家] - 查看当前等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage set <等级> [玩家] - 设置等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage unlock <等级> [玩家] - 解锁等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage unlockall [玩家] - 解锁所有等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage list [玩家] - 列出所有等级状态").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage progress add <类型> <数量> - 增加进度").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage progress get <类型> - 查看进度").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage reset [玩家] - 重置等级").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage info - 显示等级信息").styled(style -> style.withColor(Formatting.GREEN)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("/gamestage help - 显示此帮助").styled(style -> style.withColor(Formatting.GREEN)), false);

        StringBuilder levelsBuilder = new StringBuilder();
        int[] allLevels = GameStageManager.getAllLevels();
        for (int i = 0; i < allLevels.length; i++) {
            levelsBuilder.append(allLevels[i]);
            if (i < allLevels.length - 1) {
                levelsBuilder.append(", ");
            }
        }

        final String levelsList = levelsBuilder.toString();
        context.getSource().sendFeedback(() ->
                Text.literal("可用等级: " + levelsList).styled(style -> style.withColor(Formatting.YELLOW)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("进度类型: blocks_mined, mobs_killed, distance_traveled, unique_items, crafting_recipes")
                        .styled(style -> style.withColor(Formatting.YELLOW)), false);

        return 1;
    }

    private static int showLevelInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource player = context.getSource();
        if (player==null) return 0;
        // 显示等级信息
        context.getSource().sendFeedback(() ->
                Text.literal("=== 等级信息 ===").styled(style -> style.withColor(Formatting.GOLD).withBold(true)), false);

        int[] allLevels = GameStageManager.getAllLevels();
        for (int level : allLevels) {
            String levelName = GameStageManager.getLevelName(level);
            Text levelInfo = Text.literal("Lv." + level)
                    .styled(style -> style.withColor(Formatting.AQUA).withBold(true))
                    .append(": ")
                    .append(Text.literal(levelName).styled(style -> style.withColor(Formatting.GREEN)));

            context.getSource().sendFeedback(() -> levelInfo, false);
        }

        context.getSource().sendFeedback(() ->
                Text.literal("\n默认解锁: Lv.20").styled(style -> style.withColor(Formatting.YELLOW)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("解锁条件示例:").styled(style -> style.withColor(Formatting.YELLOW)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("  Lv.30: 挖掘500方块 + 击杀50生物").styled(style -> style.withColor(Formatting.GRAY)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("  Lv.40: 挖掘1000方块 + 击杀100生物 + 旅行5000距离").styled(style -> style.withColor(Formatting.GRAY)), false);

        context.getSource().sendFeedback(() ->
                Text.literal("  Lv.50: 挖掘2000方块 + 击杀200生物 + 旅行10000距离").styled(style -> style.withColor(Formatting.GRAY)), false);

        return 1;
    }
}