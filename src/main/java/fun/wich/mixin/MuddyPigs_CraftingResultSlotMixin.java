package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingResultSlot.class)
public abstract class MuddyPigs_CraftingResultSlotMixin {
	@Inject(method="getRecipeRemainders", at=@At("RETURN"), cancellable=true)
	private void RemoveRecipeRemainder_MuddyPigs_MudBucket(CraftingRecipeInput input, World world, CallbackInfoReturnable<DefaultedList<ItemStack>> cir) {
		if (world instanceof ServerWorld serverWorld) {
			ServerRecipeManager manager = serverWorld.getRecipeManager();
			manager.getFirstMatch(RecipeType.CRAFTING, input, serverWorld).ifPresent(entry -> {
				if (entry.id().getValue().equals(Identifier.of(MuddyPigsMod.MOD_ID, "mud_bucket"))) {
					DefaultedList<ItemStack> list = cir.getReturnValue();
					for (int i = 0; i < list.size(); i++) {
						if (list.get(i).isOf(Items.BUCKET)) list.set(i, ItemStack.EMPTY);
					}
					cir.setReturnValue(list);
				}
			});
		}
	}
}
