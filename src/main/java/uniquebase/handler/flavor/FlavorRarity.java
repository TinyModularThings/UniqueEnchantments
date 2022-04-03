package uniquebase.handler.flavor;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public enum FlavorRarity {
	//Unique Rarity is supposed to be applied at Enchanting and when an Entity spawns with an Enchanted Item
	//TODO please convert these to translation keys Xaikii
	NONE("", 0, 0, 0F),
	COMMON("Common", 1, 1, 0.1F),
	UNCOMMON("Uncommon", 2, 2, 0.15F),
	RARE("Rare", 4, 3, 0.25F),
	EPIC("Epic", 6, 4, 0.3F),
	LEGENDARY("Legendary", 8, 5, 0.4F),
	SCARCE("Scarce", 12, 6, 0.5F),
	MYTHICAL("Mythical", 16, 7, 0.55F),
	UNIQUE("Unique", 24, 8, 0.6F);
	
	String displayName;
	int index;
	int multiplier;
	float chance;
	
	private FlavorRarity(String displayName, int multiplier, int index, float chance) {
		this.displayName = displayName;
		this.multiplier = multiplier;
		this.index = index;
		this.chance = chance;
	}
	
	public ITextComponent getName()
	{
		return new StringTextComponent(displayName);
	}
	
	public int getIndex()
	{
		return index;
	}
	
	public int getMultiplier()
	{
		return multiplier;
	}
	
	public float getChance()
	{
		return chance;
	}
}
