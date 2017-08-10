package com.lycanitesmobs.core.command;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandMain implements ICommand {
	
	private List aliases;
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public CommandMain() {
		this.aliases = new ArrayList();
		this.aliases.add("lm");
		this.aliases.add("lycan");
		this.aliases.add("lycmobs");
		this.aliases.add("lycanmobs");
		this.aliases.add("lycanitesmobs");
	}
	
	
	// ==================================================
	//                   Command Info
	// ==================================================
	@Override
	public String getCommandName() {
		return "lycanitesmobs";
	}

	@Override
	public String getCommandUsage(ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer)
			return "/lycanitesmobs <sub-commands: mobevent [start <event name>, stop, list, enable, disable]>";
		return "/lycanitesmobs <sub-commands: mobevent [start <event name> dimensionID, stop, list, enable, disable]>";
	}

	@Override
	public List getCommandAliases() {
		return this.aliases;
	}

	@Override
	public List getTabCompletionOptions(MinecraftServer server, ICommandSender commandSender, String[] args, BlockPos pos) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}

    @Override
    public int compareTo(ICommand p_compareTo_1_) {
        return this.getCommandName().compareTo(p_compareTo_1_.getCommandName());
    }
	
	
	// ==================================================
	//                      Process
	// ==================================================
	@Override
	public void execute(MinecraftServer server, ICommandSender commandSender, String[] args) {
		String reply = I18n.translateToLocal("lyc.command.invalid");
		if(args.length < 1) {
			commandSender.addChatMessage(new TextComponentString(reply));
			commandSender.addChatMessage(new TextComponentString(this.getCommandUsage(commandSender)));
			return;
		}
		
		// Mob Event:
		if("mobevent".equalsIgnoreCase(args[0])) {
			reply = I18n.translateToLocal("lyc.command.mobevent.invalid");
			if(args.length < 2) {
				commandSender.addChatMessage(new TextComponentString(reply));
				return;
			}
			
			// Start:
			if("start".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.start.invalid");
				if(args.length < 3) {
					commandSender.addChatMessage(new TextComponentString(reply));
					return;
				}
				
				String mobEventName = args[2].toLowerCase();
				if(MobEventManager.instance.worldMobEvents.containsKey(mobEventName)) {
					
					// Get World:
					World world = null;
                    if(args.length >= 4 && NumberUtils.isNumber(args[3])) {
                        world = DimensionManager.getWorld(Integer.parseInt(args[3]));
                    }
					else {
						world = commandSender.getEntityWorld();
					}
					
					// No World:
					if(world == null) {
						reply = I18n.translateToLocal("lyc.command.mobevent.start.noworld");
						commandSender.addChatMessage(new TextComponentString(reply));
						return;
					}

                    ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
					
					// Force Enabled:
					if(!worldExt.mobEventsEnabled) {
						reply = I18n.translateToLocal("lyc.command.mobevent.enable");
						commandSender.addChatMessage(new TextComponentString(reply));
                        worldExt.mobEventsEnabled = true;
						ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
						config.setBool("Global", "Mob Events Enabled", true);
					}
					
					reply = I18n.translateToLocal("lyc.command.mobevent.start");
					commandSender.addChatMessage(new TextComponentString(reply));
                    worldExt.startWorldEvent(mobEventName);
					return;
				}
				
				reply = I18n.translateToLocal("lyc.command.mobevent.start.unknown");
				commandSender.addChatMessage(new TextComponentString(reply));
				return;
			}

            // Get World:
            World world = null;
            if(args.length >= 3 && NumberUtils.isNumber(args[2])) {
                world = DimensionManager.getWorld(Integer.parseInt(args[2]));
            }
            else {
                world = commandSender.getEntityWorld();
            }

            // No World:
            if(world == null) {
                reply = I18n.translateToLocal("lyc.command.mobevent.start.noworld");
                commandSender.addChatMessage(new TextComponentString(reply));
                return;
            }

            LycanitesMobs.printDebug("", "Getting Extended World for Dimension: " + world.provider.getDimension() + " World: " + world);
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            LycanitesMobs.printDebug("", "Got Extended World for Dimension: " + worldExt.world.provider.getDimension() + " World: " + worldExt.world);
            if(worldExt == null) return;
			
			// Random:
			if("random".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.random");
				commandSender.addChatMessage(new TextComponentString(reply));
				MobEventBase mobEvent = MobEventManager.instance.getRandomWorldMobEvent(world, worldExt);
                worldExt.startWorldEvent(mobEvent);
				return;
			}
			
			// Stop:
			if("stop".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.stop");
				commandSender.addChatMessage(new TextComponentString(reply));
                worldExt.stopWorldEvent();
				return;
			}
			
			// List:
			if("list".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.list");
				commandSender.addChatMessage(new TextComponentString(reply));
				for(MobEventBase mobEvent : MobEventManager.instance.worldMobEvents.values()) {
					String eventName = mobEvent.name + " (" + mobEvent.getTitle() + ")";
					commandSender.addChatMessage(new TextComponentString(eventName));
				}
				return;
			}
			
			// Enable:
			if("enable".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.enable");
				commandSender.addChatMessage(new TextComponentString(reply));
                worldExt.mobEventsEnabled = true;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
				config.setBool("Global", "Mob Events Enabled", true);
				return;
			}
			
			// Disable:
			if("disable".equalsIgnoreCase(args[1])) {
				reply = I18n.translateToLocal("lyc.command.mobevent.disable");
				commandSender.addChatMessage(new TextComponentString(reply));
                worldExt.mobEventsEnabled = false;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
				config.setBool("Global", "Mob Events Enabled", false);
				return;
			}
		}
		
		commandSender.addChatMessage(new TextComponentString(reply));
		commandSender.addChatMessage(new TextComponentString(this.getCommandUsage(commandSender)));
	}
	
	
	// ==================================================
	//                     Permission
	// ==================================================
    public int getRequiredPermissionLevel()
    {
        return 4;
    }

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer) {
			if(!commandSender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName()))
				return false;
		}
		return true;
	}
}
