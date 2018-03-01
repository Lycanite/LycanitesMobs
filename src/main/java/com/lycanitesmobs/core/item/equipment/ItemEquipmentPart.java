package com.lycanitesmobs.core.item.equipment;

import com.google.common.collect.Multimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.info.EntityListCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.equipment.features.EquipmentFeature;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import javax.vecmath.Vector4f;
import java.util.*;

public class ItemEquipmentPart extends ItemBase {
	/** I am sorry, I couldn't find another way. Set in getMetadata(ItemStack) as it's called just before rendering. **/
	public static ItemStack ITEMSTACK_TO_RENDER;

	/** A map of mob classes and parts that they drop. **/
	public static Map<String, ItemEquipmentPart> MOB_PART_DROPS = new HashMap<>();

	/** A list of all features this part has. **/
	public List<EquipmentFeature> features = new ArrayList<>();

	/** The slot type that this part must fit into. Can be: base, head, blade, axe, pike or jewel. **/
	public String slotType;

	/** The id of the mob that drops this part. **/
	public String dropMobId;

	/** The default chance of the part being dropped by a mob. **/
	public float dropChance = 1;

	/** The minimum random level that this part can be. **/
	public int levelMin = 1;

	/** The maximum random level that this part can be. **/
	public int levelMax = 3;


	// ==================================================
	//                   Constructor
	// ==================================================
	public ItemEquipmentPart(GroupInfo groupInfo) {
		super();
		this.group = groupInfo;
		this.setMaxStackSize(1);
		this.setCreativeTab(LycanitesMobs.equipmentPartsTab);
	}

	/** Loads this feature from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.itemName = json.get("itemName").getAsString();

		this.slotType = json.get("slotType").getAsString();

		if(json.has("dropMobId")) {
			this.dropMobId = json.get("dropMobId").getAsString();
			if(!"".equals(this.dropMobId)) {
				MOB_PART_DROPS.put(this.dropMobId, this);
			}
		}

		if(json.has("dropChance"))
			this.dropChance = json.get("dropChance").getAsFloat();

		if(json.has("levelMin"))
			this.levelMin = json.get("levelMin").getAsInt();

		if(json.has("levelMax"))
			this.levelMax = json.get("levelMax").getAsInt();

		// Features:
		if(json.has("features")) {
			JsonArray jsonArray = json.get("features").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject featureJson = jsonIterator.next().getAsJsonObject();
				EquipmentFeature feature = EquipmentFeature.createFromJSON(featureJson);
				this.features.add(feature);
			}
		}

		this.setRegistryName(this.group.filename, this.itemName);
		this.setUnlocalizedName(this.itemName);

		AssetManager.addTexture(this.itemName, this.group, "textures/equipment/" + this.itemName + ".png");
	}


	// ==================================================
	//                      Info
	// ==================================================
	@Override
	public void addInformation(ItemStack itemStack, @Nullable World world, List<String> tooltip, ITooltipFlag tooltipFlag) {
		super.addInformation(itemStack, world, tooltip, tooltipFlag);
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		for(String description : this.getAdditionalDescriptions(itemStack, world, tooltipFlag)) {
			List formattedDescriptionList = fontRenderer.listFormattedStringToWidth("-------------------\n" + description, descriptionWidth);
			for (Object formattedDescription : formattedDescriptionList) {
				if (formattedDescription instanceof String)
					tooltip.add("\u00a73" + formattedDescription);
			}
		}
	}

	@Override
	public String getDescription(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		return I18n.translateToLocal("item.equipmentpart.description");
	}

	public List<String> getAdditionalDescriptions(ItemStack itemStack, @Nullable World world, ITooltipFlag tooltipFlag) {
		List<String> descriptions = new ArrayList<>();
		int level = this.getLevel(itemStack);
		String baseFeature = I18n.translateToLocal("equipment.slottype") + " " + this.slotType;
		baseFeature += "\n" + I18n.translateToLocal("equipment.level") + " " + level + "/" + this.levelMax;
		descriptions.add(baseFeature);
		for(EquipmentFeature feature : this.features) {
			String featureDescription = feature.getDescription(itemStack, level);
			if(featureDescription != null && !"".equals(featureDescription)) {
				descriptions.add(featureDescription);
			}
		}
		return descriptions;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		return super.getAttributeModifiers(slot, stack);
	}


	@Override
	public int getMetadata(ItemStack stack) {
		ITEMSTACK_TO_RENDER = stack; // A sad hack for getting the item to render, hopefully a proper way will become available.
		return super.getMetadata(stack);
	}

	/** Gets or creates an NBT Compund for the provided itemstack. **/
	public NBTTagCompound getTagCompound(ItemStack itemStack) {
		if(itemStack.hasTagCompound()) {
			return itemStack.getTagCompound();
		}
		return new NBTTagCompound();
	}

	/** Sets up this equipment part, this is called when the provided stack is dropped and needs to have its level randomized, etc. **/
	public void initializePart(World world, ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		if(!nbt.hasKey("equipmentLevel")) {
			int level = this.levelMax;
			if(this.levelMin < this.levelMax) {
				level = this.levelMin + world.rand.nextInt(this.levelMax - this.levelMin + 1);
			}
			nbt.setInteger("equipmentLevel", level);
		}
		itemStack.setTagCompound(nbt);
	}

	/** Returns an Equipment Part Level for the provided ItemStack. **/
	public int getLevel(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		int level = 1;
		if(nbt.hasKey("equipmentLevel")) {
			level = nbt.getInteger("equipmentLevel");
		}
		return level;
	}

	/** Returns the dyed color for the provided ItemStack. **/
	public Vector4f getColor(ItemStack itemStack) {
		NBTTagCompound nbt = this.getTagCompound(itemStack);
		float r = 1;
		float g = 1;
		float b = 1;
		if(nbt.hasKey("equipmentColorR")) {
			r = nbt.getFloat("equipmentColorR");
		}
		if(nbt.hasKey("equipmentColorG")) {
			r = nbt.getFloat("equipmentColorG");
		}
		if(nbt.hasKey("equipmentColorB")) {
			r = nbt.getFloat("equipmentColorB");
		}
		return new Vector4f(r, g, b, 1);
	}


	// ==================================================
	//                   Get Sub Items
	// ==================================================
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		super.getSubItems(tab, items);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	@Override
	public ModelResourceLocation getModelResourceLocation() {
		return new ModelResourceLocation(new ResourceLocation(LycanitesMobs.modid, "equipmentpart"), "inventory");
	}

	/** Returns the texture to use for the provided ItemStack. **/
	public ResourceLocation getTexture(ItemStack itemStack) {
		return AssetManager.getTexture(this.itemName);
	}
}
