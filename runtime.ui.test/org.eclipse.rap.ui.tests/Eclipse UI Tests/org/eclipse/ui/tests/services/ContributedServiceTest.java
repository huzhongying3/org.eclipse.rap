///*******************************************************************************
// * Copyright (c) 2007 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// ******************************************************************************/
//
//package org.eclipse.ui.tests.services;
//
//import org.eclipse.ui.IViewPart;
//import org.eclipse.ui.IViewReference;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.progress.IProgressService;
//import org.eclipse.ui.progress.IWorkbenchSiteProgressService;
//import org.eclipse.ui.services.AbstractServiceFactory;
//import org.eclipse.ui.services.IDisposable;
//import org.eclipse.ui.services.IServiceLocator;
//import org.eclipse.ui.services.IServiceLocatorCreator;
//import org.eclipse.ui.tests.harness.util.UITestCase;
//
///**
// * @since 3.4
// * 
// */
//public class ContributedServiceTest extends UITestCase {
//
//	/**
//	 * @param testName
//	 */
//	public ContributedServiceTest(String testName) {
//		super(testName);
//	}
//
//	public void testGlobalService() throws Exception {
//		assertNotNull(getWorkbench().getService(IWorkbench.class));
//
//		ILevelService l = (ILevelService) getWorkbench().getService(
//				ILevelService.class);
//		assertNotNull(l);
//		assertEquals(1, l.getLevel());
//
//		l = (ILevelService) getWorkbench().getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(1, l.getLevel());
//
//		assertEquals(1, LevelServiceFactory.instancesCreated);
//	}
//
//	public void testWindowService() throws Exception {
//		IServiceLocator locator = getWorkbench().getActiveWorkbenchWindow();
//		assertNotNull(locator.getService(IWorkbenchWindow.class));
//
//		ILevelService l = (ILevelService) locator
//				.getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(2, l.getLevel());
//
//		assertEquals(2, LevelServiceFactory.instancesCreated);
//
//		l = (ILevelService) locator.getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(2, l.getLevel());
//
//		l = (ILevelService) getWorkbench().getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(1, l.getLevel());
//
//		assertEquals(2, LevelServiceFactory.instancesCreated);
//	}
//
//	private static class TempLevelFactory extends AbstractServiceFactory {
//		private int level;
//
//		public TempLevelFactory(int l) {
//			level = l;
//		}
//
//		public Object create(Class serviceInterface,
//				IServiceLocator parentLocator, IServiceLocator locator) {
//			return new ILevelService() {
//
//				public int getLevel() {
//					return level;
//				}
//			};
//		}
//	}
//
//	public void testLocalServiceCreated() throws Exception {
//		IServiceLocator parent = getWorkbench().getActiveWorkbenchWindow();
//		assertNotNull(parent.getService(IWorkbenchWindow.class));
//
//		IServiceLocatorCreator lc = (IServiceLocatorCreator) parent
//				.getService(IServiceLocatorCreator.class);
//		IServiceLocator locator = lc.createServiceLocator(parent, null);
//
//		ILevelService l = (ILevelService) locator
//				.getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(3, l.getLevel());
//
//		assertEquals(3, LevelServiceFactory.instancesCreated);
//
//		if (locator instanceof IDisposable) {
//			((IDisposable) locator).dispose();
//		}
//
//		locator = lc.createServiceLocator(parent, null);
//		l = (ILevelService) locator.getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(3, l.getLevel());
//
//		assertEquals(4, LevelServiceFactory.instancesCreated);
//		if (locator instanceof IDisposable) {
//			((IDisposable) locator).dispose();
//		}
//
//		locator = lc.createServiceLocator(parent, new TempLevelFactory(8));
//		l = (ILevelService) locator.getService(ILevelService.class);
//		assertNotNull(l);
//		assertEquals(8, l.getLevel());
//
//		assertEquals(4, LevelServiceFactory.instancesCreated);
//		if (locator instanceof IDisposable) {
//			((IDisposable) locator).dispose();
//		}
//	}
//
//	public void testWorkbenchServiceFactory() throws Exception {
//		IWorkbenchWindow window = getWorkbench().getActiveWorkbenchWindow();
//		IProgressService progress = (IProgressService) window
//				.getService(IProgressService.class);
//		assertNotNull(progress);
//
//		assertEquals(getWorkbench().getProgressService(), progress);
//		IViewPart part = null;
//		IViewReference[] refs = window.getActivePage().getViewReferences();
//		for (int i = 0; i < refs.length; i++) {
//			if ((part = refs[i].getView(false)) != null) {
//				break;
//			}
//		}
//
//		assertNotNull(part);
//		progress = (IProgressService) part.getSite().getService(
//				IProgressService.class);
//		assertFalse(progress == getWorkbench().getProgressService());
//		assertEquals(part.getSite().getService(
//				IWorkbenchSiteProgressService.class), progress);
//		assertEquals(part.getSite().getAdapter(
//				IWorkbenchSiteProgressService.class), progress);
//	}
//}
