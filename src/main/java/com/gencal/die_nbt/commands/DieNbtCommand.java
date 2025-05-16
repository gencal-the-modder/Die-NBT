package com.gencal.die_nbt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.RegExUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class DieNbtCommand {

    private static final Component YOU_DONT_HAVE_ITEM_IN_HAND = Component.literal("You are not holding any item.").withStyle(Style.EMPTY.withColor(ChatFormatting.RED));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("die_nbt")
                        .requires(source -> source.hasPermission(0))
                        .then(Commands.literal("item")
                                .executes(DieNbtCommand::getItemFromHand)
                        )
                        .then(Commands.literal("mod")
                                .executes(DieNbtCommand::getNamespaceFromHand)
                        )
                        .then(Commands.literal("nbtFields")
                            .executes(DieNbtCommand::getNbtFieldsFromHand)
                        )
                        .then(Commands.literal("fullNbt")
                            .executes(DieNbtCommand::getFullNbt)
                        )
                        .then(Commands.literal("everything")
                            .executes(DieNbtCommand::getEverythingFromHand)
                        )
                        .then(Commands.literal("help")
                            .executes(DieNbtCommand::printHelp)
                        )
        );
    }

    private static Component copypastableMessage(String string) {
        return Component.literal("    " + string)
                .withStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, string))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy")))
                        .withColor(ChatFormatting.GREEN)
                );
    }

    private static int getItemFromHand(CommandContext<CommandSourceStack> context)    {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        Player player = source.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendSuccess(() -> YOU_DONT_HAVE_ITEM_IN_HAND, false);
            return 1;
        }
        String itemName = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();

        source.sendSuccess(() -> Component.literal("Item:"), false);
        source.sendSuccess(() -> copypastableMessage("\"" + itemName + "\""), false);
        return 1;

    }

    private static int getNamespaceFromHand(CommandContext<CommandSourceStack> context)    {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        Player player = source.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendSuccess(() -> YOU_DONT_HAVE_ITEM_IN_HAND, false);
            return 1;
        }
        String namespace = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).getNamespace();

        source.sendSuccess(() -> Component.literal("Mod:"), false);
        source.sendSuccess(() -> copypastableMessage("\"" + namespace + "\""), false);
        return 1;

    }

    private static int getNbtFieldsFromHand(CommandContext<CommandSourceStack> context)    {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        Player player = source.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendSuccess(() -> YOU_DONT_HAVE_ITEM_IN_HAND, false);
            return 1;
        }
        Set<String> nbtFields = (heldItem.getTag() == null) ? new HashSet<>(): heldItem.getTag().getAllKeys();

        if (!nbtFields.isEmpty()) {
            source.sendSuccess(() -> Component.literal("NBT Fields:"), false);
            nbtFields.forEach(field -> source.sendSuccess(() -> copypastableMessage("\"" + field + "\""), false));
        } else {
            source.sendSuccess(() -> Component.literal("NBT Fields are empty."), false);
        }
        return 1;
    }


    private static int getFullNbt(CommandContext<CommandSourceStack> context)    {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        Player player = source.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendSuccess(() -> YOU_DONT_HAVE_ITEM_IN_HAND, false);
            return 1;
        }
        String nbtData = (heldItem.getTag() == null) ? "{}" : heldItem.getTag().toString();

        source.sendSuccess(() -> Component.literal("Full NBT Data:"), false);
        source.sendSuccess(() -> Component.literal("    " + nbtData)
                .withStyle(Style.EMPTY.withColor(ChatFormatting.BLUE)), false);

        return 1;
    }

    private static int getEverythingFromHand(CommandContext<CommandSourceStack> context)    {
        CommandSourceStack source = context.getSource();
        if (!source.isPlayer()) {
            source.sendFailure(Component.literal("This command can only be run by a player."));
            return 0;
        }
        Player player = source.getPlayer();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty()) {
            source.sendSuccess(() -> YOU_DONT_HAVE_ITEM_IN_HAND, false);
            return 1;
        }
        String itemName = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).toString();
        String namespace = BuiltInRegistries.ITEM.getKey(heldItem.getItem()).getNamespace();
        Set<String> nbtFields = (heldItem.getTag() == null) ? new HashSet<>(): heldItem.getTag().getAllKeys();

        source.sendSuccess(() -> Component.literal("Item:"), false);
        source.sendSuccess(() -> copypastableMessage("\"" + itemName + "\""), false);

        source.sendSuccess(() -> Component.literal("Mod:"), false);
        source.sendSuccess(() -> copypastableMessage("\"" + namespace + "\""), false);

        if (!nbtFields.isEmpty()) {
            source.sendSuccess(() -> Component.literal("NBT Fields:"), false);
            nbtFields.forEach(field -> source.sendSuccess(() -> copypastableMessage("\"" + field + "\""), false));
        } else {
            source.sendSuccess(() -> Component.literal("NBT Fields are empty."), false);
        }

        source.sendSuccess(() -> Component.literal("You might want to copy:"), false);
        source.sendSuccess(() -> copypastableMessage("[\"" + itemName + "\", []]"), false);
        source.sendSuccess(() -> copypastableMessage("[\"" + namespace + "\", []]"), false);

        if (!nbtFields.isEmpty()) {
            Pattern pattern = Pattern.compile("(\\w+)");
            String nbtWithQuotation = RegExUtils.replaceAll(nbtFields.toString(), pattern, "\"$1\"");
            source.sendSuccess(() -> copypastableMessage("[\"" + itemName + "\", " + nbtWithQuotation + "]"), false);
            source.sendSuccess(() -> copypastableMessage("[\"" + namespace + "\", " + nbtWithQuotation + "]"), false);
        }
        return 1;
    }

    private static int printHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("Put an item in your main hand, that you want to get information about, and run one of other commands. Just try it."), false);
        source.sendSuccess(() -> Component.literal("The most useful command by far is 'everything'."), false);
        source.sendSuccess(() -> Component.literal("You can copy green colored output by clicking on it."), false);
        return 1;
    }

}