/*******************************************************************************
 * Copyright (c) 2006 Klaus Wenger, Wind River Systems and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Klaus Wenger - initial API and implementation 
 * Markus Schorn - cleanup and conversion to inner part of the search plugin.
 *******************************************************************************/

package org.eclipse.search.internal.ui;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowPulldownDelegate2;
import org.eclipse.ui.activities.WorkbenchActivityHelper;

public class OpenSearchDialogPageAction implements IWorkbenchWindowPulldownDelegate2 {
	
	private IWorkbenchWindow fWorkbenchWindow;
	private Menu fMenu;
	private OpenSearchDialogAction fOpenSearchDialogAction;

	/*
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate2#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		fMenu= new Menu(parent);
		fillMenu(fMenu);
		return fMenu;
	}
	
	/*
	 * @see org.eclipse.ui.IWorkbenchWindowPulldownDelegate#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		fMenu= new Menu(parent);
		fillMenu(fMenu);
		return fMenu;
	}
	
	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
		fMenu= null;
		if (fOpenSearchDialogAction != null) {
			fOpenSearchDialogAction.dispose();
		}
	}
	
	/*
	 * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
		fWorkbenchWindow= window;
	}
	
	/*
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		if (fOpenSearchDialogAction == null) {
			fOpenSearchDialogAction= new OpenSearchDialogAction();
		}
		fOpenSearchDialogAction.run(action);
	}
	
	/*
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection sel) {
		// Empty
	}
	
	protected void fillMenu(Menu localMenu) {
		List pageDescriptors= SearchPlugin.getDefault().getSearchPageDescriptors();
		int accelerator= 1;
		for (Iterator iter= pageDescriptors.iterator(); iter.hasNext();) {
			SearchPageDescriptor desc= (SearchPageDescriptor) iter.next();
			if (!WorkbenchActivityHelper.filterItem(desc)) {
				SearchPageAction action= new SearchPageAction(fWorkbenchWindow, desc);
				addToMenu(localMenu, action, accelerator++);
			}
		}
	}
	
	protected void addToMenu(Menu localMenu, IAction action, int accelerator) {
		StringBuffer label= new StringBuffer();
		if (accelerator >= 0 && accelerator < 10) {
			//add the numerical accelerator
			label.append('&');
			label.append(accelerator);
			label.append(' ');
		}
		label.append(action.getText());
		action.setText(label.toString());
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(localMenu, -1);
	}

	private static final class SearchPageAction extends Action {
		private final OpenSearchDialogAction fOpenSearchDialogAction;
		
		public SearchPageAction(IWorkbenchWindow workbenchWindow, SearchPageDescriptor pageDescriptor) {
			super();
			fOpenSearchDialogAction= new OpenSearchDialogAction(workbenchWindow, pageDescriptor.getId());
			init(pageDescriptor);
		}

		private void init(SearchPageDescriptor pageDesc) {
			setText(pageDesc.getLabel());
			setToolTipText(pageDesc.getLabel());
			ImageDescriptor imageDescriptor= pageDesc.getImage();
			if (imageDescriptor != null) {
				setImageDescriptor(imageDescriptor);
			}
		}
		
		public void run() {
			fOpenSearchDialogAction.run(this);
		}

	}
}
