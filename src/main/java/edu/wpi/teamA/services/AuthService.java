package edu.wpi.teamA.services;

// import com.google.firebase.auth.FirebaseAuth;
import com.google.inject.Inject;
import edu.wpi.teamA.modules.Record;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AuthService {
  public static final int GUEST_AUTH = 0;
  public static final int PATIENT_AUTH = 1;
  public static final int STAFF_AUTH = 2;
  public static final int ADMIN_AUTH = 3;

  private boolean signedIn = false;

  private ObservableList<SimpleStringProperty> userData = FXCollections.observableArrayList();

  Record user;
  private List<Record> newUsers = new ArrayList<Record>();
  private List<String> keys = new ArrayList<String>();
  private int newCount = 0;

  //  private FirebaseAuth mAuth;

  @Inject DatabaseService db;
  @Inject EmailService em;

  AuthService() {
    user = new Record();
    user.addProperty("id", "Log In");
    user.addProperty("authlevel", String.valueOf(GUEST_AUTH));

    userData.add(new SimpleStringProperty("Log In"));
    userData.add(new SimpleStringProperty(""));
    userData.add(new SimpleStringProperty(""));

    //    initializeApp(FirebaseOptions.builder().build());
    //    mAuth = FirebaseAuth.getInstance();

    //    initializeApp();

  }

  public void setUser(Record user) {
    this.user = user;
  }

  public Record getUser() {
    return user;
  }

  public boolean signIn(String username, String password) {

    Record r = findUser(username);
    if (r != null && r.getFieldAsString("password").equals(password)) {
      user = r;
      signedIn = true;
      userData.get(0).setValue(r.getFieldAsString("id"));
      userData.get(1).setValue(r.getFieldAsString("email"));
      userData.get(2).setValue(r.getFieldAsString("name"));
      return true;
    }

    signedIn = false;
    return false;
  }

  public int getAuthLevel() {
    return Integer.parseInt(user.getFieldAsString("authlevel"));
  }

  public int createUser(String username, String email, String password) {
    List<Record> users = db.usersTable.getItems();
    System.out.println("trying to create a user");

    if (findUser(username) != null) {
      return 1;
    } else if (findUser(email) != null) {
      return 2;
    }

    Record newUser = new Record();
    newUser.addProperty("id", username);
    newUser.addProperty("email", email);
    newUser.addProperty("password", password);
    newUser.addProperty("authlevel", String.valueOf(PATIENT_AUTH));
    newUsers.add(newCount, newUser);
    keys.add(newCount, generateKey(email, 6, true));
    // validate(keys.get(newCount));
    newCount++;
    return 0;
  }

  public boolean validate(String key) {
    int index = keys.indexOf(key);
    if (index != -1) {
      db.addRecord("USERS", newUsers.get(index));
      keys.remove(index);
      return true;
    }
    return false;
  }

  private String generateKey(String email, int length, boolean send) {
    System.out.println("trying to make a key");
    char[] alphanum =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    Random generator = new Random(System.currentTimeMillis());
    String key = "";
    for (int i = 0; i < length; i++) {
      key += alphanum[generator.nextInt(alphanum.length)];
    }

    if (send) {
      String subject = "Account Verification";
      String content =
          "The code to activate your account is <b>"
              + key
              + "</b>."
              + " If you did not create an account you may ignore this email.";
      em.sendEmail(email, subject, content);
    }

    return key;
  }

  public boolean resetPassword(String userID) {
    Record userReset = findUser(userID);

    if (userReset == null) {
      return false;
    }

    String key = generateKey(userReset.getFieldAsString("email"), 10, false);

    String subject = "Password Reset";
    String content =
        "You are receiving this code because you requested a password reset."
            + " If you did not mean to reset your password you may ignore this email. "
            + " Your new password is <b>"
            + key
            + "</b>."
            + " You can set a new password after logging in.";

    db.updateRecord("USERS", userReset, "password", key);

    em.sendEmail(userReset.getFieldAsString("email"), subject, content);

    userData.get(1).setValue(key);

    return true;
  }

  private Record findUser(String userID) {
    List<Record> users = db.usersTable.getItems();
    for (Record r : users) {
      if (r.getFieldAsString("id").equals(userID)) {
        return r;
      } else if (r.getFieldAsString("email").equals(userID)) {
        return r;
      }
    }
    return null;
  }

  public void signOut() {
    user = new Record();
    user.addProperty("id", "Log In");
    user.addProperty("authlevel", String.valueOf(GUEST_AUTH));
    userData.get(0).setValue("Log In");
    userData.get(1).setValue("");
    userData.get(2).setValue("");
    signedIn = false;
  }

  public ObservableList<SimpleStringProperty> getUserData() {
    return userData;
  }

  public void setUserData(String key, String val) {
    user.addProperty(key, val);
    if (key.equals("id")) {
      userData.get(0).setValue(val);
    } else if (key.equals("email")) {
      userData.get(1).setValue(val);
    } else if (key.equals("name")) {
      userData.get(2).setValue(val);
    }
    db.updateRecord("USERS", user, key, val);
  }
}
