package lycanite.lycanitesmobs.api.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.material.Material;

public class BlockSoulcube extends BlockBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSoulcube(GroupInfo group, String name) {
		super(Material.iron);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = group;
		this.blockName = name;
		this.setup();
		
		// Stats:
		this.setHardness(5F);
        this.setResistance(10F);
		this.setHarvestLevel("pickaxe", 2);
		this.setStepSound(this.soundTypeGlass);
	}
}
