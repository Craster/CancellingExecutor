package events;

import java.io.Serializable;

public class HeartbeatEvent implements Serializable {

  private long userId;

  public HeartbeatEvent(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    return userId;
  }
}
