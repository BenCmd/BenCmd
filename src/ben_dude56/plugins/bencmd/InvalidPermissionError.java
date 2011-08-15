package ben_dude56.plugins.bencmd;

/* This exception is thrown when a permission is checking 
 * against that doesn't contain a period. ('.') It's use
 * is to help detect any permissions that may have missed
 * the transfer.
 */
public class InvalidPermissionError extends Error {
	private static final long serialVersionUID = 0L;
}
