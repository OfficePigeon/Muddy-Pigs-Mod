package fun.wich.client;

import fun.wich.MuddyPigsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.particle.WaterBubbleParticle;
import net.minecraft.client.particle.WaterSplashParticle;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.entity.model.ColdPigEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.PigEntityModel;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ModClient implements ClientModInitializer {
	//Mud
	public static final EntityModelLayer MUDDY_PIG_MUD = MakeModelLayer("muddy_pig_mud");
	public static final EntityModelLayer MUDDY_PIG_MUD_BABY = MakeModelLayer("muddy_pig_mud_baby");
	public static final EntityModelLayer MUDDY_PIG_MUD_COLD = MakeModelLayer("muddy_pig_mud_cold");
	public static final EntityModelLayer MUDDY_PIG_MUD_COLD_BABY = MakeModelLayer("muddy_pig_mud_cold_baby");
	//Flower
	public static final EntityModelLayer MUDDY_PIG_FLOWER = MakeModelLayer("muddy_pig_flower");
	private static EntityModelLayer MakeModelLayer(String id) {
		return new EntityModelLayer(Identifier.of(MuddyPigsMod.MOD_ID, id), "main");
	}
	@Override
	public void onInitializeClient() {
		//Muddy Pigs
		TexturedModelData pigModelData = PigEntityModel.getTexturedModelData(new Dilation(0.1F));
		EntityModelLayerRegistry.registerModelLayer(MUDDY_PIG_MUD, () -> pigModelData);
		EntityModelLayerRegistry.registerModelLayer(MUDDY_PIG_MUD_BABY, () -> pigModelData.transform(PigEntityModel.BABY_TRANSFORMER));
		TexturedModelData coldPigModelData = ColdPigEntityModel.getTexturedModelData(new Dilation(0.1F));
		EntityModelLayerRegistry.registerModelLayer(MUDDY_PIG_MUD_COLD, () -> coldPigModelData);
		EntityModelLayerRegistry.registerModelLayer(MUDDY_PIG_MUD_COLD_BABY, () -> coldPigModelData.transform(PigEntityModel.BABY_TRANSFORMER));
		//Flower
		EntityModelLayerRegistry.registerModelLayer(MUDDY_PIG_FLOWER, MuddyPigFlowerModel::getTexturedModelData);
		//Mud
		ParticleFactoryRegistry PARTICLES = ParticleFactoryRegistry.getInstance();
		PARTICLES.register(MuddyPigsMod.MUD_BUBBLE, WaterBubbleParticle.Factory::new);
		PARTICLES.register(MuddyPigsMod.MUD_SPLASH, WaterSplashParticle.SplashFactory::new);
		PARTICLES.register(MuddyPigsMod.DRIPPING_MUD, ModBlockLeakParticle.DrippingMudFactory::new);
		PARTICLES.register(MuddyPigsMod.FALLING_MUD, ModBlockLeakParticle.FallingMudFactory::new);
		PARTICLES.register(MuddyPigsMod.DRIPPING_DRIPSTONE_MUD, ModBlockLeakParticle.DrippingDripstoneMudFactory::new);
		PARTICLES.register(MuddyPigsMod.FALLING_DRIPSTONE_MUD, ModBlockLeakParticle.FallingDripstoneMudFactory::new);
		//Fluid
		FluidRenderHandlerRegistry.INSTANCE.register(MuddyPigsMod.STILL_MUD_FLUID, MuddyPigsMod.FLOWING_MUD_FLUID,
				new SimpleFluidRenderHandler(
						Identifier.of(MuddyPigsMod.MOD_ID, "block/mud_still"),
						Identifier.of(MuddyPigsMod.MOD_ID, "block/mud_flow"),
						0x472804));
		BlockRenderLayerMap.putFluids(BlockRenderLayer.SOLID, MuddyPigsMod.STILL_MUD_FLUID, MuddyPigsMod.FLOWING_MUD_FLUID);
	}
}
