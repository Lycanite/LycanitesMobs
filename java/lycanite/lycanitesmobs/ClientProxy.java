package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.block.BlockFluidBase;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.entity.EntityHitArea;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.gui.GUITabMain;
import lycanite.lycanitesmobs.api.gui.GuiOverlay;
import lycanite.lycanitesmobs.api.gui.TabManager;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.item.ItemBase;
import lycanite.lycanitesmobs.api.renderer.RenderRegister;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

    public static IItemColor itemColor = new IItemColor() {
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            Item item = stack.getItem();
            if(item == null || !(item instanceof ItemBase))
                return 16777215;
            ItemBase itemBase = (ItemBase)item;
            return itemBase.getColorFromItemstack(stack, tintIndex);
        }
    };

	
	// ========== Register Event Handlers ==========
	@Override
    public void registerEvents() {
		// Event Listeners:
		FMLCommonHandler.instance().bus().register(new KeyHandler(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
	}
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
        ObjectManager.RegisterModels();

		// ========== Add GUI Textures ==========
		GroupInfo group = LycanitesMobs.group;
		AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");
		AssetManager.addTexture("GUILMMainMenu", group, "textures/guis/lmmainmenu.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIPet", group, "textures/guis/pet.png");
		AssetManager.addTexture("GUIMount", group, "textures/guis/mount.png");
        AssetManager.addTexture("GUIFamiliar", group, "textures/guis/familiar.png");
        AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");
        AssetManager.addTexture("GUIMinionLg", group, "textures/guis/minion_lg.png");

		// ========== Add GUI Tabs ==========
		TabManager.registerTab(new GUITabMain(0));
		//TabManager.registerTab(new GUITabBeastiary(1));
		//TabManager.registerTab(new GUITabMinion(2));
		//TabManager.registerTab(new GUITabMount(3));
    }
	
	
	// ========== Register Tile Entities ==========
	@Override
	public void registerTileEntities() {
		// None
	}
	
	
	// ========== Register Renders ==========
	@Override
    public void registerRenders(GroupInfo groupInfo) {

        // Blocks:
        //RenderingRegistry.registerBlockHandler(new RenderBlock());

        // Item:
        //MinecraftForgeClient.registerItemRenderer(ObjectManager.getItem("mobtoken"), new RenderItemMobToken());

		// Special Entites:
        groupInfo.specialClasses.add(EntityHitArea.class);
        groupInfo.specialClasses.add(EntityFear.class);
        groupInfo.projectileClasses.add(EntityPortal.class);

        RenderRegister renderRegister = new RenderRegister(groupInfo);
        renderRegister.registerRenderFactories();
    }
	
	// ========== Get Client Player Entity ==========
	@Override
    public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}


    // ========== Renders ==========
    public void addBlockRender(GroupInfo group, Block block) {
        // Fluids:
        if(block instanceof BlockFluidBase) {
            BlockFluidBase blockFluid = (BlockFluidBase)block;
            Item item = Item.getItemFromBlock(block);
            ModelBakery.registerItemVariants(item);
            final ModelResourceLocation fluidLocation = new ModelResourceLocation(blockFluid.group.filename + ":fluid", blockFluid.getFluid().getName());
            ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack itemStack) {
                    return fluidLocation;
                }
            });
            ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
                @Override
                protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                    return fluidLocation;
                }
            });
            return;
        }

        this.addItemRender(group, Item.getItemFromBlock(block));
    }

    public void addItemRender(GroupInfo group, Item item) {
        if(item instanceof ItemBase) {
            ItemBase itemBase = (ItemBase) item;
            ModelLoader.setCustomModelResourceLocation(item, 0, itemBase.getModelResourceLocation());
            //if (itemBase.useItemColors()) Handled in Object Manager at PostInit for now.
                //Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientProxy.itemColor, item);
            return;
        }
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}