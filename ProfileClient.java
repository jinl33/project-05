import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

/**
 * PJ05 Option 2 - Client
 * 
 * Implements GUI for user interactions. Creates a socket for the client to be connected to the server
 * 
 * @author Ziyang Huang
 * @version November 23, 2020
 */

 // The position of GUI is commented only once
 // GUI part with a message box indicating what's happening is not commented.
 

public class ProfileClient extends JComponent implements Runnable {
    /**
     *
     */
    private static final long serialVersionUID = 5597188023823293381L;
    
    // Declare networking-related fields for global usage
    Socket socket;
    String hostName = "localhost";
    int portNumber = 6868;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    Profile currentProfile; // the profile that is displaying
    Profile myProfile; // the profile of the logged-in user

    // Declare frames (windows) that will be used
    JFrame loginFrame;
    JFrame registerFrame;
    JFrame mainFrame;
    JFrame listAllUserFrame;
    JFrame friendRequestFrame;
    JFrame requestHistoryFrame;

    // Declare elements of GUI that will be used globally
    JPanel friendListPanel;
    JButton profileAddFriendButton;
    JButton profileCancelButton;
    JButton profileSaveButton;
    JTextField profileEmailText;
    JTextField profileLikesAndInterestsText;
    JTextField profileNameText;
    JTextField profilePhoneText;
    JTextArea profileAboutMeArea;
    JLabel profileUsernameLabel;

    
    /** 
     * method that deals with login
     * 
     * @param username
     * @param password
     * @return int
     */
    private int userLogin(String username, String password) {
        String loginRequest = String.format("Req1: %s: %s", username, password);
        Object loginResponse;
        
        loginResponse = sendRequest(loginRequest); // send login request
        if (loginResponse instanceof Profile) {
            JOptionPane.showMessageDialog(null, "Login Successfully", "User Login", JOptionPane.INFORMATION_MESSAGE);
            myProfile = (Profile) loginResponse; // set my profile
            return 1;
        } else if (((String) loginResponse).split(": ")[0].equals("E1")) { // wrong
            JOptionPane.showMessageDialog(null, "Wrong username or password", "User Login",
                                                JOptionPane.INFORMATION_MESSAGE);
        } else { // unexpected response goes here
            JOptionPane.showMessageDialog(null, (String) loginResponse, "User Login", JOptionPane.INFORMATION_MESSAGE);
        }

        return 0;
    }

    
    /** 
     * Method that deals with register
     * 
     * @param username
     * @param password
     * @return int
     */
    public int userRegister(String username, String password) {
        String registrationRequest = String.format("Req2: %s: %s", username, password);
        String checkUsernameResponse;
        Object registrationResponse;

        checkUsernameResponse = (String) sendRequest(registrationRequest); // send checking request
        if (checkUsernameResponse.split(": ")[0].equals("Res2")) { // if checking passed
            Account newAccount = new Account(username, password);
            Profile blankProfile = new Profile("", newAccount, "", "", "", new String[0]);
            registrationResponse = sendRequest(blankProfile); // send creating account request

            if (registrationResponse instanceof Profile) { // success
                JOptionPane.showMessageDialog(null, "Successfully created a new account", "User Registration",
                    JOptionPane.INFORMATION_MESSAGE);
                return 1;
            } else {
                JOptionPane.showMessageDialog(null, (String) registrationResponse, "User Registration",
                            JOptionPane.ERROR_MESSAGE); // failed
                return 0;
            }
        } else if (((String) (checkUsernameResponse)).split(": ")[0].equals("E2")) { // check failed
            JOptionPane.showMessageDialog(null, "The username already exists.", "User Registration",
                    JOptionPane.ERROR_MESSAGE);
        } else { // unexpected response goes here
            JOptionPane.showMessageDialog(null, (String) (checkUsernameResponse), "User Registration",
                    JOptionPane.ERROR_MESSAGE);
        }
        return 0;
    }

    
    /** 
     * main method
     * 
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ProfileClient()); // invoke later
    }

    /** 
     * initialize the network connection and show login window
     */
    public void run() {
        initializeNetwork(); // Establish network connection
        showLoginPanel(); // Show login window
    }

    /** 
     * Show login window
     */
    private void showLoginPanel() {
        JLabel userLabel;
        JLabel passwordLabel;
        JTextField userLoginText;
        JPasswordField passwordLoginText;
        JButton loginButton;
        JButton registerButton;

        loginFrame = new JFrame("User Login");
        JPanel panel = new JPanel();

        loginFrame.setSize(300, 200);
        loginFrame.setResizable(false); // cannot resize
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit the program if clicked
        loginFrame.setVisible(true);
        loginFrame.add(panel);

        panel.setLayout(null);
        // adding elements
        userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 30, 80, 25);

        userLoginText = new JTextField(15);
        userLoginText.setBounds(110, 30, 165, 25);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);

        passwordLoginText = new JPasswordField(15);
        passwordLoginText.setBounds(110, 60, 165, 25);

        loginButton = new JButton("Login");
        loginButton.setBounds(30, 110, 90, 25);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userLoginText.getText();
                String password = String.valueOf(passwordLoginText.getPassword());
                // check validity: both username and password should be alphanumeric
                // password length >= 8, other condition not checked because they are ensured during registration
                if (!username.isBlank() && username.matches("^[a-zA-Z0-9]*$")) { // check alphanumeric
                    if (!password.isBlank() && password.matches("^[a-zA-Z0-9]*$")) {
                        if (password.length() >= 8) {
                            if (userLogin(username, password) == 1) {
                                loginFrame.dispose(); // close login window
                                showMainPanel(); // show the main window
                            } else {
                                passwordLoginText.setText(""); // clear textfield for convenience
				                userLoginText.setText("");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Your password should be at least 8 characters long.",
                                                            "Login", JOptionPane.ERROR_MESSAGE);
			                passwordLoginText.setText(""); // clear textfield for convenience
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your password should be alphanumeric.",
                                                        "Login", JOptionPane.ERROR_MESSAGE);
			            passwordLoginText.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Your username should be alphanumeric.",
                                                    "Login", JOptionPane.ERROR_MESSAGE);
		            userLoginText.setText("");
                }
            }
        });

        registerButton = new JButton("Register");
        registerButton.setBounds(170, 110, 90, 25);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showRegisterPanel(); // show register window
            }
        });

        panel.add(userLabel);
        panel.add(userLoginText);
        panel.add(passwordLabel);
        panel.add(passwordLoginText);
        panel.add(loginButton);
        panel.add(registerButton);

    }

    /** 
     * Show the window for creating a new account
     */
    private void showRegisterPanel() {
        JLabel userLabel;
        JLabel passwordLabel;
        JTextField userRegistrationText;
        JPasswordField passwordRegistrationText;
        JButton registerButton;
        JButton registerCancelButton;
        
        registerFrame = new JFrame("Register");
        JPanel panel = new JPanel();

        registerFrame.setSize(300, 200);
        registerFrame.setResizable(false);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // do not exit but just close this window
        registerFrame.setVisible(true);
        registerFrame.add(panel);

        panel.setLayout(null);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 30, 80, 25);

        userRegistrationText = new JTextField(15);
        userRegistrationText.setBounds(110, 30, 165, 25);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);

        passwordRegistrationText = new JPasswordField(15);
        passwordRegistrationText.setBounds(110, 60, 165, 25);

        registerButton = new JButton("Register");
        registerButton.setBounds(30, 110, 90, 25);
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userRegistrationText.getText();
                String password = String.valueOf(passwordRegistrationText.getPassword()); // Get input password
                // Username and password should be composed of letters and numbers.
                // Username can be no more than 15 characters, and password should be 8-20 characters long.
                if (!username.isBlank() && username.length() < 16 && username.matches("^[a-zA-Z0-9]*$")) {
                    if (!password.isBlank() && password.length() < 21 && password.matches("^[a-zA-Z0-9]*$")) {
                        if (password.length() >= 8) {
                            if (userRegister(username, password) == 1) {
                                registerFrame.dispose();
                            } else {
                                userRegistrationText.setText(""); // Reset usernamefield for convenience
                                passwordRegistrationText.setText(""); // Reset passwordfield for convenience
                            }
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Your password should be at least 8 characters long.",
                                "Creating New Account", JOptionPane.ERROR_MESSAGE);
			                passwordRegistrationText.setText(""); // Reset passwordfield for convenience
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                            "Your password should be alphanumeric and no more than 21 characters.",
                            "Creating New Account", JOptionPane.ERROR_MESSAGE);
			                passwordRegistrationText.setText(""); // Reset passwordfield for convenience
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                        "Your username should be alphanumeric and no more than 15 characters.",
                        "Creating New Account", JOptionPane.ERROR_MESSAGE);
		            userRegistrationText.setText(""); // Reset usernamefield for convenience
                    passwordRegistrationText.setText(""); // Reset passwordfield for convenience
                }
            } 
        });

        registerCancelButton = new JButton("Cancel");
        registerCancelButton.setBounds(170, 110, 90, 25);
        registerCancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registerFrame.dispose(); // Close register window
            }
        });

        panel.add(userLabel);
        panel.add(userRegistrationText);
        panel.add(passwordLabel);
        panel.add(passwordRegistrationText);
        panel.add(registerButton);
        panel.add(registerCancelButton);
    }

    /** 
     * Show the main window where most of the operations can be done
     */
    private void showMainPanel() {
        JButton listAllUserButton;
        JButton backToMeButton;
        JButton friendRequestButton;
        JButton deleteProfileButton;
        JButton deleteAccountButton;
        JButton editPasswordButton;

        JLabel profileAboutMeLabel;
        JLabel profileEmailLabel;
        JLabel profileInterestsLabel;
        JLabel profileNameLabel;
        JLabel friendListTitleLabel;
        
        JScrollPane profileAboutMeScrollPanel;
        JPanel profilePanel;
        JPanel lowerRightPanel;
        JPanel lowerLeftPanel;
        JScrollPane friendListScrollPanel;

        mainFrame = new JFrame();
        JPanel panel = new JPanel();

        mainFrame.setSize(900, 700);
        mainFrame.setVisible(true);
        mainFrame.setTitle("Profile!");
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Once closed, end the program entirely

        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainFrame.setContentPane(panel);
        panel.setLayout(null);

        JPanel upperLeftPanel = new JPanel();
        upperLeftPanel.setBorder(UIManager.getBorder("ScrollPane.border")); // set border
        upperLeftPanel.setBounds(10, 10, 285, 130); // set position and size
        panel.add(upperLeftPanel);
        upperLeftPanel.setLayout(null);

        JLabel myNameLabel = new JLabel(myProfile.getAccount().getUsername());
        myNameLabel.setFont(new Font("Arial", Font.BOLD, 18)); // set font style
        myNameLabel.setHorizontalAlignment(SwingConstants.CENTER); // set alignment
		myNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // set alignment
        myNameLabel.setBounds(55, 10, 175, 40);
        upperLeftPanel.add(myNameLabel);

        deleteProfileButton = new JButton("Delete Profile");
        deleteProfileButton.setBounds(155, 60, 120, 23);
        deleteProfileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Do you want to delete your profile?",
                                                            "Profile - Delete Profile", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // If yes, create a black profile with same account and replace.
                    Profile blankProfile = new Profile("", myProfile.getAccount(), "", "", "", new String[0]);
                    Object deleteProfileResponse = sendRequest(blankProfile);
                    if (deleteProfileResponse instanceof Profile) {
                        JOptionPane.showMessageDialog(null, "Successfully deleted your profile",
                                                        "Profile - Delete Profile",
                                                        JOptionPane.INFORMATION_MESSAGE);
                        myProfile = (Profile) deleteProfileResponse;
                        loadInfo(myProfile); // load the response profile
                    } else {
                        JOptionPane.showMessageDialog(null, (String) deleteProfileResponse,
                            "Profile - Delete Profile", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
		upperLeftPanel.add(deleteProfileButton);
		
		deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setBounds(10, 97, 265, 23);
        deleteAccountButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(null, "Do you want to delete your account?",
                                                            "Profile - Delete Account", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    JPasswordField password = new JPasswordField(); // Require password to confirm operation.
                    Object[] passwordField = {"You are trying to delete your account. " + 
                                                "Enter your password to confirm.", password};
                    int result = JOptionPane.showConfirmDialog(null, passwordField,
                                                                "Profile - Delete Account",
                                                                JOptionPane.OK_CANCEL_OPTION,
                                                                JOptionPane.WARNING_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) { // if confirmed
                        if (String.valueOf(password.getPassword()).equals(myProfile.getAccount().getPassword())) {
                            int secondChoice = JOptionPane.showConfirmDialog(null, "After deleting your account, " + 
                                                    "you can no longer login with this account. Please confirm.",
                                                    "Profile - Delete Account", JOptionPane.YES_NO_OPTION);
                            // second confirm with password verification
                            if (secondChoice == JOptionPane.YES_OPTION) {
                                String response = (String) sendRequest("Req4: " + myProfile.getAccount().getUsername());
                                if (response.split(": ")[0].equals("Res4")) {
                                    // Account deleted, and there's force quit.
                                    mainFrame.dispose();
                                    JOptionPane.showMessageDialog(null, "Successfully Deleted. You are logged out now.",
                                                                            "Profile - Delete Account",
                                                                            JOptionPane.INFORMATION_MESSAGE);
                                    System.exit(0);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Unable to proceed. Try again later.",
                                                                    "Profile - Delete Account",
                                                                    JOptionPane.INFORMATION_MESSAGE);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Wrong password",
                                                            "Profile - Delete Account", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        upperLeftPanel.add(deleteAccountButton);
        
        editPasswordButton = new JButton("Edit Password");
        editPasswordButton.setBounds(10, 60, 120, 23);
        editPasswordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPasswordField password = new JPasswordField();
                Object[] passwordField = {"Enter your current password:", password};
                int result = JOptionPane.showConfirmDialog(null, passwordField, // ask for current password
                                                            "Profile - Editing Password",
                                                            JOptionPane.OK_CANCEL_OPTION,
                                                            JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    if (String.valueOf(password.getPassword()).equals(myProfile.getAccount().getPassword())) {
                        JPasswordField newPassword = new JPasswordField(); // if match, ask for new password
                        Object[] newPasswordField = {"Enter your new password:", newPassword};
                        int result2 = JOptionPane.showConfirmDialog(null, newPasswordField,
                                                            "Profile - Editing Password",
                                                            JOptionPane.OK_CANCEL_OPTION,
                                                            JOptionPane.QUESTION_MESSAGE);
                        if (result2 == JOptionPane.OK_OPTION) {
                            String newPasswordString = String.valueOf(newPassword.getPassword());
                            if (!newPasswordString.isBlank() && newPasswordString.length() >= 8 &&
                                    newPasswordString.length() < 21 && newPasswordString.matches("^[a-zA-Z0-9]*$")) {
                                String username = myProfile.getAccount().getUsername();
                                Account newAccount = new Account(username, newPasswordString);
    
                                String name = myProfile.getName();
                                String email = myProfile.getEmail();
                                String aboutMe = myProfile.getAboutMe();
                                String likesAndInterestsText = myProfile.getLikesAndInterests();
                                String[] myFriendUserNames = myProfile.getFriendUserNames();
                                // create a profile with modified account
                                Profile tempProfile = new Profile(name, newAccount, email, aboutMe,
                                                                    likesAndInterestsText, myFriendUserNames);
                                tempProfile.setReceivedFriendRequests(myProfile.getReceivedFriendRequests());
                                tempProfile.setSentFriendRequests(myProfile.getSentFriendRequests());
                                // send new profile for update
                                Object response = sendRequest(tempProfile);
    
                                if (response instanceof Profile) {
                                    JOptionPane.showMessageDialog(null, "Successfully Updated", "Profile",
                                                                    JOptionPane.INFORMATION_MESSAGE);
                                    myProfile = (Profile) response; // set response to my profile
                                } else {
                                    JOptionPane.showMessageDialog(null, (String) response, "Profile",
                                                                    JOptionPane.ERROR_MESSAGE);
                                }
                            } else {
                                JOptionPane.showMessageDialog(null,
                                    "Your password should be 8 to 20 characters long and be alphanumeric.",
                                    "Editing Passowrd", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Wrong password",
                                                            "Profile", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
		upperLeftPanel.add(editPasswordButton);

        lowerLeftPanel = new JPanel();
        lowerLeftPanel.setBorder(UIManager.getBorder("ScrollPane.border"));
        lowerLeftPanel.setBounds(10, 550, 285, 111);
        panel.add(lowerLeftPanel);
        lowerLeftPanel.setLayout(null);

        listAllUserButton = new JButton("List Users");
        listAllUserButton.setFont(new Font("Arial", Font.BOLD, 12));
        listAllUserButton.setBounds(145, 20, 95, 30);
        listAllUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showListAllUserPanel(); // show list all user window
            }
        });
        lowerLeftPanel.add(listAllUserButton);

        backToMeButton = new JButton("My Profile");
        backToMeButton.setFont(new Font("Arial", Font.BOLD, 12));
        backToMeButton.setBounds(29, 20, 95, 30);
        backToMeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadInfo(myProfile); // load my profile
                profileSaveButton.setVisible(true);
                profileCancelButton.setVisible(true);
                updateUI();
            }
        });
        lowerLeftPanel.add(backToMeButton);

        friendRequestButton = new JButton("Requests");
        friendRequestButton.setFont(new Font("Arial", Font.BOLD, 12));
        friendRequestButton.setBounds(29, 60, 95, 30);
        friendRequestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showFriendRequestPanel(); // show friend request window
            }
        });
        lowerLeftPanel.add(friendRequestButton);

        friendListScrollPanel = new JScrollPane();
        friendListScrollPanel.setBounds(10, 149, 285, 391);
        // Vertically scrollbar as need, horizontally no scrollbar
        friendListScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        friendListScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        panel.add(friendListScrollPanel);

        friendListPanel = new JPanel();
        friendListPanel.setPreferredSize(new Dimension(285, 0));
        friendListScrollPanel.setViewportView(friendListPanel);
        friendListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        friendListTitleLabel = new JLabel("Friend List");
        friendListTitleLabel.setPreferredSize(new Dimension(0, 35));
		friendListTitleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		friendListTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		friendListTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		friendListScrollPanel.setColumnHeaderView(friendListTitleLabel);

        profilePanel = new JPanel();
        profilePanel.setBorder(UIManager.getBorder("ScrollPane.border"));
        profilePanel.setBounds(305, 70, 579, 500);
        panel.add(profilePanel);
        profilePanel.setLayout(null);

        profileUsernameLabel = new JLabel("");
		profileUsernameLabel.setBounds(325, 29, 200, 15);
		profilePanel.add(profileUsernameLabel);

        profileNameLabel = new JLabel("Name: ");
        profileNameLabel.setBounds(10, 29, 54, 15);
        profilePanel.add(profileNameLabel);

        profileEmailLabel = new JLabel("Email: ");
        profileEmailLabel.setBounds(10, 68, 54, 15);
        profilePanel.add(profileEmailLabel);

        profileAboutMeScrollPanel = new JScrollPane();
		profileAboutMeScrollPanel.setBounds(20, 135, 535, 100);
		profilePanel.add(profileAboutMeScrollPanel);

        profileAboutMeLabel = new JLabel("About Me: ");
        profileAboutMeLabel.setBounds(10, 110, 80, 15);
        profilePanel.add(profileAboutMeLabel);

        profileNameText = new JTextField();
        profileNameText.setBounds(90, 26, 120, 21);
        profilePanel.add(profileNameText);
        profileNameText.setColumns(10);

        profileEmailText = new JTextField();
        profileEmailText.setBounds(90, 65, 200, 21);
        profilePanel.add(profileEmailText);
        profileEmailText.setColumns(10);

        profileAboutMeArea = new JTextArea();
		profileAboutMeArea.setLineWrap(true); // auto line breaks
		profileAboutMeScrollPanel.setViewportView(profileAboutMeArea);

        profileInterestsLabel = new JLabel("Likes & Interests: ");
        profileInterestsLabel.setBounds(10, 277, 163, 15);
        profilePanel.add(profileInterestsLabel);

        profileLikesAndInterestsText = new JTextField();
        profileLikesAndInterestsText.setBounds(24, 302, 531, 21);
        profilePanel.add(profileLikesAndInterestsText);
        profileLikesAndInterestsText.setColumns(10);

        profileAddFriendButton = new JButton("Add Friend");
        profileAddFriendButton.setVisible(false);
        profileAddFriendButton.setBounds(462, 443, 110, 23);
        profileAddFriendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = myProfile.getAccount().getUsername();
                String targetUsername = currentProfile.getAccount().getUsername();
                // Send request to the server: Req6: username1, username 2
                String request = String.format("Req6: %s: %s", username, targetUsername);
                String response;
            
                response = (String) sendRequest(request);

                if (response.split(": ")[0].equals("Res6")) {
                    JOptionPane.showMessageDialog(null, "Request Sent",
                                                    "Friend Request", JOptionPane.INFORMATION_MESSAGE);
                    updateMyProfile();
                } else {
                    JOptionPane.showMessageDialog(null, response.split(": ")[2],
                                                    "Friend Request", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        profilePanel.add(profileAddFriendButton);

        lowerRightPanel = new JPanel();
        lowerRightPanel.setBounds(305, 580, 579, 81);
        panel.add(lowerRightPanel);
        lowerRightPanel.setLayout(null);

        profileCancelButton = new JButton("Cancel");
        profileCancelButton.setBounds(476, 32, 93, 23);
        profileCancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadInfo(myProfile);
                profileSaveButton.setVisible(false);
                profileCancelButton.setVisible(false);
                updateUI();
            }
        });
        lowerRightPanel.add(profileCancelButton);

        profileSaveButton = new JButton("Save");
        profileSaveButton.setBounds(362, 32, 93, 23);
        profileSaveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a new profile with currently input data and replace to update
                String name = profileNameText.getText();
                Account myAccount = myProfile.getAccount();
                String email = profileEmailText.getText();
                String aboutMe = profileAboutMeArea.getText();
                String likesAndInterestsText = profileLikesAndInterestsText.getText();
                String[] myFriendUserNames = myProfile.getFriendUserNames();
                Profile tempProfile = new Profile(name, myAccount, email, aboutMe,
                                                    likesAndInterestsText, myFriendUserNames);
                tempProfile.setReceivedFriendRequests(myProfile.getReceivedFriendRequests());
                tempProfile.setSentFriendRequests(myProfile.getSentFriendRequests());

                Object response = sendRequest(tempProfile);

                if (response instanceof Profile) {
                    JOptionPane.showMessageDialog(null, "Successfully Saved", "Profile",
                                                    JOptionPane.INFORMATION_MESSAGE);
                    myProfile = (Profile) response;
                } else {
                    JOptionPane.showMessageDialog(null, (String) response, "Profile", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        lowerRightPanel.add(profileSaveButton);

        loadInfo(myProfile);
        // Set a timer to automatically pull the latest Profile
        Timer timer = new Timer(500, new ActionListener() {
            public void actionPerformed(ActionEvent e) {                    
                updateMyProfile();
                panel.updateUI();
                friendListPanel.updateUI();
                if (isConnectionLost()) {
                    JOptionPane.showMessageDialog(null, "Connection Lost. Exiting...", "Profile",
                                                        JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }
        });
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.setInitialDelay(0);
        timer.start();
    }

    /** 
     * Show the window where all users are listed
     */
    private void showListAllUserPanel() {
        listAllUserFrame = new JFrame();
        JPanel panel = new JPanel();

        listAllUserFrame.setTitle("Profile - List All Users");
		listAllUserFrame.setResizable(false);
		listAllUserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        listAllUserFrame.setBounds(100, 100, 600, 400);
        listAllUserFrame.setVisible(true);
		
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		listAllUserFrame.setContentPane(panel);
		
		JPanel listAllUserUpperPanel = new JPanel();
		listAllUserUpperPanel.setPreferredSize(new Dimension(50, 30));
		panel.add(listAllUserUpperPanel, BorderLayout.NORTH);
		
		JLabel listAllUserTitleLabel = new JLabel("All Users");
		listAllUserTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
		listAllUserUpperPanel.add(listAllUserTitleLabel);
		
        JScrollPane listAllUserMainScrollPanel = new JScrollPane();
        listAllUserMainScrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listAllUserMainScrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		panel.add(listAllUserMainScrollPanel, BorderLayout.CENTER);
		
		JPanel listAllUserMainPanel = new JPanel();
		listAllUserMainPanel.setPreferredSize(new Dimension(580, 0));
		listAllUserMainPanel.setMaximumSize(new Dimension(600, 32767));
        listAllUserMainScrollPanel.setViewportView(listAllUserMainPanel);

        // To update
        listAllUserMainPanel.removeAll(); // remove all buttons first
        String[] userList = requestUserList(); // request list
        for (String username : userList) {
            addUsernameButton(username, listAllUserMainPanel); // add the button
        }
        listAllUserMainPanel.updateUI(); // update the GUI

    }

    /** 
     * show the window where one can view sent or received friend requests
     */
    private void showFriendRequestPanel() {
        friendRequestFrame = new JFrame();
        JPanel panel = new JPanel();

        friendRequestFrame.setTitle("Profile - Friend Request");
		friendRequestFrame.setResizable(false);
		friendRequestFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        friendRequestFrame.setBounds(100, 100, 320, 530);
        friendRequestFrame.setVisible(true);

		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		friendRequestFrame.setContentPane(panel);
		
		JPanel friendRequestUpperPanel = new JPanel();
        panel.add(friendRequestUpperPanel, BorderLayout.NORTH);
        
        JScrollPane friendRequestScrollPanel = new JScrollPane();
		panel.add(friendRequestScrollPanel, BorderLayout.CENTER);
		
		JPanel friendRequestPanel = new JPanel();
		friendRequestPanel.setPreferredSize(new Dimension(250, 0));
		friendRequestScrollPanel.setViewportView(friendRequestPanel);
		
		JButton friendRequestSentRequestButton = new JButton("Sent Request");
        friendRequestSentRequestButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {   
                // set a timer to automatically update the latest data
                Timer timer = new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        friendRequestPanel.removeAll();
                        friendRequestPanel.updateUI();
                        for (FriendRequest sentRequest : myProfile.getSentFriendRequests()) {
                            int status = sentRequest.getStatus();
                            String statusString;
                            if (status == 0) {
                                statusString = "Pending";
                            } else if (status == 1) {
                                statusString = "Accepted";
                            } else {
                                statusString = "Rejected";
                            }
                            String record = String.format("%s: %s", sentRequest.getUsernameWhoReceive(), statusString);
                            JLabel labelToAdd = new JLabel(record);
                            
                            labelToAdd.setPreferredSize(new Dimension(250, 25));
                            friendRequestPanel.add(labelToAdd);
                        }
                        friendRequestPanel.updateUI();
                    }
                });
                timer.setRepeats(true);
                timer.setCoalesce(true);
                timer.setInitialDelay(0);
                timer.start();
            }
        });
        friendRequestUpperPanel.add(friendRequestSentRequestButton);
		
		JButton friendRequestReceivedRequestButton = new JButton("Received Request");
        friendRequestReceivedRequestButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                // set a timer to automatically update the latest data
                Timer timer = new Timer(500, new ActionListener() {
                    public void actionPerformed(ActionEvent e) {     
                        friendRequestPanel.removeAll();
                        friendRequestPanel.updateUI();
                        for (FriendRequest receivedRequest : myProfile.getReceivedFriendRequests()) {
                            String record = receivedRequest.getUsernameWhoSent();
                            JButton buttonToAdd = new JButton(record); // define button with username
                            buttonToAdd.setPreferredSize(new Dimension(250, 25));
                            buttonToAdd.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent e) {
                                    int choice = JOptionPane.showConfirmDialog(null,
                                                            "Do you want to accept the friend request?",
                                                            "Friend Request",
                                                            JOptionPane.YES_NO_CANCEL_OPTION,
                                                            JOptionPane.QUESTION_MESSAGE);
                                    String username = myProfile.getAccount().getUsername();
                                    String request;
                                    Profile response;
                                    
                                    if (choice == JOptionPane.YES_OPTION) {
                                        // request: Req7: username, username
                                        request = String.format("Req7: %s: %s", record, username);
                                        response = (Profile) sendRequest(request);
                                        myProfile = response;
                                        loadInfo(myProfile);
                                        // disable the button and show the result temporarily
                                        buttonToAdd.setEnabled(false);
                                        buttonToAdd.setText("Accepted:" + buttonToAdd.getText());
                                    } else if (choice == JOptionPane.NO_OPTION) {
                                        request = String.format("Req8: %s: %s", record, username);
                                        response = (Profile) sendRequest(request);
                                        myProfile = response;
                                        loadInfo(myProfile);
                                        // disable the button and show the result temporarily
                                        buttonToAdd.setEnabled(false);
                                        buttonToAdd.setText("Refused:" + buttonToAdd.getText());
                                    }
                                }
                            });
                            friendRequestPanel.add(buttonToAdd);
                            friendRequestPanel.updateUI();
                        }
                    }
                });
                timer.setRepeats(true);
                timer.setCoalesce(true);
                timer.setInitialDelay(0);
                timer.start();
            }
        });
        friendRequestUpperPanel.add(friendRequestReceivedRequestButton);
    }
    
    /** 
     * method for getting all users
     * 
     * @return String[]
     */
    private String[] requestUserList() {
        String request = "Req9: Request all users";
        String[] response = new String[0];
        response = ((String) sendRequest(request)).split(",");
        return response;
    }
    
    /** 
     * method for adding username buttons to specified panel
     * 
     * @param username
     * @param targetPanel
     */
    private void addUsernameButton(String username, JPanel targetPanel) {
        // skip if username is the logged in user
        if (username.equals(myProfile.getAccount().getUsername())) {
            return;
        }

        JButton buttonToAdd = new JButton(username);
        buttonToAdd.setPreferredSize(new Dimension(125, 25));
        buttonToAdd.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                String request = "Req10: " + username;
                Object response = sendRequest(request);
                
                if (response instanceof Profile) {
                    profileAddFriendButton.setVisible(true);
                    profileSaveButton.setVisible(false);
                    profileCancelButton.setVisible(false);
                    loadInfo((Profile) response);

                    for (int i = 0; i < myProfile.getFriendUserNames().length; i++) {
                        if (myProfile.getFriendUserNames()[i].equals(username)) {
                            profileAddFriendButton.setVisible(false);
                            profileSaveButton.setVisible(false);
                            profileCancelButton.setVisible(false);
                        }
                    }
                } else { // if account is deleted
                    JOptionPane.showMessageDialog(null, "Deleted Account", "Profile",
                                                    JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        targetPanel.add(buttonToAdd);
        resizePanel(targetPanel);
        updateUI();
    }
    
    /** 
     * resize the panel to allow scroll panel to work properly
     * 
     * @param targetPanel
     */
    private void resizePanel(JPanel targetPanel) {
        int numberOfComponents = targetPanel.getComponentCount(); // get number of buttons
        int height;
        if (targetPanel.equals(friendListPanel)) {
            height = (numberOfComponents / 2) * (30) + 10; // 2 col, each row is 30, plus 10 redundancy
        } else {
            height = (numberOfComponents / 4) * 30 + 10;
        }
        // update the size and UI
        targetPanel.setPreferredSize(new Dimension(0, height)); 
        updateUI();
    }

    /** 
     * try to connect to the server and setup the global writer and reader
     */
    private void initializeNetwork() {
        String initializationRequest = "Req0: Initialization";
        String initializationResponse;
        try {
            socket = new Socket(hostName, portNumber);

            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            initializationResponse = (String) sendRequest(initializationRequest); // send reqeust for initialization
            // Res0: xxxxxx   Rep0: xxxxx
            if (((String) (initializationResponse)).split(": ")[0].equals("E0")) {
                throw new IOException();
            } else if (((String) (initializationResponse)).split(": ")[0].equals("Res0")) {
                JOptionPane.showMessageDialog(null, "Successfully connected to the server!", "Connection Established",
                        JOptionPane.INFORMATION_MESSAGE);
            } else { // Unexpected goes here
                JOptionPane.showMessageDialog(null, "Unexpected Error. Please try again.",
                                                "Profile", JOptionPane.ERROR_MESSAGE);
                System.exit(0); // force quit since its a unexpected issue
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connecting to the server failed. Please check you internet connection",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // force quit since its a connection issue
        }
    }
    
    /** 
     * method that will handle request sending and response receiving. Object in, Object out
     * Object can either be String for requests or Profile for updates
     * 
     * @param request
     * @return Object
     */
    private Object sendRequest(Object request) {
        Object response;
        try {
            oos.writeObject(request); // send the request
            oos.flush();
            response = ois.readObject(); // read the response
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed. Quiting...",
                                            "Profile", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // force quit since its a connection issue
            response = "Unknown Host";
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed. Quiting...",
                                            "Profile", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // force quit since its a connection issue
            response = "Connection Failed";
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Fatal error on server: Class Not Found. Quiting...",
                                            "Profile", JOptionPane.ERROR_MESSAGE);
            response = "Class Not Found";
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "Connection Failed. Quiting...",
                                            "Profile", JOptionPane.ERROR_MESSAGE);
            System.exit(0); // force quit since its a connection issue
            response = "Connection Failed";
        }
        return response;
    }
    
    /**
     * load a Profile object to the GUI main window
     *  
     * @param profile
     */
    private void loadInfo(Profile profile) {
        currentProfile = profile;
        profileUsernameLabel.setText("User: " + profile.getAccount().getUsername());
        profileNameText.setText(profile.getName());
        profileEmailText.setText(profile.getEmail());
        profileAboutMeArea.setText(profile.getAboutMe());

        profileLikesAndInterestsText.setText(profile.getLikesAndInterests());
        // if personal profile save and cancel button are visible, else they are not shown
        if (currentProfile.getAccount().getUsername().equals(myProfile.getAccount().getUsername())) {
            profileAddFriendButton.setVisible(false);
            profileSaveButton.setVisible(true);
            profileCancelButton.setVisible(true);
            // prepare for friendlist update
            friendListPanel.removeAll();
            friendListPanel.updateUI();
            // none to display since this is "my account"
            profileUsernameLabel.setText("");
            // add friends button to friendlist panel
            for (String friendUsername : myProfile.getFriendUserNames()) {
                addUsernameButton(friendUsername, friendListPanel);
                friendListPanel.updateUI();
            }
        }
    }

    /** 
     * update the profile of logged in user
     */
    private void updateMyProfile() {
        String request = "Req10: " + myProfile.getAccount().getUsername();
        Object response = sendRequest(request);
        if (response instanceof Profile) {
            myProfile = (Profile) response;
        }
        friendListPanel.removeAll();
        friendListPanel.updateUI();
        for (String friendUsername : myProfile.getFriendUserNames()) {
            addUsernameButton(friendUsername, friendListPanel);
        }
    }
    
    /** 
     * check socket connection
     * 
     * @return boolean
     */
    private boolean isConnectionLost() {
        return !socket.isConnected();
    }
}
