package com.gencal.die_nbt.util;

import static com.gencal.die_nbt.DieNbt.LOGGER;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;


public abstract class ConfigUtils {

    public static HashMap<Item, Set<String>> mergeItemToFieldsMaps(List<? extends ArrayList<Object>> listOfMaps) {
        HashMap<Item, Set<String>> result = new HashMap<>();

        try {
            for (ArrayList<Object> map : listOfMaps) {
                Item key = ForgeRegistries.ITEMS.getValue(new ResourceLocation(map.get(0).toString()));
                Set<String> values = new HashSet<>((Collection<String>) map.get(1));

                result.merge(key, values, (existingValues, newValues) -> {
                    Set<String> combined = new HashSet<>(existingValues);
                    combined.addAll(newValues);
                    return combined;
                });
            }

        } catch (ClassCastException e) {
            LOGGER.error("[Die, NBT!] Your per-item config is incorrectly defined. " + e.getMessage());
        }

        return result;
    }

    public static HashMap<String, Set<String>> mergeStringToFieldsMaps(List<? extends ArrayList<Object>> listOfMaps) {
        HashMap<String, Set<String>> result = new HashMap<>();

        try {
            for (ArrayList<Object> map : listOfMaps) {
                String key = map.get(0).toString();
                Set<String> values = new HashSet<>((Collection<String>) map.get(1));

                result.merge(key, values, (existingValues, newValues) -> {
                    Set<String> combined = new HashSet<>(existingValues);
                    combined.addAll(newValues);
                    return combined;
                });
            }
        } catch (ClassCastException e) {
            LOGGER.error("[Die, NBT!] Your per-mod config is incorrectly defined. " + e.getMessage());
        }

        return result;
    }

    public static Set<Item> collectItemsFromPairs(final List<? extends ArrayList<Object>> collection) {
        return collection.stream()
                .map(itemFieldPair -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemFieldPair.get(0).toString())))
                .collect(Collectors.toSet());
    }

    public static Set<String> collectNamespacesFromPairs(final List<? extends ArrayList<Object>> arrayList) {
        return arrayList.stream()
                .map(modFieldPair -> modFieldPair.get(0).toString())
                .collect(Collectors.toSet());
    }

    public static boolean validateItemFieldPair(final Object obj) {
        if (obj instanceof ArrayList<?> arrayList && arrayList.size() == 2) {
            return (arrayList.get(0) instanceof String) && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(arrayList.get(0).toString())) && (arrayList.get(1) instanceof ArrayList<?>);
        }
        return false;
    }

    public static boolean validateNamespaceFieldPair(final Object obj) {
        if (obj instanceof ArrayList<?> arrayList && arrayList.size() == 2) {
            return (arrayList.get(0) instanceof String) && ModList.get().isLoaded(arrayList.get(0).toString()) && (arrayList.get(1) instanceof ArrayList<?>);
        }
        return false;
    }

}
