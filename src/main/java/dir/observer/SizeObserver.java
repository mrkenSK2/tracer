package dir.observer;

public class SizeObserver implements Observer {
	public void update(Subject subject) {
		System.out.println("file: " + subject.getName() +  ", size : " + subject.getSize());
	}
}
