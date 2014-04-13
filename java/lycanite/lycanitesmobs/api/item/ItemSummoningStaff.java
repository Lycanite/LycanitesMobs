package lycanite.lycanitesmobs.api.item;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PlayerControlHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemSummoningStaff extends ItemScepter {
	
	// ==================================================
	//                   Constructor
	// ==================================================
    public ItemSummoningStaff(int itemID) {
        super(itemID);
    }
	
    
	// ==================================================
	//                       Use
	// ==================================================
    // ========== Durability ==========
    @Override
    public void damageItemRapid(ItemStack itemStack, EntityPlayer player) {
        return;
    }
    
    @Override
    public int getDurability() {
    	return 250;
    }
    
    // ========== Charge Time ==========
    @Override
    public int getChargeTime(ItemStack itemStack) {
        return this.getRapidTime(itemStack);
    }
    
    // ========== Rapid Time ==========
    @Override
    public int getRapidTime(ItemStack itemStack) {
        return 40;
    }
    
    // ========== Summon Cost ==========
    public int getSummonCost(ItemStack itemStack) {
    	return 1;
    }
	
    
	// ==================================================
	//                      Attack
	// ==================================================
    @Override
    public boolean rapidAttack(ItemStack itemStack, World world, EntityPlayer player) {
    	int summonAmount = PlayerControlHandler.getPlayerSummonAmount(player);
    	if(player.capabilities.isCreativeMode) {
    		PlayerControlHandler.setPlayerSummonAmount(player, ++summonAmount);
        	return true;
    	}
    	int summonFocus = PlayerControlHandler.getPlayerSummonFocus(player);
    	int summonCost = PlayerControlHandler.summonFocusCharge * this.getSummonCost(itemStack);
    	if(summonFocus < summonCost)
    		return false;
		PlayerControlHandler.setPlayerSummonFocus(player, summonFocus - summonCost);
		PlayerControlHandler.setPlayerSummonAmount(player, ++summonAmount);
		LycanitesMobs.printDebug("", "Summon CHARGED! Will summon: " + PlayerControlHandler.getPlayerSummonAmount(player) + " " + player);
    	return true;
    }
    
    @Override
    public boolean chargedAttack(ItemStack itemStack, World world, EntityPlayer player, float power) {
		int summonAmount = PlayerControlHandler.getPlayerSummonAmount(player);
    	if(summonAmount <= 0) {
			return false;
		}
    	PlayerControlHandler.setPlayerSummonAmount(player, 0);
    	this.summonCreatures(world, player, summonAmount);
    	return true;
    }
    
    public void summonCreatures(World world, EntityPlayer player, int summonAmount) {
    	LycanitesMobs.printDebug("", "Summon ACTIVATED! Summoning " + summonAmount + " mobs.");
    }

	
	// ==================================================
	//                     Repairs
	// ==================================================
    @Override
    public boolean getIsRepairable(ItemStack itemStack, ItemStack repairStack) {
        if(repairStack.itemID == ObjectManager.getItem("HellfireCharge").itemID) return true;
        return super.getIsRepairable(itemStack, repairStack);
    }
}
