package net.tenrem.doorcloser;

import java.util.ArrayList;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.logging.Logger;

public class Settings 
{
	final static String configFileGeneratedByVersion_Key = "GeneratedByVersion";
	final static String configFileGeneratedByVersion_Default = "unknown (pre 1.0.12)";
	public static String configFileGeneratedByVersion = configFileGeneratedByVersion_Default;

	final static String respectEventCancellation_Key = "RespectEventCancellation";
	final static boolean respectEventCancellation_Default = true;
	public static boolean respectEventCancellation = respectEventCancellation_Default;

	final static String secondsToRemainOpen_Key = "Time";
	final static int secondsToRemainOpen_Default = 5;
	public static int secondsToRemainOpen = secondsToRemainOpen_Default;


	final static String synchronizeDoubleDoorOpen_Key = "SynchronizeDoubleDoorOpen";
	final static boolean synchronizeDoubleDoorOpen_Default = true;
	public static boolean synchronizeDoubleDoorOpen = synchronizeDoubleDoorOpen_Default;

	final static String synchronizeDoubleDoorClose_Key = "SynchronizeDoubleDoorClose1";	
	final static boolean synchronizeDoubleDoorClose_Default = true;
	public static boolean synchronizeDoubleDoorClose = synchronizeDoubleDoorClose_Default;

	final static String playSound_Key = "PlaySound";
	final static boolean playSound_Default = true;
	public static boolean playSound = playSound_Default;


	final static String ignoreIfInCreative_Key = "IgnoreIfInCreative";
	final static boolean ignoreIfInCreative_Default = true;
	public static boolean ignoreIfInCreative = ignoreIfInCreative_Default;

	final static String ignoreIfSneaking_Key = "IgnoreIfSneaking";
	final static boolean ignoreIfSneaking_Default = false;
	public static boolean ignoreIfSneaking = ignoreIfSneaking_Default;

	final static String trapDoorsInScope_Key = "TrapDoorBlocks";
	public static List<Material> trapDoorsInScope = new ArrayList<Material>();

	final static String gatesInScope_Key = "GateBlocks";
	public static List<Material> gatesInScope = new ArrayList<Material>();

	final static String doorsInScope_Key = "DoorBlocks";
	public static List<Material> doorsInScope = new ArrayList<Material>();


	// worldguard integration settings

	final static String enableWorldGuardIntegration_Key = "EnableWorldGuardIntegration";
	final static boolean enableWorldGuardIntegration_Default = true;
	public static boolean enableWorldGuardIntegration = enableWorldGuardIntegration_Default;
	
	final static String useWorldGuardFlag_Key = "UseWorldGuardFlag";
	final static boolean useWorldGuardFlag_Default = true;
	public static boolean useWorldGuardFlag = useWorldGuardFlag_Default;

	final static String worldGuardFlag_Key = "WorldGuardFlag";
	final static String worldGuardFlag_Default = "door-closer-enabled";
	public static String worldGuardFlag = worldGuardFlag_Default;



	enum UnlistedRegionPriorityBehaviors
	{
		BehaviorEnabled,
		BehaviorDisabled
	}
	final static String unlistedRegionBehavior_Key = "UnlistedRegionBehavior";
	final static UnlistedRegionPriorityBehaviors unlistedRegionBehavior_Default = UnlistedRegionPriorityBehaviors.BehaviorEnabled;
	public static UnlistedRegionPriorityBehaviors unlistedRegionBehavior = unlistedRegionBehavior_Default;

	enum OverlappingRegionPriorityBehaviors
	{
		UseLowestPriority,
		UseHighestPriority
	}
	final static String overlappingRegionPriorityBehavior_Key = "OverlappingRegionPriorityBehavior";
	final static OverlappingRegionPriorityBehaviors overlappingRegionPriorityBehavior_Default = OverlappingRegionPriorityBehaviors.UseHighestPriority;
	public static OverlappingRegionPriorityBehaviors overlappingRegionPriorityBehavior = overlappingRegionPriorityBehavior_Default;

	final static String logSamePriorityRegionConflictResolution_Key = "LogSamePriorityRegionConflictResolution";
	final static boolean logSamePriorityRegionConflictResolution_Default = true;
	public static boolean logSamePriorityRegionConflictResolution = logSamePriorityRegionConflictResolution_Default;

	enum RegionListTypes
	{
		WhiteList,
		BlackList
	}
	final static String regionListType_Key = "RegionListType";
	final static RegionListTypes regionListType_Default = RegionListTypes.BlackList;
	public static RegionListTypes regionListType = regionListType_Default;

	final static String regions_Key = "Regions";
	public static List<String> regions = new ArrayList<String>();
	
	
			
	public static DoorCloserPlugin ThisPlugin;

	
	public static void Reload()
	{
		if (ThisPlugin != null)
		{
			ThisPlugin.reloadConfig();
		
			ReadConfigValues();
			
			ThisPlugin.getLogger().info("Settings reloaded from configuration file.");
		}
	}
	
	public static void ReadConfigValues()
	{		
		if (ThisPlugin == null)
			return;
		
		// save the default config, if it's not already present
		ThisPlugin.saveDefaultConfig();		
		
		FileConfiguration config = ThisPlugin.getConfig();
		Logger logger = ThisPlugin.getLogger();

		config.addDefault(configFileGeneratedByVersion_Key, configFileGeneratedByVersion_Default);
		config.addDefault(respectEventCancellation_Key, respectEventCancellation_Default);
		config.addDefault(secondsToRemainOpen_Key, secondsToRemainOpen_Default);
		config.addDefault(synchronizeDoubleDoorOpen_Key, synchronizeDoubleDoorOpen_Default);
		config.addDefault(synchronizeDoubleDoorClose_Key, synchronizeDoubleDoorClose_Default);
		config.addDefault(playSound_Key, playSound_Default);
		config.addDefault(ignoreIfInCreative_Key, ignoreIfInCreative_Default);
		config.addDefault(ignoreIfSneaking_Key, ignoreIfSneaking_Default);


		config.addDefault(enableWorldGuardIntegration_Key, enableWorldGuardIntegration_Default);
		config.addDefault(useWorldGuardFlag_Key, useWorldGuardFlag_Default);
		config.addDefault(worldGuardFlag_Key, worldGuardFlag_Default);
		config.addDefault(unlistedRegionBehavior_Key, unlistedRegionBehavior_Default);
		config.addDefault(overlappingRegionPriorityBehavior_Key, overlappingRegionPriorityBehavior_Default);
		config.addDefault(logSamePriorityRegionConflictResolution_Key, logSamePriorityRegionConflictResolution_Default);
		config.addDefault(regionListType_Key, regionListType_Default);
				
		

		


		// this is far from foolproof, but will at least help people realize they may be missing settings
		configFileGeneratedByVersion = config.getString(configFileGeneratedByVersion_Key);

		logger.info("Config file generated by version: " + Settings.configFileGeneratedByVersion);

		if ((configFileGeneratedByVersion == configFileGeneratedByVersion_Default) ||
			(configFileGeneratedByVersion != null && configFileGeneratedByVersion.startsWith("1.0")))
		{
			logger.warning("Old configuration file. Take note of your settings, and then delete config.yml, and then execute /dcreload or reboot your server to generate a new config.");
		}



		// read remaining settings

		respectEventCancellation = config.getBoolean(respectEventCancellation_Key);

		secondsToRemainOpen = config.getInt(secondsToRemainOpen_Key);

		synchronizeDoubleDoorOpen = config.getBoolean(synchronizeDoubleDoorOpen_Key);
		synchronizeDoubleDoorClose = config.getBoolean(synchronizeDoubleDoorClose_Key);

		playSound = config.getBoolean(playSound_Key);

		ignoreIfInCreative = config.getBoolean(ignoreIfInCreative_Key);
		ignoreIfSneaking = config.getBoolean(ignoreIfSneaking_Key);

		// the actual blocks to interact with		
		List<String> trapDoorsInScopeStrings = (List<String>) config.getStringList(trapDoorsInScope_Key);
		List<String> gatesInScopeStrings = (List<String>) config.getStringList(gatesInScope_Key);
		List<String> doorsInScopeStrings = (List<String>) config.getStringList(doorsInScope_Key);


		trapDoorsInScope.clear();
		gatesInScope.clear();
		doorsInScope.clear();
		
		for (String val : trapDoorsInScopeStrings)
		{
			Material m = Material.matchMaterial(val);	
			
			if (m != null)
			{
				trapDoorsInScope.add(m);
			}
			else
			{
				logger.warning("Unexpected value '" + val + "' in config trap door list.");
			}
		}
		
		for (String val : gatesInScopeStrings)
		{
			Material m = Material.matchMaterial(val);

			if (m != null)
			{
				gatesInScope.add(m);
			}
			else
			{
				logger.warning("Unexpected value '" + val + "' in config gate list.");
			}
		}
		
		for (String val : doorsInScopeStrings)
		{
			Material m = Material.matchMaterial(val);

			if (m != null)
			{
				doorsInScope.add(m);
			}
			else
			{
				logger.warning("Unexpected value '" + val + "' in config door list.");
			}
	
		}
		


		// if no blocks are in scope, warn. The plugin will still intercept events		
		if (Settings.trapDoorsInScope.isEmpty() && Settings.gatesInScope.isEmpty() && Settings.doorsInScope.isEmpty())
		{
			logger.warning("No doors, gates, or trap doors configured to auto-close. Is the config file up to date?");
			logger.warning("The DoorCloser plugin will still run and consume resources.");
			logger.warning("Update the configuration file and then use the /dcreload command to reload it.");
		}
		else
		{
			logger.info("Count of trap doors in scope: " + Settings.trapDoorsInScope.size());
			logger.info("Count of gate types in scope: " + Settings.gatesInScope.size());
			logger.info("Count of door types in scope: " + Settings.doorsInScope.size());
		}

		logger.info("Seconds to remain open: " + Settings.secondsToRemainOpen);
		logger.info("Ignore if in creative mode: " + Settings.ignoreIfInCreative);
		logger.info("Ignore if sneaking: " + Settings.ignoreIfSneaking);
		logger.info("Play sound: " + Settings.playSound);




		// read worldguard settings

		SharedState.WorldGuardIntegrationEnabled = foo;







	}
	
	
}
