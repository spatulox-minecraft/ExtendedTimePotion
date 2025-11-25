package com.spatulox;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;

import net.fabricmc.fabric.mixin.content.registry.BrewingRecipeRegistryBuilderMixin;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTimePotion implements ModInitializer {
    public static final String MOD_ID = "extended-time-potion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final int ELEVEN_MINUTES = 13200;  // 11 minutes en ticks
    public static final int FIFTEEN_MINUTES = 18000; // 15 minutes en ticks

    // NIGHT_VISION
    public static RegistryEntry<Potion> LONG_LONG_NIGHT_VISION = registerPotion("long_long_night_vision",
            new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_NIGHT_VISION = registerPotion("ultra_long_night_vision",
            new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, FIFTEEN_MINUTES)));

    // INVISIBILITY
    public static RegistryEntry<Potion> LONG_LONG_INVISIBILITY = registerPotion("long_long_invisibility",
            new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_INVISIBILITY = registerPotion("ultra_long_invisibility",
            new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, FIFTEEN_MINUTES)));

    // LEAPING
    public static RegistryEntry<Potion> LONG_LONG_LEAPING = registerPotion("long_long_leaping",
            new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_LEAPING = registerPotion("ultra_long_leaping",
            new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, FIFTEEN_MINUTES)));

    // STRONG_LEAPING
    public static RegistryEntry<Potion> LONG_LONG_STRONG_LEAPING = registerPotion("long_long_strong_leaping",
            new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_LEAPING = registerPotion("ultra_long_strong_leaping",
            new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, FIFTEEN_MINUTES)));

    // FIRE RESISTANCE
    public static RegistryEntry<Potion> LONG_LONG_FIRE_RESISTANCE = registerPotion("long_long_fire_resistance",
            new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_FIRE_RESISTANCE = registerPotion("ultra_long_fire_resistance",
            new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, FIFTEEN_MINUTES)));

    // SWIFTNESS
    public static RegistryEntry<Potion> LONG_LONG_SWIFTNESS = registerPotion("long_long_swiftness",
            new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_SWIFTNESS = registerPotion("ultra_long_swiftness",
            new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, FIFTEEN_MINUTES)));

    // STRONG SWIFTNESS
    public static RegistryEntry<Potion> LONG_LONG_STRONG_SWIFTNESS = registerPotion("long_long_strong_swiftness",
            new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, ELEVEN_MINUTES, 1)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_SWIFTNESS = registerPotion("ultra_long_strong_swiftness",
            new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, FIFTEEN_MINUTES, 1)));

    // SLOWNESS
    public static RegistryEntry<Potion> LONG_LONG_SLOWNESS = registerPotion("long_long_slowness",
            new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_SLOWNESS = registerPotion("ultra_long_slowness",
            new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES)));

    // STRONG SLOWNESS
    public static RegistryEntry<Potion> LONG_LONG_STRONG_SLOWNESS = registerPotion("long_long_strong_slowness",
            new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 3)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_SLOWNESS = registerPotion("ultra_long_strong_slowness",
            new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 3)));

    // TURTLE MASTER
    public static RegistryEntry<Potion> LONG_LONG_TURTLE_MASTER = registerPotion("long_long_turtle_master",
            new Potion("turtle_master", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 3),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, ELEVEN_MINUTES, 2)
            }));
    public static RegistryEntry<Potion> ULTRA_LONG_TURTLE_MASTER = registerPotion("ultra_long_turtle_master",
            new Potion("turtle_master", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 3),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, FIFTEEN_MINUTES, 2)
            }));

    // STRONG TURTLE MASTER
    public static RegistryEntry<Potion> LONG_LONG_STRONG_TURTLE_MASTER = registerPotion("long_long_strong_turtle_master",
            new Potion("turtle_master", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 5),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, ELEVEN_MINUTES, 3)
            }));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_TURTLE_MASTER = registerPotion("ultra_long_strong_turtle_master",
            new Potion("turtle_master", new StatusEffectInstance[]{
                    new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 5),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, FIFTEEN_MINUTES, 3)
            }));

    // WATER BREATHING
    public static RegistryEntry<Potion> LONG_LONG_WATER_BREATHING = registerPotion("long_long_water_breathing",
            new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_WATER_BREATHING = registerPotion("ultra_long_water_breathing",
            new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, FIFTEEN_MINUTES)));

    // POISON
    public static RegistryEntry<Potion> LONG_LONG_POISON = registerPotion("long_long_poison",
            new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_POISON = registerPotion("ultra_long_poison",
            new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, FIFTEEN_MINUTES)));

    // STRONG POISON
    public static RegistryEntry<Potion> LONG_LONG_STRONG_POISON = registerPotion("long_long_strong_poison",
            new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, ELEVEN_MINUTES, 1)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_POISON = registerPotion("ultra_long_strong_poison",
            new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, FIFTEEN_MINUTES, 1)));

    // REGENERATION
    public static RegistryEntry<Potion> LONG_LONG_REGENERATION = registerPotion("long_long_regeneration",
            new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_REGENERATION = registerPotion("ultra_long_regeneration",
            new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, FIFTEEN_MINUTES)));

    // STRONG REGENERATION
    public static RegistryEntry<Potion> LONG_LONG_STRONG_REGENERATION = registerPotion("long_long_strong_regeneration",
            new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, ELEVEN_MINUTES, 1)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_REGENERATION = registerPotion("ultra_long_strong_regeneration",
            new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, FIFTEEN_MINUTES, 1)));

    // STRENGTH
    public static RegistryEntry<Potion> LONG_LONG_STRENGTH = registerPotion("long_long_strength",
            new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRENGTH = registerPotion("ultra_long_strength",
            new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, FIFTEEN_MINUTES)));

    // STRONG STRENGTH
    public static RegistryEntry<Potion> LONG_LONG_STRONG_STRENGTH = registerPotion("long_long_strong_strength",
            new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, ELEVEN_MINUTES, 1)));
    public static RegistryEntry<Potion> ULTRA_LONG_STRONG_STRENGTH = registerPotion("ultra_long_strong_strength",
            new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, FIFTEEN_MINUTES, 1)));

    // WEAKNESS
    public static RegistryEntry<Potion> LONG_LONG_WEAKNESS = registerPotion("long_long_weakness",
            new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_WEAKNESS = registerPotion("ultra_long_weakness",
            new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, FIFTEEN_MINUTES)));

    // LUCK
    public static RegistryEntry<Potion> LONG_LONG_LUCK = registerPotion("long_long_luck",
            new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_LUCK = registerPotion("ultra_long_luck",
            new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, FIFTEEN_MINUTES)));

    // SLOW FALLING
    public static RegistryEntry<Potion> LONG_LONG_SLOW_FALLING = registerPotion("long_long_slow_falling",
            new Potion("slow_falling", new StatusEffectInstance(StatusEffects.SLOW_FALLING, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_SLOW_FALLING = registerPotion("ultra_long_slow_falling",
            new Potion("slow_falling", new StatusEffectInstance(StatusEffects.SLOW_FALLING, FIFTEEN_MINUTES)));

    // WIND CHARGED
    public static RegistryEntry<Potion> LONG_LONG_WIND_CHARGED = registerPotion("long_long_wind_charged",
            new Potion("wind_charged", new StatusEffectInstance(StatusEffects.WIND_CHARGED, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_WIND_CHARGED = registerPotion("ultra_long_wind_charged",
            new Potion("wind_charged", new StatusEffectInstance(StatusEffects.WIND_CHARGED, FIFTEEN_MINUTES)));

    // WEAVING
    public static RegistryEntry<Potion> LONG_LONG_WEAVING = registerPotion("long_long_weaving",
            new Potion("weaving", new StatusEffectInstance(StatusEffects.WEAVING, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_WEAVING = registerPotion("ultra_long_weaving",
            new Potion("weaving", new StatusEffectInstance(StatusEffects.WEAVING, FIFTEEN_MINUTES)));

    // OOZING
    public static RegistryEntry<Potion> LONG_LONG_OOZING = registerPotion("long_long_oozing",
            new Potion("oozing", new StatusEffectInstance(StatusEffects.OOZING, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_OOZING = registerPotion("ultra_long_oozing",
            new Potion("oozing", new StatusEffectInstance(StatusEffects.OOZING, FIFTEEN_MINUTES)));

    // INFESTED
    public static RegistryEntry<Potion> LONG_LONG_INFESTED = registerPotion("long_long_infested",
            new Potion("infested", new StatusEffectInstance(StatusEffects.INFESTED, ELEVEN_MINUTES)));
    public static RegistryEntry<Potion> ULTRA_LONG_INFESTED = registerPotion("ultra_long_infested",
            new Potion("infested", new StatusEffectInstance(StatusEffects.INFESTED, FIFTEEN_MINUTES)));

    @Override
    public void onInitialize() {

        // HEALING and HARMING cannot be time extended

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            // NIGHT_VISION
            builder.registerPotionRecipe(Potions.LONG_NIGHT_VISION, Items.GOLD_NUGGET, LONG_LONG_NIGHT_VISION);
            builder.registerPotionRecipe(LONG_LONG_NIGHT_VISION, Items.GOLDEN_CARROT, ULTRA_LONG_NIGHT_VISION);

            // INVISIBILITY
            builder.registerPotionRecipe(Potions.LONG_INVISIBILITY, Items.GOLD_NUGGET, LONG_LONG_INVISIBILITY);
            builder.registerPotionRecipe(LONG_LONG_INVISIBILITY, Items.GOLDEN_CARROT, ULTRA_LONG_INVISIBILITY);

            // LEAPING
            builder.registerPotionRecipe(Potions.LONG_LEAPING, Items.GOLD_NUGGET, LONG_LONG_LEAPING);
            builder.registerPotionRecipe(LONG_LONG_LEAPING, Items.GOLDEN_CARROT, ULTRA_LONG_LEAPING);

            // STRONG_LEAPING
            builder.registerPotionRecipe(Potions.STRONG_LEAPING, Items.GOLD_NUGGET, LONG_LONG_STRONG_LEAPING);
            builder.registerPotionRecipe(LONG_LONG_STRONG_LEAPING, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_LEAPING);

            // FIRE_RESISTANCE
            builder.registerPotionRecipe(Potions.LONG_FIRE_RESISTANCE, Items.GOLD_NUGGET, LONG_LONG_FIRE_RESISTANCE);
            builder.registerPotionRecipe(LONG_LONG_FIRE_RESISTANCE, Items.GOLDEN_CARROT, ULTRA_LONG_FIRE_RESISTANCE);

            // SWIFTNESS
            builder.registerPotionRecipe(Potions.LONG_SWIFTNESS, Items.GOLD_NUGGET, LONG_LONG_SWIFTNESS);
            builder.registerPotionRecipe(LONG_LONG_SWIFTNESS, Items.GOLDEN_CARROT, ULTRA_LONG_SWIFTNESS);

            // STRONG_SWIFTNESS
            builder.registerPotionRecipe(Potions.STRONG_SWIFTNESS, Items.GOLD_NUGGET, LONG_LONG_STRONG_SWIFTNESS);
            builder.registerPotionRecipe(LONG_LONG_STRONG_SWIFTNESS, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_SWIFTNESS);

            // SLOWNESS
            builder.registerPotionRecipe(Potions.LONG_SLOWNESS, Items.GOLD_NUGGET, LONG_LONG_SLOWNESS);
            builder.registerPotionRecipe(LONG_LONG_SLOWNESS, Items.GOLDEN_CARROT, ULTRA_LONG_SLOWNESS);

            // STRONG_SLOWNESS
            builder.registerPotionRecipe(Potions.STRONG_SLOWNESS, Items.GOLD_NUGGET, LONG_LONG_STRONG_SLOWNESS);
            builder.registerPotionRecipe(LONG_LONG_STRONG_SLOWNESS, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_SLOWNESS);

            // TURTLE_MASTER
            builder.registerPotionRecipe(Potions.LONG_TURTLE_MASTER, Items.GOLD_NUGGET, LONG_LONG_TURTLE_MASTER);
            builder.registerPotionRecipe(LONG_LONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ULTRA_LONG_TURTLE_MASTER);

            // STRONG_TURTLE_MASTER
            builder.registerPotionRecipe(Potions.STRONG_TURTLE_MASTER, Items.GOLD_NUGGET, LONG_LONG_STRONG_TURTLE_MASTER);
            builder.registerPotionRecipe(LONG_LONG_STRONG_TURTLE_MASTER, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_TURTLE_MASTER);

            // WATER_BREATHING
            builder.registerPotionRecipe(Potions.LONG_WATER_BREATHING, Items.GOLD_NUGGET, LONG_LONG_WATER_BREATHING);
            builder.registerPotionRecipe(LONG_LONG_WATER_BREATHING, Items.GOLDEN_CARROT, ULTRA_LONG_WATER_BREATHING);

            // POISON
            builder.registerPotionRecipe(Potions.LONG_POISON, Items.GOLD_NUGGET, LONG_LONG_POISON);
            builder.registerPotionRecipe(LONG_LONG_POISON, Items.GOLDEN_CARROT, ULTRA_LONG_POISON);

            // STRONG_POISON
            builder.registerPotionRecipe(Potions.STRONG_POISON, Items.GOLD_NUGGET, LONG_LONG_STRONG_POISON);
            builder.registerPotionRecipe(LONG_LONG_STRONG_POISON, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_POISON);

            // REGENERATION
            builder.registerPotionRecipe(Potions.LONG_REGENERATION, Items.GOLD_NUGGET, LONG_LONG_REGENERATION);
            builder.registerPotionRecipe(LONG_LONG_REGENERATION, Items.GOLDEN_CARROT, ULTRA_LONG_REGENERATION);

            // STRONG_REGENERATION
            builder.registerPotionRecipe(Potions.STRONG_REGENERATION, Items.GOLD_NUGGET, LONG_LONG_STRONG_REGENERATION);
            builder.registerPotionRecipe(LONG_LONG_STRONG_REGENERATION, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_REGENERATION);

            // STRENGTH
            builder.registerPotionRecipe(Potions.LONG_STRENGTH, Items.GOLD_NUGGET, LONG_LONG_STRENGTH);
            builder.registerPotionRecipe(LONG_LONG_STRENGTH, Items.GOLDEN_CARROT, ULTRA_LONG_STRENGTH);

            // STRONG_STRENGTH
            builder.registerPotionRecipe(Potions.STRONG_STRENGTH, Items.GOLD_NUGGET, LONG_LONG_STRONG_STRENGTH);
            builder.registerPotionRecipe(LONG_LONG_STRONG_STRENGTH, Items.GOLDEN_CARROT, ULTRA_LONG_STRONG_STRENGTH);

            // WEAKNESS
            builder.registerPotionRecipe(Potions.LONG_WEAKNESS, Items.GOLD_NUGGET, LONG_LONG_WEAKNESS);
            builder.registerPotionRecipe(LONG_LONG_WEAKNESS, Items.GOLDEN_CARROT, ULTRA_LONG_WEAKNESS);

            // LUCK
            builder.registerPotionRecipe(Potions.LUCK, Items.GOLD_NUGGET, LONG_LONG_LUCK);
            builder.registerPotionRecipe(LONG_LONG_LUCK, Items.GOLDEN_CARROT, ULTRA_LONG_LUCK);

            // SLOW_FALLING
            builder.registerPotionRecipe(Potions.LONG_SLOW_FALLING, Items.GOLD_NUGGET, LONG_LONG_SLOW_FALLING);
            builder.registerPotionRecipe(LONG_LONG_SLOW_FALLING, Items.GOLDEN_CARROT, ULTRA_LONG_SLOW_FALLING);

            // WIND_CHARGED
            builder.registerPotionRecipe(Potions.WIND_CHARGED, Items.GOLD_NUGGET, LONG_LONG_WIND_CHARGED);
            builder.registerPotionRecipe(LONG_LONG_WIND_CHARGED, Items.GOLDEN_CARROT, ULTRA_LONG_WIND_CHARGED);

            // WEAVING
            builder.registerPotionRecipe(Potions.WEAVING, Items.GOLD_NUGGET, LONG_LONG_WEAVING);
            builder.registerPotionRecipe(LONG_LONG_WEAVING, Items.GOLDEN_CARROT, ULTRA_LONG_WEAVING);

            // OOZING
            builder.registerPotionRecipe(Potions.OOZING, Items.GOLD_NUGGET, LONG_LONG_OOZING);
            builder.registerPotionRecipe(LONG_LONG_OOZING, Items.GOLDEN_CARROT, ULTRA_LONG_OOZING);

            // INFESTED
            builder.registerPotionRecipe(Potions.INFESTED, Items.GOLD_NUGGET, LONG_LONG_INFESTED);
            builder.registerPotionRecipe(LONG_LONG_INFESTED, Items.GOLDEN_CARROT, ULTRA_LONG_INFESTED);
        });
    }

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(MOD_ID, name), potion);
    }

    public static Identifier id(String path){
        return Identifier.of(MOD_ID, path);
    }
}
