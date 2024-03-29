import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Objects;

/**
 * PJ05 Option 2 - Request Handler for Server
 * 
 * Handles various requests from the server
 * 
 * @author Eashan Dubey
 * @version November 22, 2020
 */
public final class ServerRequestHandler implements Runnable {

    private final Socket clientSocket;
    // private String[] userNames;
    // private String[] onlineUsers;
    private Profile[] profiles;

    /**
     * ServerRequestHandler
     *
     * Constructor that instantiates the client socket instance object
     * 
     * @param clientSocket
     */
    public ServerRequestHandler(Socket clientSocket) {
        Objects.requireNonNull(clientSocket, "the specified client socket is null");
        this.clientSocket = clientSocket;
    } // ServerRequestHandler

    /**
     * Returns the appropriate response depending on the type of request
     *
     * @param request
     * @return Object
     */
    private synchronized Object getResponse(Object request) { // synchronized to avoid race conditions
        profiles = Server.getProfiles(); // updates ServerRequestHandler data from Server
        Object response = null;
        String req = null;
        if (request == null) {
            return "MALFORMED_REQUEST";
        } // end if

        if (request instanceof Profile) {
            Profile receivedProfile = (Profile) request;
            if (usernameIsTaken(receivedProfile.getAccount().getUsername())) {
                updateProfile(receivedProfile);
            } else {
                profiles = Arrays.copyOf(profiles, profiles.length + 1);
                profiles[profiles.length - 1] = receivedProfile;
            }
            response = receivedProfile;
        } else if (request instanceof String) {
            req = (String) request;
            String[] requestVals = req.split(":");
            switch (requestVals[0]) {
                case "Req0": {
                    response = "Res0: Connection Established";
                    break;
                }
                case "Req1": { // Login: “Req1: <username>: <password>”
                    String username = removeSpaceAtStart(requestVals[1]);
                    String password = removeSpaceAtStart(requestVals[2]);

                    if (login(username, password)) {
                        response = returnProfileFromUsername(username);
                    } else {
                        response = "E1: ERROR: Login Failed";
                    }
                    break;
                }
                case "Req2": { // Check if username is unique: “Req2: <username>”
                    String username = removeSpaceAtStart(requestVals[1]);
                    boolean usernameTaken = usernameIsTaken(username);
                    if (usernameTaken) {
                        response = "E2: ERROR: Username already exists";
                    } else {
                        response = "Res2: username is available";
                    }
                    break;
                }
                case "Req4": { // Delete user: “Req4: <username>”
                    String username = removeSpaceAtStart(requestVals[1]);
                    deleteAccount(username);
                    response = "Res4: Successfully Deleted";
                    break;
                }
                case "Req6": { // Send FriendRequest: “Req6: <username>: <targetUsername>”
                    String senderUsername = removeSpaceAtStart(requestVals[1]);
                    String recipientUsername = removeSpaceAtStart(requestVals[2]);

                    if (usersAreFriends(senderUsername, recipientUsername).equals("true")) {
                        response = "E61: ERROR: Users are already friends";
                        break;
                    } else if (usersAreFriends(senderUsername, recipientUsername).equals("Invalid Username")) {
                        response = "E62: ERROR: Invalid username";
                        break;
                    } else if (!friendRequestAlreadyExists(senderUsername, recipientUsername)) {
                        if (sendFriendRequest(senderUsername, recipientUsername)) {
                            response = "Res6: Request Sent";
                        }
                    } else if (friendRequestAlreadyExists(senderUsername, recipientUsername)) {
                        response = "E63: ERROR: Friend request already sent";
                    }
                    break;
                }
                case "Req7": { // Accept FriendRequest: “Req7: <username>: <targetUsername>”
                    String senderUsername = removeSpaceAtStart(requestVals[1]);
                    String recipientUsername = removeSpaceAtStart(requestVals[2]);
                    acceptFriendRequest(senderUsername, recipientUsername);

                    // response is the profile of the recipient user / user that accepts request
                    response = returnProfileFromUsername(recipientUsername);
                    break;
                }
                case "Req8": { // Refuse Friend Request: “Req8: <username>: <targetUsername>”
                    String senderUsername = removeSpaceAtStart(requestVals[1]);
                    String recipientUsername = removeSpaceAtStart(requestVals[2]);
                    rejectFriendRequest(senderUsername, recipientUsername);

                    // response is the profile of the recipient user / user that refuses request
                    response = returnProfileFromUsername(recipientUsername);
                    break;
                }
                case "Req9": { // Get all profile usernames: “Req9: ”
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < profiles.length; i++) {
                        sb.append(profiles[i].getAccount().getUsername());
                        sb.append(",");
                    }

                    response = sb.toString();
                    break;
                }
                case "Req10": { // Refresh / resend Profile Data “Req10: <username>”
                    String username = removeSpaceAtStart(requestVals[1]);
                    response = returnProfileFromUsername(username);
                    break;
                }
                default: {
                    response = "invalid request";
                }
            }
        }
        Server.setProfiles(profiles); // updates server with ServerRequestHandler Data
        Server.writeProfilesToFile("serverData.txt");
        // all responses are either Profile or String Objects
        return response;
    } // getResponse

    /**
     * Serves the request made by the client connected to this request handler's
     * client socket by calling the getResponse method and sending back that response.
     */
    @Override
    public void run() {
        Object request;
        Object response;

        try (var inputStream = this.clientSocket.getInputStream();
             var reader = new ObjectInputStream(inputStream);
             var outputStream = this.clientSocket.getOutputStream();
             var writer = new ObjectOutputStream(outputStream)) {
            while (true) {
                request = reader.readObject();
                response = this.getResponse(request);

                writer.writeUnshared(response);
                writer.flush();
                writer.reset();
            }
        } catch (SocketException e) {
            String ipAddress = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Connection Lost from: " + ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } // end try catch
    } // run

    /**
     * Verifies credentials and returns a boolean accordingly.
     *
     * @param username
     * @param password
     * @return boolean
     */
    private boolean login(String username, String password) {
        // check credentials from arraylist, return user details if correct
        boolean login = false;
        String profilePassword = null;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(username)) {
                login = true;
                profilePassword = profiles[i].getAccount().getPassword();
            }
        }
        if (login) {
            login = false;
            if (profilePassword.equals(password)) {
                login = true;
            }
        }
        return login;
    }

    /**
     * Finds profile object through unique identifier (username),
     * then returns its values accordingly.
     *
     * @param username
     * @return Profile
     */
    private Profile returnProfileFromUsername(String username) {
        Profile profile = null;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(username)) {
                profile = profiles[i];
            }
        }
        return profile;
    }

    /**
     * Replaces the object in the profiles array of the same username with
     * the parameter profile object.
     * 
     * @param profileToUpdate
     */
    private void updateProfile(Profile profileToUpdate) {
        String username = profileToUpdate.getAccount().getUsername();
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(username)) {
                // profiles.set(i, profileToUpdate);
                profiles[i] = profileToUpdate;
            }
        }
    }

    /**
     * Takes in a String username, returns boolean according to availability
     * 
     * @param username
     * @return boolean
     */
    private boolean usernameIsTaken(String username) {
        boolean usernameExists = false;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(username)) {
                usernameExists = true;
            }
        }
        return usernameExists;
    }

    /**
     * Deletes profile according to username String parameter
     * 
     * @param username
     */
    private void deleteAccount(String username) {
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(username)) {
                profiles[i] = profiles[profiles.length - 1];
                profiles = Arrays.copyOf(profiles, profiles.length - 1);
            }
        }

        for (Profile profile : profiles) {
            for (int i = 0; i < profile.getFriendUserNames().length; i++) {
                if (profile.getFriendUserNames()[i].equals(username)) {
                    String[] tempFriendUsernames = Arrays.copyOf(profile.getFriendUserNames(),
                            profile.getFriendUserNames().length);
                    tempFriendUsernames[i] = tempFriendUsernames[tempFriendUsernames.length - 1];
                    profile.setFriendUserNames(Arrays.copyOf(tempFriendUsernames,
                            tempFriendUsernames.length - 1));


                }
            }

            for (int i = 0; i < profile.getReceivedFriendRequests().length; i++) {
                if (profile.getReceivedFriendRequests()[i].getUsernameWhoSent().equals(username)) {
                    FriendRequest[] tempReceivedFriendRequests = Arrays.copyOf(profile.getReceivedFriendRequests(),
                            profile.getReceivedFriendRequests().length);
                    tempReceivedFriendRequests[i] = tempReceivedFriendRequests[tempReceivedFriendRequests.length - 1];
                    profile.setReceivedFriendRequests(Arrays.copyOf(tempReceivedFriendRequests,
                            tempReceivedFriendRequests.length - 1));
                }
            }
        }
    }

    /**
     * Deletes friendRequest from sender & recipient records
     * adds users to both users' friend lists respectively
     *
     * @param senderUsername
     * @param recipientUsername
     */
    private void acceptFriendRequest(String senderUsername, String recipientUsername) {
        FriendRequest[] senderSentRequests;
        FriendRequest[] recipientReceivedRequests;

        int senderIndex = -1;
        int recipientIndex = -1;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(senderUsername)) {
                senderIndex = i;
            }
            if (profiles[i].getAccount().getUsername().equals(recipientUsername)) {
                recipientIndex = i;
            }
            if ((senderIndex != -1) && (recipientIndex != -1)) {
                break;
            }
        }
        senderSentRequests = profiles[senderIndex].getSentFriendRequests();
        for (int i = 0; i < senderSentRequests.length; i++) {
            if (senderSentRequests[i].usernameWhoReceive.equals(recipientUsername)) {
                senderSentRequests[i].setStatus(1);
                senderSentRequests[i] = senderSentRequests[senderSentRequests.length - 1];
                senderSentRequests = Arrays.copyOf(senderSentRequests, senderSentRequests.length - 1);
            }
        }
        profiles[senderIndex].setSentFriendRequests(senderSentRequests);

        recipientReceivedRequests = profiles[recipientIndex].getReceivedFriendRequests();
        for (int i = 0; i < recipientReceivedRequests.length; i++) {
            if (recipientReceivedRequests[i].usernameWhoSent.equals(senderUsername)) {
                recipientReceivedRequests[i].setStatus(1);
                recipientReceivedRequests[i] = recipientReceivedRequests[recipientReceivedRequests.length - 1];
                recipientReceivedRequests = Arrays.copyOf(recipientReceivedRequests,
                        recipientReceivedRequests.length - 1);
            }
        }
        profiles[recipientIndex].setReceivedFriendRequests(recipientReceivedRequests);

        profiles[senderIndex].addToFriendUsernames(recipientUsername);
        profiles[recipientIndex].addToFriendUsernames(senderUsername);
    }

    /**
     * Deletes friendRequest from sender & recipient records
     *
     * @param senderUsername
     * @param recipientUsername
     */
    private void rejectFriendRequest(String senderUsername, String recipientUsername) {
        FriendRequest[] senderSentRequests;
        FriendRequest[] recipientReceivedRequests;
        int senderIndex = -1;
        int recipientIndex = -1;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(senderUsername)) {
                senderIndex = i;
            }
            if (profiles[i].getAccount().getUsername().equals(recipientUsername)) {
                recipientIndex = i;
            }
            if ((senderIndex != -1) && (recipientIndex != -1)) {
                break;
            }
        }
        senderSentRequests = profiles[senderIndex].getSentFriendRequests();
        for (int i = 0; i < senderSentRequests.length; i++) {
            if (senderSentRequests[i].usernameWhoReceive.equals(recipientUsername)) {
                senderSentRequests[i].setStatus(-1);
                senderSentRequests[i] = senderSentRequests[senderSentRequests.length - 1];
                senderSentRequests = Arrays.copyOf(senderSentRequests, senderSentRequests.length - 1);
            }
        }
        profiles[senderIndex].setSentFriendRequests(senderSentRequests);

        recipientReceivedRequests = profiles[recipientIndex].getReceivedFriendRequests();
        for (int i = 0; i < recipientReceivedRequests.length; i++) {
            if (recipientReceivedRequests[i].usernameWhoSent.equals(senderUsername)) {
                recipientReceivedRequests[i].setStatus(-1);
                recipientReceivedRequests[i] = recipientReceivedRequests[recipientReceivedRequests.length - 1];
                recipientReceivedRequests = Arrays.copyOf(recipientReceivedRequests,
                        recipientReceivedRequests.length - 1);
            }
        }
        profiles[recipientIndex].setReceivedFriendRequests(recipientReceivedRequests);
    }

    /**
     * Creates a new FriendRequest Object, adds this to both users' respective
     * arrays
     * Returns false if either username is invalid
     * 
     * @param senderUsername
     * @param recipientUsername
     * @return boolean
     */
    private boolean sendFriendRequest(String senderUsername, String recipientUsername) {

        FriendRequest thisFriendRequest = new FriendRequest(senderUsername, recipientUsername);
        boolean invalidSenderUsername = true;
        boolean invalidRecipientUsername = true;

        FriendRequest[] senderSentRequests;
        FriendRequest[] recipientReceivedRequests;
        int senderIndex = -1;
        int recipientIndex = -1;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(senderUsername)) {
                senderIndex = i;
                invalidSenderUsername = false;
            }
            if (profiles[i].getAccount().getUsername().equals(recipientUsername)) {
                recipientIndex = i;
                invalidRecipientUsername = false;
            }
            if ((senderIndex != -1) && (recipientIndex != -1)) {
                break;
            }
        }
        if (invalidRecipientUsername || invalidSenderUsername) {
            return false;
        } else {
            senderSentRequests = profiles[senderIndex].getSentFriendRequests();
            senderSentRequests = Arrays.copyOf(senderSentRequests, senderSentRequests.length + 1);
            senderSentRequests[senderSentRequests.length - 1] = thisFriendRequest;
            profiles[senderIndex].setSentFriendRequests(senderSentRequests);

            recipientReceivedRequests = profiles[recipientIndex].getReceivedFriendRequests();
            recipientReceivedRequests = Arrays.copyOf(recipientReceivedRequests, recipientReceivedRequests.length + 1);
            recipientReceivedRequests[recipientReceivedRequests.length - 1] = thisFriendRequest;
            profiles[recipientIndex].setReceivedFriendRequests(recipientReceivedRequests);
            return true;
        }
    }

    /**
     * Checks if a friend request has already been sent and returns boolean accordingly
     * 
     * @param senderUsername
     * @param recipientUsername
     * @return boolean
     */
    private boolean friendRequestAlreadyExists(String senderUsername, String recipientUsername) {
        FriendRequest[] senderSentRequests;
        FriendRequest[] recipientReceivedRequests;
        boolean exists = false;
        Boolean existsInSenderRecords = false;
        Boolean existsInRecipientRecords = false;

        int senderIndex = -1;
        int recipientIndex = -1;
        for (int i = 0; i < profiles.length; i++) {
            if (profiles[i].getAccount().getUsername().equals(senderUsername)) {
                senderIndex = i;
            }
            if (profiles[i].getAccount().getUsername().equals(recipientUsername)) {
                recipientIndex = i;
            }
            if ((senderIndex != -1) && (recipientIndex != -1)) {
                break;
            }
        }

        senderSentRequests = profiles[senderIndex].getSentFriendRequests();
        for (int i = 0; i < senderSentRequests.length; i++) {
            if (senderSentRequests[i].usernameWhoReceive.equals(recipientUsername)) {
                existsInSenderRecords = true;
            }
        }
        profiles[senderIndex].setSentFriendRequests(senderSentRequests);

        recipientReceivedRequests = profiles[recipientIndex].getReceivedFriendRequests();
        for (int i = 0; i < recipientReceivedRequests.length; i++) {
            if (recipientReceivedRequests[i].usernameWhoSent.equals(senderUsername)) {
                existsInRecipientRecords = true;
            }
        }
        profiles[recipientIndex].setReceivedFriendRequests(recipientReceivedRequests);

        if (existsInRecipientRecords && existsInSenderRecords) {
            // if request exists on records of both sides
            exists = true;
        } else if (!existsInRecipientRecords && existsInSenderRecords) {
            // if request only exists on records of one side, update other side
            recipientReceivedRequests = Arrays.copyOf(recipientReceivedRequests, recipientReceivedRequests.length + 1);
            recipientReceivedRequests[recipientReceivedRequests.length - 1] = new FriendRequest(senderUsername,
                    recipientUsername);
            profiles[recipientIndex].setReceivedFriendRequests(recipientReceivedRequests);
            exists = true;
        } else if (existsInRecipientRecords && !existsInSenderRecords) {
            // if request only exists on records of one side, update other side
            senderSentRequests = Arrays.copyOf(senderSentRequests, senderSentRequests.length + 1);
            senderSentRequests[senderSentRequests.length - 1] = new FriendRequest(senderUsername, recipientUsername);
            profiles[senderIndex].setSentFriendRequests(senderSentRequests);
            exists = true;
        } else {
            // if request doesn't exist on records of either side
            // in this case, friend request can be sent as long as usernames exist
            exists = false;
        }

        return exists;
    }

    /**
     * Checks if user usernames are in each others' friends lists,
     * Returns either "true", "false", or "Invalid Username" string values
     *
     * @param username1
     * @param username2
     * @return String
     */
    private String usersAreFriends(String username1, String username2) {
        Profile profile1 = returnProfileFromUsername(username1);
        Profile profile2 = returnProfileFromUsername(username2);

        if ((profile1 == null) || (profile2 == null)) {
            return "Invalid Username";
        } else {
            var profile1FriendList = profile1.getFriendUserNames();
            var profile2FriendList = profile2.getFriendUserNames();

            if ((Arrays.asList(profile1FriendList).contains(username2))
                    && (Arrays.asList(profile2FriendList).contains(username2))) {
                return "true";
            } else if (!(Arrays.asList(profile1FriendList)).contains(username2)
                    && (Arrays.asList(profile2FriendList).contains(username2))) {
                profile1FriendList = Arrays.copyOf(profile1FriendList, profile1FriendList.length + 1);
                profile1FriendList[profile1FriendList.length - 1] = username2;

                profile1.setFriendUserNames(profile1FriendList);
                updateProfile(profile1);
                return "true";
            } else if ((Arrays.asList(profile1FriendList)).contains(username2)
                    && (!Arrays.asList(profile2FriendList).contains(username2))) {

                profile2FriendList = Arrays.copyOf(profile2FriendList, profile2FriendList.length + 1);
                profile2FriendList[profile2FriendList.length - 1] = username1;
                profile2.setFriendUserNames(profile2FriendList);
                updateProfile(profile2);
                return "true";
            } else {
                // users are not friends
                return "false";
            }
        }
    }

    
    /** 
     * Remove the space at the beginning of the string
     * 
     * @param input
     * @return String
     */
    private String removeSpaceAtStart(String input) {
        if (input.charAt(0) == ' ') {
            input = input.substring(1);
        }
        return input;
    }
}
