package fun.wich;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.*;
import net.minecraft.block.cauldron.CauldronBehavior;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.*;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Function;

public class MuddyPigsMod implements ModInitializer {
	public static final String MOD_ID = "wich";
	public static final SimpleParticleType MUD_BUBBLE = FabricParticleTypes.simple(false);
	public static final SimpleParticleType MUD_SPLASH = FabricParticleTypes.simple(false);
	public static final SimpleParticleType DRIPPING_MUD = FabricParticleTypes.simple(false);
	public static final SimpleParticleType FALLING_MUD = FabricParticleTypes.simple(false);
	public static final SimpleParticleType DRIPPING_DRIPSTONE_MUD = FabricParticleTypes.simple(false);
	public static final SimpleParticleType FALLING_DRIPSTONE_MUD = FabricParticleTypes.simple(false);
	public static final FlowableFluid STILL_MUD_FLUID = register("still_mud",new MudFluid.Still());
	public static final FlowableFluid FLOWING_MUD_FLUID = register("flowing_mud", new MudFluid.Flowing());
	public static <T extends Fluid> T register(String name, T fluid) {
		Identifier id = Identifier.of(MOD_ID, name);
		Registry.register(Registries.FLUID, id, fluid);
		return fluid;
	}
	public static final Block MUD_FLUID_BLOCK = register(
			"mud_fluid_block",
			settings -> new FluidBlock(STILL_MUD_FLUID, settings),
			AbstractBlock.Settings.create()
					.mapColor(MapColor.BROWN)
					.replaceable()
					.noCollision()
					.strength(100.0F)
					.pistonBehavior(PistonBehavior.DESTROY)
					.dropsNothing()
					.liquid()
					.sounds(BlockSoundGroup.INTENTIONALLY_EMPTY)
	);
	public static final Block MUD_CAULDRON = register("mud_cauldron", MudCauldronBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));
	public static Block register(String name, Function<Block.Settings, Block> blockFactory, Block.Settings settings) {
		RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, name));
		Block block = blockFactory.apply(settings.registryKey(key));
		Registry.register(Registries.BLOCK, key, block);
		return block;
	}
	public static final Item MUD_BUCKET = register("mud_bucket", settings -> new BucketItem(STILL_MUD_FLUID, settings), new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));
	public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
		RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MOD_ID, name));
		Item item = itemFactory.apply(settings.registryKey(key));
		Registry.register(Registries.ITEM, key, item);
		return item;
	}
	public static final SoundEvent ENTITY_PIG_MUDDY_BLOOM = register("entity.pig.muddy.bloom");
	public static final SoundEvent ENTITY_PIG_MUDDY_SHEAR = register("entity.pig.muddy.shear");
	public static final SoundEvent ENTITY_PIG_MUDDY_WALLOW = register("entity.pig.muddy.wallow");
	private static SoundEvent register(String path) {
		Identifier id = Identifier.of(MOD_ID, path);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}
	public static final TagKey<Fluid> TAG_MUD = TagKey.of(RegistryKeys.FLUID, Identifier.of(MOD_ID, "mud"));
	@Override
	public void onInitialize() {
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "mud_bubble"), MUD_BUBBLE);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "mud_splash"), MUD_SPLASH);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "dripping_mud"), DRIPPING_MUD);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "falling_mud"), FALLING_MUD);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "dripping_dripstone_mud"), DRIPPING_DRIPSTONE_MUD);
		Registry.register(Registries.PARTICLE_TYPE, Identifier.of(MOD_ID, "falling_dripstone_mud"), FALLING_DRIPSTONE_MUD);
		Item.BLOCK_ITEMS.put(MUD_CAULDRON, Items.CAULDRON);
		CauldronBehavior.EMPTY_CAULDRON_BEHAVIOR.map().put(MuddyPigsMod.MUD_BUCKET, MudCauldronBlock::tryFillWithMud);
		CauldronBehavior.WATER_CAULDRON_BEHAVIOR.map().put(MuddyPigsMod.MUD_BUCKET, MudCauldronBlock::tryFillWithMud);
		CauldronBehavior.LAVA_CAULDRON_BEHAVIOR.map().put(MuddyPigsMod.MUD_BUCKET, MudCauldronBlock::tryFillWithMud);
		CauldronBehavior.POWDER_SNOW_CAULDRON_BEHAVIOR.map().put(MuddyPigsMod.MUD_BUCKET, MudCauldronBlock::tryFillWithMud);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(itemGroup -> itemGroup.add(MUD_BUCKET));
		DispenserBlock.registerBehavior(MUD_BUCKET, new ItemDispenserBehavior() {
			private final ItemDispenserBehavior fallback = new ItemDispenserBehavior();
			@Override
			public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
				FluidModificationItem fluidModificationItem = (FluidModificationItem)stack.getItem();
				BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
				World world = pointer.world();
				if (fluidModificationItem.placeFluid(null, world, blockPos, null)) {
					fluidModificationItem.onEmptied(null, world, stack, blockPos);
					return this.decrementStackWithRemainder(pointer, stack, new ItemStack(Items.BUCKET));
				}
				return this.fallback.dispense(pointer, stack);
			}
		});
	}
}