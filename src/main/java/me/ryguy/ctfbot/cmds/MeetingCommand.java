package me.ryguy.ctfbot.cmds;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.types.Meeting;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.util.WorkFlow;
import reactor.core.publisher.Mono;

import java.util.Arrays;


public class MeetingCommand extends Command {

    public MeetingCommand() {
        super("setupmeetingtimes");
        setPermsFilter(Arrays.asList(Permission.MANAGE_ROLES));
        setGuildOnly(true);
    }

    @Override
    public Mono<Void> execute(Message msg, String s, String[] strings) {
        Meeting toUse = new Meeting();
        WorkFlow<Meeting> flow = new WorkFlow<Meeting>(toUse, msg.getChannel().block(), msg.getAuthor().get());
        flow.deletePreviousStep();
        flow.addRule("!cancel", e -> {
            msg.getChannel().block().createEmbed(em -> {
                em.setDescription(":white_check_mark: Meeting Creation Cancelled!");
                em.setColor(Color.GREEN);
            }).block();
            flow.end();
        }).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setTitle(":white_check_mark: Welcome to meeting Setup!");
                e.addField("To Start", "Enter the name of your meeting!", false);
                e.setFooter("Use !cancel to cancel the meeting setup", null);
            }));
        }, ((meeting, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(!message.getContent().toLowerCase().startsWith("!meeting")) { //idk why i need to put this check in but this is running differently on windows vs linux
                    meeting.setName(message.getContent());
                    workflow.nextStep();
                }
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":x: You need to include content in the message!");
                    e.setColor(Color.RED);
                }).block();
            }
        })).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription("Set the name of your meeting to be: \n " + toUse.getName());
                e.addField("Next", "Enter the information about your meeting!", false);
                e.setFooter("Use !cancel to cancel the meeting setup", null);
            }));
        }, ((meeting, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                meeting.setDesc(message.getContent());
                workflow.nextStep();
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":x: You need to include content in the message!");
                    e.setColor(Color.RED);
                }).block();
            }
        })).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription("Set description of your meeting to be: \n " + toUse.getDesc());
                e.addField("Next", "Enter channel where meeting should be posted \n (write the full channel mention, i.e. like with the #!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((meeting, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(Util.parseMention(message.getContent()) != null) {
                    if(message.getGuild().block().getChannelById(Snowflake.of(Util.parseMention(message.getContent()))).blockOptional().isPresent()) {
                        meeting.setChannelToPost(Long.valueOf(Util.parseMention(message.getContent())));
                        workflow.nextStep();
                    }else {
                        message.getChannel().block().createEmbed(e -> {
                            e.setDescription(":x: Invalid channel: This guild doesn't have this channel!");
                            e.setColor(Color.RED);
                        }).block();
                    }
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Invalid channel: This is not a valid channel mention!!");
                        e.setColor(Color.RED);
                    }).block();
                }
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":x: You need to include content in the message!");
                    e.setColor(Color.RED);
                }).block();
            }
        })).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription("Set the channel to post your meeting to be:  <#" + toUse.getChannelToPost() + ">!");
                e.addField("Next", "Enter your meeting times! \n To do this: just enter any text (preferably a date and time), and the bot will use that text \n To finish meeting, type !finish", false);
                e.setFooter("Use !cancel to cancel the meeting setup", null);
            }));
        }, ((meeting, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(message.getContent().toLowerCase().startsWith("!finish")) {
                    if(meeting.getTimes().size() <= 1) {
                        message.getChannel().block().createEmbed(e -> {
                            e.setColor(Color.RED);
                            e.setDescription(String.format(":x: Invalid meeting, you only have %s options chosen!", meeting.getTimes().size()));
                        }).block();
                    }else {
                        workflow.nextStep();
                    }
                }else {
                    Meeting.TimeEntry entry = new Meeting.TimeEntry();
                    entry.setTime(message.getContent());
                    meeting.getTimes().add(entry);
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":white_check_mark: Added time `" + message.getContent() + "`!");
                        e.setColor(Color.GREEN);
                    }).block();
                }
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":x: You need to include content in the message!");
                    e.setColor(Color.RED);
                }).block();
            }
        })).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setTitle("You're all set!");
                e.setDescription("Use `!confirm` to confirm or `!cancel` to abort");
                e.addField("Summary", toUse.toString(), false);
            }));
        }, ((meeting, workflow, message) -> {
            if(message.getContent().equalsIgnoreCase("!confirm")) {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":white_check_mark: Meeting Setup!");
                    e.setColor(Color.GREEN);
                }).block();
                meeting.init();
                workflow.end();
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":x: Invalid input! Use `!confirm` or `!cancel`!");
                    e.setColor(Color.RED);
                }).block();
            }
        })).start();
        return null;
    }
}
