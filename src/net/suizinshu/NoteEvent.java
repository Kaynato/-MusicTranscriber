package net.suizinshu;

/**
 * A note event for numerical interpretation of midi data and manipulation between modes.
 * @author Zicheng Gao
 */
public final class NoteEvent {
	public long activationTime;
	public long relativeTime;
	public int key;
	public int velocity;
	public int duration;

	public NoteEvent(long activationTime, long relativeTime, int key, int velocity, int duration) {
		this.activationTime = activationTime;
		this.relativeTime = relativeTime;
		this.key = key;
		this.velocity = velocity;
		this.duration = duration;
	}

	@Override
	public String toString() {
		return (relativeTime + "," + key + "," + velocity + "," + duration);
	}
	
	public NoteEvent(String data) {
		int index = 0;
		StringBuilder num = new StringBuilder();
		
		for (int i = 0; i < data.length() + 1 && index < 4; i++) {
			
			if (i < data.length() && data.charAt(i) != ',')
					num.append(data.charAt(i));
			else {
				if (num.length() > 0) {
					int datum = Integer.parseInt(num.toString());

					if (index == 0)
						relativeTime = datum;
					else if (index == 1)
						key = datum;
					else if (index == 2)
						velocity = datum;
					else if (index == 3)
						duration = datum;

					num.delete(0, num.length());
					index++;
				}
			}
		}
	}
	
}