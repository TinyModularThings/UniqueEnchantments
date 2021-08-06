package uniquee.enchantments.unique;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import uniquebase.api.UniqueEnchantment;
import uniquebase.api.filters.IGraceEnchantment;
import uniquebase.utils.MiscUtil;
import uniquee.UniqueEnchantments;

public class AlchemistsGrace extends UniqueEnchantment implements IGraceEnchantment
{
	public static final List<List<PotionPlan>> EFFECTS = new ObjectArrayList<>();
	
	public AlchemistsGrace()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, 4, true, 18, 6, 40), EnumEnchantmentType.WEAPON, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
	}
	
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof ItemAxe || stack.getItem() instanceof ItemHoe || EnumEnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
	
	@Override
	public void loadIncompats()
	{
		addIncompats(UniqueEnchantments.WARRIORS_GRACE, UniqueEnchantments.NATURES_GRACE);
	}
	
	public static void applyToEntity(Entity entity, boolean mining, float hitScalar)
	{
		if(entity instanceof EntityLivingBase)
		{
			EntityLivingBase base = (EntityLivingBase)entity;
			Object2IntMap.Entry<EntityEquipmentSlot> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ALCHEMISTS_GRACE, base);
			if(slot.getIntValue() > 0)
			{
				hitScalar = Math.max(0.5F, hitScalar);
				int level = slot.getIntValue();
				Set<Potion> potions = new ObjectOpenHashSet<Potion>();
				for(int i = level;i>=0;i--)
				{
					if(EFFECTS.size() > i)
					{
						for(PotionPlan plan : EFFECTS.get(i))
						{
							if(plan.isValid(mining)) {
								PotionEffect effect = plan.createEffect(level, hitScalar);
								if(potions.add(effect.getPotion())) base.addPotionEffect(effect);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void loadData(Configuration config)
	{
		EFFECTS.clear();
		String[] potions = config.getStringList("effects", getConfigName(), new String[]{"minecraft:regeneration;1;0;0.25;10;true;false", "minecraft:speed;1;0;1.0;60;true;true", "minecraft:haste;2;0;1.0;40;true;true", "minecraft:speed;2;1;0.5;70;true;true","minecraft:resistance;3;0;1.0;20;true;false", "minecraft:haste;3;1;0.5;60;true;true", "minecraft:strength;4;0;1.0;20;true;false", "minecraft:resistance;4;1;0.25;25;true;false","minecraft:fire_resistance;4;0;1.0;30;false;true", "minecraft:strength;5;1;0.2;30;true;false"}, "Which Potion Effects should be applied. Format: Potion;StartEnchantmentLvL;StartPotionLvL;PotionLvLPerEnchantLvL;BaseDuration;Fighting;Mining");
		for(String s : potions)
		{
			String[] split = s.split(";");
			if(split.length == 7)
			{
				Potion p = Potion.REGISTRY.getObject(new ResourceLocation(split[0]));
				if(p != null)
				{
					try
					{
						PotionPlan plan = new PotionPlan(p, split);
						while(EFFECTS.size() <= plan.baseEnchantment) EFFECTS.add(new ObjectArrayList<>());
						EFFECTS.get(plan.baseEnchantment).add(plan);	
					}
					catch(Exception e) { e.printStackTrace(); }
				}
			}
		}
	}
	
	public static class PotionPlan
	{
		Potion potion;
		int baseEnchantment;
		int basePotionLevel;
		double PotionLevelIncrease;
		int baseDuration;
		boolean fighting;
		boolean mining;
		
		public PotionPlan(Potion potion, String[] data)
		{
			this.potion = potion;
			baseEnchantment = Integer.parseInt(data[1]);
			basePotionLevel = Integer.parseInt(data[2]);
			PotionLevelIncrease = Double.parseDouble(data[3]);
			baseDuration = Integer.parseInt(data[4]);
			fighting = Boolean.parseBoolean(data[5]);
			mining = Boolean.parseBoolean(data[6]);
		}
		
		public PotionEffect createEffect(int baseLevel, float hitScalar)
		{
			int diff = Math.max(0, baseLevel - baseEnchantment);
			return new PotionEffect(potion, (int)(baseDuration * Math.log((hitScalar+0.5F)*baseLevel)), basePotionLevel + (int)(PotionLevelIncrease * diff));
		}
		
		public boolean isValid(boolean mining)
		{
			return mining ? this.mining : fighting;
		}
	}
}