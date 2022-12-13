You may find files in audio folders named "procgen_info.json". These files are used for the Ori Mod's procedural sounds.json generator.
They contain a simple structure of data that is used to tell a modified datagen system how to create sounds.json.

>>> Directories missing procgen_info.json will not be put into sounds.json!!!
>>> Directories missing procgen_info.json will not be put into sounds.json!!!
>>> Directories missing procgen_info.json will not be put into sounds.json!!!

Some audio files that share a directory may be their own standalone sounds:
{
	"category" : "master", // Identical to MC
	"audioNames" : {
		// These sounds are put into groups in this manner.
		"sound.path.asdfg" : [
			"asdfg.ogg",
			"asdfg2.ogg"
		],
		"sound.path.qwerty" : [
			"qwerty.ogg"
		]
	}
}

However, most audio files in a folder are the same variant of some sound (denoted by the folder name). These have a special shorthand term:
{
	"category" : "master", // Identical to MC
	"audioName" : "sound.path.here"
}

You cannot mix both (you must either use "audioName" OR "audioNames", never both).