import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ProfileClient extends JComponent implements Runnable {
    /**
     *
     */
    private static final long serialVersionUID = 5597188023823293381L;
    
    Boolean loggedIn = false;
    Socket socket;
    String hostName = "localhost";
    int portNumber = 6868;

    JFrame loginFrame;
    JFrame registerFrame;
    JFrame mainFrame;
    JFrame listAllUserFrame;
    JFrame friendRequestFrame;
    JFrame requestHistoryFrame;

    // BufferedReader reader;
    // PrintWriter writer;
    ObjectOutputStream oos;
    ObjectInputStream ois;

    JLabel userLabel;
    JLabel passwordLabel;
    JTextField userText;
    JPasswordField passwordText;
    JButton loginButton;
    JButton registerButton;
    JButton registerButton2;
    JButton registerCancelButton;
    JButton listAllUserButton;
    JButton backToMeButton;
    JPanel friendListPanel;
    JScrollPane friendListScrollPanel;
    JButton friendsButton;
    JPanel lowerLeftPanel;
    JLabel myNameLabel;
    JLabel phoneLabel;
    JLabel profileAboutMeLabel;
    JButton profileAddFriendButton;
    JButton profileCancelButton;
    JLabel profileEmailLabel;
    JTextField profileEmailText;
    JLabel profileInterestsLabel;
    JTextField profileLikesAndInterestsText;
    JLabel profileNameLabel;
    JTextField profileNameText;
    JPanel upperLeftPanel;
    JTextField profilePhoneText;
    JButton profileSaveButton;
    JPanel profilePanel;
    JTextArea profileAboutMeArea;
    JPanel lowerRightPanel;
    JScrollPane profileAboutMeScrollPanel;

    ProfileClient profileClient;
    Profile currentProfile;
    // final Profile myProfile = profileA;

    

    ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == loginButton) {
                String username = userText.getText();
                String password = String.valueOf(passwordText.getPassword());
                
                if (!username.isBlank() && username.matches("^[a-zA-Z0-9]*$")) {
                    if (!password.isBlank() && password.matches("^[a-zA-Z0-9]*$")) {
                        if (password.length() >= 8) {
                            if (userLogin(username, password) == 1) {
                                loginFrame.dispose();
                                loggedIn = true;
                                showMainPanel();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Your password should be at least 8 characters long.", "Login", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your password should be alphanumeric.", "Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Your username should be alphanumeric.", "Login", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (e.getSource() == registerButton) {
                showRegisterPanel();
            }

            if (e.getSource() == registerButton2) {
                String username = userText.getText();
                String password = String.valueOf(passwordText.getPassword());

                if (!username.isBlank() && username.matches("^[a-zA-Z0-9]*$")) {
                    if (!password.isBlank() && password.matches("^[a-zA-Z0-9]*$")) {
                        if (password.length() >= 8) {
                            if (userRegister(username, password) == 1) {
                                registerFrame.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Your password should be at least 8 characters long.", "Login", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Your password should be alphanumeric.", "Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Your username should be alphanumeric.", "Login", JOptionPane.ERROR_MESSAGE);
                }

            }

            if (e.getSource() == registerCancelButton) {
                registerFrame.dispose();
            }

            if (e.getSource() == listAllUserButton) {
                showListAllUserPanel();
                // showFriendRequestPanel();
            }

            if (e.getSource() == backToMeButton) {
                loadInfo(myProfile);
                profileSaveButton.setVisible(true);
                profileCancelButton.setVisible(true);
                updateUI();
            }

            if (e.getSource() == profileCancelButton) {
                loadInfo(myProfile);
                profileSaveButton.setVisible(false);
                profileCancelButton.setVisible(false);
                updateUI();
            }

            if (e.getSource() == profileSaveButton) {
                String name = profileNameText.getText();
                Account myAccount = myProfile.getAccount();
                String email = profileEmailText.getText();
                String aboutMe = profileAboutMeArea.getText();
                ArrayList<String> likesAndInterestsText = new ArrayList<>(Arrays.asList(profileLikesAndInterestsText.getText().split(", ")));
                ArrayList<String> myFriendUserNames = myProfile.getFriendUserNames();
                Profile tempProfile = new Profile(name, myAccount, email, aboutMe, likesAndInterestsText, myFriendUserNames);

                if (((String) sendRequest(tempProfile)).split(": ")[0].equals("Res5")) {
                    JOptionPane.showMessageDialog(null, "Successfully Saved", "Profile", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, ((String) sendRequest(tempProfile)).split(": ")[1], "Profile", JOptionPane.ERROR_MESSAGE);
                }

                myProfile = tempProfile;
            }

        }
    };

    public int userLogin(String username, String password) {
        String loginRequest = String.format("Req1: %s: %s", username, password);
        Object loginResponse;
        
        loginResponse = sendRequest(loginRequest);
        if (loginResponse instanceof Profile) {
            JOptionPane.showMessageDialog(null, "Login Successfully", "User Login", JOptionPane.INFORMATION_MESSAGE);
            myProfile = (Profile) loginResponse;
            return 1;
        } else if (((String) loginResponse).split(": ")[0].equals("E1")) {
            JOptionPane.showMessageDialog(null, "Login Failed", "User Login", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, (String) loginResponse, "User Login", JOptionPane.INFORMATION_MESSAGE);
        }

        return 0;

    }

    public int userRegister(String username, String password) {
        String registrationRequest = String.format("Req2: %s: %s", username, password);
        String checkUsernameResponse;
        Object registrationResponse;

        checkUsernameResponse = (String) sendRequest(registrationRequest);
        if (((String) (checkUsernameResponse)).split(": ")[0].equals("Res2")) {
            // JOptionPane.showMessageDialog(null, "Registered Successfully!", "User Login",
            //         JOptionPane.INFORMATION_MESSAGE);
            Account newAccount = new Account(username, password);
            Profile blankProfile = new Profile("", newAccount, "", "", new ArrayList<String>(), new ArrayList<String>());
            registrationResponse = sendRequest(blankProfile);

            if (registrationResponse instanceof Profile) {
                JOptionPane.showMessageDialog(null, "Successfully created a new account", "User Login",
                    JOptionPane.INFORMATION_MESSAGE);
                return 1;
            } else {
                JOptionPane.showMessageDialog(null, (String) registrationResponse, "User Login",
                            JOptionPane.ERROR_MESSAGE);
                return 0;
            }

        } else if (((String) (checkUsernameResponse)).split(": ")[0].equals("E2")) {
            JOptionPane.showMessageDialog(null, "The username already exists.", "User Login",
                    JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, (String) (checkUsernameResponse), "User Login",
                    JOptionPane.ERROR_MESSAGE);
        }

        return 0;

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new ProfileClient());
    }

    public void run() {
        initializeNetwork();

        showLoginPanel();
    }

    private void showLoginPanel() {
        loginFrame = new JFrame("User Login");
        JPanel panel = new JPanel();

        loginFrame.setSize(300, 200);
        loginFrame.setResizable(false);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setVisible(true);
        loginFrame.add(panel);

        panel.setLayout(null);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 30, 80, 25);

        userText = new JTextField(15);
        userText.setBounds(110, 30, 165, 25);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);

        passwordText = new JPasswordField(15);
        passwordText.setBounds(110, 60, 165, 25);

        loginButton = new JButton("Login");
        loginButton.setBounds(30, 110, 90, 25);
        loginButton.addActionListener(actionListener);

        registerButton = new JButton("Register");
        registerButton.setBounds(170, 110, 90, 25);
        registerButton.addActionListener(actionListener);

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(loginButton);
        panel.add(registerButton);

    }

    private void showRegisterPanel() {
        registerFrame = new JFrame("Register");
        JPanel panel = new JPanel();

        registerFrame.setSize(300, 200);
        registerFrame.setResizable(false);
        registerFrame.setLocationRelativeTo(null);
        registerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        registerFrame.setVisible(true);
        registerFrame.add(panel);

        panel.setLayout(null);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 30, 80, 25);

        userText = new JTextField(15);
        userText.setBounds(110, 30, 165, 25);

        passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 60, 80, 25);

        passwordText = new JPasswordField(15);
        passwordText.setBounds(110, 60, 165, 25);

        registerButton2 = new JButton("Register");
        registerButton2.setBounds(30, 110, 90, 25);
        registerButton2.addActionListener(actionListener);

        registerCancelButton = new JButton("Cancel");
        registerCancelButton.setBounds(170, 110, 90, 25);
        registerCancelButton.addActionListener(actionListener);

        panel.add(userLabel);
        panel.add(userText);
        panel.add(passwordLabel);
        panel.add(passwordText);
        panel.add(registerButton2);
        panel.add(registerCancelButton);
    }

    private void showMainPanel() {
        mainFrame = new JFrame();
        JPanel panel = new JPanel();

        // mainFrame.setLocationRelativeTo(null);
        mainFrame.setSize(900, 700);
        mainFrame.setVisible(true);
        mainFrame.setTitle("Profile!");
        mainFrame.setResizable(false);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        mainFrame.setContentPane(panel);
        panel.setLayout(null);

        upperLeftPanel = new JPanel();
        upperLeftPanel.setBounds(10, 10, 285, 130);
        panel.add(upperLeftPanel);
        upperLeftPanel.setLayout(null);

        myNameLabel = new JLabel("Ziyang Huang");
        myNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        myNameLabel.setBounds(73, 10, 177, 40);
        upperLeftPanel.add(myNameLabel);

        lowerLeftPanel = new JPanel();
        lowerLeftPanel.setBounds(10, 550, 285, 111);
        panel.add(lowerLeftPanel);
        lowerLeftPanel.setLayout(null);

        listAllUserButton = new JButton("List Users");
        listAllUserButton.setFont(new Font("Arial", Font.BOLD, 12));
        listAllUserButton.setBounds(145, 10, 95, 30);
        listAllUserButton.addActionListener(actionListener);
        lowerLeftPanel.add(listAllUserButton);

        backToMeButton = new JButton("My Profile");
        backToMeButton.setFont(new Font("Arial", Font.BOLD, 12));
        backToMeButton.setBounds(29, 10, 95, 30);
        backToMeButton.addActionListener(actionListener);
        lowerLeftPanel.add(backToMeButton);

        friendListScrollPanel = new JScrollPane();
        friendListScrollPanel.setBounds(10, 149, 285, 391);
        panel.add(friendListScrollPanel);

        friendListPanel = new JPanel();
        friendListPanel.setPreferredSize(new Dimension(285, 0));
        friendListScrollPanel.setViewportView(friendListPanel);
        friendListPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        profilePanel = new JPanel();
        profilePanel.setBounds(305, 70, 579, 500);
        panel.add(profilePanel);
        profilePanel.setLayout(null);

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
		profileAboutMeArea.setLineWrap(true);
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
        profileAddFriendButton.setBounds(462, 443, 93, 23);
        profileAddFriendButton.addActionListener(actionListener);
        profilePanel.add(profileAddFriendButton);

        lowerRightPanel = new JPanel();
        lowerRightPanel.setBounds(305, 580, 579, 81);
        panel.add(lowerRightPanel);
        lowerRightPanel.setLayout(null);

        profileCancelButton = new JButton("Cancel");
        profileCancelButton.setBounds(476, 32, 93, 23);
        profileCancelButton.addActionListener(actionListener);
        lowerRightPanel.add(profileCancelButton);

        profileSaveButton = new JButton("Save");
        profileSaveButton.setBounds(362, 32, 93, 23);
        profileSaveButton.addActionListener(actionListener);
        lowerRightPanel.add(profileSaveButton);

        loadInfo(myProfile);
    }

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
		panel.add(listAllUserMainScrollPanel, BorderLayout.CENTER);
		
		JPanel listAllUserMainPanel = new JPanel();
		listAllUserMainPanel.setPreferredSize(new Dimension(580, 0));
		listAllUserMainPanel.setMaximumSize(new Dimension(600, 32767));
        listAllUserMainScrollPanel.setViewportView(listAllUserMainPanel);

        String[] userList = requestUserList();
        for (String username : userList) {
            addUsernameButton(username, listAllUserMainPanel);
        }

    }

    private void showFriendRequestPanel() {
    
    }

    private String[] requestUserList() {
        String request = "Req9: Request all users";
        String[] response;
        
        response = ((String) sendRequest(request)).split(",");

        return response;
    }

    private void addUsernameButton(String username, JPanel targetPanel) {
        JButton buttonToAdd = new JButton(username);
        buttonToAdd.setPreferredSize(new Dimension(140, 25));
        buttonToAdd.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e)
            {
                String request = "Req10: " + username;
                Object response = sendRequest(request);
                
                if (response instanceof Profile) {
                    loadInfo((Profile) response);
                    // TODO: Check if is friend already
                    profileAddFriendButton.setVisible(true);
                    profileSaveButton.setVisible(false);
                    profileCancelButton.setVisible(false);
                } else {
                    // TODO: Check error response
                    JOptionPane.showMessageDialog(null, (String) response, "User Login", JOptionPane.INFORMATION_MESSAGE);
                }

                
            }

        });
        targetPanel.add(buttonToAdd);
        updateUI();
        // TODO: Resize
        // resizePanel(targetPanel);
    }

    private void resizePanel(JPanel targetPanel) {
        int numComponents = targetPanel.getComponentCount();
        // TODO: check which panel to resize
        int height = (numComponents / (targetPanel.getWidth() / (100 + 5))) * (100 + 5);  
        targetPanel.setPreferredSize(new Dimension(0, height)); 
        
        updateUI();
    }

    private void initializeNetwork() {
        String initializationRequest = "Req0: Initialization";
        String initializationResponse;
        try {
            socket = new Socket(hostName, portNumber);

            // ois = new ObjectInputStream(new
            // BufferedInputStream(socket.getInputStream()));
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));

            // writer = new PrintWriter(socket.getOutputStream());
            // reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            initializationResponse = (String) sendRequest(initializationRequest);
            if (((String) (initializationResponse)).split(": ")[0].equals("E0")) {
                throw new IOException();
            } else if (((String) (initializationResponse)).split(": ")[0].equals("Res0")) {
                JOptionPane.showMessageDialog(null, "Successfully connected to the server!", "Connection Established",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Connecting to the server failed. Please check you internet connection",
                    "Connection Failed", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

    }

    private Object sendRequest(Object request) {
        Object response;
        try {
            oos.writeObject(request);
            oos.flush();
            response = ois.readObject();

            // writer.println(request);
            // writer.flush();

            // response = reader.readLine();

        } catch (UnknownHostException e) {
            response = "E0: Unknown Host";
            e.printStackTrace();
        } catch (IOException e) {
            response = "E0: Connection Failed";
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            response = "E0: Class Not Found";
            e.printStackTrace();
        } catch (NullPointerException e) {
            response = "E0: Null Pointer Exception";
        }
        return response;
    }

    // private String[] rHandler(String r) {
    //     return r.split(": ");
    //     // [Req1: username: password] -> []
    // }

    private void loadInfo(Profile profile) {
        currentProfile = profile;
        profileNameText.setText(profile.getName());
        profileEmailText.setText(profile.getEmail());
        profileAboutMeArea.setText(profile.getAboutMe());
        profileLikesAndInterestsText.setText(profile.getLikesAndInterests().toString());
    }

    // Account accountA = new Account("unA12345", "pwA12345");
    // String name = "zyh";
    // String email = "123123123@gmail.com";
    // String aboutMe = "qwerqwerqwerwqerwqerqwerwqerqwerqwerwqerwqerwerqwerqwerqwerwqerwqerqwerwqerqwerqwerwqerwqerwerqwerqwerqwerwqerwqerqwerwqerqwerqwerwqerwqerwerqwerqwerqwerwqerwqerqwerwqerqwerqwerwqerwqerwer";
    // ArrayList<String> likesAndInterests = new ArrayList<>();

    // ArrayList<String> friendUserNames = new ArrayList<>();

    // Profile profileA = new Profile(name, accountA, email, aboutMe, likesAndInterests, friendUserNames);
    Profile myProfile;
}