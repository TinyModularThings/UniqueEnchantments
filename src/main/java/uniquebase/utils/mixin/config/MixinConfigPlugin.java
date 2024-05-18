package uniquebase.utils.mixin.config;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.LoadingModList;

public class MixinConfigPlugin implements IMixinConfigPlugin
{
	UEMixinConfig config;
	String mixinPath;
	
	@Override
	public void onLoad(String mixinPackage) {
		Path path = FMLPaths.CONFIGDIR.get().resolve("ue").resolve("MixinConfig.properties");
		if(Files.notExists(path.getParent())) {
			try { Files.createDirectories(path.getParent()); }
			catch(Exception e) { e.printStackTrace(); }
		}
		mixinPath = "uniquebase.utils.mixin.";
		config = UEMixinConfig.builder()
				.add("client", "This defines all the Client Sided Mixins, set to false turns off all Client related Mixins")
				.addDependency("client.FontMixin", "client", "Enables the Enchantment Icons System that UE comes with")
				.addDependency("client.ItemStackMixin", "client", "Enables the Tooltip Override on Enchantments that UE does to increase Performance on its rendering feature")
				.add("common", "This defines all the Common Sided Mixins, set to false turns off all Common related Mixins")
				.addDependency("common.enchantments", "common", "Adds Enchantment Specific Mixins.")
				.addDependency("common.enchantments.CombatRulesMixin", "common.enchantment", "Enables the Rebalance Changes of the Protection Enchantments")
				.addDependency("common.enchantments.EnchantmentHelperMixin", "common.enchantment", "Enables the Control of ApexEnchantments")
				.addDependency("common.enchantments.EnchantmentMixin", "common.enchantment", "Accessor for Enchantment slots")
				.addDependency("common.enchantments.EnchantmentsMixin", "common.enchantment", "Enables Several Enchantment Features of the Vanilla Enchantments")
				.addDependency("common.enchantments.FishingLuckMixin", "common.enchantment", "Enables the Fishing Luck Event")
				.addDependency("common.enchantments.RealHelperMixin", "common.enchantment", "Enables the Control of ApexEnchantments in Apotheosis")
				.addDependency("common.enchantments.ThornsEnchantmentMixin", "common.enchantment", "Enables that Thorns Damage can stack instead of being limited to one effect")
				.addDependency("common.enchantments.UnbreakingEnchantmentMixin", "common.enchantment", "Enables that the rebalancing of the Unbreaking Enchantment")
				.addDependency("common.entity", "common", "Adds Entity Specific Mixins.")
				.addDependency("common.entity.ArrowMixin", "common.entity", "Accessor Mixin to access the Item inside the Arrow")
				.addDependency("common.entity.CombatTrackerMixin", "common.entity", "Accessor Mixin to access the Combat Log of an Entity, used by Alchemists Grace")
				.addDependency("common.entity.DragonManagerMixin", "common.entity", "Accessor Mixin to make Wars odium work with Enderdragons")
				.addDependency("common.entity.EndermenMixin", "common.entity", "Mixin Required to make the EnderEyes enchantment work")
				.addDependency("common.entity.EntityMixin", "common.entity", "Accessor Mixin that makes the Rocket man more consistent")
				.addDependency("common.entity.EntityMixinP", "common.entity", "Mixin that is enabling Protection specific features on items, also required to make common.enchantment.ThornsEnchantmentMixin work")
				.addDependency("common.entity.FireworkMixin", "common.entity", "Accessor Mixin that makes Rocketmans fireworks lifetime expansion possible")
				.addDependency("common.entity.LivingEntityMixin", "common.entity", "Mixin that rebalances the Resistance Potion Effect")
				.addDependency("common.entity.PiglinMixin", "common.entity", "Mixin that makes the Piglin neutral on you when Treasurers Eyes is applied")
				.addDependency("common.entity.PlayerEntityMixin", "common.entity", "Mixin that rebalances the Efficiency Enchantment")
				.addDependency("common.entity.PotionMixin", "common.entity", "Mixin that makes certain Enchantments possible")
				.addDependency("common.item", "common", "Adds Item Specific Mixins.")
				.addDependency("common.item.EnchantedBookMixin", "common.item", "Mixin that allows the UE Config to provide control if the Enchanted Glint is applied")
				.addDependency("common.item.MapDataMixin", "common.item", "Accessor Mixin that makes the Banner teleporting of Enderlibrian possible")
				.addDependency("common.item.StackMixin", "common.item", "Mixin that makes the Second Life (Apex) and Grimoires Upgrade (UE) possible")
				.addDependency("common.tile", "common", "Enables Tile/BlockEntity specific Mixins")
				.addDependency("common.tile.AnvilMixin", "common.tile", "Enables the XP Rebalancing and Enchantment Limits of the Base Mod")
				.addDependency("common.tile.BeaconMixin", "common.tile", "Accessor Mixin that makes the Apex Enchanting possible")
				.addDependency("common.tile.ChunkMixin", "common.tile", "Mixin that makes the Entity Accellerator possible")
				.addDependency("common.tile.EnchTableMixin", "common.tile", "Enables the XP Rebalancing and Enchantment Limits of the Base Mod")
				.build(path);
		if(LoadingModList.get().getModFileById("apotheosis") != null) {
			config.getEntry("common.enchantments.EnchantmentHelperMixin").setConfig(false);
			config.getEntry("common.enchantments.CombatRulesMixin").setConfig(false);
			config.getEntry("common.tile.AnvilMixin").setConfig(false);
			config.getEntry("common.tile.EnchTableMixin").setConfig(false);
		}
		else {
			config.getEntry("common.enchantments.RealHelperMixin").setConfig(false);
		}
	}
	
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		if(!mixinClassName.startsWith(mixinPath)) {
			throw new IllegalStateException("I am pretty sure we don't own that mixin (UE)");
		}
		return config.isMixinEnabled(mixinClassName.substring(mixinPath.length()));
	}
		
	@Override
	public String getRefMapperConfig() { return null; }
	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}
	@Override
	public List<String> getMixins() { return null; }
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
