package org.LiangMi.soulstone.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.LiangMi.soulstone.access.SpellAccess;
import org.LiangMi.soulstone.api.ManaInterface;
import org.LiangMi.soulstone.data.PlayerSpellData;
import org.LiangMi.soulstone.manager.GetSpellManager;

import static net.minecraft.server.command.CommandManager.*;
public class PromotionCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                RegistrationEnvironment registrationEnvironment) {

        dispatcher.register(literal("promotion")
                .requires(source -> source.hasPermissionLevel(2))
                .executes(PromotionCommand::promotion)
        );
    }
    private static int promotion(CommandContext<ServerCommandSource> context){

//        PlayerSpellData data = SpellAccess.getPlayerData(context.getSource().getPlayer());
//        data.addSpell("arcane_bolt");
        GetSpellManager.upGetSpell(context.getSource().getPlayer());
        return 1;
    }

}
