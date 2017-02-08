package nbtkits;

import java.util.concurrent.TimeUnit;

public class Time {
	private long days;
	private long hours;
	private long minutes;
	private long seconds;

	public Time(long l) {
		this.days = ((long)TimeUnit.SECONDS.toDays(l));
		this.hours = ((long)(TimeUnit.SECONDS.toHours(l) - this.days * 24));
		this.minutes = ((long)(TimeUnit.SECONDS.toMinutes(l) - TimeUnit.SECONDS.toHours(l) * 60L));
		this.seconds = ((long)(TimeUnit.SECONDS.toSeconds(l) - TimeUnit.SECONDS.toMinutes(l) * 60L));
	}

	public String getFormat() {
		String day = pluralForm(this.days, " день ", " дня ", " дней ");
		String hour = pluralForm(this.hours, " час ", " часа ", " часов ");
		String min = pluralForm(this.minutes, " минуту ", " минуты ", " минут ");
		String sec = pluralForm(this.seconds, " секунду.", " секунды.", " секунд.");
		String format = this.days + day + this.hours + hour + this.minutes + min + this.seconds + sec;
		if (this.days == 0) {
			format = this.hours + hour + this.minutes + min + this.seconds + sec;
			if (this.hours == 0) {
				format = this.minutes + min + this.seconds + sec;
				if (this.minutes == 0) {
					format = this.seconds + sec;
				}
			}
		}
		return format;
	}


	private String pluralForm(long endTime, String form1, String form2, String form5) {
		endTime = endTime % 100;
		long n1 = endTime % 10;
		if (endTime > 10 && endTime < 20)
			return form5;
		if (n1 > 1 && n1 < 5)
			return form2;
		if (n1 == 1)
			return form1;
		return form5;
	}
}