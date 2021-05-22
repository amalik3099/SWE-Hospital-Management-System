package edu.wpi.teamA.services;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService {
  private Properties properties;
  private String email = "TeamA3733@gmail.com";
  private String pass = "teama123";
  private Session session;
  private String recipient;
  private String subject;
  private String content;
  private final int MAX_DELAY = 480;

  public EmailService() {
    properties = new Properties();
    properties.put("mail.smtp.auth", true);
    properties.put("mail.smtp.starttls.enable", true);
    properties.put("mail.smtp.host", "smtp.gmail.com");
    properties.put("mail.smtp.port", "587");

    session =
        Session.getInstance(
            properties,
            new Authenticator() {
              @Override
              protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email, pass);
              }
            });
  }

  /**
   * Creates a thread to send an email in the backgorund without causing the program to hang.
   *
   * @param recipient Target email address
   * @param subject Subject line of the email
   * @param content Contents of the email. Accepts html formatting
   */
  public void sendEmail(String recipient, String subject, String content) {
    Thread th =
        new Thread(
            () -> {
              threadEmail(recipient, subject, content);
            });
    th.setDaemon(true);
    th.start();
  }

  /** Calls sendEmail using the information saved from the last call of sendEmail. */
  public void resendEmail() {
    if (recipient != null && subject != null && content != null) {
      sendEmail(recipient, subject, content);
    }
  }

  /**
   * Attempts to send an email to specified address. Attempts to resend the email using threadResend
   * if it fails.
   *
   * @param recipient Target email address
   * @param subject Subject line of the email
   * @param content Contents of the email. Accepts html formatting
   */
  private void threadEmail(String recipient, String subject, String content) {
    Message message = new MimeMessage(session);
    this.recipient = recipient;
    this.subject = subject;
    this.content = content;
    try {
      message.setFrom(new InternetAddress(email));
      message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
      message.setSubject(subject);
      message.setContent(content, "text/html");
      Transport.send(message);
    } catch (MessagingException e) {
      threadResend(60);
    }
  }

  /**
   * Recursively attempts to send an email up to 4 times before giving up.
   *
   * @param delay Time in seconds to wait between retries
   */
  private void threadResend(int delay) {
    if (recipient != null && subject != null && content != null && delay <= MAX_DELAY) {
      try {
        TimeUnit.SECONDS.sleep(delay);
      } catch (InterruptedException e) {
      }
      Message message = new MimeMessage(session);
      try {
        message.setFrom(new InternetAddress(email));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setContent(content, "text/html");
        Transport.send(message);
      } catch (MessagingException e) {
        threadResend(delay * 2);
      }
    }
  }
}
