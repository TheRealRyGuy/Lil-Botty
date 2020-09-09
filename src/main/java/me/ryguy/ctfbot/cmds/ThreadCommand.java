package me.ryguy.ctfbot.cmds;

import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import reactor.core.publisher.Mono;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

public class ThreadCommand extends Command {

    public static final String RSS_OFFICIAL_TEAMS = "https://www.brawl.com/forums/299/index.rss";

    public ThreadCommand() {
        super("thread", "teamthread");
    }

    @Override
    public Mono<Void> execute(Message message, String s, String[] strings) {
        Map<String, String> res = new HashMap<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(Util.requestBrawlURL(RSS_OFFICIAL_TEAMS));
            NodeList threads = doc.getElementsByTagName("item");
            for(int i = 0; i < threads.getLength(); i++) {
                Node threadNode = threads.item(i);
                if(threadNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element thread = (Element) threadNode;
                    String author = thread.getElementsByTagName("dc:creator").item(0).getTextContent();
                    String link = thread.getElementsByTagName("link").item(0).getTextContent();
                    String title = parseName(thread.getElementsByTagName("title").item(0).getTextContent());
                    String date = thread.getElementsByTagName("pubDate").item(0).getTextContent();
                    res.put(title, buildDesc(author, link, date));
                }
            }
            message.getChannel().block().createEmbed(e -> {
               e.setColor(Color.TAHITI_GOLD);
               e.setTitle("Official Team Threads!");
               e.setFooter("love and pugs :D", null);
                for(Map.Entry<String, String> entry : res.entrySet()) {
                    e.addField(entry.getKey(), entry.getValue(), true);
                }
            }).block();
        }catch(Exception e) {
            message.getChannel().block().createEmbed(em -> {
                em.setDescription(":x: Error grabbing team threads!");
                em.setColor(Color.RED);
            }).block();
            e.printStackTrace();
            return null;
        }
        return null;
    }
    private String buildDesc(String author, String link, String date) {
        return "**Creator: ** " + author + "\n " +
                "**Created at: **" + date + "\n" +
                "**URL: **" + link;
    }

    private String parseName(String threadName) {
        String [] args = threadName.split(" ");
        StringBuilder ret = new StringBuilder();
        for(String s : args) {
            if(s.contains("Recruiting"))
                continue;
            if(s.matches(".*\\d.*") && (s.contains("/") || s.contains("\\") || s.contains("[") || s.contains("(")))
                continue;

            ret.append(s + " ");
        }
        return ret.toString();
    }
}
