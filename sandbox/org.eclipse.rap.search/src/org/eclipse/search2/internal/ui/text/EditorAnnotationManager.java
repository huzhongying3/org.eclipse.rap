///*******************************************************************************
// * Copyright (c) 2000, 2006 IBM Corporation and others.
// * All rights reserved. This program and the accompanying materials
// * are made available under the terms of the Eclipse Public License v1.0
// * which accompanies this distribution, and is available at
// * http://www.eclipse.org/legal/epl-v10.html
// *
// * Contributors:
// *     IBM Corporation - initial API and implementation
// *******************************************************************************/
//
//package org.eclipse.search2.internal.ui.text;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eclipse.core.runtime.Assert;
//
//import org.eclipse.core.resources.IFile;
//
//import org.eclipse.jface.text.IDocument;
//import org.eclipse.jface.text.source.IAnnotationModel;
//
//import org.eclipse.ui.IEditorInput;
//import org.eclipse.ui.IEditorPart;
//import org.eclipse.ui.IFileEditorInput;
//import org.eclipse.ui.IWorkbenchPart;
//import org.eclipse.ui.texteditor.IDocumentProvider;
//import org.eclipse.ui.texteditor.ITextEditor;
//
//import org.eclipse.search.ui.ISearchResult;
//import org.eclipse.search.ui.ISearchResultListener;
//import org.eclipse.search.ui.SearchResultEvent;
//import org.eclipse.search.ui.text.AbstractTextSearchResult;
//import org.eclipse.search.ui.text.FilterUpdateEvent;
//import org.eclipse.search.ui.text.IEditorMatchAdapter;
//import org.eclipse.search.ui.text.ISearchEditorAccess;
//import org.eclipse.search.ui.text.Match;
//import org.eclipse.search.ui.text.MatchEvent;
//import org.eclipse.search.ui.text.RemoveAllEvent;
//
//public class EditorAnnotationManager implements ISearchResultListener {
//	
//	private ArrayList fResults;
//	private IEditorPart fEditor;
//	private Highlighter fHighlighter; // initialized lazy
//	
//	public static final int HIGHLLIGHTER_ANY= 0;
//	public static final int HIGHLIGHTER_MARKER= 1;
//	public static final int HIGHLIGHTER_ANNOTATION= 2;
//	public static final int HIGHLIGHTER_EDITOR_ACCESS= 3;
//	private static int fgHighlighterType= HIGHLLIGHTER_ANY;
//	
//	
//	public EditorAnnotationManager(IEditorPart editorPart) {
//		Assert.isNotNull(editorPart);
//		fEditor= editorPart;
//		fHighlighter= null; // lazy initialization
//		fResults= new ArrayList(3);
//	}
//	
//
//	public static final void debugSetHighlighterType(int type) {
//		fgHighlighterType= type;
//	}
//
//
//	void dispose() {
//		removeAllAnnotations();
//		if (fHighlighter != null)
//			fHighlighter.dispose();
//		
//		for (int i= 0; i < fResults.size(); i++) {
//			((AbstractTextSearchResult) fResults.get(i)).removeListener(this);
//		}
//		fResults.clear();
//	}
//	
//	public synchronized void doEditorInputChanged() {
//		removeAllAnnotations();
//		
//		if (fHighlighter != null) {
//			fHighlighter.dispose();
//			fHighlighter= null;
//		}
//		
//		for (int i= 0; i < fResults.size(); i++) {
//			AbstractTextSearchResult curr= (AbstractTextSearchResult) fResults.get(i);
//			addAnnotations(curr);
//		}
//	}
//
//	public synchronized void setSearchResults(List results) {
//		removeAllAnnotations();
//		for (int i= 0; i < fResults.size(); i++) {
//			((AbstractTextSearchResult) fResults.get(i)).removeListener(this);
//		}
//		fResults.clear();
//		
//		for (int i= 0; i < results.size(); i++) {
//			addSearchResult((AbstractTextSearchResult) results.get(i));
//		}
//	}
//	
//	public synchronized void addSearchResult(AbstractTextSearchResult result) {
//		fResults.add(result);
//		result.addListener(this);
//		addAnnotations(result);
//	}
//	
//	public synchronized void removeSearchResult(AbstractTextSearchResult result) {
//		fResults.remove(result);
//		result.removeListener(this);
//		removeAnnotations(result);
//	}
//	
//
//	public synchronized void searchResultChanged(SearchResultEvent e) {
//		ISearchResult searchResult= e.getSearchResult();
//		if (searchResult instanceof AbstractTextSearchResult) {
//			AbstractTextSearchResult result= (AbstractTextSearchResult) searchResult;
//			if (e instanceof MatchEvent) {
//				MatchEvent me= (MatchEvent) e;
//				Match[] matchesInEditor= getMatchesInEditor(me.getMatches(), result);
//				if (matchesInEditor != null) {
//					if (me.getKind() == MatchEvent.ADDED) {
//						addAnnotations(matchesInEditor);
//					} else {
//						removeAnnotations(matchesInEditor);
//					}
//				}
//			} else if (e instanceof RemoveAllEvent) {
//				removeAnnotations(result);
//			} else if (e instanceof FilterUpdateEvent) {
//				Match[] matchesInEditor= getMatchesInEditor(((FilterUpdateEvent) e).getUpdatedMatches(), result);
//				if (matchesInEditor != null) {
//					removeAnnotations(matchesInEditor);
//					addAnnotations(matchesInEditor);
//				}
//			}
//		}
//	}
//
//	private Match[] getMatchesInEditor(Match[] matches, AbstractTextSearchResult result) {
//		IEditorMatchAdapter adapter= result.getEditorMatchAdapter();
//		if (adapter == null) {
//			return null;
//		}
//		
//		// optimize the array-length == 1 case (most common)
//		if (matches.length == 1) {
//			return adapter.isShownInEditor(matches[0], fEditor) ? matches : null;
//		}
//		
//		ArrayList matchesInEditor= null; // lazy initialization
//		for (int i= 0; i < matches.length; i++) {
//			Match curr= matches[i];
//			if (adapter.isShownInEditor(curr, fEditor)) {
//				if (matchesInEditor == null) {
//					matchesInEditor= new ArrayList();
//				}
//				matchesInEditor.add(curr);
//			}
//		}
//		if (matchesInEditor != null) {
//			return (Match[]) matchesInEditor.toArray(new Match[matchesInEditor.size()]);
//		}
//		return null;
//	}
//
//	private void removeAllAnnotations() {
//		if (fHighlighter != null)
//			fHighlighter.removeAll();
//	}
//
//	private Highlighter createHighlighter() {
//		IEditorPart editor= fEditor;
//		if (fgHighlighterType != HIGHLLIGHTER_ANY) {
//			return debugCreateHighlighter(editor);
//		}
//		ISearchEditorAccess access= (ISearchEditorAccess) editor.getAdapter(ISearchEditorAccess.class);
//		if (access != null)
//			return new EditorAccessHighlighter(access);
//		IAnnotationModel model= getAnnotationModel(editor);
//		if (model != null)
//			return new AnnotationHighlighter(model, getDocument(editor));
//		IEditorInput input= editor.getEditorInput();
//		if (input instanceof IFileEditorInput) {
//			IFile file= ((IFileEditorInput)input).getFile();
//			if (file != null)
//				return new MarkerHighlighter(file);
//		}
//		return new Highlighter(); // does nothing
//	}
//
//	private static Highlighter debugCreateHighlighter(IEditorPart editor) {
//		if (fgHighlighterType == HIGHLIGHTER_ANNOTATION) {
//			IAnnotationModel model= getAnnotationModel(editor);
//			if (model != null)
//				return new AnnotationHighlighter(model, getDocument(editor));		
//		} else if (fgHighlighterType == HIGHLIGHTER_MARKER) {
//			IEditorInput input= editor.getEditorInput();
//			if (input instanceof IFileEditorInput) {
//				IFile file= ((IFileEditorInput)input).getFile();
//				if (file != null)
//					return new MarkerHighlighter(file);
//			}
//			
//		} else if (fgHighlighterType == HIGHLIGHTER_EDITOR_ACCESS) {
//			ISearchEditorAccess access= (ISearchEditorAccess) editor.getAdapter(ISearchEditorAccess.class);
//			if (access != null)
//				return new EditorAccessHighlighter(access);
//		}
//		return null;
//	}
//
//	private void addAnnotations(AbstractTextSearchResult result) {
//		IEditorMatchAdapter matchAdapter= result.getEditorMatchAdapter();
//		if (matchAdapter == null)
//			return;
//		Match[] matches= matchAdapter.computeContainedMatches(result, fEditor);
//		if (matches == null || matches.length == 0)
//			return;
//		addAnnotations(matches);
//	}
//	
//	private void removeAnnotations(AbstractTextSearchResult result) {
//		removeAllAnnotations();
//		
//		for (int i= 0; i < fResults.size(); i++) {
//			AbstractTextSearchResult curr= (AbstractTextSearchResult) fResults.get(i);
//			if (curr != result) {
//				addAnnotations(curr);
//			}
//		}
//	}
//
//	private void addAnnotations(Match[] matches) {
//		if (fHighlighter == null) {
//			fHighlighter= createHighlighter();
//		}
//		fHighlighter.addHighlights(matches);
//	}
//
//	private void removeAnnotations(Match[] matches) {
//		if (fHighlighter != null)
//			fHighlighter.removeHighlights(matches);
//	}
//
//	private static IAnnotationModel getAnnotationModel(IWorkbenchPart part) {
//		IAnnotationModel model= null;
//		model= (IAnnotationModel) part.getAdapter(IAnnotationModel.class); 
//		if (model == null) {
//			ITextEditor textEditor= null;
//			if (part instanceof ITextEditor) {
//				textEditor= (ITextEditor) part; 
//			}
//			if (textEditor != null) {
//				IDocumentProvider dp= textEditor.getDocumentProvider();
//				if (dp != null)
//					model= dp.getAnnotationModel(textEditor.getEditorInput());
//			}
//		}
//		return model;
//	}
//
//	private static IDocument getDocument(IWorkbenchPart part) {
//		IDocument doc= null;
//		doc= (IDocument) part.getAdapter(IDocument.class); 
//		if (doc == null) {
//			ITextEditor textEditor= null;
//			if (part instanceof ITextEditor) {
//				textEditor= (ITextEditor) part; 
//			}
//			if (textEditor != null) {
//				IDocumentProvider dp= textEditor.getDocumentProvider();
//				if (dp != null)
//					doc= dp.getDocument(textEditor.getEditorInput());
//			}
//		}
//		return doc;
//	}
//}
