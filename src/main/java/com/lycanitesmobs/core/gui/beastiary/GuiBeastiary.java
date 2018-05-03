package com.lycanitesmobs.core.gui.beastiary;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.GuiHandler;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.gui.GuiBaseScreen;
import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.io.IOException;
import java.net.URI;

public abstract class GuiBeastiary extends GuiBaseScreen {
	/** A snapshot of the users GUI Scale setting so it can be restored on closing the Beastiary. **/
	static int OPENED_GUI_SCALE;
	/** Set to true when any Beastiary GUI is active in order to prevent the GUI Scaling going out of sync. **/
	static boolean GUI_ACTIVE;

	public EntityPlayer player;
	public ExtendedPlayer playerExt;
	public EntityLivingBase creaturePreviewEntity;
	public float creaturePreviewTicks = 0;

	public ScaledResolution scaledResolution;
	public int centerX;
	public int centerY;
	public int windowWidth;
	public int windowHeight;
	public int halfX;
	public int halfY;
	public int windowX;
	public int windowY;

	public int colLeftX;
	public int colLeftY;
	public int colLeftWidth;
	public int colLeftHeight;
	public int colLeftCenterX;
	public int colLeftCenterY;

	public int colRightX;
	public int colRightY;
	public int colRightWidth;
	public int colRightHeight;
	public int colRightCenterX;
	public int colRightCenterY;


	/**
	 * Constructor
	 * @param player The player to create the GUI instance for.
	 */
	public GuiBeastiary(EntityPlayer player) {
		super();
		this.player = player;
		this.playerExt = ExtendedPlayer.getForPlayer(player);

		this.mc = Minecraft.getMinecraft();
		if(this.mc.gameSettings.guiScale != 2 || GUI_ACTIVE) {
			OPENED_GUI_SCALE = this.mc.gameSettings.guiScale;
			this.mc.gameSettings.setOptionValue(GameSettings.Options.GUI_SCALE, 2 - OPENED_GUI_SCALE);
		}
		else {
			GUI_ACTIVE = true;
		}
	}


	@Override
	public void onGuiClosed() {
		if(this.mc.gameSettings.guiScale == 2 && !GUI_ACTIVE) {
			this.mc.gameSettings.setOptionValue(GameSettings.Options.GUI_SCALE, OPENED_GUI_SCALE - 2);
		}
		GUI_ACTIVE = false;
		super.onGuiClosed();
	}


	/**
	 * Returns the title of this Beastiary Page.
	 * @return The title text string to display.
	 */
	public String getTitle() {
		return "Beastiary";
	}


	/**
	 * Whether this GUI should pause a single player game or not.
	 * @return True to pause the game.
	 */
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}


	/**
	 * Returns a scaled x coordinate.
	 * @param x The x float to scale where 1.0 is the entire GUI width.
	 * @return A scaled x position.
	 */
	public int getScaledX(float x) {
		if(this.scaledResolution == null) {
			this.scaledResolution = new ScaledResolution(this.mc);
		}
		return Math.round((float)scaledResolution.getScaledWidth() * x);
	}


	/**
	 * Returns a scaled y coordinate based on the scaled width with an aspect ratio applied to it.
	 * @param y The y float to scale where 1.0 is the entire GUI height.
	 * @return A scaled y position.
	 */
	public int getScaledY(float y) {
		float baseHeight = Math.round((float)this.getScaledX(y) * (1080F / 1920F));
		return Math.round(baseHeight * y);
	}


	/**
	 * Initializes this gui, called when first opening or on window resizing.
	 */
	@Override
	public void initGui() {
		super.initGui();
		if(this.scaledResolution == null) {
			this.scaledResolution = new ScaledResolution(this.mc);
		}

		this.zLevel = -1000F;

		// Main Window:
		this.windowWidth = this.getScaledX(0.95F);
		this.windowHeight = this.getScaledY(0.95F);
		this.halfX = this.windowWidth / 2;
		this.halfY = this.windowHeight / 2;
		this.windowX = (this.width / 2) - (this.windowWidth / 2);
		this.windowY = (this.height / 2) - (this.windowHeight / 2);
		this.centerX = this.windowX + (this.windowWidth / 2);
		this.centerY = this.windowY + (this.windowHeight / 2);

		// Left Column:
		this.colLeftX = this.windowX + this.getScaledX(80F / 1920F);
		this.colLeftY = this.windowY + this.getScaledY(460F / 1080F);
		this.colLeftWidth = this.getScaledX(320F / 1920F);
		this.colLeftHeight = this.getScaledX(380F / 1080F);
		this.colLeftCenterX = this.colLeftX + Math.round(this.colLeftWidth / 2);
		this.colLeftCenterY = this.colLeftY + Math.round(this.colLeftHeight / 2);

		// Right Column:
		this.colRightX = this.windowX + this.getScaledX(480F / 1920F);
		this.colRightY = this.windowY + this.getScaledY(420F / 1080F);
		this.colRightWidth = this.getScaledX(1260F / 1920F);
		this.colRightHeight = this.getScaledX(400F / 1080F);
		this.colRightCenterX = this.colRightX + Math.round(this.colRightWidth / 2);
		this.colRightCenterY = this.colRightY + Math.round(this.colRightHeight / 2);

		this.buttonList.clear();
		this.initControls();
	}


	/**
	 * Draws the buttons and other controls for this GUI.
	 */
	protected void initControls() {
		int menuPadding = 6;
		int menuX = this.centerX - Math.round((float)this.windowWidth / 2) + menuPadding;
		int menuY = this.windowY + menuPadding;
		int menuWidth = this.windowWidth - (menuPadding * 2);

		int buttonCount = 5;
		int buttonPadding = 2;
		int buttonX = menuX + buttonPadding;
		int buttonWidth = Math.round((float)(menuWidth / buttonCount)) - (buttonPadding * 2);
		int buttonWidthPadded = buttonWidth + (buttonPadding * 2);
		int buttonHeight = 20;
		GuiButton button;

		// Top Menu:
		button = new GuiButton(GuiHandler.Beastiary.INDEX.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.index.title"));
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.Beastiary.CREATURES.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.creatures"));
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.Beastiary.PETS.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.pets"));
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.Beastiary.SUMMONING.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.summoning"));
		this.buttonList.add(button);
		button = new GuiButton(GuiHandler.Beastiary.ELEMENTS.id, buttonX + (buttonWidthPadded * this.buttonList.size()), menuY, buttonWidth, buttonHeight, I18n.translateToLocal("gui.beastiary.elements"));
		this.buttonList.add(button);
	}


	/**
	 * Draws and updates the GUI.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(mouseX, mouseY, partialTicks);
		this.drawForeground(mouseX, mouseY, partialTicks);
		this.updateControls(mouseX, mouseY, partialTicks);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}


	/**
	 * Draws the background image.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawBackground(int mouseX, int mouseY, float partialTicks) {
		this.drawTexture(AssetManager.getTexture("GUIBeastiaryBackground"), this.windowX, this.windowY, this.zLevel, 1, 1, this.windowWidth, this.windowHeight);
	}


	/**
	 * Updates buttons and other controls for this GUI.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	protected void updateControls(int mouseX, int mouseY, float partialTicks) {
		for(GuiButton button : this.buttonList) {
			button.drawButton(this.mc, mouseX, mouseY, partialTicks);
		}
	}


	/**
	 * Draws foreground elements.
	 * @param mouseX The x position of the mouse cursor.
	 * @param mouseY The y position of the mouse cursor.
	 * @param partialTicks Ticks for animation.
	 */
	public void drawForeground(int mouseX, int mouseY, float partialTicks) {
		String title = "\u00A7l\u00A7n" + this.getTitle();
		float width = this.getFontRenderer().getStringWidth(title);
		this.getFontRenderer().drawString(title, this.colRightCenterX - Math.round(width / 2), this.colRightY, 0xFFFFFF, true);
	}


	/**
	 * Called when a GUI button is interacted with.
	 * @param guiButton The button that was interacted with.
	 * @throws IOException
	 */
	@Override
	protected void actionPerformed(GuiButton guiButton) throws IOException {
		if(guiButton != null) {
			if(guiButton.id == GuiHandler.Beastiary.INDEX.id) {
				GuiBeastiaryIndex.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.Beastiary.CREATURES.id) {
				GuiBeastiaryCreatures.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.Beastiary.PETS.id) {
				GuiBeastiaryPets.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.Beastiary.SUMMONING.id) {
				GuiBeastiarySummoning.openToPlayer(this.player);
			}
			if(guiButton.id == GuiHandler.Beastiary.ELEMENTS.id) {
				GuiBeastiaryElements.openToPlayer(this.player);
			}
		}

		super.actionPerformed(guiButton);
	}


	/**
	 * Called when a key is pressed.
	 * @param typedChar The character typed.
	 * @param keyCode The keycode of the key pressed.
	 * @throws IOException
	 */
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if(keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
			this.mc.player.closeScreen();
		}
		super.keyTyped(typedChar, keyCode);
	}


	/**
	 * Opens a URI in the users default web browser.
	 * @param uri The URI link to open.
	 */
	protected void openURI(URI uri) {
		try {
			Class oclass = Class.forName("java.awt.Desktop");
			Object object = oclass.getMethod("getDesktop", new Class[0]).invoke(null);
			oclass.getMethod("browse", new Class[] {URI.class}).invoke(object, uri);
		}
		catch (Throwable throwable) {
			LycanitesMobs.printWarning("", "Unable to open link: " + uri.toString());
		}
	}


	/**
	 * Draws a level bar for the provided creature info.
	 * @param creatureInfo The creature info to get stats from.
	 * @param texture The texture to use as a level dot/star.
	 * @param x The x position to draw from.
	 * @param y The y position to draw from.
	 */
	public void drawLevel(CreatureInfo creatureInfo, ResourceLocation texture, int x, int y) {
		int level = creatureInfo.summonCost;
		if(level <= 10) {
			this.drawBar(texture, x, y, 0, 9, 9, level, 10);
		}
	}


	public void renderCreature(CreatureInfo creatureInfo, int x, int y, int mouseX, int mouseY, float partialTicks) {
		// Clear:
		if(creatureInfo == null) {
			this.creaturePreviewEntity = null;
			return;
		}

		try {
			// Subspecies:
			boolean subspeciesMatch = true;
			if(this.creaturePreviewEntity instanceof EntityCreatureBase) {
				subspeciesMatch = ((EntityCreatureBase)this.creaturePreviewEntity).getSubspeciesIndex() == this.getDisplaySubspecies(creatureInfo);
			}

			// Create New:
			if(this.creaturePreviewEntity == null || this.creaturePreviewEntity.getClass() != creatureInfo.entityClass || !subspeciesMatch) {
				this.creaturePreviewEntity = creatureInfo.entityClass.getConstructor(new Class[]{World.class}).newInstance(this.player.getEntityWorld());
				this.creaturePreviewEntity.onGround = true;
				if (this.creaturePreviewEntity instanceof EntityCreatureBase) {
					((EntityCreatureBase) this.creaturePreviewEntity).setSubspecies(this.getDisplaySubspecies(creatureInfo), false);
					((EntityCreatureBase) this.creaturePreviewEntity).updateSize();
				}
				if (this.creaturePreviewEntity instanceof EntityCreatureAgeable) {
					((EntityCreatureAgeable) this.creaturePreviewEntity).setGrowingAge(0);
				}
				this.onCreateDisplayEntity(creatureInfo, this.creaturePreviewEntity);
				this.playCreatureSelectSound(creatureInfo);
			}

			// Render:
			if(this.creaturePreviewEntity != null) {
				int creatureSize = 70;
				float creatureWidth = this.creaturePreviewEntity.width;
				float creatureHeight = this.creaturePreviewEntity.height;
				int scale = Math.round((1.8F / Math.max(creatureWidth, creatureHeight)) * creatureSize);
				int posX = x;
				int posY = y + 32 + creatureSize;
				float lookX = (float)posX - mouseX;
				float lookY = (float)posY - mouseY;
				this.creaturePreviewTicks += partialTicks;
				if(this.creaturePreviewEntity instanceof EntityCreatureBase) {
					EntityCreatureBase previewCreatureBase = (EntityCreatureBase)this.creaturePreviewEntity;
					previewCreatureBase.onlyRenderTicks = this.creaturePreviewTicks;
				}

				GlStateManager.enableColorMaterial();
				GlStateManager.pushMatrix();
				GlStateManager.translate((float)posX, (float)posY, -500.0F);
				GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
				GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
				float f = this.creaturePreviewEntity.renderYawOffset;
				float f1 = this.creaturePreviewEntity.rotationYaw;
				float f2 = this.creaturePreviewEntity.rotationPitch;
				float f3 = this.creaturePreviewEntity.prevRotationYawHead;
				float f4 = this.creaturePreviewEntity.rotationYawHead;
				GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
				RenderHelper.enableStandardItemLighting();
				GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-((float)Math.atan((double)(lookY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
				this.creaturePreviewEntity.renderYawOffset = (float)Math.atan((double)(lookX / 40.0F)) * 20.0F;
				this.creaturePreviewEntity.rotationYaw = (float)Math.atan((double)(lookX / 40.0F)) * 40.0F;
				this.creaturePreviewEntity.rotationPitch = -((float)Math.atan((double)(lookY / 40.0F))) * 20.0F;
				this.creaturePreviewEntity.rotationYawHead = this.creaturePreviewEntity.rotationYaw;
				this.creaturePreviewEntity.prevRotationYawHead = this.creaturePreviewEntity.rotationYaw;
				GlStateManager.translate(0.0F, 0.0F, 0.0F);
				RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
				rendermanager.setPlayerViewY(180.0F);
				rendermanager.setRenderShadow(false);
				rendermanager.renderEntity(this.creaturePreviewEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
				rendermanager.setRenderShadow(true);
				this.creaturePreviewEntity.renderYawOffset = f;
				this.creaturePreviewEntity.rotationYaw = f1;
				this.creaturePreviewEntity.rotationPitch = f2;
				this.creaturePreviewEntity.prevRotationYawHead = f3;
				this.creaturePreviewEntity.rotationYawHead = f4;
				GlStateManager.popMatrix();
				RenderHelper.disableStandardItemLighting();
				GlStateManager.disableRescaleNormal();
				GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
				GlStateManager.disableTexture2D();
				GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			}
		}
		catch (Exception e) {
			LycanitesMobs.printWarning("", "An exception occurred when trying to preview a creature in the Beastiary.");
			e.printStackTrace();
		}
	}


	/**
	 * Gets the Subspecies to use for the display creature.
	 * @param creatureInfo The Creature Info being displayed.
	 */
	public int getDisplaySubspecies(CreatureInfo creatureInfo) {
		return this.playerExt.selectedSubspecies;
	}


	/**
	 * Plays an idle or tame sound of the provided creature when it is selected in the GUI.
	 * @param creatureInfo The creature to play the sound from.
	 */
	public void playCreatureSelectSound(CreatureInfo creatureInfo) {
		this.player.getEntityWorld().playSound(this.player, this.player.posX, this.player.posY, this.player.posZ, AssetManager.getSound(creatureInfo.getName() + "_say"), SoundCategory.NEUTRAL, 1, 1);
	}


	/**
	 * Called when a display entity is created.
	 * @param creatureInfo The Creature Info used to create the entity.
	 * @param entity The display entity instance.
	 */
	public void onCreateDisplayEntity(CreatureInfo creatureInfo, EntityLivingBase entity) {}
}
