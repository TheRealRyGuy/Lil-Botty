package me.ryguy.ctfbot.modules.poll;

import com.vdurmont.emoji.EmojiManager;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import me.ryguy.ctfbot.util.Util;
import me.ryguy.discordapi.command.Command;
import me.ryguy.discordapi.util.WorkFlow;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PollCommand extends Command {
    public PollCommand() {
        super("poll");
    }

    @Override
    public Mono<Void> execute(Message msg, String s, String[] strings) {
        Poll toUse = new Poll();
        WorkFlow<Poll> flow = new WorkFlow<Poll>(toUse, msg.getChannel().block(), msg.getAuthor().get());
        flow.deletePreviousStep();
        flow.addRule("!cancel", e -> {
            msg.getChannel().block().createEmbed(em -> {
                em.setDescription(":white_check_mark: Poll Creation Cancelled!");
                em.setColor(Color.GREEN);
            }).block();
            flow.end();
        }).andThen(ev -> {
            flow.sendMessage(msg.getChannel().block().createEmbed(e -> {
                e.setColor(Color.TAHITI_GOLD);
                e.setTitle(":white_check_mark: Welcome to Poll Setup!");
                e.addField("To Start", "Enter the name of your Poll!", false);
                e.setFooter("Use !cancel to cancel the poll setup", null);
            }));
        }, ((poll, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(!message.getContent().toLowerCase().startsWith("!poll")) { //idk why i need to put this check in but this is running differently on windows vs linux
                    poll.setName(message.getContent());
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
                e.setDescription("Set the name of your poll to be: \n " + toUse.getName());
                e.addField("Next", "Enter the information about your poll!", false);
                e.setFooter("Use !cancel to cancel the poll setup", null);
            }));
        }, ((poll, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                poll.setDescription(message.getContent());
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
                e.setDescription("Set the description of your poll to be: \n " + toUse.getDescription());
                e.addField("Next", "Enter true / false whether or not you'd like everyone to see the results!", false);
                e.setFooter("Use !cancel to cancel the poll setup", null);
            }));
        }, ((poll, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(Util.getBoolean(message.getContent()) != null) {
                    poll.setShowVotes(Util.getBoolean(message.getContent()));
                    workflow.nextStep();
                }else {
                    message.getChannel().block().createEmbed(e -> {
                        e.setDescription(":x: Invalid boolean!");
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
                e.setDescription("Set whether or not people could see results to: \n " + toUse.isShowVotes());
                e.addField("Next", "Enter channel where poll should be posted \n (write the full channel mention, i.e. like with the #!", false);
                e.setFooter("Use !cancel to cancel the event", null);
            }));
        }, ((poll, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(Util.parseMention(message.getContent()) != null) {
                    if(message.getGuild().block().getChannelById(Snowflake.of(Util.parseMention(message.getContent()))).blockOptional().isPresent()) {
                        GuildMessageChannel channel = (GuildMessageChannel) message.getGuild().block().getChannelById(Snowflake.of(Util.parseMention(message.getContent()))).block();
                        if(channel.getMembers().collect(Collectors.toList()).block().contains(message.getAuthorAsMember().block()) && channel.getEffectivePermissions(message.getAuthorAsMember().block().getId()).block().contains(Permission.SEND_MESSAGES)) {
                            poll.setChannelToPost(Long.valueOf(Util.parseMention(message.getContent())));
                            workflow.nextStep();
                        }else {
                            message.getChannel().block().createEmbed(e -> {
                                e.setDescription(":x: Invalid channel: You cannot talk in this channel!");
                                e.setColor(Color.RED);
                            }).block();
                        }
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
                e.setDescription("Set the channel to post your poll to be:  <#" + toUse.getChannelToPost() + ">!");
                e.addField("Next", "Enter your poll options! \n To do this: post your options in the format `emoji<>option` i.e. :wc:<>love and waffles! \n To finish poll, type !finish", false);
                e.setFooter("Use !cancel to cancel the poll setup", null);
            }));
        }, ((poll, workflow, message) ->  {
            if(!message.getContent().isEmpty()) {
                if(message.getContent().toLowerCase().startsWith("!finish")) {
                    if(poll.getOptions().size() <= 1) {
                        message.getChannel().block().createEmbed(e -> {
                            e.setColor(Color.RED);
                            e.setDescription(String.format(":x: Invalid poll, you only have %s options chosen!", poll.getOptions().size()));
                        }).block();
                    }else {
                        workflow.nextStep();
                    }
                }else {
                    if(message.getContent().split("<>").length != 2) {
                        message.getChannel().block().createEmbed(e -> {
                            e.setColor(Color.RED);
                            e.setDescription(":x: Invalid option format! Use `emoji<>option!` \n Tried to set to " + Arrays.asList(message.getContent().split("<>")).toString());
                        }).block();
                    }else {
                        String emoji = message.getContent().split("<>")[0];
                        String text = message.getContent().split("<>")[1];
                        if(EmojiManager.isEmoji(emoji)) {
                            Poll.Option option = new Poll.Option();
                            option.setEmoji(emoji);
                            option.setDescription(text);
                            poll.getOptions().add(option);
                            message.getChannel().block().createEmbed(e -> {
                                e.setColor(Color.RED);
                                e.setDescription(String.format(":white_check_mark: Added option `" + text + "` with emoji " + emoji));
                                e.setFooter("Use !finish to finish the poll!", null);
                            }).block();
                        }else {
                            message.getChannel().block().createEmbed(e -> {
                                e.setColor(Color.RED);
                                e.setDescription(":x: Emoji `" + emoji + "` is not a valid emoji!");
                            }).block();
                        }
                    }
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
        }, ((poll, workflow, message) -> {
            if(message.getContent().equalsIgnoreCase("!confirm")) {
                message.getChannel().block().createEmbed(e -> {
                    e.setDescription(":white_check_mark: Poll Setup!");
                    e.setColor(Color.GREEN);
                }).block();
                poll.init();
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
