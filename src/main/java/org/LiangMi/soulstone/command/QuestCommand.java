package org.LiangMi.soulstone.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.spell_engine.api.spell.Spell;
import net.spell_engine.internals.SpellHelper;
import net.spell_engine.internals.SpellRegistry;

// getString(ctx, "string")
// word()
// literal("foo")
import static net.minecraft.server.command.CommandManager.literal;
// argument("bar", word())
import static net.minecraft.server.command.CommandManager.argument;
// Import everything in the CommandManager

public class QuestCommand{
    public static void Init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
                dispatcher.register(literal("quest")
                        .then(literal("all")


                        )
                        .then(literal("add")
                                .then(argument("string1", StringArgumentType.string())
                                        .then(argument("int1", IntegerArgumentType.integer())
                                                .then(argument("string2", StringArgumentType.string())
                                                        .then(argument("int2", IntegerArgumentType.integer())

                                                        )
                                                )
                                        )
                                )

                        )
                        .then(literal("remove")
                                .executes(context -> {
                                    context.getSource().sendFeedback(() -> Text.literal("33"),false);
                                    return 1;
                                })

                        )
                        .then(literal("query")
                                .then(argument("int2", IntegerArgumentType.integer())
                                        
                                )
                        )
                ));
    }
}