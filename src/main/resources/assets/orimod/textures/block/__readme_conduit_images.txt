Conduit images are based on an 8x8 texture (or 4x4 texture).

As of 1.17, this will cause a critical texture load failure (See <https://github.com/MinecraftForge/MinecraftForge/issues/8094>)
For this reason, the textures have been upscaled to be larger than or equal to 16x.