/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jface.internal.databinding.provisional.viewers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.internal.databinding.provisional.swt.SWTUtil;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IViewerLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.swt.graphics.Image;

/**
 * NON-API - Generic viewer label provider.
 * @since 1.1
 *
 */
public class ViewerLabelProvider implements IViewerLabelProvider,
		ILabelProvider {

	private List listeners = new ArrayList();

	/**
	 * Subclasses should override this method. They should not call the base
	 * class implementation.
	 */
	public void updateLabel(ViewerLabel label, Object element) {
		label.setText(element.toString());
	}

	protected final void fireChangeEvent(Collection changes) {
		final LabelProviderChangedEvent event = new LabelProviderChangedEvent(
				this, changes.toArray());
		ILabelProviderListener[] listenerArray = (ILabelProviderListener[]) listeners
				.toArray(new ILabelProviderListener[listeners.size()]);
		for (int i = 0; i < listenerArray.length; i++) {
			ILabelProviderListener listener = listenerArray[i];
			try {
				listener.labelProviderChanged(event);
			} catch (Exception e) {
				SWTUtil.logException(e);
			}
		}
	}

	public final Image getImage(Object element) {
		ViewerLabel label = new ViewerLabel("", null); //$NON-NLS-1$
		updateLabel(label, element);
		return label.getImage();
	}

	public final String getText(Object element) {
		ViewerLabel label = new ViewerLabel("", null); //$NON-NLS-1$
		updateLabel(label, element);
		return label.getText();
	}

	public void addListener(ILabelProviderListener listener) {
		listeners.add(listener);
	}

	public void dispose() {
		listeners.clear();
	}

	public final boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
		listeners.remove(listener);
	}

}
