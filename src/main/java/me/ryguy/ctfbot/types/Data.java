package me.ryguy.ctfbot.types;

import lombok.Getter;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.modules.ctf.PPMStrike;
import me.ryguy.ctfbot.modules.events.Event;
import me.ryguy.ctfbot.modules.meetings.Meeting;
import me.ryguy.ctfbot.modules.poll.Poll;
import me.ryguy.ctfbot.modules.reminders.Reminder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Data {
    public List<Event> events = new ArrayList<>();
    public List<Poll> polls = new ArrayList<>();
    public List<Meeting> meetings = new ArrayList<>();
    public List<PPMStrike> strikes = new ArrayList<>();
    public List<Reminder> reminders = new ArrayList<>();

    public static Data load(File dataFile) throws IOException {
        if(!dataFile.exists())
            dataFile.createNewFile();

        BufferedReader br = new BufferedReader(new FileReader(dataFile));
        Data loadedData = CTFDiscordBot.gson().fromJson(br, Data.class);
        br.close();
        if(loadedData != null) {
            return loadedData;
        }else
            return new Data();

    }
    public void save(File dataFile) throws IOException {
        if(!dataFile.exists())
            dataFile.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
        bw.write(CTFDiscordBot.gson().toJson(CTFDiscordBot.data));
        bw.close();
    }
}
