/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.forms.widgets;

import java.util.HashMap;

import org.eclipse.rwt.graphics.Graphics;

import org.eclipse.jface.resource.DeviceResourceException;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class FormFonts {
	private static FormFonts instance;

	public static FormFonts getInstance() {
		if (instance == null)
			instance = new FormFonts();
		return instance;
	}
	
	private LocalResourceManager resources;
	private HashMap descriptors;
	
	private FormFonts() {
	}
	
	private class BoldFontDescriptor extends FontDescriptor {
		private FontData[] fFontData;
		
		BoldFontDescriptor (Font font) {
// RAP [if] Changes due to different way of creating fonts
//			fFontData = font.getFontData();
//			for (int i = 0; i < fFontData.length; i++) {
//				fFontData[i].setStyle(fFontData[i].getStyle() | SWT.BOLD);
//			}
		  FontData fontData = font.getFontData()[ 0 ];
		  Font boldFont = Graphics.getFont( fontData.getName(), 
		                                    fontData.getHeight(), 
		                                    fontData.getStyle() | SWT.BOLD );
		  fFontData = boldFont.getFontData();
		}
		
		public boolean equals(Object obj) {
			if (obj instanceof BoldFontDescriptor) {
				BoldFontDescriptor desc = (BoldFontDescriptor)obj;
				if (desc.fFontData.length != fFontData.length)
					return false;
				for (int i = 0; i < fFontData.length; i++)
					if (!fFontData[i].equals(desc.fFontData[i]))
						return false;
				return true;
			}
			return false;
		}
		
		public int hashCode() {
			int hash = 0;
			for (int i = 0; i < fFontData.length; i++)
				hash = hash * 7 + fFontData[i].hashCode();
			return hash;
		}

		public Font createFont(Device device) throws DeviceResourceException {
// RAP [if] Changes due to different way of creating fonts
//			return new Font(device, fFontData);
		    return Graphics.getFont( fFontData[ 0 ] );
		}

		public void destroyFont(Font previouslyCreatedFont) {
// RAP [if] unnecessary
//			previouslyCreatedFont.dispose();
		}
	}
	
	public Font getBoldFont(Display display, Font font) {
		checkHashMaps();
		BoldFontDescriptor desc = new BoldFontDescriptor(font);
		Font result = getResourceManager().createFont(desc);
		descriptors.put(result, desc);
		return result;
	}
	
	public boolean markFinished(Font boldFont) {
		checkHashMaps();
		BoldFontDescriptor desc = (BoldFontDescriptor)descriptors.get(boldFont);
		if (desc != null) {
			getResourceManager().destroyFont(desc);
			if (getResourceManager().find(desc) == null) {
				descriptors.remove(boldFont);
				validateHashMaps();
			}
			return true;
			
		}
		// if the image was not found, dispose of it for the caller
// RAP [if] unnecessary
//		boldFont.dispose();
		return false;
	}
	
	private LocalResourceManager getResourceManager() {
		if (resources == null)
			resources = new LocalResourceManager(JFaceResources.getResources());
		return resources;
	}

	private void checkHashMaps() {
		if (descriptors == null)
			descriptors = new HashMap();
	}
	
	private void validateHashMaps() {
		if (descriptors.size() == 0)
			descriptors = null;
	}
}
