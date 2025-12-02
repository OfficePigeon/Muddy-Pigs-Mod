package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public abstract class MuddyPigs_CraftingResultSlotMixin extends Slot {
	@Shadow protected abstract DefaultedList<ItemStack> getRecipeRemainders(CraftingRecipeInput input, World world);
	@Shadow @Final private RecipeInputInventory input;
	@Shadow @Final private PlayerEntity player;
	public MuddyPigs_CraftingResultSlotMixin(Inventory inventory, int index, int x, int y) { super(inventory, index, x, y); }
	@Inject(method = "onTakeItem", at = @At("HEAD"), cancellable=true)
	private void RemoveRecipeRemainder_MuddyPigs_MudBucket(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		if (stack.isOf(MuddyPigsMod.MUD_BUCKET)) {
			this.onCrafted(stack);
			CraftingRecipeInput.Positioned positioned = this.input.createPositionedRecipeInput();
			CraftingRecipeInput craftingRecipeInput = positioned.input();
			int i = positioned.left();
			int j = positioned.top();
			DefaultedList<ItemStack> defaultedList = this.getRecipeRemainders(craftingRecipeInput, player.getEntityWorld());
			for (int index = 0; index < defaultedList.size(); index++) {
				if (defaultedList.get(index).isOf(Items.BUCKET)) {
					defaultedList.set(index, ItemStack.EMPTY);
					break;
				}
			}
			for (int k = 0; k < craftingRecipeInput.getHeight(); k++) {
				for (int l = 0; l < craftingRecipeInput.getWidth(); l++) {
					int m = l + i + (k + j) * this.input.getWidth();
					ItemStack itemStack = this.input.getStack(m);
					ItemStack itemStack2 = defaultedList.get(l + k * craftingRecipeInput.getWidth());
					if (!itemStack.isEmpty()) {
						this.input.removeStack(m, 1);
						itemStack = this.input.getStack(m);
					}
					if (!itemStack2.isEmpty()) {
						if (itemStack.isEmpty()) this.input.setStack(m, itemStack2);
						else if (ItemStack.areItemsAndComponentsEqual(itemStack, itemStack2)) {
							itemStack2.increment(itemStack.getCount());
							this.input.setStack(m, itemStack2);
						}
						else if (!this.player.getInventory().insertStack(itemStack2)) this.player.dropItem(itemStack2, false);
					}
				}
			}
			ci.cancel();
		}
	}
}
