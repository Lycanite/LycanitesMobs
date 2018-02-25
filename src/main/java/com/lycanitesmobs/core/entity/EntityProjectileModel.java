package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class EntityProjectileModel extends EntityProjectileBase {


	// ==================================================
	//                   Constructors
	// ==================================================
	public EntityProjectileModel(World world) {
		super(world);
	}

	public EntityProjectileModel(World world, EntityLivingBase entityLiving) {
		super(world, entityLiving);
	}

	public EntityProjectileModel(World world, double x, double y, double z) {
		super(world, x, y, z);
	}


	// ==================================================
	//                      Visuals
	// ==================================================
	public String getTextureName() {
	return this.entityName.toLowerCase();
	}

	public ResourceLocation getTexture() {
		if(AssetManager.getTexture(this.getTextureName()) == null)
			AssetManager.addTexture(this.getTextureName(), this.group, "textures/projectile/" + this.getTextureName() + ".png");
		return AssetManager.getTexture(this.getTextureName());
	}
}
