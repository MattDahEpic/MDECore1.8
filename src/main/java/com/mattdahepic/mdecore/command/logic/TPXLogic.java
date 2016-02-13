package com.mattdahepic.mdecore.command.logic;

import com.mattdahepic.mdecore.command.AbstractCommand;
import com.mattdahepic.mdecore.command.ICommandLogic;
import com.mattdahepic.mdecore.helpers.TeleportHelper;
import com.mattdahepic.mdecore.helpers.TranslationHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.DimensionManager;

import java.util.List;

public class TPXLogic implements ICommandLogic {
    public static TPXLogic instance = new TPXLogic();

    @Override
    public String getCommandName () {
        return "tpx";
    }
    @Override
    public int getPermissionLevel () {
        return 2;
    }
    @Override
    public String getCommandSyntax () {
        return TranslationHelper.getTranslatedString("mdecore.command.tpx.usage");
    }
    @Override
    public void handleCommand (ICommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 1: // (tpx) invalid command
                AbstractCommand.throwUsages(instance);
            case 2: // (tpx {<player>|<dimension>}) teleporting player to self, or self to dimension
                EntityPlayerMP playerSender = CommandBase.getCommandSenderAsPlayer(sender);
                try {
                    EntityPlayerMP player = CommandBase.getPlayer(sender, args[1]);
                    if (!player.equals(playerSender)) {
                        if (playerSender.dimension == player.dimension) {
                            player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
                        } else {
                            TeleportHelper.transferPlayerToDimension(player, playerSender.dimension, playerSender.mcServer.getConfigurationManager());
                            player.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.LIGHT_PURPLE+TranslationHelper.getTranslatedString("mdecore.command.tpx.selftp")));
                    }
                    break;
                } catch (PlayerNotFoundException t) {
                    int dimension = 0;
                    try {
                        dimension = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        throw t;
                    }
                    if (!DimensionManager.isDimensionRegistered(dimension)) {
                        AbstractCommand.throwNoWorld();
                    }
                    if (playerSender.dimension != dimension) {
                        TeleportHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getConfigurationManager());
                    }
                    TeleportHelper.sendPlayerToSpawnInCurrentWorld(playerSender);
                }
                break;
            case 3: // (tpx <player> {<player>|<dimension>}) teleporting player to player or player to dimension
                EntityPlayerMP player = CommandBase.getPlayer(sender, args[1]);
                try {
                    EntityPlayerMP otherPlayer = CommandBase.getPlayer(sender, args[2]);
                    if (!player.equals(otherPlayer)) {
                        if (otherPlayer.dimension == player.dimension) {
                            player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
                        } else {
                            TeleportHelper.transferPlayerToDimension(player, otherPlayer.dimension, otherPlayer.mcServer.getConfigurationManager());
                            player.setPositionAndUpdate(otherPlayer.posX, otherPlayer.posY, otherPlayer.posZ);
                        }
                    } else {
                        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+TranslationHelper.getTranslatedString("mdecore.command.tpx.tptoself")));
                    }
                    break;
                } catch (PlayerNotFoundException t) {
                    int dimension = 0;
                    try {
                        dimension = Integer.parseInt(args[2]);
                    } catch (Exception e) { // not a number, assume they wanted a player
                        AbstractCommand.throwNoPlayer();
                    }
                    if (!DimensionManager.isDimensionRegistered(dimension)) {
                        AbstractCommand.throwNoWorld();
                    }
                    if (player.dimension != dimension) {
                        TeleportHelper.transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
                    }
                    TeleportHelper.sendPlayerToSpawnInCurrentWorld(player);
                }
                break;
            case 4: // (tpx <x> <y> <z>) teleporting self within dimension
                playerSender = CommandBase.getCommandSenderAsPlayer(sender);
                try {
                    playerSender.setPositionAndUpdate(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                } catch (NumberFormatException e) {
                    AbstractCommand.throwInvalidNumber(e.getMessage().substring(e.getMessage().indexOf('"')+1,e.getMessage().length()-1));
                }
                break;
            case 5: // (tpx {<player> <x> <y> <z> | <x> <y> <z> <dimension>}) teleporting player within player's dimension or self to dimension
                try {
                    player = CommandBase.getPlayer(sender, args[1]);
                    try {
                        player.setPositionAndUpdate(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                    } catch (NumberFormatException e) {
                        AbstractCommand.throwInvalidNumber(e.getMessage().substring(e.getMessage().indexOf('"')+1,e.getMessage().length()-1));
                    }
                } catch (PlayerNotFoundException t) {
                    int dimension;
                    try {
                        dimension = Integer.parseInt(args[4]);
                    } catch (Exception e) {
                        throw t;
                    }
                    playerSender = CommandBase.getCommandSenderAsPlayer(sender);
                    if (!DimensionManager.isDimensionRegistered(dimension)) {
                        AbstractCommand.throwNoWorld();
                    }
                    if (playerSender.dimension != dimension) {
                        TeleportHelper.transferPlayerToDimension(playerSender, dimension, playerSender.mcServer.getConfigurationManager());
                    }
                    playerSender.setPositionAndUpdate(playerSender.posX, playerSender.posY, playerSender.posZ);
                }
                break;
            case 6: // (tpx <player> <x> <y> <z> <dimension>) teleporting player to dimension and location
            default: // ignore excess tokens. warn?
                player = CommandBase.getPlayer(sender, args[1]);
                int dimension = Integer.parseInt(args[5]);

                if (!DimensionManager.isDimensionRegistered(dimension)) {
                    AbstractCommand.throwNoWorld();
                }
                if (player.dimension != dimension) {
                    TeleportHelper.transferPlayerToDimension(player, dimension, player.mcServer.getConfigurationManager());
                }
                try {
                    player.setPositionAndUpdate(Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
                } catch (NumberFormatException e) {
                    AbstractCommand.throwInvalidNumber(e.getMessage().substring(e.getMessage().indexOf('"')+1,e.getMessage().length()-1));
                }
                break;
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public List<String> addTabCompletionOptions (ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 2 || args.length == 3) {
            return AbstractCommand.getPlayerNamesStartingWithLastArg(args);
        } else if (args.length >= 6) {
            Integer[] ids = DimensionManager.getIDs();
            String[] strings = new String[ids.length];

            for (int i = 0; i < ids.length; i++) {
                strings[i] = ids[i].toString();
            }
            return CommandBase.getListOfStringsMatchingLastWord(args, strings);
        }

        return null;
    }
}
