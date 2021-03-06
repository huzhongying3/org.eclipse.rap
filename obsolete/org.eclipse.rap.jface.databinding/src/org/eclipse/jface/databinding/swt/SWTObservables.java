/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matt Carter - bug 170668
 *     Brad Reynolds - bug 170848
 *     Samy Abou-Shama - initial adaptation for Eclipse RAP
 *     
 *******************************************************************************/
package org.eclipse.jface.databinding.swt;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.internal.databinding.internal.swt.*;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.*;

/**
 * A factory for creating observables for SWT widgets
 * 
 * @since 1.1
 * 
 */
public class SWTObservables {

	private static java.util.List realms = new ArrayList();

	/**
	 * Returns the realm representing the UI thread for the given display.
	 * 
	 * @param display
	 * @return the realm representing the UI thread for the given display
	 */
	public static Realm getRealm(final Display display) {
		synchronized (realms) {
			for (Iterator it = realms.iterator(); it.hasNext();) {
				DisplayRealm displayRealm = (DisplayRealm) it.next();
				if (displayRealm.display == display) {
					return displayRealm;
				}
			}
			DisplayRealm result = new DisplayRealm(display);
			realms.add(result);			
			return result;
		}
	}

	/**
	 * @param control
	 * @return an observable value tracking the enabled state of the given
	 *         control
	 */
	public static ISWTObservableValue observeEnabled(Control control) {
		return new ControlObservableValue(control, SWTProperties.ENABLED);
	}

	/**
	 * @param control
	 * @return an observable value tracking the visible state of the given
	 *         control
	 */
	public static ISWTObservableValue observeVisible(Control control) {
		return new ControlObservableValue(control, SWTProperties.VISIBLE);
	}

	/**
	 * @param control
	 * @return an observable value tracking the tooltip text of the given
	 *         control
	 */
	public static ISWTObservableValue observeTooltipText(Control control) {
		return new ControlObservableValue(control, SWTProperties.TOOLTIP_TEXT);
	}

	/**
	 * Returns an observable observing the selection attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Button</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeSelection(Control control) {
		if (control instanceof Spinner) {
			return new SpinnerObservableValue((Spinner) control,
					SWTProperties.SELECTION);
		} else if (control instanceof Button) {
			return new ButtonObservableValue((Button) control);
		} else if (control instanceof Combo) {
			return new ComboObservableValue((Combo) control,
					SWTProperties.SELECTION);
//		} else if (control instanceof CCombo) {
//			return new CComboObservableValue((CCombo) control,
//					SWTProperties.SELECTION);
		} else if (control instanceof List) {
			return new ListObservableValue((List) control);
		}
		// TODO [sa] Scale not supported yet (not implemented as RAP swt)	
		//} else if (control instanceof Scale) {
		//	return new ScaleObservableValue((Scale) control,
		//			SWTProperties.SELECTION);

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the minimum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeMin(Control control) {
		if (control instanceof Spinner) {
			return new SpinnerObservableValue((Spinner) control,
					SWTProperties.MIN);
		}
		// TODO [sa] Scale not supported yet (not implemented as RAP swt)			
//		} else if (control instanceof Scale) {
//			return new ScaleObservableValue((Scale) control, SWTProperties.MIN);
//		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the maximum attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Spinner</li>
	 * <li>org.eclipse.swt.widgets.Scale</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeMax(Control control) {
		if (control instanceof Spinner) {
			return new SpinnerObservableValue((Spinner) control,
					SWTProperties.MAX);
		}
		// TODO [sa] Scale not supported yet (not implemented as RAP swt)	
//		} else if (control instanceof Scale) {
//			return new ScaleObservableValue((Scale) control, SWTProperties.MAX);
//		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * </ul>
	 * 
	 * @param control
	 * @param event event type to register for change events
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeText(Control control, int event) {
		if (control instanceof Text) {
			return new TextObservableValue((Text) control, event);
		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the text attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Label</li>
	 * <li>org.eclipse.swt.custom.Label</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeText(Control control) {
		if (control instanceof Label) {
			return new LabelObservableValue((Label) control);
		} else if (control instanceof CLabel) {
			return new CLabelObservableValue((CLabel) control);
		} else if (control instanceof Combo) {
			return new ComboObservableValue((Combo) control, SWTProperties.TEXT);
//		} else if (control instanceof CCombo) {
//			return new CComboObservableValue((CCombo) control,
//					SWTProperties.TEXT);
		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the items attribute of the provided
	 * <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable list
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static IObservableList observeItems(Control control) {
		if (control instanceof Combo) {
			return new ComboObservableList((Combo) control);
//		} else if (control instanceof CCombo) {
//			return new CComboObservableList((CCombo) control);
		} else if (control instanceof List) {
			return new ListObservableList((List) control);
		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * Returns an observable observing the single selection index attribute of
	 * the provided <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Table</li>
	 * <li>org.eclipse.swt.widgets.Combo</li>
	 * <li>org.eclipse.swt.custom.CCombo</li>
	 * <li>org.eclipse.swt.widgets.List</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeSingleSelectionIndex(
			Control control) {
		if (control instanceof Table) {
			return new TableSingleSelectionObservableValue((Table) control);
		} else if (control instanceof Combo) {
			return new ComboSingleSelectionObservableValue((Combo) control);
//		} else if (control instanceof CCombo) {
//			return new CComboSingleSelectionObservableValue((CCombo) control);
		} else if (control instanceof List) {
			return new ListSingleSelectionObservableValue((List) control);
		}

		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	/**
	 * @param control
	 * @return an observable value tracking the foreground color of the given
	 *         control
	 */
	public static ISWTObservableValue observeForeground(Control control) {
		return new ControlObservableValue(control, SWTProperties.FOREGROUND);
	}

	/**
	 * @param control
	 * @return an observable value tracking the background color of the given
	 *         control
	 */
	public static ISWTObservableValue observeBackground(Control control) {
		return new ControlObservableValue(control, SWTProperties.BACKGROUND);
	}

	/**
	 * @param control
	 * @return an observable value tracking the font of the given control
	 */
	public static ISWTObservableValue observeFont(Control control) {
		return new ControlObservableValue(control, SWTProperties.FONT);
	}
	
	/**
	 * Returns an observable observing the editable attribute of
	 * the provided <code>control</code>. The supported types are:
	 * <ul>
	 * <li>org.eclipse.swt.widgets.Text</li>
	 * </ul>
	 * 
	 * @param control
	 * @return observable value
	 * @throws IllegalArgumentException
	 *             if <code>control</code> type is unsupported
	 */
	public static ISWTObservableValue observeEditable(Control control) {
		if (control instanceof Text) {
			return new TextEditableObservableValue((Text) control);
		}
		
		throw new IllegalArgumentException(
				"Widget [" + control.getClass().getName() + "] is not supported."); //$NON-NLS-1$//$NON-NLS-2$
	}

	private static class DisplayRealm extends Realm {
		private Display display;

		/**
		 * @param display
		 */
		private DisplayRealm(Display display) {
			this.display = display;
			Realm.setDefault(this);
		}

		public boolean isCurrent() {
			return Display.getCurrent() == display;
		}

		public void asyncExec(final Runnable runnable) {
			Runnable safeRunnable = new Runnable() {
				public void run() {
					safeRun(runnable);
				}
			};
			if (!display.isDisposed()) {
				display.asyncExec(safeRunnable);
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return (display == null) ? 0 : display.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final DisplayRealm other = (DisplayRealm) obj;
			if (display == null) {
				if (other.display != null)
					return false;
			} else if (!display.equals(other.display))
				return false;
			return true;
		}
	}
}
