package fun.wich.mixin;

import fun.wich.MuddyPigsMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CrafterBlock;
import net.minecraft.block.entity.CrafterBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public abstract class MuddyPigs_CrafterBlockMixin {
	@Shadow
	public static Optional<RecipeEntry<CraftingRecipe>> getCraftingRecipe(ServerWorld world, CraftingRecipeInput input) { return Optional.empty(); }
	@Shadow @Final public static BooleanProperty CRAFTING;
	@Shadow
	protected abstract void transferOrSpawnStack(ServerWorld world, BlockPos pos, CrafterBlockEntity blockEntity, ItemStack stack, BlockState state, RecipeEntry<?> recipe);

	@Inject(method="craft", at=@At("HEAD"), cancellable=true)
	private void RemoveRecipeRemainder_MuddyPigs_MudBucket(BlockState state, ServerWorld world, BlockPos pos, CallbackInfo ci) {
		if (!(world.getBlockEntity(pos) instanceof CrafterBlockEntity crafterBlockEntity)) return;
		CraftingRecipeInput input = crafterBlockEntity.createRecipeInput();
		Optional<RecipeEntry<CraftingRecipe>> optional =  getCraftingRecipe(world, input);
		optional.ifPresent(recipeEntry -> {
			ItemStack itemStack = recipeEntry.value().craft(input, world.getRegistryManager());
			if (!itemStack.isOf(MuddyPigsMod.MUD_BUCKET)) return;
			crafterBlockEntity.setCraftingTicksRemaining(6);
			world.setBlockState(pos, state.with(CRAFTING, true), Block.NOTIFY_LISTENERS);
			itemStack.onCraftByCrafter(world);
			this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack, state, recipeEntry);
			for (ItemStack itemStack2 : recipeEntry.value().getRecipeRemainders(input)) {
				if (!itemStack2.isEmpty() && !itemStack2.isOf(Items.BUCKET)) {
					this.transferOrSpawnStack(world, pos, crafterBlockEntity, itemStack2, state, recipeEntry);
				}
			}
			crafterBlockEntity.getHeldStacks().forEach(stack -> { if (!stack.isEmpty()) stack.decrement(1); });
			crafterBlockEntity.markDirty();
			ci.cancel();
		});
	}
}
