package com.spatulox;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtendedTimePotion implements ModInitializer {
    public static final String MOD_ID = "extended-time-potion";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
    public void onInitialize() {
        final int ELEVEN_MINUTES = 13200;  // 11 minutes en ticks
        final int FIFTEEN_MINUTES = 18000; // 15 minutes en ticks

        // HEALING and HARMING cannot be time extended

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            // NIGHT_VISION
            RegistryEntry<Potion> longLongNightVision = registerPotion("long_long_night_vision",
                    new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongNightVision = registerPotion("ultra_long_night_vision",
                    new Potion("night_vision", new StatusEffectInstance(StatusEffects.NIGHT_VISION, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_NIGHT_VISION, Items.GOLD_NUGGET, longLongNightVision);
            builder.registerPotionRecipe(longLongNightVision, Items.GOLDEN_CARROT, ultraLongNightVision);

            // INVISIBILITY
            RegistryEntry<Potion> longLongInvisibility = registerPotion("long_long_invisibility",
                    new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongInvisibility = registerPotion("ultra_long_invisibility",
                    new Potion("invisibility", new StatusEffectInstance(StatusEffects.INVISIBILITY, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_INVISIBILITY, Items.GOLD_NUGGET, longLongInvisibility);
            builder.registerPotionRecipe(longLongInvisibility, Items.GOLDEN_CARROT, ultraLongInvisibility);

            // LEAPING
            RegistryEntry<Potion> longLongLeaping = registerPotion("long_long_leaping",
                    new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongLeaping = registerPotion("ultra_long_leaping",
                    new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_LEAPING, Items.GOLD_NUGGET, longLongLeaping);
            builder.registerPotionRecipe(longLongLeaping, Items.GOLDEN_CARROT, ultraLongLeaping);

            // STRONG_LEAPING
            RegistryEntry<Potion> longLongStrongLeaping = registerPotion("long_long_strong_leaping",
                    new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongStrongLeaping = registerPotion("ultra_long_strong_leaping",
                    new Potion("leaping", new StatusEffectInstance(StatusEffects.JUMP_BOOST, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.STRONG_LEAPING, Items.GOLD_NUGGET, longLongStrongLeaping);
            builder.registerPotionRecipe(longLongStrongLeaping, Items.GOLDEN_CARROT, ultraLongStrongLeaping);

            // FIRE_RESISTANCE
            RegistryEntry<Potion> longLongFireResistance = registerPotion("long_long_fire_resistance",
                    new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongFireResistance = registerPotion("ultra_long_fire_resistance",
                    new Potion("fire_resistance", new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_FIRE_RESISTANCE, Items.GOLD_NUGGET, longLongFireResistance);
            builder.registerPotionRecipe(longLongFireResistance, Items.GOLDEN_CARROT, ultraLongFireResistance);

            // SWIFTNESS
            RegistryEntry<Potion> longLongSwiftness = registerPotion("long_long_swiftness",
                    new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongSwiftness = registerPotion("ultra_long_swiftness",
                    new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_SWIFTNESS, Items.GOLD_NUGGET, longLongSwiftness);
            builder.registerPotionRecipe(longLongSwiftness, Items.GOLDEN_CARROT, ultraLongSwiftness);

            // STRONG_SWIFTNESS
            RegistryEntry<Potion> longLongStrongSwiftness = registerPotion("long_long_strong_swiftness",
                    new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, ELEVEN_MINUTES, 1)));
            RegistryEntry<Potion> ultraLongStrongSwiftness = registerPotion("ultra_long_strong_swiftness",
                    new Potion("swiftness", new StatusEffectInstance(StatusEffects.SPEED, FIFTEEN_MINUTES, 1)));
            builder.registerPotionRecipe(Potions.STRONG_SWIFTNESS, Items.GOLD_NUGGET, longLongStrongSwiftness);
            builder.registerPotionRecipe(longLongStrongSwiftness, Items.GOLDEN_CARROT, ultraLongStrongSwiftness);

            // SLOWNESS
            RegistryEntry<Potion> longLongSlowness = registerPotion("long_long_slowness",
                    new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongSlowness = registerPotion("ultra_long_slowness",
                    new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_SLOWNESS, Items.GOLD_NUGGET, longLongSlowness);
            builder.registerPotionRecipe(longLongSlowness, Items.GOLDEN_CARROT, ultraLongSlowness);

            // STRONG_SLOWNESS
            RegistryEntry<Potion> longLongStrongSlowness = registerPotion("long_long_strong_slowness",
                    new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 3)));
            RegistryEntry<Potion> ultraLongStrongSlowness = registerPotion("ultra_long_strong_slowness",
                    new Potion("slowness", new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 3)));
            builder.registerPotionRecipe(Potions.STRONG_SLOWNESS, Items.GOLD_NUGGET, longLongStrongSlowness);
            builder.registerPotionRecipe(longLongStrongSlowness, Items.GOLDEN_CARROT, ultraLongStrongSlowness);

            // TURTLE_MASTER
            RegistryEntry<Potion> longLongTurtleMaster = registerPotion("long_long_turtle_master",
                    new Potion("turtle_master", new StatusEffectInstance[]{
                            new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 3),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, ELEVEN_MINUTES, 2)
                    }));
            RegistryEntry<Potion> ultraLongTurtleMaster = registerPotion("ultra_long_turtle_master",
                    new Potion("turtle_master", new StatusEffectInstance[]{
                            new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 3),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, FIFTEEN_MINUTES, 2)
                    }));
            builder.registerPotionRecipe(Potions.LONG_TURTLE_MASTER, Items.GOLD_NUGGET, longLongTurtleMaster);
            builder.registerPotionRecipe(longLongTurtleMaster, Items.GOLDEN_CARROT, ultraLongTurtleMaster);

            // STRONG_TURTLE_MASTER
            RegistryEntry<Potion> longLongStrongTurtleMaster = registerPotion("long_long_strong_turtle_master",
                    new Potion("turtle_master", new StatusEffectInstance[]{
                            new StatusEffectInstance(StatusEffects.SLOWNESS, ELEVEN_MINUTES, 5),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, ELEVEN_MINUTES, 3)
                    }));
            RegistryEntry<Potion> ultraLongStrongTurtleMaster = registerPotion("ultra_long_strong_turtle_master",
                    new Potion("turtle_master", new StatusEffectInstance[]{
                            new StatusEffectInstance(StatusEffects.SLOWNESS, FIFTEEN_MINUTES, 5),
                            new StatusEffectInstance(StatusEffects.RESISTANCE, FIFTEEN_MINUTES, 3)
                    }));
            builder.registerPotionRecipe(Potions.STRONG_TURTLE_MASTER, Items.GOLD_NUGGET, longLongStrongTurtleMaster);
            builder.registerPotionRecipe(longLongStrongTurtleMaster, Items.GOLDEN_CARROT, ultraLongStrongTurtleMaster);

            // WATER_BREATHING
            RegistryEntry<Potion> longLongWaterBreathing = registerPotion("long_long_water_breathing",
                    new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongWaterBreathing = registerPotion("ultra_long_water_breathing",
                    new Potion("water_breathing", new StatusEffectInstance(StatusEffects.WATER_BREATHING, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_WATER_BREATHING, Items.GOLD_NUGGET, longLongWaterBreathing);
            builder.registerPotionRecipe(longLongWaterBreathing, Items.GOLDEN_CARROT, ultraLongWaterBreathing);

            // POISON
            RegistryEntry<Potion> longLongPoison = registerPotion("long_long_poison",
                    new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongPoison = registerPotion("ultra_long_poison",
                    new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_POISON, Items.GOLD_NUGGET, longLongPoison);
            builder.registerPotionRecipe(longLongPoison, Items.GOLDEN_CARROT, ultraLongPoison);

            // STRONG_POISON
            RegistryEntry<Potion> longLongStrongPoison = registerPotion("long_long_strong_poison",
                    new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, ELEVEN_MINUTES, 1)));
            RegistryEntry<Potion> ultraLongStrongPoison = registerPotion("ultra_long_strong_poison",
                    new Potion("poison", new StatusEffectInstance(StatusEffects.POISON, FIFTEEN_MINUTES, 1)));
            builder.registerPotionRecipe(Potions.STRONG_POISON, Items.GOLD_NUGGET, longLongStrongPoison);
            builder.registerPotionRecipe(longLongStrongPoison, Items.GOLDEN_CARROT, ultraLongStrongPoison);

            // REGENERATION
            RegistryEntry<Potion> longLongRegeneration = registerPotion("long_long_regeneration",
                    new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongRegeneration = registerPotion("ultra_long_regeneration",
                    new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_REGENERATION, Items.GOLD_NUGGET, longLongRegeneration);
            builder.registerPotionRecipe(longLongRegeneration, Items.GOLDEN_CARROT, ultraLongRegeneration);

            // STRONG_REGENERATION
            RegistryEntry<Potion> longLongStrongRegeneration = registerPotion("long_long_strong_regeneration",
                    new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, ELEVEN_MINUTES, 1)));
            RegistryEntry<Potion> ultraLongStrongRegeneration = registerPotion("ultra_long_strong_regeneration",
                    new Potion("regeneration", new StatusEffectInstance(StatusEffects.REGENERATION, FIFTEEN_MINUTES, 1)));
            builder.registerPotionRecipe(Potions.STRONG_REGENERATION, Items.GOLD_NUGGET, longLongStrongRegeneration);
            builder.registerPotionRecipe(longLongStrongRegeneration, Items.GOLDEN_CARROT, ultraLongStrongRegeneration);

            // STRENGTH
            RegistryEntry<Potion> longLongStrength = registerPotion("long_long_strength",
                    new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongStrength = registerPotion("ultra_long_strength",
                    new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_STRENGTH, Items.GOLD_NUGGET, longLongStrength);
            builder.registerPotionRecipe(longLongStrength, Items.GOLDEN_CARROT, ultraLongStrength);

            // STRONG_STRENGTH
            RegistryEntry<Potion> longLongStrongStrength = registerPotion("long_long_strong_strength",
                    new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, ELEVEN_MINUTES, 1)));
            RegistryEntry<Potion> ultraLongStrongStrength = registerPotion("ultra_long_strong_strength",
                    new Potion("strength", new StatusEffectInstance(StatusEffects.STRENGTH, FIFTEEN_MINUTES, 1)));
            builder.registerPotionRecipe(Potions.STRONG_STRENGTH, Items.GOLD_NUGGET, longLongStrongStrength);
            builder.registerPotionRecipe(longLongStrongStrength, Items.GOLDEN_CARROT, ultraLongStrongStrength);


            // WEAKNESS
            RegistryEntry<Potion> longLongWeakness = registerPotion("long_long_weakness",
                    new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongWeakness = registerPotion("ultra_long_weakness",
                    new Potion("weakness", new StatusEffectInstance(StatusEffects.WEAKNESS, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_WEAKNESS, Items.GOLD_NUGGET, longLongWeakness);
            builder.registerPotionRecipe(longLongWeakness, Items.GOLDEN_CARROT, ultraLongWeakness);

            // LUCK
            RegistryEntry<Potion> longLongLuck = registerPotion("long_long_luck",
                    new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongLuck = registerPotion("ultra_long_luck",
                    new Potion("luck", new StatusEffectInstance(StatusEffects.LUCK, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LUCK, Items.GOLD_NUGGET, longLongLuck);
            builder.registerPotionRecipe(longLongLuck, Items.GOLDEN_CARROT, ultraLongLuck);

            // SLOW_FALLING
            RegistryEntry<Potion> longLongSlowFalling = registerPotion("long_long_slow_falling",
                    new Potion("slow_falling", new StatusEffectInstance(StatusEffects.SLOW_FALLING, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongSlowFalling = registerPotion("ultra_long_slow_falling",
                    new Potion("slow_falling", new StatusEffectInstance(StatusEffects.SLOW_FALLING, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.LONG_SLOW_FALLING, Items.GOLD_NUGGET, longLongSlowFalling);
            builder.registerPotionRecipe(longLongSlowFalling, Items.GOLDEN_CARROT, ultraLongSlowFalling);

            // WIND_CHARGED
            RegistryEntry<Potion> longLongWindCharged = registerPotion("long_long_wind_charged",
                    new Potion("wind_charged", new StatusEffectInstance(StatusEffects.WIND_CHARGED, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongWindCharged = registerPotion("ultra_long_wind_charged",
                    new Potion("wind_charged", new StatusEffectInstance(StatusEffects.WIND_CHARGED, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.WIND_CHARGED, Items.GOLD_NUGGET, longLongWindCharged);
            builder.registerPotionRecipe(longLongWindCharged, Items.GOLDEN_CARROT, ultraLongWindCharged);

            // WEAVING
            RegistryEntry<Potion> longLongWeaving = registerPotion("long_long_weaving",
                    new Potion("weaving", new StatusEffectInstance(StatusEffects.WEAVING, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongWeaving = registerPotion("ultra_long_weaving",
                    new Potion("weaving", new StatusEffectInstance(StatusEffects.WEAVING, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.WEAVING, Items.GOLD_NUGGET, longLongWeaving);
            builder.registerPotionRecipe(longLongWeaving, Items.GOLDEN_CARROT, ultraLongWeaving);

            // OOZING
            RegistryEntry<Potion> longLongOozing = registerPotion("long_long_oozing",
                    new Potion("oozing", new StatusEffectInstance(StatusEffects.OOZING, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongOozing = registerPotion("ultra_long_oozing",
                    new Potion("oozing", new StatusEffectInstance(StatusEffects.OOZING, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.OOZING, Items.GOLD_NUGGET, longLongOozing);
            builder.registerPotionRecipe(longLongOozing, Items.GOLDEN_CARROT, ultraLongOozing);

            // INFESTED
            RegistryEntry<Potion> longLongInfested = registerPotion("long_long_infested",
                    new Potion("infested", new StatusEffectInstance(StatusEffects.INFESTED, ELEVEN_MINUTES)));
            RegistryEntry<Potion> ultraLongInfested = registerPotion("ultra_long_infested",
                    new Potion("infested", new StatusEffectInstance(StatusEffects.INFESTED, FIFTEEN_MINUTES)));
            builder.registerPotionRecipe(Potions.INFESTED, Items.GOLD_NUGGET, longLongInfested);
            builder.registerPotionRecipe(longLongInfested, Items.GOLDEN_CARROT, ultraLongInfested);
        });
    }

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(MOD_ID, name), potion);
    }

    public static Identifier id(String path){
        return Identifier.of(MOD_ID, path);
    }
}
