package org.main;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.main.components.AbilityType;

import java.util.ArrayList;
import java.util.List;

public class AbilityManager {

    private final Main plugin;
    public static final NamespacedKey ABILITIES_KEY = new NamespacedKey("serverplugin", "unlocked_abilities");

    public AbilityManager(Main plugin) {
        this.plugin = plugin;
    }

    public void unlockAbility(Player player, AbilityType ability) {
        List<String> abilities = getUnlockedAbilities(player);
        if (!abilities.contains(ability.name())) {
            abilities.add(ability.name());
            saveAbilities(player, abilities);
            player.sendMessage("§b§l[Ability] §7You have unlocked §f" + ability.getName() + "§7!");
        } else {
            player.sendMessage("§cYou already have this ability unlocked!");
        }
    }

    public static final NamespacedKey ACTIVE_ABILITY_KEY = new NamespacedKey("serverplugin", "active_ability");

    public void setActiveAbility(Player player, AbilityType ability) {
        if (ability == null) {
            player.getPersistentDataContainer().remove(ACTIVE_ABILITY_KEY);
            player.sendMessage("§b§l[Ability] §7Deactivated ability.");
            return;
        }
        if (hasAbility(player, ability)) {
            player.getPersistentDataContainer().set(ACTIVE_ABILITY_KEY, PersistentDataType.STRING, ability.name());
            player.sendMessage("§b§l[Ability] §7Set active ability to §f" + ability.getName());
        } else {
            player.sendMessage("§cYou haven't unlocked this ability yet!");
        }
    }

    public AbilityType getActiveAbility(Player player) {
        String data = player.getPersistentDataContainer().get(ACTIVE_ABILITY_KEY, PersistentDataType.STRING);
        if (data == null) return null;
        try {
            return AbilityType.valueOf(data);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean hasAbility(Player player, AbilityType ability) {
        return getUnlockedAbilities(player).contains(ability.name());
    }

    public void cycleAbility(Player player) {
        List<String> unlocked = getUnlockedAbilities(player);
        if (unlocked.isEmpty()) {
            player.sendMessage("§cYou haven't unlocked any abilities yet!");
            return;
        }

        AbilityType current = getActiveAbility(player);
        int nextIndex = 0;

        if (current != null) {
            int currentIndex = -1;
            for (int i = 0; i < unlocked.size(); i++) {
                if (unlocked.get(i).equals(current.name())) {
                    currentIndex = i;
                    break;
                }
            }
            nextIndex = (currentIndex + 1) % unlocked.size();
        }

        try {
            AbilityType next = AbilityType.valueOf(unlocked.get(nextIndex));
            setActiveAbility(player, next);
        } catch (IllegalArgumentException e) {
            player.sendMessage("§cError cycling abilities.");
        }
    }

    public List<String> getUnlockedAbilities(Player player) {
        String data = player.getPersistentDataContainer().get(ABILITIES_KEY, PersistentDataType.STRING);
        if (data == null || data.isEmpty()) return new ArrayList<>();
        return new ArrayList<>(List.of(data.split(",")));
    }

    private void saveAbilities(Player player, List<String> abilities) {
        String data = String.join(",", abilities);
        player.getPersistentDataContainer().set(ABILITIES_KEY, PersistentDataType.STRING, data);
    }
}
