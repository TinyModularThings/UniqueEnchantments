package uniquebase.utils;

public enum UniqueRarity {
	//Unique Rarity is supposed to be applied at Enchanting and when an Entity spawns with an Enchanted Item
	NONE("", 0, 0, 0.0f),
	COMMON("Common", 1, 1, 0.1f),
	UNCOMMON("Uncommon", 2, 2, 0.15f),
	RARE("Rare", 4, 3, 0.25f),
	EPIC("Epic", 6, 4, 0.3f),
	LEGENDARY("Legendary", 8, 5, 0.4f),
	SCARCE("Scarce", 12, 6, 0.5f),
	MYTHICAL("Mythical", 16, 7, 0.55f),
	UNIQUE("Unique", 24, 8, 0.6f);
	
	public String displayName;
	public int index;
	public int multiplier;
	public float chance;
	
	private UniqueRarity(String displayName, int multiplier, int index, float chance) {
		this.displayName = displayName;
		this.multiplier = multiplier;
		this.index = index;
		this.chance = chance;
	}

}
