## Plugin description

**This plugin is made for the Minecraft version 1.17.x
It sets the border size to the level of the player(s).
The border size is of course reloaded when a player's level increases, but also when a new player joins the world or when a player respawns.
However, it is not a normal border, but a border that only applies to the player himself, which in turn means that monsters can spawn outside the border.
The plugin can be played in single player but also in multiplayer mode. The starting size of the border is a 4 by 4 field. One level corresponds to approximately one block.
The exact calculation of the size of the border is done like this:
First, the total levels of all players are multiplied by 1.8. Then 3 is added to the result. This gives the total size of the border.
This border is set using the WorldBorder API plugin (https://github.com/yannicklamprecht/WorldBorderAPI/releases/download/1.171.0/worldborderapiplugin-1.171.0-all.jar).**