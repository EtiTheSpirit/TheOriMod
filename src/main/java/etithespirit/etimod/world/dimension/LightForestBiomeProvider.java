package etithespirit.etimod.world.dimension;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import etithespirit.etimod.registry.BiomeRegistry;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProvider;

/**
 * Based off of McJty's tutorial. Modified for personal use.
 * @author Eti
 *
 */
public class LightForestBiomeProvider extends BiomeProvider {
	
	public static final Codec<LightForestBiomeProvider> BIOME_CODEC = RegistryLookupCodec.getLookUpCodec(Registry.BIOME_KEY)
			.xmap(LightForestBiomeProvider::new, LightForestBiomeProvider::getBiomeRegistry).codec();

	
	// TODO: Figure out what this exactly is. I should go and review the original 1.14 video. Is an array even a feasible approach if I want multiple biomes? I'll assume it is.
	private final Biome[] biomes;
	private final Registry<Biome> biomeRegistry;
	private static final List<RegistryKey<Biome>> SPAWN_BIOMES = Collections.singletonList(Biomes.THE_VOID);
	
	public LightForestBiomeProvider(Registry<Biome> biomes) {
		super(getStartBiomes(biomes));
		biomeRegistry = biomes;
		this.biomes = new Biome[] { getBiome(Biomes.THE_VOID), getBiome(BiomeRegistry.GLADES_KEY) };
	}
	
	private Biome getBiome(RegistryKey<Biome> key) {
		return biomeRegistry.getOrDefault(key.getLocation());
	}
	
	private static List<Biome> getStartBiomes(Registry<Biome> registry) {
		return SPAWN_BIOMES.stream().map(s -> registry.getOrDefault(s.getLocation())).collect(Collectors.toList());
	};
	
	public Registry<Biome> getBiomeRegistry() {
		return biomeRegistry;
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return biomes[1];
	}

	@Override
	protected Codec<? extends BiomeProvider> getBiomeProviderCodec() {
		return BIOME_CODEC;
	}

	@Override
	public BiomeProvider getBiomeProvider(long seed) {
		return this;
	}

}
