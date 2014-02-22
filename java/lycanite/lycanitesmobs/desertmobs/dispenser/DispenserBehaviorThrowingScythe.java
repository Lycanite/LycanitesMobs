package lycanite.lycanitesmobs.desertmobs.dispenser;

import java.util.Random;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.api.entity.EntityRapidFire;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class DispenserBehaviorThrowingScythe extends BehaviorProjectileDispense {
	
	// ==================================================
	//                      Dispense
	// ==================================================
	@Override
    public ItemStack dispenseStack(IBlockSource par1IBlockSource, ItemStack par2ItemStack) {
        World world = par1IBlockSource.getWorld();
        IPosition iposition = BlockDispenser.getIPositionFromBlockSource(par1IBlockSource);
        EnumFacing enumfacing = BlockDispenser.getFacing(par1IBlockSource.getBlockMetadata());
        IProjectile iprojectile = this.getProjectileEntity(world, iposition);
        iprojectile.setThrowableHeading((double)enumfacing.getFrontOffsetX(), (double)enumfacing.getFrontOffsetY(), (double)enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
        world.spawnEntityInWorld((Entity)iprojectile);
        par2ItemStack.splitStack(1);
        return par2ItemStack;
    }
    
	@Override
    protected IProjectile getProjectileEntity(World par1World, IPosition par2IPosition) {
        return new EntityRapidFire(EntityThrowingScythe.class, par1World, par2IPosition.getX(), par2IPosition.getY(), par2IPosition.getZ(), 15, 5);
    }
    
    
	// ==================================================
	//                        Sound
	// ==================================================
	@Override
    protected void playDispenseSound(IBlockSource par1IBlockSource) {
        //par1IBlockSource.getWorld().playSoundEffect(par1IBlockSource.getX(), par1IBlockSource.getY(), par1IBlockSource.getZ(), AssetManager.getSound("ThrowingScythe"), 1.0F, 1.0F / (new Random().nextFloat() * 0.4F + 0.8F));
    }
}