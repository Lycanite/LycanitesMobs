package lycanite.lycanitesmobs.core.spawning;

import lycanite.lycanitesmobs.ExtendedWorld;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnTypeDarkness extends SpawnTypeBase {
	public boolean displayChatWarnings = true;
    /** The highest light level this spawner will work in. 5 Is above ground in the overworld at night time. 0 is pitch black. **/
    public int lightLevelMax = 5;
    public int checkRate = 5 * 20;
    public double lowChance = 0.125F;
    public double medChance = 0.25F;
    public double hiChance = 0.5F;
	public Map<EntityPlayer, Byte> darknessLevels = new HashMap<EntityPlayer, Byte>();

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeDarkness(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");
        this.displayChatWarnings = config.getBool("Spawner Features", "Darkness Spawn Chat Warnings", this.displayChatWarnings, "Set to false to prevent the darkness warning messages from showing.");
        this.lightLevelMax = config.getInt("Spawner Features", "Darkness Spawn Highest Light Level", this.lightLevelMax, "The highest light level the Darkness spawn type will work in. 5 Is above ground in the overworld at night time. 0 is pitch black.");
        this.checkRate = config.getInt("Spawner Features", "Darkness Spawn Check Rate", this.checkRate, "The rate in ticks (20 ticks = 1 second) that the light level is checked, a higher rate will make things spawn much faster from the darkness.");
        this.lowChance = config.getDouble("Spawner Features", "Darkness Spawn Low Chance", this.lowChance, "The chance from 0.0-1.0 that a monster will spawn when in most dark light levels.");
        this.medChance = config.getDouble("Spawner Features", "Darkness Spawn Medium Chance", this.medChance, "The chance from 0.0-1.0 that a monster will spawn when in light level 1 (almost the darkest).");
        this.hiChance = config.getDouble("Spawner Features", "Darkness Spawn High Chance", this.hiChance, "The chance from 0.0-1.0 that a monster will spawn when in light level 0 (the darkest).");
    }
	
	
	// ==================================================
	//                    Spawn Mobs
	// ==================================================
    /**
     * Tells this spawn type to try and spawn mobs at or near the provided coordinates.
     * Usually custom spawn types shouldn't need to override this method and should instead override methods called by this method.
     * This method is usually called by the Custom Spawner class where this spawn type is added to its Spawn Type lists.
     * @param tick Used by spawn types that attempt spawn on a regular basis. Use 0 for event based spawning.
     * @param world The world to spawn in.
     * @param pos Spawn origin position.
     * @param player The player or null if there is no player.
     */
    @Override
    public boolean spawnMobs(long tick, World world, BlockPos pos, EntityPlayer player) {
    	boolean spawned = false;

        BlockPos playerCoords = player.getPosition();
        IBlockState blockState = world.getBlockState(playerCoords);
        Block block = blockState.getBlock();
        boolean isValidBlock = block != null;
        if(isValidBlock)
            isValidBlock = !blockState.isNormalCube();
        if(isValidBlock)
            isValidBlock = blockState.getMaterial() != Material.WATER;

        if(!player.capabilities.isCreativeMode && isValidBlock && tick % this.checkRate == 0 && this.enabled && this.hasSpawns()) {
			int lightLevel = world.getLight(playerCoords);
			byte darknessLevel = 0;
			if(this.darknessLevels.containsKey(player))
				darknessLevel = (byte)Math.max(0, Math.min(2, this.darknessLevels.get(player)));
            LycanitesMobs.printDebug("CustomSpawner", "Darkness Level Read: " + darknessLevel);

			if(lightLevel <= this.lightLevelMax) {
				double chance = this.lowChance;
				if(lightLevel <= 0)
					chance = this.hiChance;
				else if(lightLevel == 1)
					chance = this.medChance;
				float roll = player.getRNG().nextFloat();
				ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		    	if(worldExt != null) {
		    		if("shadowgames".equalsIgnoreCase(worldExt.getWorldEventType()))
		    			roll /= 2;
		    	}
				
				if(chance > roll) {
					darknessLevel++;
					if(darknessLevel == 1 && this.displayChatWarnings) {
						String message = I18n.translateToLocal("spawner.darkness.level1");
						player.sendMessage(new TextComponentString(message));
					}
					else if(darknessLevel == 2 && this.displayChatWarnings) {
						String message = I18n.translateToLocal("spawner.darkness.level2");
						player.sendMessage(new TextComponentString(message));
					}
					else if(darknessLevel == 3) {
						if(this.displayChatWarnings) {
							String message = I18n.translateToLocal("spawner.darkness.level3");
							player.sendMessage(new TextComponentString(message));
						}
						spawned = super.spawnMobs(tick, world, playerCoords, player);
						darknessLevel = 0;
					}
					else
						darknessLevel = 0;
				}
			}
			
			// Light
			else if(darknessLevel > 0) {
				if(darknessLevel == 2 && this.displayChatWarnings) {
					String message = I18n.translateToLocal("spawner.darkness.level1.back");
					player.sendMessage(new TextComponentString(message));
				}
				darknessLevel--;
			}
			
			this.darknessLevels.put(player, darknessLevel);
            LycanitesMobs.printDebug("CustomSpawner", "Darkness Level Write: " + darknessLevel);
		}
		return spawned;
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos pos, boolean rare) {
        return true;
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    @Override
    public List<BlockPos> orderCoords(List<BlockPos> coords, BlockPos pos) {
        return this.orderCoordsCloseToFar(coords, pos);
    }
}
