# ChatLab (MsgLab)
Advanced message plugin for your [spigot](http://spigotmc.org/) minecraft server 1.8-1.16.4

## Introduction
ChatLab (MsgLab) is a messaging plugin with many features, it supports bukkit and spigot; you can change the format of the chat on your server, you can use emojis in the chat, you have the ability to mention people in the chat by typing their names, you can block commands, it supports PlaceHolderAPI, and much more.
## Features
- Compatible with 1.8+. <br>
- MiniMessage support. <br>
- PlaceholderAPI support. <br>
- Paths customizables. <br>
- Sounds customizables. <br>
- Optimized code. <br>
- Supports mentions and emojis. <br>
- AntiTab (Requires ProtocolLib) <br>
- CommandBlocker <br>
- You can fitler the commands in the tab (1.13+)
## Commands
Use the command `/bmsg commands` to see all commands <br>
The permissions of these commands are the default ones. <br>
[] = optional <br>
<> = required <br>
/ = or
|    Command     |                          Usage                       |                  Description                |           Permission          |
|----------------|------------------------------------------------------|---------------------------------------------|-------------------------------|
| bmsg   	       | `/bmsg [reload/help/debug..]`                        | Plugin main command                         | bmsg.commands.bmsg	          |
| chat   	       | `/chat [help/clear/mute/unmute/reload..]`            | Chat functions                              | bmsg.commands.chat	          |
| socialspy      | `/socialspy [on/off] <player>`                       | Spy on a player's social activity           | bmsg.commands.socialspy	      |
| broadcast      | `/broadcast <message>`                               | Send a message to all players               | bmsg.commands.broadcast       |
| broadcastworld | `broadcastworld <message>`                           | Send a message to all players in a world    | bmsg.commands.broadcast-world |
| sc             | `/sc [on/off] / /sc [message]`                       | Send messages to staff chat                 | bmsg.commands.staffchat       |
| helpop         | `/helpop <message>`                                  | Send help to the help chat                  | bmsg.commands.helpop/watch    |
| motd           | `/motd [addline/removeline/seline] <id/text> <text>` | To watch or modify the server motd          | NONE/bmsg.commands.motd.admin |
| channel        | `/channel [join/quit/list/move] [<channel>]`         | To use the channel manager                  | NONE/bmsg.commands.info/move  |
| stream         | `/stream [<text>]`                                   | To stream                                   | bmsg.commands.stream          |
| commandspy     | `/commandspy [on/off/list..]`                        | Spying on the commands executed by a player | bmsg.commands.commandspy      |
| ignore         | `/ignore <player>`                                   | Ignore a player                             | NONE                          |
| unignore       | `/unignore <player>`                                 | Stop ignoring a player                      | NONE                          |
| msg   	       | `/msg <player> <message>`                            | Send a message to a player                  | NONE	                        |
| reply   	     | `/reply <message>`                                   | Reply to a received message                 | NONE                          |
## Permissions
All permissions can be modified in `config.yml` file.
## Revisor
A Reviewer is a function that reviews messages in the chat, the permission to view and skip them is `bmsg.reviewer`. Currently the reviewers check: words, flood, caps, links and dots.
## Support
Check out the official [discord server](https://discord.gg/wpSh4Bf4Es)!.
