package com.shinoow.btg.common.util;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

public class PlayerKillUtil {

	public static DamageSource cthulhu = new DamageSource("cthulhu").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource dagon = new DamageSource("dagon").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource azathoth_invocation = new DamageSource("azathoth.invocation").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource azathoth = new DamageSource("azathoth").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource shub_niggurath = new DamageSource("shub-niggurath").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource yog_sothoth = new DamageSource("yog-sothoth").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute();
	public static DamageSource nyarlathotep_explosion = new DamageSource("explosion.nyarlathotep").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute().setExplosion();
	public static DamageSource hastur_explosion = new DamageSource("explosion.hastur").setDamageAllowedInCreativeMode().setDamageBypassesArmor().setDamageIsAbsolute().setExplosion();

	/**
	 * Probably annihilates the target
	 * @param target The Entity to kill
	 * @param source Damage source
	 */
	public static void killEntity(Entity target, DamageSource source){
		if(target.attackEntityFrom(source, 1000000000)){
			if(!target.isDead){
				if(target instanceof EntityLivingBase)
					for(PotionEffect p :((EntityLivingBase)target).getActivePotionEffects())
						((EntityLivingBase) target).removePotionEffect(p.getPotion());
				target.attackEntityFrom(source, 1000000000);
				if(!target.isDead){
					clearArmor(target);
					target.attackEntityFrom(source, 1000000000);
					if(!target.isDead){
						clearHeldItems(target);
						target.attackEntityFrom(source, 1000000000);
						if(!target.isDead && target instanceof EntityPlayer){
							((EntityPlayer)target).inventory.dropAllItems();
							target.attackEntityFrom(source, 1000000000);
						}
					}
				}
			}
		} else{
			clearArmor(target);
			killEntity(target, source);
		}
	}

	private static void clearHeldItems(Entity target){
		List<ItemStack> stacks = Lists.newArrayList();

		if(target instanceof EntityPlayer){
			for(int i = 0; i < 9; i++)
				if(((EntityPlayer) target).inventory.getStackInSlot(i) != null)
					stacks.add(((EntityPlayer) target).inventory.removeStackFromSlot(i));
			if(((EntityPlayer) target).inventory.offHandInventory[0] != null)
				stacks.add(((EntityPlayer) target).inventory.offHandInventory[0]);
			target.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
		} else {
			for(ItemStack stack : target.getHeldEquipment())
				if(stack != null)
					stacks.add(stack);

			target.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, null);
			target.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, null);
		}

		for(ItemStack stack : stacks)
			target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX + target.worldObj.rand.nextInt(5),
					target.posY + target.worldObj.rand.nextInt(5), target.posZ + target.worldObj.rand.nextInt(5), stack));
	}

	private static void clearArmor(Entity target){
		List<ItemStack> stacks = Lists.newArrayList();
		for(ItemStack stack : target.getArmorInventoryList())
			if(stack != null)
				stacks.add(stack);
		target.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
		target.setItemStackToSlot(EntityEquipmentSlot.CHEST, null);
		target.setItemStackToSlot(EntityEquipmentSlot.LEGS, null);
		target.setItemStackToSlot(EntityEquipmentSlot.FEET, null);

		for(ItemStack stack : stacks)
			target.worldObj.spawnEntityInWorld(new EntityItem(target.worldObj, target.posX + target.worldObj.rand.nextInt(5),
					target.posY + target.worldObj.rand.nextInt(5), target.posZ + target.worldObj.rand.nextInt(5), stack));
	}

	/**
	 * Simply ends all life in the current world
	 * @param world World to end
	 * @param scapegoat Who's to blame for this
	 */
	public static void endAllLife(World world, EntityPlayer scapegoat){
		for(EntityPlayer p : world.playerEntities)
			if(!p.getUniqueID().equals(scapegoat.getUniqueID()))
				killEntity(p, azathoth);

		for(Entity e : world.loadedEntityList.stream().filter(e -> e instanceof EntityLiving).collect(Collectors.toList()))
			killEntity(e, azathoth);
		killEntity(scapegoat, azathoth_invocation);
	}
}