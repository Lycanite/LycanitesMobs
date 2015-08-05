package lycanite.lycanitesmobs.api.command;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import org.apache.commons.lang3.math.NumberUtils;

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
	public List addTabCompletionOptions(ICommandSender commandSender, String[] args) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		return false;
	}
	
	
	// ==================================================
	//                      Process
	// ==================================================
	@Override
	public void processCommand(ICommandSender commandSender, String[] args) {
		String reply = StatCollector.translateToLocal("lyc.command.invalid");
		if(args.length < 1) {
			commandSender.addChatMessage(new ChatComponentText(reply));
			commandSender.addChatMessage(new ChatComponentText(this.getCommandUsage(commandSender)));
			return;
		}
		
		// Mob Event:
		if("mobevent".equalsIgnoreCase(args[0])) {
			reply = StatCollector.translateToLocal("lyc.command.mobevent.invalid");
			if(args.length < 2) {
				commandSender.addChatMessage(new ChatComponentText(reply));
				return;
			}
			
			// Start:
			if("start".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.start.invalid");
				if(args.length < 3) {
					commandSender.addChatMessage(new ChatComponentText(reply));
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
						reply = StatCollector.translateToLocal("lyc.command.mobevent.start.noworld");
						commandSender.addChatMessage(new ChatComponentText(reply));
						return;
					}

                    ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
					
					// Force Enabled:
					if(!worldExt.mobEventsEnabled) {
						reply = StatCollector.translateToLocal("lyc.command.mobevent.enable");
						commandSender.addChatMessage(new ChatComponentText(reply));
                        worldExt.mobEventsEnabled = true;
						ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
						config.setBool("Global", "Mob Events Enabled", true);
					}
					
					reply = StatCollector.translateToLocal("lyc.command.mobevent.start");
					commandSender.addChatMessage(new ChatComponentText(reply));
                    worldExt.startWorldEvent(mobEventName);
					return;
				}
				
				reply = StatCollector.translateToLocal("lyc.command.mobevent.start.unknown");
				commandSender.addChatMessage(new ChatComponentText(reply));
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
                reply = StatCollector.translateToLocal("lyc.command.mobevent.start.noworld");
                commandSender.addChatMessage(new ChatComponentText(reply));
                return;
            }

            LycanitesMobs.printDebug("", "Getting Extended World for Dimension: " + world.provider.dimensionId + " World: " + world);
            ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
            LycanitesMobs.printDebug("", "Got Extended World for Dimension: " + worldExt.world.provider.dimensionId + " World: " + worldExt.world);
            if(worldExt == null) return;
			
			// Random:
			if("random".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.random");
				commandSender.addChatMessage(new ChatComponentText(reply));
				MobEventBase mobEvent = MobEventManager.instance.getRandomWorldMobEvent(world, worldExt);
                worldExt.startWorldEvent(mobEvent);
				return;
			}
			
			// Stop:
			if("stop".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.stop");
				commandSender.addChatMessage(new ChatComponentText(reply));
                worldExt.stopWorldEvent();
				return;
			}
			
			// List:
			if("list".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.list");
				commandSender.addChatMessage(new ChatComponentText(reply));
				for(MobEventBase mobEvent : MobEventManager.instance.worldMobEvents.values()) {
					String eventName = mobEvent.name + " (" + mobEvent.getTitle() + ")";
					commandSender.addChatMessage(new ChatComponentText(eventName));
				}
				return;
			}
			
			// Enable:
			if("enable".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.enable");
				commandSender.addChatMessage(new ChatComponentText(reply));
                worldExt.mobEventsEnabled = true;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
				config.setBool("Global", "Mob Events Enabled", true);
				return;
			}
			
			// Disable:
			if("disable".equalsIgnoreCase(args[1])) {
				reply = StatCollector.translateToLocal("lyc.command.mobevent.disable");
				commandSender.addChatMessage(new ChatComponentText(reply));
                worldExt.mobEventsEnabled = false;
				ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "mobevents");
				config.setBool("Global", "Mob Events Enabled", false);
				return;
			}
		}
		
		commandSender.addChatMessage(new ChatComponentText(reply));
		commandSender.addChatMessage(new ChatComponentText(this.getCommandUsage(commandSender)));
	}
	
	
	// ==================================================
	//                     Permission
	// ==================================================
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender commandSender) {
		if(commandSender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)commandSender;
			if(!MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile())) //isOpped()
				return false;
		}
		return true;
	}

	
	
	// ==================================================
	//                      Unknown
	// ==================================================
	@Override
	public int compareTo(Object object) {
		return 0;
	}

}
