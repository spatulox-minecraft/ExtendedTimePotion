package com.spatulox.datagen;

import com.spatulox.ExtendedTimePotion;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.BrewingProvider;
import net.minecraft.data.recipes.BrewingRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * The mixes FabricPotionBrewingBuilder used to register at runtime, as data.
 *
 * <p>Since 26.3 a mix no longer applies to every container on its own: vanilla
 * writes one recipe per container, plus one gunpowder / dragon breath recipe per
 * potion. {@link BrewingProvider} does both, the same way VanillaBrewingProvider does.
 */
class ExtendedTimePotionBrewingProvider extends BrewingProvider {
    private record Transformation(Item container, Item reagent, Item output) {
    }

    private static final List<Transformation> TRANSFORMATIONS = List.of(
            new Transformation(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION),
            new Transformation(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION));

    private final RecipeOutput output;
    private final Set<Holder<Potion>> modPotions = new LinkedHashSet<>();

    ExtendedTimePotionBrewingProvider(RecipeOutput output) {
        super(output);
        this.output = output;
    }

    @Override
    protected void addContainers() {
        addContainer(Items.POTION);
        addContainer(Items.SPLASH_POTION);
        addContainer(Items.LINGERING_POTION);
    }

    @Override
    protected void addContainerTransformations() {
        // Intentionally empty, see buildTransformations().
    }

    @Override
    protected void buildMix(Holder<Potion> input, Item reagent, Holder<Potion> result) {
        super.buildMix(input, reagent, result);
        modPotions.add(result);
    }

    /**
     * The inherited version would transform every potion a mix mentions, inputs
     * included, and so emit a second copy of vanilla's own long_night_vision ->
     * splash recipe. Only the potions this mod adds need one.
     */
    @Override
    protected void buildTransformations() {
        for (Transformation transformation : TRANSFORMATIONS) {
            for (Holder<Potion> potion : modPotions) {
                save(BrewingRecipeBuilder.brewingContainerTransform(
                        transformation.container(), potion, transformation.reagent(), transformation.output()));
            }
        }
    }

    /**
     * The default id takes the namespace of the input potion, which files every mix
     * starting from a vanilla potion under data/minecraft. Keep them all in ours.
     */
    @Override
    protected void save(BrewingRecipeBuilder builder) {
        String path = builder.defaultId().identifier().getPath();
        builder.save(output, ResourceKey.create(Registries.RECIPE, ExtendedTimePotion.id(path)));
    }

    @Override
    protected void buildMixes() {
        // HEALING and HARMING cannot be time extended
        // NIGHT_VISION
        buildMix(Potions.LONG_NIGHT_VISION, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_NIGHT_VISION);
        buildMix(ExtendedTimePotion.LONG_LONG_NIGHT_VISION, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_NIGHT_VISION);

        // INVISIBILITY
        buildMix(Potions.LONG_INVISIBILITY, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_INVISIBILITY);
        buildMix(ExtendedTimePotion.LONG_LONG_INVISIBILITY, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_INVISIBILITY);

        // LEAPING
        buildMix(Potions.LONG_LEAPING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_LEAPING);
        buildMix(ExtendedTimePotion.LONG_LONG_LEAPING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_LEAPING);

        // STRONG_LEAPING
        buildMix(Potions.STRONG_LEAPING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_LEAPING);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_LEAPING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_LEAPING);

        // FIRE_RESISTANCE
        buildMix(Potions.LONG_FIRE_RESISTANCE, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_FIRE_RESISTANCE);
        buildMix(ExtendedTimePotion.LONG_LONG_FIRE_RESISTANCE, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_FIRE_RESISTANCE);

        // SWIFTNESS
        buildMix(Potions.LONG_SWIFTNESS, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_SWIFTNESS);
        buildMix(ExtendedTimePotion.LONG_LONG_SWIFTNESS, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_SWIFTNESS);

        // STRONG_SWIFTNESS
        buildMix(Potions.STRONG_SWIFTNESS, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_SWIFTNESS);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_SWIFTNESS, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_SWIFTNESS);

        // SLOWNESS
        buildMix(Potions.LONG_SLOWNESS, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_SLOWNESS);
        buildMix(ExtendedTimePotion.LONG_LONG_SLOWNESS, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_SLOWNESS);

        // STRONG_SLOWNESS
        buildMix(Potions.STRONG_SLOWNESS, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_SLOWNESS);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_SLOWNESS, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_SLOWNESS);

        // TURTLE_MASTER
        buildMix(Potions.LONG_TURTLE_MASTER, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_TURTLE_MASTER);
        buildMix(ExtendedTimePotion.LONG_LONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_TURTLE_MASTER);

        // STRONG_TURTLE_MASTER
        buildMix(Potions.STRONG_TURTLE_MASTER, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_TURTLE_MASTER);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_TURTLE_MASTER);

        // WATER_BREATHING
        buildMix(Potions.LONG_WATER_BREATHING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_WATER_BREATHING);
        buildMix(ExtendedTimePotion.LONG_LONG_WATER_BREATHING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_WATER_BREATHING);

        // POISON
        buildMix(Potions.LONG_POISON, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_POISON);
        buildMix(ExtendedTimePotion.LONG_LONG_POISON, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_POISON);

        // STRONG_POISON
        buildMix(Potions.STRONG_POISON, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_POISON);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_POISON, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_POISON);

        // REGENERATION
        buildMix(Potions.LONG_REGENERATION, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_REGENERATION);
        buildMix(ExtendedTimePotion.LONG_LONG_REGENERATION, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_REGENERATION);

        // STRONG_REGENERATION
        buildMix(Potions.STRONG_REGENERATION, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_REGENERATION);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_REGENERATION, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_REGENERATION);

        // STRENGTH
        buildMix(Potions.LONG_STRENGTH, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRENGTH);
        buildMix(ExtendedTimePotion.LONG_LONG_STRENGTH, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRENGTH);

        // STRONG_STRENGTH
        buildMix(Potions.STRONG_STRENGTH, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_STRONG_STRENGTH);
        buildMix(ExtendedTimePotion.LONG_LONG_STRONG_STRENGTH, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_STRONG_STRENGTH);

        // WEAKNESS
        buildMix(Potions.LONG_WEAKNESS, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_WEAKNESS);
        buildMix(ExtendedTimePotion.LONG_LONG_WEAKNESS, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_WEAKNESS);

        // LUCK
        buildMix(Potions.LUCK, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_LUCK);
        buildMix(ExtendedTimePotion.LONG_LONG_LUCK, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_LUCK);

        // SLOW_FALLING
        buildMix(Potions.LONG_SLOW_FALLING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_SLOW_FALLING);
        buildMix(ExtendedTimePotion.LONG_LONG_SLOW_FALLING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_SLOW_FALLING);

        // WIND_CHARGED
        buildMix(Potions.WIND_CHARGED, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_WIND_CHARGED);
        buildMix(ExtendedTimePotion.LONG_LONG_WIND_CHARGED, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_WIND_CHARGED);

        // WEAVING
        buildMix(Potions.WEAVING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_WEAVING);
        buildMix(ExtendedTimePotion.LONG_LONG_WEAVING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_WEAVING);

        // OOZING
        buildMix(Potions.OOZING, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_OOZING);
        buildMix(ExtendedTimePotion.LONG_LONG_OOZING, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_OOZING);

        // INFESTED
        buildMix(Potions.INFESTED, Items.GOLD_NUGGET, ExtendedTimePotion.LONG_LONG_INFESTED);
        buildMix(ExtendedTimePotion.LONG_LONG_INFESTED, Items.GOLDEN_CARROT, ExtendedTimePotion.ULTRA_LONG_INFESTED);
    }
}
