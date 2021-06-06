package etithespirit.etimod.imc;


public enum IMCRegistryError {
	
	/** No error: This task occurred successfully. */
	SUCCESS("Success."),
	
	/** Invalid method sent over IMC. */
	FAILURE_INVALID_METHOD("Method \"%s\" is not registered in EtiMod!"),
	
	/** IMC sender didn't attach a mod ID. */
	@Deprecated
	FAILURE_NULL_MOD_ID(
		"A null mod ID was sent with over IMC to EtiMod!\r\n"
		+ "Unfortunately, this can not be traced back to a specific mod without doing some digging "
		+ "because the mod author specifically wrote in a null value as the source mod when trying to send data to EtiMod.\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "The binary search is a way of finding a faulty thing among a lot of other things, without having to remove the things one-by-one.\r\n"
		+ "IF YOU WANT A VIDEO TUTORIAL, CHECK OUT https://youtu.be/XPBBHLncD84\r\n"
		+ "\r\n"
		+ "This is useful for finding a broken mod among hundreds of mods, without having to spend time testing the mods one-by-one.\r\n"
		+ "\r\n"
		+ "The procedure is simple:\r\n"
		+ "  1. Remove half of the existing mods, and put them aside (in a new folder, for example).\r\n"
		+ "  2. Run the game again.\r\n"
		+ "  3. Does the issue still exist?\r\n"
		+ "    If YES: Repeat from step 1 with the current mods left in your mods folder.\r\n"
		+ "    IF NO: Swap out the current mods left in your mods folder with the ones set aside, and repeat from step 1, removing the opposite half instead.\r\n"
		+ "  4. Repeat this process until the problematic mod(s) have been found.",
		
		"§eSearch the crash report for \"FAILURE_NULL_MOD_ID\"."
	),
	
	/** IMC sender sent a bogus mod ID. */
	@Deprecated
	FAILURE_UNKNOWN_MOD_ID(
		"A mod ID that is not registered with Forge was sent to EtiMod, or a mod ID with the name \"minecraft\" was sent to EtiMod.\r\n"
		+ "I was given a mod ID of %s\r\n"
		+ "Unfortunately, this might not be easily traced back to a specific mod without doing some digging "
		+ "because the mod author specifically wrote in a bogus value as the source mod when trying to send data to EtiMod.\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "THE MOST EFFICIENT WAY TO FIND THE PROBLEM IS WITH A MANUAL BINARY SEARCH (IT'S EASIER THAN IT SOUNDS!)\r\n"
		+ "The binary search is a way of finding a faulty thing among a lot of other things, without having to remove the things one-by-one.\r\n"
		+ "IF YOU WANT A VIDEO TUTORIAL, CHECK OUT https://youtu.be/XPBBHLncD84\r\n"
		+ "\r\n"
		+ "This is useful for finding a broken mod among hundreds of mods, without having to spend time testing the mods one-by-one.\r\n"
		+ "\r\n"
		+ "The procedure is simple:\r\n"
		+ "  1. Remove half of the existing mods, and put them aside (in a new folder, for example).\r\n"
		+ "  2. Run the game again.\r\n"
		+ "  3. Does the issue still exist?\r\n"
		+ "    If YES: Repeat from step 1 with the current mods left in your mods folder.\r\n"
		+ "    IF NO: Swap out the current mods left in your mods folder with the ones set aside, and repeat from step 1, removing the opposite half instead.\r\n"
		+ "  4. Repeat this process until the problematic mod(s) have been found.",
		
		"§eSearch the crash report for \"FAILURE_UNKNOWN_MOD_ID\"."
	),
	
	/** The IMC method being used hasn't been implemented yet. */
	FAILURE_NOT_IMPLEMENTED("Method \"%s\" has not net been implemented in EtiMod!"),
	
	/** Wrong number of args. */
	FAILURE_INVALID_ARG_COUNT("There was an invalid number of args provided (expecting %s args, but got %s instead)."),
	
	/** Null was passed through IMC */
	FAILURE_NULL_ARGS("A null reference was provided in place of the arguments being sent to Eti's Mod."),
	
	/** Block does not belong to this modder. */
	FAILURE_BLOCK_IS_UNOWNED("%s is a block registered to a different mod / vanilla! You cannot modify the assocations of blocks that are not yours."),
	
	/** An invalid material was given. */
	FAILURE_INVALID_SPIRIT_MATERIAL("Unrecognized SpiritMaterial \"%s\"."),
	
	/** An invalid sub-material state was given. */
	FAILURE_INVALID_SPIRIT_SUB_MATERIAL("Unrecognized SpiritMaterialModState \"%s\"."),
	
	/** Trying to set a sub-material on a non-conditional SpiritMaterial. */
	FAILURE_BLOCK_IS_NOT_CONDITIONAL_SPIRIT_MATERIAL("The given block that is having its sub-material state set does not use a conditional SpiritMaterial, and so it cannot use states!"),
	
	/** A custom material was registered, but the material wasn't a material. */
	FAILURE_NOT_MATERIAL_CLASS("The given value (%s) is incorrect! Expecting a custom instance of net.minecraft.block.material.Material or (\"cva\" in an obfuscated environment)"),
	
	/** A custom material pointed to a class that doesn't exist. */
	FAILURE_COULD_NOT_RESOLVE_CLASS("The given classname (%s) is incorrect! This class could not be found."),
	
	/** A custom material field was defined for a class that does exist, and the field didn't exist. */
	FAILURE_COULD_NOT_RESOLVE_FIELD("The given field (%s) is not a valid member of class %s!"),
	
	/** The field given is an instance field. */
	FAILURE_FIELD_IS_NOT_STATIC("The given field (%s) is not a static member of class %s!"),
	
	/** A custom material was actually a vanilla material. */
	FAILURE_MATERIAL_IS_VANILLA("The given value is a vanilla material instance, which cannot be modified by external mods!"),
	
	/** A custom material is a null instance of Material. */
	FAILURE_MATERIAL_IS_NULL("The given material is null!"),
	
	/** A custom material's field was protected/private and had a security manager on it preventing setAccessible from working. */
	FAILURE_MATERIAL_FIELD_IS_PRIVATE_AND_LOCKED("The given field storing the material is a protected or private field, and could not be made accessible!"),
	
	/** A string couldn't be cast into a value. */
	FAILURE_INVALID_TYPE("The given string (%s) could not be cast into the desired type of %s!");
	
	/** The message associated with this error. */
	public final String message;
	
	/** A user-facing message not for developers. */
	public final String userMessage;
	
	/** Whether or not a distinct and unique userMessage is present. */
	public final boolean hasUserMessage;
	
	private IMCRegistryError(String message) {
		this.message = message;
		this.userMessage = message;
		this.hasUserMessage = false;
	}
	
	private IMCRegistryError(String message, String userMessage) {
		this.message = message;
		this.userMessage = userMessage;
		this.hasUserMessage = true;
	}

}
