package uniquee.enchantments.unique;

import java.util.List;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.ForgeRegistries;
import uniquee.UniqueEnchantments;
import uniquee.enchantments.UniqueEnchantment;
import uniquee.enchantments.type.IGraceEnchantment;
import uniquee.utils.MiscUtil;

public class AlchemistsGraceEnchantment extends UniqueEnchantment implements IGraceEnchantment
{
	public static final List<List<PotionPlan>> EFFECTS = new ObjectArrayList<>();
	static ConfigValue<List<? extends String>> EFFECT_CONFIG;
	
	public AlchemistsGraceEnchantment()
	{
		super(new DefaultData("alchemistsgrace", Rarity.VERY_RARE, 4, true, 20, 3, 18), EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND});
	}
		
	@Override
	protected boolean canApplyToItem(ItemStack stack)
	{
		return stack.getItem() instanceof AxeItem || stack.getItem() instanceof HoeItem || EnchantmentType.BOW.canEnchantItem(stack.getItem());
	}
		
	@Override
	protected boolean canApplyTogether(Enchantment ench)
	{
		return ench instanceof WarriorsGraceEnchantment || ench instanceof NaturesGraceEnchantment ? false : super.canApplyTogether(ench);
	}
	
	public static void applyToEntity(Entity entity)
	{
		if(entity instanceof LivingEntity)
		{
			LivingEntity base = (LivingEntity)entity;
			Object2IntMap.Entry<EquipmentSlotType> slot = MiscUtil.getEnchantedItem(UniqueEnchantments.ALCHEMISTS_GRACE, base);
			if(slot.getIntValue() > 0)
			{
				int level = slot.getIntValue();
				Set<Effect> potions = new ObjectOpenHashSet<Effect>();
				for(int i = level;i>=0;i--)
				{
					if(EFFECTS.size() <= i)
					{
						continue;
					}
					for(PotionPlan plan : EFFECTS.get(i))
					{
						EffectInstance effect = plan.createEffect(level);
						if(potions.add(effect.getPotion()))
						{
							base.addPotionEffect(effect);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void loadData(Builder config)
	{
		config.comment("Which Potion Effects should be applied. Format: MinimumEnchantLevel;Potion;PotionLevel;BaseDuration");
		EFFECT_CONFIG = config.defineList("effects", ObjectArrayList.wrap(new String[]{"1;minecraft:speed;1;4", "2;minecraft:haste;1;6", "2;minecraft:speed;2;6", "3;minecraft:resistance;1;8", "3;minecraft:haste;2;8", "4;minecraft:strength;2;10", "4;minecraft:resistance;2;10"}), (T) -> true);
	}
	
	@Override
	public void onConfigChanged()
	{
		EFFECTS.clear();
		List<? extends String> list = EFFECT_CONFIG.get();
		for(int i = 0,m=list.size();i<m;i++)
		{
			String[] split = list.get(i).split(";");
			if(split.length == 5)
			{
				Effect p = ForgeRegistries.POTIONS.getValue(new ResourceLocation(split[0]));
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
		Effect potion;
		int baseEnchantment;
		int basePotionLevel;
		int PotionLevelIncrease;
		int baseDuration;
		
		public PotionPlan(Effect potion, String[] data)
		{
			this.potion = potion;
			baseEnchantment = Integer.parseInt(data[1]);
			basePotionLevel = Integer.parseInt(data[2]);
			PotionLevelIncrease = Integer.parseInt(data[3]);
			baseDuration = Integer.parseInt(data[4]);
		}
		
		public EffectInstance createEffect(int baseLevel)
		{
			int diff = Math.max(0, baseLevel - baseEnchantment);
			return new EffectInstance(potion, baseDuration * baseLevel, basePotionLevel + (PotionLevelIncrease * diff));
		}
	}
}
