package edu.wpi.teamA.views;

import javax.mail.MessagingException;

public interface InnerServiceController {

  public boolean addRequestToDB(String name, String email, boolean urgency)
      throws MessagingException;

  public void clearFields();
}
