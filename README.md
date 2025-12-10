# Minecraft-DoorCloser

Minecraft plugin to automatically close doors, gates and trap-doors that were opened by a player. The plugin also allows reverting the state instead of only closing, so if a door was closed before being opened by the player, it will be closed again when the timer runs out, and if it was open before, it will be opened again.

You can configure which ones are in scope to be auto-closed, as well as if it should close when the player is in creative mode or is sneaking. I recommend leaving both of those as false to support using things like trap doors as decoration blocks (sides of chairs, etc.)

Requires
* Java 1.8 or newer
* Minecraft Spigot 1.13 or newer

Latest .jar file can be found in the /target folder

Latest standard configuration File can be found in /src/main/resources/config.yml. It's also auto-generated when you load the plugin for the first time.

[Spigot resource page](https://www.spigotmc.org/resources/doorcloser.130704/)

[![Overview Video](https://img.youtube.com/vi/sSEuPI7GZ9I/0.jpg)](https://www.youtube.com/watch?v=sSEuPI7GZ9I)

## Building from source
This project uses Maven as build system. To build the .jar file, run the following command in the root folder of the project:

```
mvn clean package
```
