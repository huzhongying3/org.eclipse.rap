/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ******************************************************************************/

package org.eclipse.ui;



/**
 * Listener for model lifecycle events.
 * 
 * @since 1.0
 */
public interface ISaveablesLifecycleListener {
	
	/**
	 * Handle the given model lifecycle event. This method must be called on the UI thread.
	 * @param event
	 */
	public void handleLifecycleEvent(SaveablesLifecycleEvent event);

}
