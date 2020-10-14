package me.ryguy.ctfbot.util;

import discord4j.core.object.entity.User;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.modules.ctf.PPMStrike;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StrikeUtil {
    public static List<PPMStrike> getAllStrikes() {
        return CTFDiscordBot.data.getStrikes();
    }

    public static List<PPMStrike> getActiveStrikes() {
        return getAllStrikes().parallelStream().filter(PPMStrike::isActive).collect(Collectors.toList());
    }

    public static List<PPMStrike> getExpiredStrikes() {
        return getAllStrikes().parallelStream().filter(s -> !s.isActive()).collect(Collectors.toList());
    }

    public static List<PPMStrike> getStrikes(User user) {
        return getAllStrikes().parallelStream().filter(s -> s.getStriked() == user.getId().asLong()).collect(Collectors.toList());
    }

    public static boolean isUserStriked(User user) {
        return getActiveStrikes().parallelStream().filter(s -> s.getStriked() == user.getId().asLong()).collect(Collectors.toList()).size() != 0;
    }

    public static PPMStrike.Tier getNextTier(User user) {
        Optional<PPMStrike> strike = getStrikes(user).parallelStream().filter(PPMStrike::isActive).max(Comparator.comparing(PPMStrike::getTier));
        if (strike.isPresent()) {
            return strike.get().getTier().getNextLevel();
        } else {
            return PPMStrike.Tier.FIRST;
        }
    }

    public static boolean strikeUser(PPMStrike strike) {
        try {
            CTFDiscordBot.data.strikes.add(strike);
            CTFDiscordBot.data.save(CTFDiscordBot.DATA_FILE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String buildStrikeList(List<PPMStrike> strikes) {

        StringBuilder sb = new StringBuilder();
        for (PPMStrike strike : strikes) {
            sb.append(strike.toString() + "\n");
        }
        return sb.toString();
    }
}
