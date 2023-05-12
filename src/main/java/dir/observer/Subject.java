package dir.observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Subject {
	
	private final List<Observer> observers = new ArrayList<>();

	public void addObserver(final Observer observer) {
		observers.add(observer);
	}

	public void deleteObserver(final Observer observer) {
		observers.remove(observer);
	}

	public void notifyObservers() {
		final Iterator<Observer> it = observers.iterator();
		while (it.hasNext()) {
			final Observer o = it.next();
			o.update(this);
		}
	}
	public abstract int getSize();

	public abstract String getName();
}
