package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockFluidBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.gui.GuiTabMain;
import com.lycanitesmobs.core.gui.GuiOverlay;
import com.lycanitesmobs.core.gui.TabManager;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.ItemEquipmentPart;
import com.lycanitesmobs.core.model.EquipmentPartModelLoader;
import com.lycanitesmobs.core.renderer.EquipmentPartRenderer;
import com.lycanitesmobs.core.renderer.RenderRegister;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentPart;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAir;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy extends CommonProxy {

    public static IItemColor itemColor = (stack, tintIndex) -> {
		Item item = stack.getItem();
		if(item == null || !(item instanceof ItemBase))
			return 16777215;
		ItemBase itemBase = (ItemBase)item;
		return itemBase.getColorFromItemstack(stack, tintIndex);
	};

	
	// ========== Register Event Handlers ==========
	@Override
    public void registerEvents() {
		// Event Listeners:
		FMLCommonHandler.instance().bus().register(new KeyHandler(Minecraft.getMinecraft()));
		MinecraftForge.EVENT_BUS.register(new GuiOverlay(Minecraft.getMinecraft()));
        MinecraftForge.EVENT_BUS.register(new ClientEventListener());
	}
	
	// ========== Register Assets ==========
	@Override
    public void registerAssets() {
        ObjectManager.RegisterModels();

		// ========== Add GUI Textures ==========
		GroupInfo group = LycanitesMobs.group;
		AssetManager.addTexture("GUIInventoryCreature", group, "textures/guis/inventory_creature.png");

		// Beastiary:
		AssetManager.addTexture("GUIBeastiaryBackground", group, "textures/guis/beastiary/background.png");
		AssetManager.addTexture("GUIPetLevel", group, "textures/guis/beastiary/level.png");
		AssetManager.addTexture("GUIPetSpirit", group, "textures/guis/beastiary/spirit.png");
		AssetManager.addTexture("GUIPetSpiritEmpty", group, "textures/guis/beastiary/spirit_empty.png");
		AssetManager.addTexture("GUIPetSpiritUsed", group, "textures/guis/beastiary/spirit_used.png");
		AssetManager.addTexture("GUIPetSpiritFilling", group, "textures/guis/beastiary/spirit_filling.png");
		AssetManager.addTexture("GUIPetBarHealth", group, "textures/guis/beastiary/bar_health.png");
		AssetManager.addTexture("GUIPetBarRespawn", group, "textures/guis/beastiary/bar_respawn.png");
		AssetManager.addTexture("GUIPetBarEmpty", group, "textures/guis/beastiary/bar_empty.png");

		AssetManager.addTexture("GUILMMainMenu", group, "textures/guis/lmmainmenu.png");
		AssetManager.addTexture("GUIBeastiary", group, "textures/guis/beastiary.png");
		AssetManager.addTexture("GUIPet", group, "textures/guis/pet.png");
		AssetManager.addTexture("GUIMount", group, "textures/guis/mount.png");
        AssetManager.addTexture("GUIFamiliar", group, "textures/guis/familiar.png");
        AssetManager.addTexture("GUIMinion", group, "textures/guis/minion.png");
        AssetManager.addTexture("GUIMinionLg", group, "textures/guis/minion_lg.png");
		AssetManager.addTexture("GUIEquipmentForge", group, "textures/guis/equipmentforge.png");

		// ========== Add GUI Tabs ==========
		TabManager.registerTab(new GuiTabMain(0));
    }


	// ========== Register Tile Entities ==========
	@Override
	public void registerTileEntities() {
		// None
	}
	
	
	// ========== Register Renders ==========
	@Override
    public void registerRenders(GroupInfo groupInfo) {

        // Equipment Parts:
		ModelLoaderRegistry.registerLoader(new EquipmentPartModelLoader());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEquipmentPart.class, new EquipmentPartRenderer());

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
		return Minecraft.getMinecraft().player;
	}


    // ========== Renders ==========
    public void addBlockRender(GroupInfo group, Block block) {
        // Fluids:
        if(block instanceof BlockFluidBase) {
            BlockFluidBase blockFluid = (BlockFluidBase)block;
            Item item = Item.getItemFromBlock(block);
            ModelBakery.registerItemVariants(item);
            ModelResourceLocation fluidLocation = new ModelResourceLocation(blockFluid.group.filename + ":fluid", blockFluid.getFluid().getName());
            ModelLoader.setCustomMeshDefinition(item, itemStack -> fluidLocation);
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
	    if(group == null) {
	        group = LycanitesMobs.group;
        }

        if(item instanceof ItemEquipmentPart) {
			ForgeHooksClient.registerTESRItemStack(item, 0, TileEntityEquipmentPart.class); // A deprecated yet the only way to render dynamic OBJ models that can be animated, rendered in stages, layers and mixed with other models.
		}

        if(item instanceof ItemBase) {
			ItemBase itemBase = (ItemBase)item;
			ModelLoader.setCustomModelResourceLocation(item, 0, itemBase.getModelResourceLocation());
            return;
        }

        if(item instanceof ItemAir) {
	    	return;
		}

        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}