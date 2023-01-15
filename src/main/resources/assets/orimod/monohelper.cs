using System.Diagnostics;

namespace HelperMakeAllAudioMono {
	internal class Program {

		private static void Convert(FileInfo soundFile) {
			FileInfo target = new FileInfo(soundFile.FullName + ".stereo");
			FileInfo clone;
			if (target.Exists) {
				clone = new FileInfo(soundFile.FullName + ".tmp");
				if (clone.Exists) clone.Delete();
				soundFile.CopyTo(clone.FullName);

			} else {
				clone = soundFile.CopyTo(soundFile.FullName + ".stereo");
			}

			Process.Start("ffmpeg", $"-i \"{clone.FullName}\" -f ogg -y -ac 1 \"{soundFile.FullName}\"").WaitForExit();
			Console.ForegroundColor = ConsoleColor.Green;
			Console.WriteLine($"Replaced {soundFile.FullName}");
			Console.ForegroundColor = ConsoleColor.White;
		}

		private static void Recurse(DirectoryInfo parent) {
			foreach (FileInfo file in parent.GetFiles()) {
				if (file.Extension.ToLower() == ".ogg") {
					Convert(file);
				}
			}
			foreach (DirectoryInfo dir in parent.GetDirectories()) {
				Recurse(dir);
			}
		}

		static void Main(string[] args) {

			DirectoryInfo dir = new DirectoryInfo(@"F:\Users\Xan\Desktop\Software\Minecraft Forge\OriMod1.19\src\main\resources\assets\orimod\sounds");
			Console.WriteLine($"I am about to overwrite all sounds recursively within {dir.FullName} - press ENTER to confirm, or exit to cancel.");
			Console.ReadLine();
			Recurse(dir);
			Console.ForegroundColor = ConsoleColor.Red;
			Console.WriteLine("Friendly reminder to add *.stereo to .gitignore! Press any key to quit.");
			Console.ReadKey(true);

		}
	}
}