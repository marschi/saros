package de.fu_berlin.inf.dpp.ui.widgetGallery;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.fu_berlin.inf.dpp.ui.widgetGallery.application.Activator;

/**
 * Handles references to all used images throughout this plug-in.
 */
public class ImageManager {

	public static Image RELOAD = getImage("icons/reload.png");

	/**
	 * Returns an image from the file at the given plug-in relative path.
	 * 
	 * @param path
	 * @return image; the returned image <b>MUST be disposed after usage</b> to
	 *         free up memory
	 */
	public static Image getImage(String path) {
		return new Image(Display.getDefault(), getImageDescriptor(path)
				.getImageData());
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
				path);
	}

}
