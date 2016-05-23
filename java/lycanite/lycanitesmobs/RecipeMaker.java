package lycanite.lycanitesmobs;


import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.block.BlockPillar;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeMaker {
    public static List<RecipeMakerEntry> STONE_ENTRIES = new ArrayList<RecipeMakerEntry>();

    public static class RecipeMakerEntry {
        public String stoneName;
        public Object creationItem;
        public Object creationBlock;

        public RecipeMakerEntry(String stoneName, Object creationItem, Object creationBlock) {
            this.stoneName = stoneName;
            this.creationItem = creationItem;
            this.creationBlock = creationBlock;
        }
    }

    // ==================================================
    //                Add Stone Blocks
    // ==================================================
    /** Creates a set of stone blocks such as tiles, bricks, pillars, etc as well as a crystal light source. The block name is added to a list that is used to automatically add each recipe at Post Init.
     * @param group The group info to add each block with.
     * @param stoneName The name of the stone block, such as "demon" or "shadow", etc. (stone and crystal are appended).
     * @param creationItem The block, item or item stack used to create this stone block from vanilla stone, such as how Nether Warts are used for demonstone (can be null for none).
     * @param creationBlock The block (can be item or item stack also) used in the base crafting recipe, usually cobblestone (null will deault to cobblestone).
     * **/
    public static void addStoneBlocks(GroupInfo group, String stoneName, Object creationItem, Object creationBlock) {
        ObjectManager.addBlock(stoneName + "stone", new BlockBase(Material.ROCK, group, stoneName + "stone").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonebrick", new BlockBase(Material.ROCK, group, stoneName + "stonebrick").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonepolished", new BlockBase(Material.ROCK, group, stoneName + "stonepolished").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonechiseled", new BlockBase(Material.ROCK, group, stoneName + "stonechiseled").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonetile", new BlockBase(Material.ROCK, group, stoneName + "stonetile").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "stonepillar", new BlockPillar(Material.ROCK, group, stoneName + "stonepillar").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.blocksTab));
        ObjectManager.addBlock(stoneName + "crystal", new BlockBase(Material.GLASS, group, stoneName + "crystal").setBlockStepSound(SoundType.GLASS).setHardness(5.0F).setResistance(2000.0F).setLightLevel(1.0F).setCreativeTab(LycanitesMobs.blocksTab));

        STONE_ENTRIES.add(new RecipeMakerEntry(stoneName, creationItem, creationBlock));
    }
    public static void addStoneBlocks(GroupInfo group, String stoneName, Object creationItem) {
        addStoneBlocks(group, stoneName, creationItem, Blocks.COBBLESTONE);
    }


    // ==================================================
    //                 Create All Recipes
    // ==================================================
    /** Adds all automatic recipes for blocks added via this class. **/
    public static void createAllRecipies() {
        for(RecipeMakerEntry recipeMakerEntry : STONE_ENTRIES) {
            createStoneRecipies(recipeMakerEntry.stoneName, recipeMakerEntry.creationItem, recipeMakerEntry.creationBlock);
        }
    }


    // ==================================================
    //               Create Stone Recipes
    // ==================================================
    /** Creates a set of crafting recipes for stone blocks such as tiles, bricks, pillars, etc. This is automatically called in Post Init on all blocks added via addStoneBlocks().
     * @param stoneName The name of the stone block, such as "demon" or "shadow", etc. (stone and crystal are appended).
     * @param creationItem The block, item or item stack used to create this stone block from vanilla stone, such as how Nether Warts are used for demonstone (can be null for none).
     * @param creationBlock The block (can be item or item stack also) used in the base crafting recipe, usually cobblestone (can be null for none).
     * **/
    public static void createStoneRecipies(String stoneName, Object creationItem, Object creationBlock) {

        // Base Recipe:
        if(creationItem != null && creationBlock != null) {
            GameRegistry.addRecipe(new ShapelessOreRecipe(
                    new ItemStack(ObjectManager.getBlock(stoneName + "stone"), 1, 0),
                    new Object[]{
                            creationItem,
                            creationBlock
                    }
            ));
        }

        // Brick: Made from 4x4
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "stonebrick"), 4, 0),
                new Object[]{"BB", "BB",
                        Character.valueOf('B'), ObjectManager.getBlock(stoneName + "stone")
                }
        ));

        // Pillar: Made from 2 vertically
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "stonepillar"), 2, 0),
                new Object[]{"B ", "B ",
                        Character.valueOf('B'), ObjectManager.getBlock(stoneName + "stone")
                }
        ));

        // Chiseled: TODO Use slabs!
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "stonechiseled"), 2, 0),
                new Object[]{"B ", " B",
                        Character.valueOf('B'), ObjectManager.getBlock(stoneName + "stone")
                }
        ));

        // Polished: Made from 2 horizontally
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "stonepolished"), 2, 0),
                new Object[]{"BB", "  ",
                        Character.valueOf('B'), ObjectManager.getBlock(stoneName + "stone")
                }
        ));

        // Tile: Made from 4x4 polished
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "stonetile"), 2, 0),
                new Object[]{"BB", "BB",
                        Character.valueOf('B'), ObjectManager.getBlock(stoneName + "stonepolished")
                }
        ));

        // Crystal:
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(ObjectManager.getBlock(stoneName + "crystal"), 1, 0),
                new Object[] {
                        creationItem,
                        Blocks.GLOWSTONE
                }
        ));
    }
}
