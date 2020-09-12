package me.ryguy.ctfbot.types;

import lombok.Getter;
import me.ryguy.ctfbot.CTFDiscordBot;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static me.ryguy.ctfbot.CTFDiscordBot.GSON;

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
        Data loadedData = GSON.fromJson(br, Data.class);
        br.close();
        if(loadedData != null) {
            loadedData.reminders.forEach(r -> r.schedule(r.getChannel()));
            return loadedData;
        }else
            return new Data();

    }
    public void save(File dataFile) throws IOException {
        if(!dataFile.exists())
            dataFile.createNewFile();

        BufferedWriter bw = new BufferedWriter(new FileWriter(dataFile));
        bw.write(GSON.toJson(CTFDiscordBot.data));
        bw.close();
    }
}
