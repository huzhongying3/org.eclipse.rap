/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.search2.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.search.internal.ui.SearchPlugin;
import org.eclipse.search.internal.ui.util.PixelConverter;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.MatchFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


/**
 * A dialog that lets users configure the active {@link MatchFilter match filters} and (optionally) the
 * maximal number of top level elements.
 * 
 * @since 3.3
 */
public class MatchFilterSelectionDialog extends StatusDialog {

	private final boolean fShowLimitConfigurationControls;
	private final MatchFilter[] fAllFilters;
	
	private MatchFilter[] fEnabledFilters;
	
	private CheckboxTableViewer fListViewer;
	private Button fLimitElementsCheckbox;
	private Text fLimitElementsField;
	private Text fDescription;
	
	private int fLimitElementCount;
	private int fLastLimit;
	private final boolean fEnableMatchFilterConfiguration;


	/**
	 * Creates a {@link MatchFilterSelectionDialog}.
	 * 
	 * @param shell the parent shell
	 * @param enableMatchFilterConfiguration 
	 * @param allFilters all filters available for selection
	 * @param selectedFilters the initially selected filters
	 * @param enableLimitConfiguration if set, the dialog will also contain controls to limit
	 * the number of top level elements
	 * @param limit the initial limit or -1 if no limit should be used.
	 */
	public MatchFilterSelectionDialog(Shell shell, boolean enableMatchFilterConfiguration, MatchFilter[] allFilters, MatchFilter[] selectedFilters, boolean enableLimitConfiguration, int limit) {
		super(shell);

		setTitle(SearchMessages.MatchFilterSelectionDialog_label);
		setStatusLineAboveButtons(true);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		
		fShowLimitConfigurationControls= enableLimitConfiguration;
		fEnableMatchFilterConfiguration= enableMatchFilterConfiguration;

		fAllFilters= allFilters;
		fEnabledFilters= selectedFilters;
		
		fLimitElementCount= limit;
		fLastLimit= limit != -1 ? limit : 1000;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#getDialogBoundsSettings()
	 */
	protected IDialogSettings getDialogBoundsSettings() {
		String name= "MatchFilterSelectionDialog_" + String.valueOf(fShowLimitConfigurationControls) + '.' + String.valueOf(fEnableMatchFilterConfiguration); //$NON-NLS-1$
		return SearchPlugin.getDefault().getDialogSettingsSection(name);
	}
		
	/**
	 * Returns the currently selected match filters.
	 * 
	 * @return the currently selected match filters.
	 */
	public MatchFilter[] getMatchFilters() {
		return fEnabledFilters;
	}

	/**
	 * Returns the currently configured limit for top level elements. <code>-1</code> is returned if
	 * no limit should be applied.
	 * 
	 * @return the currently configured limit for top level elements or -1 if no limit should be used.
	 */
	public int getLimit() {
		return fLimitElementCount;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite composite) {
		Composite parent = (Composite) super.createDialogArea(composite);
		initializeDialogUnits(composite);

		if (fShowLimitConfigurationControls) {
			createTableLimit(parent);
		}
		if (fEnableMatchFilterConfiguration) {
			createMatchFilterControls(parent);
		}
		return parent;
	}

	private void createMatchFilterControls(Composite parent) {
		// Create list viewer
		Label l= new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText(SearchMessages.MatchFilterSelectionDialog_filter_description); 
		
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER);
		table.setFont(parent.getFont());
		fListViewer = new CheckboxTableViewer(table);
		
		GridData data = new GridData(GridData.FILL_BOTH);
		PixelConverter tc = new PixelConverter( table );
		data.minimumHeight= tc.convertHeightInCharsToPixels(8);
		table.setLayoutData(data);

		class ListenerAndLabelProvider extends LabelProvider implements ISelectionChangedListener, ICheckStateListener {
			public void selectionChanged(SelectionChangedEvent event) {
				performFilterListSelectionChanged();
			}

			public void checkStateChanged(CheckStateChangedEvent event) {
				performFilterListCheckStateChanged();
			}
			
			public String getText(Object element) {
				return ((MatchFilter) element).getName();
			}
		}
		ListenerAndLabelProvider listenerAndLP= new ListenerAndLabelProvider();
		
		fListViewer.setLabelProvider(listenerAndLP);
		fListViewer.setContentProvider(new ArrayContentProvider());
		fListViewer.addSelectionChangedListener(listenerAndLP);
		fListViewer.addCheckStateListener(listenerAndLP);
		fListViewer.setInput(fAllFilters);
		fListViewer.setCheckedElements(fEnabledFilters);

		l= new Label(parent, SWT.NONE);
		l.setFont(parent.getFont());
		l.setText(SearchMessages.MatchFilterSelectionDialog_description_label); 
		fDescription = new Text(parent, SWT.LEFT | SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
		fDescription.setFont(parent.getFont());
		data = new GridData(GridData.FILL_HORIZONTAL);
		PixelConverter pcDesc = new PixelConverter( fDescription );
		data.heightHint = pcDesc.convertHeightInCharsToPixels(3);
		fDescription.setLayoutData(data);
	}

	private void createTableLimit(Composite ancestor) {
		Composite parent = new Composite(ancestor, SWT.NONE);
		parent.setFont(ancestor.getFont());
		GridLayout gl = new GridLayout();
		gl.numColumns = 2;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		parent.setLayout(gl);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		parent.setLayoutData(gd);

		fLimitElementsCheckbox = new Button(parent, SWT.CHECK);
		fLimitElementsCheckbox.setText(SearchMessages.MatchFilterSelectionDialog_limit_description);  
		fLimitElementsCheckbox.setLayoutData(new GridData());
		fLimitElementsCheckbox.setFont(parent.getFont());
		
		fLimitElementsField = new Text(parent, SWT.BORDER);
		fLimitElementsField.setFont(parent.getFont());
		gd = new GridData();
		PixelConverter pcLimitsElementsFields
		  = new PixelConverter( fLimitElementsField );
		gd.widthHint = pcLimitsElementsFields.convertWidthInCharsToPixels(6);
		fLimitElementsField.setLayoutData(gd);

		fLimitElementsCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				performLimitCheckboxChanged();
			}
		});

		fLimitElementsField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				performLimitTextModified();
			}
		});
		fLimitElementsCheckbox.setSelection(fLimitElementCount != -1);
		fLimitElementsField.setText(String.valueOf(fLastLimit));
		fLimitElementsField.setEnabled(fLimitElementsCheckbox.getSelection());
	}

	private void performFilterListSelectionChanged() {
		Object selectedElement = ((IStructuredSelection) fListViewer.getSelection()).getFirstElement();
		if (selectedElement != null)
			fDescription.setText(((MatchFilter) selectedElement).getDescription());
		else
			fDescription.setText(new String());
	}
	
	private void performFilterListCheckStateChanged() {
		Object[] checked= fListViewer.getCheckedElements();
		fEnabledFilters= new MatchFilter[checked.length];
		System.arraycopy(checked, 0, fEnabledFilters, 0, checked.length);
	}
	
	private void performLimitCheckboxChanged() {
		fLimitElementsField.setEnabled(fLimitElementsCheckbox.getSelection());
		fLimitElementCount= fLimitElementsCheckbox.getSelection() ? fLastLimit : -1;
		performLimitTextModified();
	}

	private void performLimitTextModified() {
		String text = fLimitElementsField.getText();
		int value = -1;
		try {
			value = Integer.valueOf(text).intValue();
		} catch (NumberFormatException e) {
		}
		fLimitElementCount= value;
		if (fLimitElementsCheckbox.getSelection() && value <= 0)
			updateStatus(createStatus(IStatus.ERROR, SearchMessages.MatchFilterSelectionDialog_error_invalid_limit)); 
		else
			updateStatus(createStatus(IStatus.OK, "")); //$NON-NLS-1$
	}
	
	private IStatus createStatus(int severity, String message) {
		return new Status(severity, NewSearchUI.PLUGIN_ID, severity, message, null);
	}
}
