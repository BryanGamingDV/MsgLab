# ChatLab ![Project Status](https://img.shields.io/github/workflow/status/BryanGamingDV/ChatLab/Maven%20Build/main) 

Advanced message plugin for your [Spigot](http://spigotmc.org/) minecraft server 1.8.8 - 1.16.5

## Introduction
ChatLab (MsgLab) is a messaging plugin with many features, it supports Spigot, [Paper](https://papermc.io/downloads) and forks. With this plugin you can change the format of the chat on your server, you can use emojis in the chat, you have the ability to mention people in the chat by typing their names with a prefix, you can block commands, it supports PlaceHolderAPI, and much more.
## Features
- Compatible with 1.8.8+. <br>
- Full [MiniMessage](https://docs.adventure.kyori.net/minimessage.html) support. <br>
- PlaceholderAPI support. <br>
- Paths customizables. <br>
- Sounds customizables. <br>
- Optimized code. <br>
- Supports mentions and emojis. <br>
- Anti Tab Complete (Requires ProtocolLib). <br>
- Configurable Command Blocker. <br>
- You can filter the commands in the tab complete (1.13+).
## Commands
Use the command `/clab commands` to see all commands <br>
The default permissions of these commands are the default ones. <br>
#### Note:  The permissions of each feature are customizable.
[] = optional <br>
<> = required <br>
/ = or
|    Command     |                          Usage                       |                  Description                |           Permission          |
|----------------|------------------------------------------------------|---------------------------------------------|-------------------------------|
| clab   	       | `/clab [reload/help/debug..]`                        | Plugin main command                         | clab.commands.clab	          |
| chat   	       | `/chat [help/clear/mute/unmute/reload..]`            | Chat functions                              | clab.commands.chat	          |
| socialspy      | `/socialspy [on/off] <player>`                       | Spy on a player's social activity           | clab.commands.socialspy	      |
| broadcast      | `/broadcast <message>`                               | Send a message to all players               | clab.commands.broadcast       |
| broadcastworld | `/broadcastworld <message>`                           | Send a message to all players in a world    | clab.commands.broadcast-world |
| sc             | `/sc [on/off] / /sc [message]`                       | Send messages to staff chat                 | clab.commands.staffchat       |
| helpop         | `/helpop <message>`                                  | Send help to the help chat                  | clab.commands.helpop/watch    |
| motd           | `/motd [addline/removeline/seline] <id/text> <text>` | To watch or modify the server motd          | clab.commands.motd.admin      |
| channel        | `/channel [join/quit/list/move] [<channel>]`         | To use the channel manager                  | clab.commands.info/move       |
| stream         | `/stream [<text>]`                                   | To stream                                   | clab.commands.stream          |
| commandspy     | `/commandspy [on/off/list..]`                        | Spying on the commands executed by a player | clab.commands.commandspy      |
| ignore         | `/ignore <player>`                                   | Ignore a player                             | NONE                          |
| unignore       | `/unignore <player>`                                 | Stop ignoring a player                      | NONE                          |
| msg   	       | `/msg <player> <message>`                            | Send a message to a player                  | NONE	                        |
| reply   	     | `/reply <message>`                                   | Reply to a received message                 | NONE                          |
## Permissions
All permissions can be modified in `config.yml` file.
## Revisor
A Reviewer is a function that reviews messages in the chat, the permission to view and skip them is `revisor`. Currently the reviewers check: words, flood, caps, links and dots.
## Support
Check out the official [discord server](https://discord.gg/wpSh4Bf4Es)!.
