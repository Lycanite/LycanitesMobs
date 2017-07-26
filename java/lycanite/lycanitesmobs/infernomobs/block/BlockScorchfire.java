package lycanite.lycanitesmobs.infernomobs.block;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.block.BlockFireBase;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockScorchfire extends BlockFireBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockScorchfire() {
		super(Material.FIRE, InfernoMobs.group, "scorchfire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 6;
        this.spreadChance = 1;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Scorchfire", true);
        this.removeOnNoFireTick = ConfigBase.getConfig(this.group, "general").getBool("Features", "Remove Scorchfire on No Fire Tick", false);
		
		this.setLightOpacity(1);
        this.setLightLevel(0.8F);
	}


    // ==================================================
    //                       Break
    // ==================================================
    @Override
    public Item getItemDropped(IBlockState state, Random random, int zero) {
        return ObjectManager.getItem("scorchfirecharge");
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);
		if(entity instanceof EntityItem && ((EntityItem)entity).getEntityItem() != null)
    		if(((EntityItem)entity).getEntityItem().getItem() == ObjectManager.getItem("scorchfirecharge"))
    			return;

        if(ObjectManager.getPotionEffect("penetration") != null) {
            PotionEffect effectPenetration = new PotionEffect(ObjectManager.getPotionEffect("penetration"), 5 * 20, 0);
            if(entity instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase)entity;
                if(!entityLiving.isPotionApplicable(effectPenetration))
                    return;
                entityLiving.addPotionEffect(effectPenetration);
            }
        }

		if(entity.isImmuneToFire())
			return;
    	entity.attackEntityFrom(DamageSource.IN_FIRE, 1);
		entity.setFire(3);
	}


    // ==================================================
    //                      Particles
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("scorchfire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.FLAME, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }
}