/*******************************************************************************
 * Copyright (c) 2008 Matthew Hall and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthew Hall - initial API and implementation (bug 215531)
 ******************************************************************************/

package org.eclipse.jface.internal.databinding.viewers;

import org.eclipse.jface.viewers.AbstractListViewer;

/**
 * NON-API - An {@link IViewerUpdater} that sends updates to an
 * {@link AbstractListViewer} instance.
 * 
 * @since 1.1
 */
class ListViewerUpdater implements IViewerUpdater {
	AbstractListViewer viewer;

	ListViewerUpdater(AbstractListViewer viewer) {
		this.viewer = viewer;
	}

	public void insert(Object element, int position) {
		viewer.insert(element, position);
	}

	public void remove(Object element, int position) {
		viewer.remove(element);
	}

	public void replace(Object oldElement, Object newElement, int position) {
		remove(oldElement, position);
		insert(newElement, position);
	}

	public void add(Object[] elements) {
		viewer.add(elements);
	}

	public void remove(Object[] elements) {
		viewer.remove(elements);
	}
}
