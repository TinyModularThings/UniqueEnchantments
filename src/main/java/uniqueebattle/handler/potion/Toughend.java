package uniqueebattle.handler.potion;

import net.minecraft.potion.Potion;

public class Toughend extends Potion
{
	public Toughend()
	{
		super(false, 0xFF99453A);
		setRegistryName("toughend");
		setBeneficial();
	}
}