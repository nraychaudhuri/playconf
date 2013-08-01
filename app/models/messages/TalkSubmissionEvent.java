package models.messages;

public class TalkSubmissionEvent {
	
	private final String name;
	private final String title;

	public TalkSubmissionEvent(String name, String title) {
		this.name = name;
		this.title = title;
	}
	
	public String name() {
		return name;
	}

	public String title() {
		return title;
	}

}
