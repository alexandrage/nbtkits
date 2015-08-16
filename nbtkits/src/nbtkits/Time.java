package nbtkits;

import java.util.concurrent.TimeUnit;

public class Time
{
  private long days;
  private long hours;
  private long minutes;
  private long seconds;

  public Time(long l)
  {
    this.days = ((long)TimeUnit.SECONDS.toDays(l));
    this.hours = ((long)(TimeUnit.SECONDS.toHours(l) - this.days * 24));
    this.minutes = ((long)(TimeUnit.SECONDS.toMinutes(l) - TimeUnit.SECONDS.toHours(l) * 60L));
    this.seconds = ((long)(TimeUnit.SECONDS.toSeconds(l) - TimeUnit.SECONDS.toMinutes(l) * 60L));
  }
  
  public String getFormat() {
		String format = this.days+" дн. "+this.hours+" ч. "+this.minutes+" мин. "+this.seconds+" сек.";
		if(this.days==0) {
			format = this.hours+" ч. "+this.minutes+" мин. "+this.seconds+" сек.";
			if(this.hours==0) {
				format = this.minutes+" мин. "+this.seconds+" сек.";
				if(this.minutes==0) {
					format = this.seconds+" сек.";
				}
			}
		}
		return format;
  }
}