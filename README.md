<h1 align="center">Minecraft-DoorCloser </h1>

<p align="center">
<img align="center" src="logo.png">
</p>

<p align="center">
<a href="https://modrinth.com/plugin/doorcloser2" title="DoorCloser on Modrinth">Modrinth</a>
•
<a href="https://www.spigotmc.org/resources/doorcloser.130704" title="DoorCloser on SpigotMC">SpigotMC</a>
•
<a href="https://hangar.papermc.io/Maingron/DoorCloser" title="DoorCloser on Hangar">Hangar</a>
</p>
<p align="center">
<a href="https://github.com/Maingron/Minecraft-DoorCloser" title="DoorCloser on GitHub">GitHub</a>
•
<a href="https://gitlab.com/Maingron/Minecraft-DoorCloser" title="DoorCloser on GitLab">GitLab</a>
</p>

<h2 align="center">Supported Versions and Platforms</h2>
<p align="center">
1.13 - 26.3
</p>
<p align="center"> 
<abbr title="1.13 - 26.3">Folia</abbr>
•
<abbr title="1.13 - 26.3">Purpur</abbr>
•
<abbr title="1.13 - 26.3">Paper</abbr>
•
<abbr title="1.13 - 26.3">SpigotMC</abbr>
•
<abbr title="1.13 - 26.3">CraftBukkit</abbr>
</p>

## Feature List
- Close (Revert) **doors**, **trapdoors** and **fence gates** automatically after player interaction
- Sync **double-doors** (Opening one also opens the other)
- Configurable: Allow ignoring functionality when **sneaking**, in **creative mode** or by granting **permission** `doorcloser.bypass`
- Configurable: List of doors, trapdoors and fence gates affected by the plugin

## About DoorCloser

Minecraft plugin to automatically close doors, gates and trap-doors that were opened by a player. The plugin also allows reverting the state instead of only closing, so if a door was closed before being opened by the player, it will be closed again when the timer runs out, and if it was open before, it will be opened again.

You can configure which ones are in scope to be auto-closed, as well as if it should close when the player is in creative mode or is sneaking. I recommend leaving both of those as false to support using things like trap doors as decoration blocks (sides of chairs, etc.)

Latest .jar file can be found in the /target folder

Latest standard configuration File can be found in /src/main/resources/config.yml. It's also auto-generated when you load the plugin for the first time.

[Spigot resource page](https://www.spigotmc.org/resources/doorcloser.130704/)

[![Overview Video](https://img.youtube.com/vi/sSEuPI7GZ9I/0.jpg)](https://www.youtube.com/watch?v=sSEuPI7GZ9I)

## Building from source
This project uses Maven as build system. To build the .jar file, run the following command in the root folder of the project:

```
mvn clean package
```
