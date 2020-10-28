package me.ryguy.ctfbot.types;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.User;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class UserList<T extends User> extends LinkedList<T> {

    public UserList(T... list) {
        for(T t : list)
            this.add(t);
    }
    public String toMentions(boolean numbered, String separator) {
        StringBuilder sb = new StringBuilder();
        for(T t : this) {
            if(numbered) {
                sb.append(this.indexOf(t) + separator + t.getMention() + "\n");
            }else {
                sb.append(t.getMention() + "\n");
            }
        }
        return sb.toString();
    }
    public String toMentions(boolean numbered) {
        StringBuilder sb = new StringBuilder();
        for(T t : this) {
            if(numbered) {
                sb.append(this.indexOf(t) + ". " + t.getMention() + "\n");
            }else {
                sb.append(t.getMention() + "\n");
            }
        }
        return sb.toString();
    }
    public String toMentions() {
        StringBuilder sb = new StringBuilder();
        for(T t : this) {
            sb.append(t.getMention() + "\n");
        }
        return sb.toString();
    }
    public List<Snowflake> toIds() {
        return this.parallelStream().map(User::getId).collect(Collectors.toList());
    }
}
