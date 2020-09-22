package me.ryguy.ctfbot.modules.events;

import com.vdurmont.emoji.EmojiManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.CTFDiscordBot;
import me.ryguy.ctfbot.modules.events.Event;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.util.WorkFlow;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventCommand extends Command {
    public EventCommand() {
        super("event");
    }

    @Override
    public boolean canExecute(Message e, boolean shouldSend) {
        if(e.getGuildId().isPresent()) {
            if(e.getGuildId().get().asLong() == CTFDiscordBot.CTF_DISCORD_ID) {
                if(e.getAuthorAsMember().blockOptional().isPresent()) {
                    if(e.getAuthorAsMember().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains("PPM Host")) {
                        return true;
                    }else {
                        if(shouldSend) {
                            e.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription(":x: You need to have the role `PPM Host` to use this command!");
                            }).block();
                        }
                        return false;
                    }
                }
            }else {
                if(e.getAuthorAsMember().blockOptional().isPresent()) {
                    if(e.getAuthorAsMember().block().getBasePermissions().block().contains(Permission.MANAGE_ROLES)) {
                        return true;
                    }else {
                        if(shouldSend) {
                            e.getChannel().block().createEmbed(em -> {
                                em.setColor(Color.RED);
                                em.setDescription(":x: You need to have the permission `MANAGE_ROLES` to use this command!");
                            }).block();
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Mono<Void> execute(Message msg, String s, String[] strings) {
        Event toUse = new Event();
        toUse.setGuild(msg.getGuild().block());
        WorkFlow<Event> flow = new WorkFlow<Event>(toUse, msg.getChannel().block(), msg.getAuthor().get());
        flow.deletePreviousStep();
        flow.addRule("!cancel", e -> {
            msg.getChannel().block().createEmbed(em -> {
                em.setDescription(":white_check_mark: Event Creation Cancelled!");
                em.setColor(Color.GREEN);
            }).block();
            flow.end();
        }).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setTitle(":white_check_mark: Welcome to Event Setup!");
                e.addField("To Start", "Enter the name of your event", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(!message.getContent().toLowerCase().startsWith("!event")) { //idk why i need to put this check in but this is running differently on windows vs linux
                    event.setName(message.getContent());
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
                e.setDescription("Set name to \"" + toUse.getName() + "\" !");
                e.addField("Next", "Enter the event information here!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                event.setDescription(message.getContent());
                workflow.nextStep();
            }else {
                message.getChannel().block().createEmbed(e -> {
                    e.setColor(Color.RED);
                    e.setDescription(":x: You need to include content in the message!");
                }).block();
            }
        })).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription("Set description to: \n \n " + toUse.getDescription());
                e.addField("Next", "Enter channel where announcement should be posted \n (write the full channel mention, i.e. like with the #!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(Util.parseMention(message.getContent()) != null) {
                    if(message.getGuild().block().getChannelById(Snowflake.of(Util.parseMention(message.getContent()))).blockOptional().isPresent()) {
                        event.setAnnounceChannel(Long.valueOf(Util.parseMention(message.getContent())));
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
                e.setDescription("Set announcement channel to <#" + toUse.getAnnounceChannel() + ">!");
                e.addField("Next", "Enter channel where the signups list should be posted \n (write the full channel mention, i.e. like with the #!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(Util.parseMention(message.getContent()) != null) {
                    if(message.getGuild().block().getChannelById(Snowflake.of(Util.parseMention(message.getContent()))).blockOptional().isPresent()) {
                        event.setListChannel(Long.valueOf(Util.parseMention(message.getContent())));
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
                e.setDescription("Set signups channel to <#" + toUse.getListChannel() + ">!");
                e.addField("Next", "Entire the name of a roll that should be tagged, separated by a comma! \n Use `none` for no role, or `everyone` for @ everyone\n i.e. everyone,blue team,red team", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                List<String> failures = new ArrayList<>();
                for(String role : message.getContent().split(",")) {
                    if(role.equalsIgnoreCase("everyone")) {
                        event.getTagRoles().add(message.getGuild().block().getEveryoneRole().block().getId().asLong());
                    }else if(role.equalsIgnoreCase("none")) {
                        workflow.nextStep();
                    }else {
                        if(message.getGuild().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains(role)) {
                            Role ro = message.getGuild().block().getRoles().filter(r -> r.getName().equalsIgnoreCase(role)).blockFirst();
                            event.getTagRoles().add(ro.getId().asLong());
                        }else {
                            failures.add(role);
                        }
                    }
                }
                if(failures.isEmpty()) {
                    workflow.nextStep();
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Some roles failed, try again!");
                        e.addField("Failures", "Failure List: \n" + failures.toString(), false);
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
            StringBuilder sb = new StringBuilder();
            List<Role> roles = toUse.getTagRoles().parallelStream().map(l -> msg.getGuild().block().getRoleById(Snowflake.of(l)).block()).collect(Collectors.toList());
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> { //<@&
                e.setColor(Color.TAHITI_GOLD);
                e.setDescription("Set roles to tag: \n " + roles.parallelStream().map(Role::getMention).collect(Collectors.toList()).toString());
                e.addField("Next", "Enter the name of a role that should be given on signup, or use none for no role!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(message.getGuild().block().getRoles().map(Role::getName).collect(Collectors.toList()).block().contains(message.getContent())) {
                    Role ro = message.getGuild().block().getRoles().filter(r -> r.getName().equalsIgnoreCase(message.getContent())).blockFirst();
                    event.setGiveRole(ro);
                    workflow.nextStep();
                }else if(message.getContent().equalsIgnoreCase("none")) {
                    workflow.nextStep();
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Role `" + message.getContent() + "` does not exist in this guild!");
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
                e.setDescription("Set role to give on signup to be \n" + (toUse.getGiveRole() != null ? toUse.getGiveRole().getMention() : "None"));
                e.addField("Next", "Enter an emoji to sign up!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(EmojiManager.isEmoji(message.getContent())) {
                    event.setSignUpEmoji(message.getContent());
                    workflow.nextStep();
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Invalid Emoji: You need to use a standard Discord emoji!");
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
                e.setDescription("Set emoji to be given on signup to be \n" + toUse.getSignUpEmoji());
                e.addField("Next", "Enter an emoji to reject signup!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((event, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(EmojiManager.isEmoji(message.getContent())) {
                    event.setRejectEmoji(message.getContent());
                    workflow.nextStep();
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Invalid Emoji: Need to use a standard Discord emoji!");
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
                e.setTitle("You're all set!");
                e.setDescription("Use `!confirm` to confirm or `!cancel` to abort");
                e.addField("Summary", toUse.toString(), false);
            }));
        }, ((event, workflow, message) -> {
            if(message.getContent().equalsIgnoreCase("!confirm")) {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":white_check_mark: Event Setup!");
                    e.setColor(Color.GREEN);
                }).block();
                event.init();
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