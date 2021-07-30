# Inverse

*A Minecraft mod where the overworld fights back*

**Version**: 0.0.1

## WAIT. YOU. YES YOU.

This mod would not be possible without the help and support from the incredibly talented [battesonb](https://github.com/battesonb). Not only are they the only one between the two of us who actually can write Java, but they also set up the inital development environment, created the Iodised Apple art, added the initial capability functionality, provided a whole bunch of other logic, and was more patient with me than I deserved with my "I don't know java but want to make a mod" questions. Unfortunately, because porting to 1.17 came with a bunch of issues, keeping that old repo history was fairly difficult (read: impossible) and the rush to get this version out meant creating a new environment was faster than struggling to get the old one working. So this section is for you, battesonb. Thank you <3

## What does this mod aim to do?

The mod has the following effects by default:

- Spawn players in the Nether
- When players are in the Overworld
  - Apply Poison I potion effect for `15 seconds` to players every `60s` they are in the overworld
  - Reduce max health from players for evey `10 minutes` they are in the overworld
  - Restore max health to the player for each hour they are in the nether or the end.
- Adds an expensive item (Iodised Apple) which allows players to get back max health  
<img src="docs/examplerecipe.png" width="350" height="130" />

## Mod Configuration

The bahaviour given above is default behaviour. The mod allows All values can be configured in `inverseforge-common.toml` in your Minecraft "config" folder. Values and effects are as follows:

- `poisonDuration`
  - How long (in seconds) the poison effect lasts
  - Default is 15 seconds
- `maxHealthPenalty`
  - Maximum hit points [0..19] that the mod should remove. 2HP = 1 heart
  - Default is 16 HP, or 8 hearts
- `poisonFrequency`
  - How often (in seconds) the player should take poison damage in the Overworld
  - Default is every 60 seconds
- `poisonMultiplier`
  - The "level" (eg, I, II) of poison damage. In game Poison has a multiplier of 0. Poison II has a multiplier of 1, etc.
  - Default is 0, for Poison I
- `appleInvigoration`
  - How much health points [0..19] should be recovered upon eating an iodised apple
  - Default value is 2
- `maxToxication`
  - Maximum toxication, bigger means slower healthy penalty effects (both positive and negative)
  - Default value is 160
- `invigorationFrequency`
  - How often (in seconds) the player should regenerate toxication in the End/Nether
  - Default value is 360
- `spawnInNether`
  - Set to `true` (default) to spawn in the Nether
  - Will respawn you in the nether if you don't ahve a bed or respawn anchor set
- `useDeterminedSpawn`
  - Set to true for the mod to use the values specified in spawnCoordinates and spawnRotation
  - Defaults to false
- `spawnCoordinates`
  - X Y and Z coordinates. Use your brain on these values
  - Defaules to "0.0, 64.0, 0.0"
- `spawnRotation`
  - Specifies the X Rotation and Y Rotation of the player (where they are looking)
  - Defaults to "0.0, 0.0" (i.e straight ahead)

## Suggested Add On Mods

- [Nether Portal Spread](https://www.curseforge.com/minecraft/mc-mods/nether-portal-spread)
  - Spreads the nether around portals in the overworld
- [Immerisve Portals](https://www.curseforge.com/minecraft/mc-mods/immersive-portals-mod)
  - Though this might cause quite a performance hit
  
## Currently developing for

- Forge
- Minecraft version 1.17.1
- Potentially 1.18 if required

There are no plans to support other versions
