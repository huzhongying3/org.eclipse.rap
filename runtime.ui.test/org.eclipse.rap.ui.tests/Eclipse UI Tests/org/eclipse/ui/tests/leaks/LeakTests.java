/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.tests.leaks;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

//import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
//import org.eclipse.jface.dialogs.Dialog;
//import org.eclipse.ui.IEditorInput;
//import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.dialogs.SaveAsDialog;
//import org.eclipse.ui.ide.IDE;
//import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.tests.api.MockViewPart;
import org.eclipse.ui.tests.harness.util.FileUtil;
import org.eclipse.ui.tests.harness.util.UITestCase;

/**
 * Very simple leak tests to determine if any of our heavy objects are not being
 * disposed properly. Note that the results of these tests will in no way
 * actually assist you in tracking down the leak.
 * 
 * @since 3.1
 */
public class LeakTests extends UITestCase {
    private IWorkbenchPage fActivePage;

    private IWorkbenchWindow fWin;

    private IProject proj;

    /**
     * @param testName
     */
    public LeakTests(String testName) {
        super(testName);
    }

    public static void checkRef(ReferenceQueue queue, Reference ref)
            throws IllegalArgumentException, InterruptedException {
        boolean flag = false;
        for (int i = 0; i < 100; i++) {
            System.runFinalization();
            System.gc();
            Thread.yield();
            processEvents();
            Reference checkRef = queue.remove(100);
            if (checkRef != null && checkRef.equals(ref)) {
                flag = true;
                break;
            }
        }

        assertTrue("Reference not enqueued", flag);
    }

    /**
     * @param queue
     * @param object
     * @return
     */
    private Reference createReference(ReferenceQueue queue, Object object) {
        return new PhantomReference(object, queue);
    }

    protected void doSetUp() throws Exception {
        super.doSetUp();
//        fWin = openTestWindow(IDE.RESOURCE_PERSPECTIVE_ID);
//        fActivePage = fWin.getActivePage();
    }

    protected void doTearDown() throws Exception {
        super.doTearDown();
        fWin = null;
        fActivePage = null;
        if (proj != null) {
            FileUtil.deleteProject(proj);
            proj = null;
        }
    }

//    public void testSimpleEditorLeak() throws Exception {
//        proj = FileUtil.createProject("testEditorLeaks");
//
//        IFile file = FileUtil.createFile("test.mock1", proj);
//
//        ReferenceQueue queue = new ReferenceQueue();
//        IEditorPart editor = IDE.openEditor(fActivePage, file);
//        assertNotNull(editor);
//        Reference ref = createReference(queue, editor);
//        try {
//            fActivePage.closeEditor(editor, false);
//            editor = null;
//            checkRef(queue, ref);
//        } finally {
//            ref.clear();
//        }
//    }

    public void testSimpleViewLeak() throws Exception {
        ReferenceQueue queue = new ReferenceQueue();
        IViewPart view = fActivePage.showView(MockViewPart.ID);
        assertNotNull(view);
        Reference ref = createReference(queue, view);

        try {
            fActivePage.hideView(view);
            view = null;
            checkRef(queue, ref);
        } finally {
            ref.clear();
        }
    }
    
//    public void testTextEditorContextMenu() throws Exception {
//    	proj = FileUtil.createProject("testEditorLeaks");
//
//    	IEditorInput input = new NullEditorInput();
//        ReferenceQueue queue = new ReferenceQueue();
//        IEditorPart editor = IDE.openEditor(fActivePage, input, "org.eclipse.ui.tests.leak.contextEditor");
//        assertTrue(editor instanceof ContextEditorPart);
//        Reference ref = createReference(queue, editor);
//        
//        ContextEditorPart contextMenuEditor = (ContextEditorPart) editor;
//        
//        contextMenuEditor.showMenu();
//        processEvents();
//        
//        contextMenuEditor.hideMenu();
//        processEvents();
//        
//        try {
//            contextMenuEditor = null;
//            fActivePage.closeEditor(editor, false);
//            editor = null;
//            checkRef(queue, ref);
//        } finally {
//            ref.clear();
//        }
//    }

      /**
       * No idea why the following test is failing.  Doug has ran this through a 
       * profiler and for some reason the window just isn't being GCd despite 
       * there not being nay incoming references.
       */
    public void testSimpleWindowLeak() throws Exception {
        //turn off window management so that we dont have a reference to our
        // new
        //window in the listener
        manageWindows(false);
        try {
            ReferenceQueue queue = new ReferenceQueue();
            IWorkbenchWindow newWindow = openTestWindow();

            assertNotNull(newWindow);
            Reference ref = createReference(queue, newWindow);
            try {
                newWindow.close();
                newWindow = null;
                checkRef(queue, ref);
            } finally {
                ref.clear();
            }
        } finally {
            manageWindows(true);
        }
    }
    
    /**
     * Test for leaks if dialog is disposed before it is closed.
     * This is really testing the framework rather than individual
     * dialogs, since many dialogs or windows will fail if the shell
     * is destroyed prior to closing them.
     * See bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=123296
     */
//  public void testDestroyedDialogLeaks() throws Exception {
//	  ReferenceQueue queue = new ReferenceQueue();
//	  // Use SaveAs dialog because it's simple to invoke and utilizes
//	  // framework function such as storing dialog bounds.  
//	  // We are really testing the framework itself here.
//	  Dialog newDialog = new SaveAsDialog(fWin.getShell());
//      newDialog.setBlockOnOpen(false);
//      newDialog.open();
//      assertNotNull(newDialog);
//      Reference ref = createReference(queue, newDialog);
//      try {
//      	  // Dispose the window before closing it.  
//       	  newDialog.getShell().dispose();
//       	  newDialog.close();
//       	  newDialog = null;
//          checkRef(queue, ref);
//      } finally {
//    	  ref.clear();
//      }
//  }
}
