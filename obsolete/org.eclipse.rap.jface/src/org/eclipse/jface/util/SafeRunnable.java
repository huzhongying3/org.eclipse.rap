/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Chris Gross (schtoo@schtoo.com) - support for ISafeRunnableRunner added
 *       (bug 49497 [RCP] JFace dependency on org.eclipse.core.runtime enlarges standalone JFace applications)
 *******************************************************************************/
package org.eclipse.jface.util;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Display;

/**
 * Implements a default implementation of ISafeRunnable. The default
 * implementation of <code>handleException</code> opens a message dialog.
 * <p>
 * <b>Note:<b> This class can open an error dialog and should not be used
 * outside of the UI Thread.
 * </p>
 */
public abstract class SafeRunnable implements ISafeRunnable {

	private static boolean ignoreErrors = false;

	private static ISafeRunnableRunner runner;

	private String message;

	private static SafeRunnableDialog dialog;

	/**
	 * Creates a new instance of SafeRunnable with a default error message.
	 */
	public SafeRunnable() {
		// do nothing
	}

	/**
	 * Creates a new instance of SafeRunnable with the given error message.
	 * 
	 * @param message
	 *            the error message to use
	 */
	public SafeRunnable(String message) {
		this.message = message;
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.ISafeRunnable#handleException(java.lang.Throwable)
	 */
	public void handleException(Throwable e) {
		// Workaround to avoid interactive error dialogs during automated
		// testing
		if (!ignoreErrors) {
			if (message == null)
				message = JFaceResources.getString("SafeRunnable.errorMessage"); //$NON-NLS-1$

//			final IStatus status = new Status(IStatus.ERROR, Policy.JFACE, message,e);
			final IStatus status = new Status(IStatus.ERROR, "org.eclipse.rap.jface",IStatus.OK, message,e);
            

			Runnable runnable = new Runnable() {
				public void run() {
					if (dialog == null || dialog.getShell().isDisposed()) {
						dialog = new SafeRunnableDialog(status);
						dialog.create();
						dialog.getShell().addDisposeListener(
								new DisposeListener() {
									public void widgetDisposed(DisposeEvent e) {
										dialog = null;
									}
								});
						dialog.open();
					} else {
						dialog.addStatus(status);
						dialog.refresh();
					}
				}
			};
			if (Display.getCurrent() != null) {
				runnable.run();
			} else {
//				Display.getDefault().asyncExec(runnable);
				runnable.run();
			}
		}
	}

	/**
	 * Flag to avoid interactive error dialogs during automated testing.
	 * 
	 * @param flag
	 * @return true if errors should be ignored
	 * @deprecated use getIgnoreErrors()
	 */
	public static boolean getIgnoreErrors(boolean flag) {
		return ignoreErrors;
	}

	/**
	 * Flag to avoid interactive error dialogs during automated testing.
	 * 
	 * @return true if errors should be ignored
	 * 
	 * @since 1.0
	 */
	public static boolean getIgnoreErrors() {
		return ignoreErrors;
	}

	/**
	 * Flag to avoid interactive error dialogs during automated testing.
	 * 
	 * @param flag
	 *            set to true if errors should be ignored
	 */
	public static void setIgnoreErrors(boolean flag) {
		ignoreErrors = flag;
	}

	/**
	 * Returns the safe runnable runner.
	 * 
	 * @return the safe runnable runner
	 * 
	 * @since 1.0
	 */
	public static ISafeRunnableRunner getRunner() {
		if (runner == null) {
			runner = createDefaultRunner();
		}
		return runner;
	}

	/**
	 * Creates the default safe runnable runner.
	 * 
	 * @return the default safe runnable runner
	 * @since 1.0
	 */
	private static ISafeRunnableRunner createDefaultRunner() {
		return new ISafeRunnableRunner() {
			public void run(ISafeRunnable code) {
				try {
					code.run();
				} catch (Exception e) {
					handleException(code, e);
				} catch (LinkageError e) {
					handleException(code, e);
				}
			}

			private void handleException(ISafeRunnable code, Throwable e) {
				if (!(e instanceof OperationCanceledException)) {
					e.printStackTrace();
				}
				code.handleException(e);
			}
		};
	}

	/**
	 * Sets the safe runnable runner.
	 * 
	 * @param runner
	 *            the runner to set, or <code>null</code> to reset to the
	 *            default runner
	 * @since 1.0
	 */
	public static void setRunner(ISafeRunnableRunner runner) {
		SafeRunnable.runner = runner;
	}

	/**
	 * Runs the given safe runnable using the safe runnable runner. This is a
	 * convenience method, equivalent to:
	 * <code>SafeRunnable.getRunner().run(runnable)</code>.
	 * 
	 * @param runnable
	 *            the runnable to run
	 * @since 1.0
	 */
	public static void run(ISafeRunnable runnable) {
		getRunner().run(runnable);
	}

}
