/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Brad Reynolds - bug 116920
 *     Brad Reynolds - bug 147515
 *******************************************************************************/
package org.eclipse.core.databinding.observable.value;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.IStaleListener;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.StaleEvent;

/**
 * A Lazily calculated value that automatically computes and registers listeners
 * on its dependencies as long as all of its dependencies are IObservable
 * objects
 * <p>
 * This class is thread safe. All state accessing methods must be invoked from
 * the {@link Realm#isCurrent() current realm}. Methods for adding and removing
 * listeners may be invoked from any thread.
 * </p>
 * @since 1.0
 */
public abstract class ComputedValue extends AbstractObservableValue {

	private boolean dirty = true;

	private boolean stale = false;

	private Object cachedValue = null;

	/**
	 * Dependencies list. This is a collection that contains no duplicates. It
	 * is normally an ArrayList to conserve memory, but if it ever grows above a
	 * certain number of elements, a HashSet is substited to conserve runtime.
	 */
	private IObservable[] dependencies = new IObservable[0];

	/**
	 * 
	 */
	public ComputedValue() {
		this(Realm.getDefault(), null);
	}

	/**
	 * @param valueType can be <code>null</code>
	 */
	public ComputedValue(Object valueType) {
		this(Realm.getDefault(), valueType);
	}

	/**
	 * @param realm 
	 * 
	 */
	public ComputedValue(Realm realm) {
		this(realm, null);
	}
	
	/**
	 * @param realm 
	 * @param valueType
	 */
	public ComputedValue(Realm realm, Object valueType) {
		super(realm);
		this.valueType = valueType;
	}
	
	/**
	 * Inner class that implements interfaces that we don't want to expose as
	 * public API. Each interface could have been implemented using a separate
	 * anonymous class, but we combine them here to reduce the memory overhead
	 * and number of classes.
	 * 
	 * <p>
	 * The Runnable calls computeValue and stores the result in cachedValue.
	 * </p>
	 * 
	 * <p>
	 * The IChangeListener stores each observable in the dependencies list.
	 * This is registered as the listener when calling ObservableTracker, to
	 * detect every observable that is used by computeValue.
	 * </p>
	 * 
	 * <p>
	 * The IChangeListener is attached to every dependency.
	 * </p>
	 * 
	 */
	private class PrivateInterface implements Runnable, IChangeListener,
			IStaleListener {
		public void run() {
			cachedValue = calculate();
		}

		public void handleStale(StaleEvent event) {
			if (!dirty && !stale) {
				stale = true;
				fireStale();
			}
		}

		public void handleChange(ChangeEvent event) {
			makeDirty();
		}
	}

	private PrivateInterface privateInterface = new PrivateInterface();

	private Object valueType;

	protected final Object doGetValue() {
		if (dirty) {
			// This line will do the following:
			// - Run the calculate method
			// - While doing so, add any observable that is touched to the
			// dependencies list
			IObservable[] newDependencies = ObservableTracker.runAndMonitor(
					privateInterface, privateInterface, null);

			stale = false;
			for (int i = 0; i < newDependencies.length; i++) {
				IObservable observable = newDependencies[i];
				// Add a change listener to the new dependency.
				if (observable.isStale()) {
					stale = true;
				} else {
					observable.addStaleListener(privateInterface);
				}
			}

			dependencies = newDependencies;

			dirty = false;
		}

		return cachedValue;
	}

	/**
	 * Subclasses must override this method to provide the object's value.
	 * 
	 * @return the object's value
	 */
	protected abstract Object calculate();

	protected final void makeDirty() {
		if (!dirty) {
			dirty = true;

			stopListening();

			// copy the old value
			final Object oldValue = cachedValue;
			// Fire the "dirty" event. This implementation recomputes the new
			// value lazily.
			fireValueChange(new ValueDiff() {

				public Object getOldValue() {
					return oldValue;
				}

				public Object getNewValue() {
					return getValue();
				}
			});
		}
	}

	/**
	 * 
	 */
	private void stopListening() {
		// Stop listening for dependency changes.
		for (int i = 0; i < dependencies.length; i++) {
			IObservable observable = dependencies[i];

			observable.removeChangeListener(privateInterface);
			observable.removeStaleListener(privateInterface);
		}
	}

	public boolean isStale() {
		// we need to recompute, otherwise staleness wouldn't mean anything
		getValue();
		return stale;
	}

	public Object getValueType() {
		return valueType;
	}

	public synchronized void addChangeListener(IChangeListener listener) {
		super.addChangeListener(listener);
		// If somebody is listening, we need to make sure we attach our own
		// listeners
		getValue();
	}

	public synchronized void addValueChangeListener(IValueChangeListener listener) {
		super.addValueChangeListener(listener);
		// If somebody is listening, we need to make sure we attach our own
		// listeners
		getValue();
	}
	
	public synchronized void dispose() {
		super.dispose();
		stopListening();
	}

}
