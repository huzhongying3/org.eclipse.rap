/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.ui.internal.services;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.WorkbenchWindow;

/**
 * A provider of notifications for when the active shell changes.
 * 
 * @since 1.0
 */
public final class ActiveShellSourceProvider extends AbstractSourceProvider {

	/**
	 * The names of the sources supported by this source provider.
	 */
	private static final String[] PROVIDED_SOURCE_NAMES = new String[] {
			ISources.ACTIVE_SHELL_NAME, ISources.ACTIVE_WORKBENCH_WINDOW_NAME,
			ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME,
			ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME,
			ISources.ACTIVE_WORKBENCH_WINDOW_IS_PERSPECTIVEBAR_VISIBLE_NAME };

	/**
	 * The display on which this provider is working.
	 */
	private final Display display;

	/**
	 * The last shell seen as active by this provider. This value may be
	 * <code>null</code> if the last call to
	 * <code>Display.getActiveShell()</code> returned <code>null</code>.
	 */
	private Shell lastActiveShell = null;

	/**
	 * The last workbench window shell seen as active by this provider. This
	 * value may be <code>null</code> if the last call to
	 * <code>workbench.getActiveWorkbenchWindow()</code> returned
	 * <code>null</code>.
	 */
	private Shell lastActiveWorkbenchWindowShell = null;
	
	/**
	 * The last workbench window seen as active by this provider. This value may
	 * be null if the last call to
	 * <code>workbench.getActiveWorkbenchWindow()</code> returned
	 * <code>null</code>.
	 * 
	 * @since 1.0
	 */
	private WorkbenchWindow lastActiveWorkbenchWindow = null;

	/**
	 * The result of the last visibility check on the coolbar of the last active
	 * workbench window.
	 * 
	 * @since 1.0
	 */
	private Boolean lastCoolbarVisibility = Boolean.FALSE;
	
	/**
	 * The result of the last visibility check on the perspective bar of the
	 * last active workbench window.
	 * 
	 * @since 1.0
	 */
	private Boolean lastPerspectiveBarVisibility = Boolean.FALSE;
	
	/**
	 * The listener to individual window properties.
	 * 
	 * @since 1.0
	 */
	private final IPropertyChangeListener propertyListener = new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent event) {
			if (WorkbenchWindow.PROP_COOLBAR_VISIBLE
					.equals(event.getProperty())) {
				Object newValue = event.getNewValue();
				if (newValue == null || !(newValue instanceof Boolean))
					return;
				if (!lastCoolbarVisibility.equals(newValue)) {
					fireSourceChanged(
							ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE,
							ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME,
							newValue);
					lastCoolbarVisibility = (Boolean) newValue;
				}
			}
			else if (WorkbenchWindow.PROP_PERSPECTIVEBAR_VISIBLE.equals(event
					.getProperty())) {
				Object newValue = event.getNewValue();
				if (newValue == null || !(newValue instanceof Boolean))
					return;
				if (!lastPerspectiveBarVisibility.equals(newValue)) {
					fireSourceChanged(
							ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE,
							ISources.ACTIVE_WORKBENCH_WINDOW_IS_PERSPECTIVEBAR_VISIBLE_NAME,
							newValue);
					lastPerspectiveBarVisibility = (Boolean) newValue;
				}
			}
		}
		
	};
	
	/**
	 * The listener to shell activations on the display.
	 */
	private final Listener listener = new Listener() {
		/**
		 * Notifies all listeners that the source has changed.
		 */
		public final void handleEvent(final Event event) {
			if (!(event.widget instanceof Shell)) {
//				if (DEBUG) {
//					logDebuggingInfo("ASSP: passOnEvent: " + event.widget); //$NON-NLS-1$
//				}
				return;
			}
			
//			if (DEBUG) {
//				logDebuggingInfo("\tASSP:lastActiveShell: " + lastActiveShell); //$NON-NLS-1$
//				logDebuggingInfo("\tASSP:lastActiveWorkbenchWindowShell" + lastActiveWorkbenchWindowShell); //$NON-NLS-1$
//			}

			final Map currentState = getCurrentState();
			final Shell newActiveShell = (Shell) currentState
					.get(ISources.ACTIVE_SHELL_NAME);
			final WorkbenchWindow newActiveWorkbenchWindow = (WorkbenchWindow) currentState
					.get(ISources.ACTIVE_WORKBENCH_WINDOW_NAME);
			final Shell newActiveWorkbenchWindowShell = (Shell) currentState
					.get(ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME);
			
			// dont update the coolbar/perspective bar visibility unless we're
			// processing a workbench window change
			final Boolean newCoolbarVisibility = newActiveWorkbenchWindow == null ? lastCoolbarVisibility
					: (newActiveWorkbenchWindow.getCoolBarVisible() ? Boolean.TRUE
							: Boolean.FALSE);
			final Boolean newPerspectiveBarVisibility = newActiveWorkbenchWindow == null ? lastPerspectiveBarVisibility
					: (newActiveWorkbenchWindow.getPerspectiveBarVisible() ? Boolean.TRUE
							: Boolean.FALSE);

			// Figure out which variables have changed.
			final boolean shellChanged = newActiveShell != lastActiveShell;
			final boolean windowChanged = newActiveWorkbenchWindowShell != lastActiveWorkbenchWindowShell;
			final boolean coolbarChanged = newCoolbarVisibility != lastCoolbarVisibility;
			final boolean perspectiveBarChanged = newPerspectiveBarVisibility != lastPerspectiveBarVisibility;
			// Fire an event for those sources that have changed.
			if (shellChanged && windowChanged) {
				final Map sourceValuesByName = new HashMap(5);
				sourceValuesByName.put(ISources.ACTIVE_SHELL_NAME,
						newActiveShell);
				sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_NAME,
						newActiveWorkbenchWindow);
				sourceValuesByName.put(
						ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME,
						newActiveWorkbenchWindowShell);
				int sourceFlags = ISources.ACTIVE_SHELL | ISources.ACTIVE_WORKBENCH_WINDOW;
				
				if (coolbarChanged) {
					sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME,
							newCoolbarVisibility);
					sourceFlags |= ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE;
				}
				if (perspectiveBarChanged) {
					sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_IS_PERSPECTIVEBAR_VISIBLE_NAME,
							newPerspectiveBarVisibility);
					sourceFlags |= ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE;
				}

//				if (DEBUG) {
//					logDebuggingInfo("Active shell changed to " //$NON-NLS-1$
//							+ newActiveShell);
//					logDebuggingInfo("Active workbench window changed to " //$NON-NLS-1$
//							+ newActiveWorkbenchWindow);
//					logDebuggingInfo("Active workbench window shell changed to " //$NON-NLS-1$
//							+ newActiveWorkbenchWindowShell);
//					logDebuggingInfo("Active workbench window coolbar visibility " //$NON-NLS-1$
//							+ newCoolbarVisibility);
//					logDebuggingInfo("Active workbench window perspective bar visibility " //$NON-NLS-1$
//							+ newPerspectiveBarVisibility);
//				}
				
				fireSourceChanged(sourceFlags, sourceValuesByName);
				hookListener(lastActiveWorkbenchWindow, newActiveWorkbenchWindow);

			} else if (shellChanged) {
//				if (DEBUG) {
//					logDebuggingInfo("Active shell changed to " //$NON-NLS-1$
//							+ newActiveShell);
//				}
				fireSourceChanged(ISources.ACTIVE_SHELL,
						ISources.ACTIVE_SHELL_NAME, newActiveShell);

			} else if (windowChanged) {
				final Map sourceValuesByName = new HashMap(4);
				sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_NAME,
						newActiveWorkbenchWindow);
				sourceValuesByName.put(
						ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME,
						newActiveWorkbenchWindowShell);
				
				int sourceFlags = ISources.ACTIVE_SHELL | ISources.ACTIVE_WORKBENCH_WINDOW;
				
				if (coolbarChanged) {
					sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_IS_COOLBAR_VISIBLE_NAME,
							newCoolbarVisibility);
					sourceFlags |= ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE;
				}
				if (perspectiveBarChanged) {
					sourceValuesByName.put(ISources.ACTIVE_WORKBENCH_WINDOW_IS_PERSPECTIVEBAR_VISIBLE_NAME,
							newPerspectiveBarVisibility);
					sourceFlags |= ISources.ACTIVE_WORKBENCH_WINDOW_SUBORDINATE;
				}
				
//				if (DEBUG) {
//					logDebuggingInfo("Active workbench window changed to " //$NON-NLS-1$
//							+ newActiveWorkbenchWindow);
//					logDebuggingInfo("Active workbench window shell changed to " //$NON-NLS-1$
//							+ newActiveWorkbenchWindowShell);
//					logDebuggingInfo("Active workbench window coolbar visibility " //$NON-NLS-1$
//							+ newCoolbarVisibility);
//					logDebuggingInfo("Active workbench window perspective bar visibility " //$NON-NLS-1$
//							+ newPerspectiveBarVisibility);
//				}

				fireSourceChanged(sourceFlags,
						sourceValuesByName);
				hookListener(lastActiveWorkbenchWindow, newActiveWorkbenchWindow);
			}

			// Update the member variables.
			lastActiveShell = newActiveShell;
			lastActiveWorkbenchWindowShell = newActiveWorkbenchWindowShell;
			lastActiveWorkbenchWindow = newActiveWorkbenchWindow;
			lastCoolbarVisibility = newCoolbarVisibility;
			lastPerspectiveBarVisibility = newPerspectiveBarVisibility;
		}
	};

	/**
	 * The workbench on which to work; never <code>null</code>.
	 */
	private final Workbench workbench;

	/**
	 * Constructs a new instance of <code>ShellSourceProvider</code>.
	 * 
	 * @param workbench
	 *            The workbench on which to monitor shell activations; must not
	 *            be <code>null</code>.
	 */
	public ActiveShellSourceProvider(final Workbench workbench) {
		this.workbench = workbench;
		this.display = workbench.getDisplay();
		this.display.addFilter(SWT.Activate, listener);
	}

	public final void dispose() {
		display.removeFilter(SWT.Activate, listener);
		hookListener(lastActiveWorkbenchWindow, null);
		lastActiveWorkbenchWindow = null;
		lastActiveWorkbenchWindowShell = null;
		lastActiveShell = null;
	}

	public final Map getCurrentState() {
		final Map currentState = new HashMap(4);

		final Shell newActiveShell = display.getActiveShell();
		currentState.put(ISources.ACTIVE_SHELL_NAME, newActiveShell);

		/*
		 * We will fallback to the workbench window, but only if a dialog is not
		 * open.
		 */
//		final IContextService contextService = (IContextService) workbench
//				.getService(IContextService.class);		
//		final int shellType = contextService.getShellType(newActiveShell);
//		if (shellType != IContextService.TYPE_DIALOG) {
			final IWorkbenchWindow newActiveWorkbenchWindow = workbench
					.getActiveWorkbenchWindow();
			final Shell newActiveWorkbenchWindowShell;
			if (newActiveWorkbenchWindow == null) {
				newActiveWorkbenchWindowShell = null;
			} else {
				newActiveWorkbenchWindowShell = newActiveWorkbenchWindow
						.getShell();
			}
			currentState.put(ISources.ACTIVE_WORKBENCH_WINDOW_NAME,
					newActiveWorkbenchWindow);
			currentState.put(ISources.ACTIVE_WORKBENCH_WINDOW_SHELL_NAME,
					newActiveWorkbenchWindowShell);
//		}

		return currentState;
	}

	public final String[] getProvidedSourceNames() {
		return PROVIDED_SOURCE_NAMES;
	}
	
	private void hookListener(WorkbenchWindow lastActiveWorkbenchWindow,
			WorkbenchWindow newActiveWorkbenchWindow) {
		if (lastActiveWorkbenchWindow != null)
			lastActiveWorkbenchWindow.removePropertyChangeListener(propertyListener);
		
		if (newActiveWorkbenchWindow != null)
			newActiveWorkbenchWindow.addPropertyChangeListener(propertyListener);	
	}
}
