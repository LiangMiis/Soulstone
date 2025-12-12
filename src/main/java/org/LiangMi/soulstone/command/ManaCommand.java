package org.LiangMi.soulstone.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.api.ManaInterface;
// Import everything in the CommandManager
import static net.minecraft.server.command.CommandManager.*;


public class ManaCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(literal("mana")
                .requires(source -> source.hasPermissionLevel(2))
                .then(literal("add")
                        .then(argument("amount", FloatArgumentType.floatArg())
                                .executes(context ->addMana(context,FloatArgumentType.getFloat(context, "amount")))
                        )
                )
                .then(literal("get")
                        .executes(context -> getMana(context)))
        );
    }
    private static int addMana(CommandContext<ServerCommandSource> context, float amount){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (player instanceof ManaInterface manaInterface){
            manaInterface.spendMana(amount);
        }
        return 1;
    }
    private static int getMana(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;
        if (player instanceof ManaInterface manaInterface){
            player.sendMessage(Text.literal(String.format("当前以太值: %.2f", manaInterface.getMana())), false);
        }
        return 1;
    }
}
