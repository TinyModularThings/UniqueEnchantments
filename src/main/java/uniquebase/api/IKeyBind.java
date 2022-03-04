package uniquebase.api;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public interface IKeyBind
{
	public boolean test(PlayerEntity player);
	public ITextComponent getName();
	public ITextComponent getKeyName();
	
	public static IKeyBind empty() {
		return EmptyKeyBind.INSTANCE;
	}
	
	static class EmptyKeyBind implements IKeyBind
	{
		static final IKeyBind INSTANCE = new EmptyKeyBind();
		
		@Override
		public boolean test(PlayerEntity player) {
			return false;
		}

		@Override
		public ITextComponent getName() {
			return new StringTextComponent("Unbound Keybinding");
		}

		@Override
		public ITextComponent getKeyName() {
			return new StringTextComponent("Unknown");
		}
	}
}
