/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.statushandlers;

import java.util.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.rwt.SessionSingletonBase;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchPlugin;


/**
 * The StatusNotificationManager is the class that manages the display of status
 * information.
 */
public class StatusNotificationManager extends SessionSingletonBase {

	private Collection errors = Collections.synchronizedSet(new HashSet());

	private StatusDialog dialog;
//	private ErrorDialog dialog;

	private boolean dialogOpened = false;

//	private static StatusNotificationManager sharedInstance;

	private DisposeListener disposeListener = new DisposeListener() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
		 */
		public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
			dialog = null;
			dialogOpened = false;
			errors.clear();
		}
	};

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static StatusNotificationManager getInstance() {
//		if (sharedInstance == null) {
//			sharedInstance = new StatusNotificationManager();
//		}
//		return sharedInstance;
		return ( StatusNotificationManager )getInstance( StatusNotificationManager.class );
	}

	/**
	 * Create a new instance of the manager.
	 */
	private StatusNotificationManager() {
	}

	/**
	 * Add a new error to the list.
	 * 
	 * @param statusInfo
	 *            the error to be displayed
	 */
	public void addError(StatusAdapter statusAdapter, final boolean modal) {

		if (ErrorDialog.AUTOMATED_MODE == true) {
			return;
		}

		final StatusInfo statusInfo = new StatusInfo(statusAdapter);

		if (!PlatformUI.isWorkbenchRunning()) {
			// we are shuttting down, so just log
			WorkbenchPlugin.log(statusInfo.getStatus().getStatus());
			return;
		}

		// Add the error in the UI thread to ensure thread safety in the
		// dialog
		if (dialogOpened == true) {
//			if (statusInfo.getStatus().getProperty(
//					IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY) != null) {
//				statusInfo.getStatus().setProperty(
//						IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY,
//						Boolean.FALSE);
//			}

//			Display.getDefault().syncExec(new Runnable() {
//				public void run() {
					openStatusDialog(modal, statusInfo);
//				}
//			});

		} else {
			errors.add(statusInfo);
			// Delay prompting if the status adapter property is set
//			Object noPromptProperty = statusInfo.getStatus().getProperty(
//					IProgressConstants.NO_IMMEDIATE_ERROR_PROMPT_PROPERTY);

			boolean prompt = true;
//			if (noPromptProperty instanceof Boolean) {
//				prompt = !((Boolean) noPromptProperty).booleanValue();
//			}

			if (prompt) {
				dialogOpened = true;
//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						dialog = new StatusDialog(ProgressManagerUtil
//								.getDefaultParent(), statusInfo, IStatus.OK
//								| IStatus.INFO | IStatus.WARNING
//								| IStatus.ERROR, modal);
				dialog = new StatusDialog(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getShell(),
						statusInfo, IStatus.OK
						| IStatus.INFO | IStatus.WARNING
						| IStatus.ERROR, modal);
						dialog.open();
						dialog.getShell().addDisposeListener(disposeListener);
//					}
//				});
			}
		}
	}

	/**
	 * Get the currently registered errors in the receiver.
	 * 
	 * @return Collection of ErrorInfo
	 */
	Collection getErrors() {
		return errors;
	}

	/**
	 * @param modal
	 * @param statusInfo
	 */
	void openStatusDialog(final boolean modal, final StatusInfo statusInfo) {
		errors.add(statusInfo);
//		if (modal && !dialog.isModal()) {
			dialog.getShell().removeDisposeListener(disposeListener);
			dialog.close();
//			dialog = new StatusDialog(ProgressManagerUtil.getDefaultParent(),
//					statusInfo, IStatus.OK | IStatus.INFO | IStatus.WARNING
//							| IStatus.ERROR, modal);
			dialog = new StatusDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(),
					statusInfo, IStatus.OK | IStatus.INFO | IStatus.WARNING
					| IStatus.ERROR, modal);
			dialog.open();
			dialog.getShell().addDisposeListener(disposeListener);
//		} else {
//			dialog.refresh();
//		}
	}

	/**
	 * A wrapper class for statuses displayed in the dialog.
	 * 
	 */
	protected static class StatusInfo implements Comparable {

		private final StatusAdapter statusAdapter;

		/**
		 * Constructs a simple <code>StatusInfo</code>, without any
		 * extensions.
		 * 
		 * @param status
		 *            the root status for this status info
		 */
		public StatusInfo(StatusAdapter statusAdapter) {
			this.statusAdapter = statusAdapter;

			Object timestampProperty = statusAdapter
					.getProperty(StatusAdapter.TIMESTAMP_PROPERTY);

			if (timestampProperty == null
					|| !(timestampProperty instanceof Long)) {
				statusAdapter.setProperty(StatusAdapter.TIMESTAMP_PROPERTY,
						new Long(System.currentTimeMillis()));
			}
		}

		String getDisplayString() {
			String text = statusAdapter.getStatus().getMessage();

			Job job = (Job) (statusAdapter.getAdapter(Job.class));
			if (job != null) {
				text = job.getName();
			}

//			return NLS.bind(ProgressMessages.JobInfo_Error,
//					(new Object[] {
//							text,
//							DateFormat.getDateTimeInstance(DateFormat.LONG,
//									DateFormat.LONG).format(
//									new Date(getTimestamp())) }));
			return "";
		}

		/**
		 * Time when this status info was created.
		 * 
		 * @return the time
		 */
		public long getTimestamp() {
			return ((Long) statusAdapter
					.getProperty(StatusAdapter.TIMESTAMP_PROPERTY)).longValue();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(T)
		 */
		public int compareTo(Object arg0) {
			if (arg0 instanceof StatusInfo) {
				// Order ErrorInfo by time received
				long otherTimestamp = ((StatusInfo) arg0).getTimestamp();
				if (getTimestamp() < otherTimestamp) {
					return -1;
				} else if (getTimestamp() > otherTimestamp) {
					return 1;
				} else {
					return getDisplayString().compareTo(
							((StatusInfo) arg0).getDisplayString());
				}
			}
			return 0;
		}

		/**
		 * @return Returns the status.
		 */
		public StatusAdapter getStatus() {
			return statusAdapter;
		}
	}
}
