///*******************************************************************************
// * Copyright (c) 2005, 2007 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *     Brad Reynolds - bug 164653
// *******************************************************************************/
//package org.eclipse.jface.internal.databinding.internal.swt;
//
//import org.eclipse.core.databinding.observable.Diffs;
//import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
//import org.eclipse.jface.util.Assert;
//import org.eclipse.swt.custom.CCombo;
//
///**
// * @since 3.2
// * 
// */
//public class CComboObservableValue extends AbstractSWTObservableValue {
//
//	/**
//	 * 
//	 */
//
//	private final CCombo ccombo;
//
//	private final String attribute;
//
////	private boolean updating = false;
//
//	// TODO [sa] implement when ComboBox implements setText and ModifyListener
////	private String currentValue;
//
//	/**
//	 * @param ccombo
//	 * @param attribute
//	 */
//	public CComboObservableValue(CCombo ccombo, String attribute) {
//		super(ccombo);
//		this.ccombo = ccombo;
//		this.attribute = attribute;
//
//		// TODO [sa] implement when ComboBox implements setText and ModifyListener
////		if (attribute.equals(SWTProperties.SELECTION)
////				|| attribute.equals(SWTProperties.TEXT)) {
////			this.currentValue = ccombo.getText();
////			ccombo.addModifyListener(new ModifyListener() {
////
////				public void modifyText(ModifyEvent e) {
////					if (!updating) {
////						String oldValue = currentValue;
////						currentValue = CComboObservableValue.this.ccombo
////								.getText();
////						fireValueChange(Diffs.createValueDiff(oldValue,
////								currentValue));
////					}
////				}
////			});
////		} else
//			throw new IllegalArgumentException();
//	}
//
//	public void doSetValue(final Object value) {
//		String oldValue = ccombo.getText();
//		try {
//			// TODO [sa] implement when ComboBox implements setText and ModifyListener
////			updating = true;
//			if (attribute.equals(SWTProperties.TEXT)) {
////				String stringValue = value != null ? value.toString() : ""; //$NON-NLS-1$
////				ccombo.setText(stringValue);
//			} else if (attribute.equals(SWTProperties.SELECTION)) {
//				String items[] = ccombo.getItems();
//				int index = -1;
//				if (items != null && value != null) {
//					for (int i = 0; i < items.length; i++) {
//						if (value.equals(items[i])) {
//							index = i;
//							break;
//						}
//					}
//					if (index == -1) {
////						ccombo.setText((String) value);
//					} else {
//						ccombo.select(index); // -1 will not "unselect"
//					}
//				}
//			}
//		} finally {
////			updating = false;
//		}
//		fireValueChange(Diffs.createValueDiff(oldValue, ccombo.getText()));
//	}
//
//	public Object doGetValue() {
//		if (attribute.equals(SWTProperties.TEXT))
//			return ccombo.getText();
//
//		Assert.isTrue(attribute.equals(SWTProperties.SELECTION),
//				"unexpected attribute: " + attribute); //$NON-NLS-1$
//		// The problem with a ccombo, is that it changes the text and
//		// fires before it update its selection index
//		return ccombo.getText();
//	}
//
//	public Object getValueType() {
//		Assert.isTrue(attribute.equals(SWTProperties.TEXT)
//				|| attribute.equals(SWTProperties.SELECTION),
//				"unexpected attribute: " + attribute); //$NON-NLS-1$
//		return String.class;
//	}
//
//	/**
//	 * @return attribute being observed
//	 */
//	public String getAttribute() {
//		return attribute;
//	}
//}
