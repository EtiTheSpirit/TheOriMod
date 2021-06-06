package error;

import etithespirit.etimod.imc.IMCRegistryError;
import net.minecraftforge.fml.common.LoaderException;

/**
 * This is in an odd package for a shorter error path.
 * @author Eti
 *
 */
public class IMCException extends LoaderException {
	private static final long serialVersionUID = 1L;

	public IMCException(String message) {
		super(message);
	}
	
	@Deprecated
	public IMCException(String modIdSender, IMCRegistryError errorCode, Object... format) {
		super("§c" + modIdSender + " sent bad IMC: " + String.format(errorCode.message, format));
	}
	
}
