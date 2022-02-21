package uniquebase.api;

import net.minecraft.entity.player.EntityPlayer;

public interface IKeyBind
{
	public boolean test(EntityPlayer player);
	public String getName();
	public String getKeyName();
	
	public static IKeyBind empty() {
		return EmptyKeyBind.INSTANCE;
	}
	
	static class EmptyKeyBind implements IKeyBind
	{
		static final IKeyBind INSTANCE = new EmptyKeyBind();
		
		@Override
		public boolean test(EntityPlayer player) {
			return false;
		}

		@Override
		public String getName() {
			return "Unbound Keybinding";
		}

		@Override
		public String getKeyName() {
			return "Unknown";
		}
	}
}
