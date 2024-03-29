import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.assertEquals;

/**
 * PJ05 Option 2 - ProfileTest
 * 
 * Test cases for Profile.java
 *
 * @author Gilbert Hsu, Kyochul Jang
 * @version November 30, 2020
 */
public class ProfileTest {

    private final PrintStream originalOutput = System.out;
    private final InputStream originalSysIn = System.in;

    @SuppressWarnings("FieldCanBeLocal")
    private ByteArrayInputStream testIn;

    @SuppressWarnings("FieldCanBeLocal")
    private ByteArrayOutputStream testOut;

    @Before
    public void outputStart() {
        testOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testOut));
    }

    @After
    public void restoreInputAndOutput() {
        System.setIn(originalSysIn);
        System.setOut(originalOutput);
    }

    
    /** 
     * returns the output from the test
     *
     * @return String
     */
    private String getOutput() {
        return testOut.toString();
    }

    
    /** 
     * method receive a String input
     * 
     * @param str
     */
    @SuppressWarnings("SameParameterValue")
    private void receiveInput(String str) {
        testIn = new ByteArrayInputStream(str.getBytes());
        System.setIn(testIn);
    }

    Class<?> object = Object.class;
    Class<?> profile = Profile.class;
    String[] b = {"a"};
    static Account newAccount = new Account("abc", "12345678");
    static FriendRequest newFriendRequest = new FriendRequest("a", "b");
    static FriendRequest[] newFriendRequestArray = {newFriendRequest};
    Profile newProfile = new Profile("Kyochul Jang", newAccount, "email@email.com", "aboutMe", "likesAndInterests", b);
    String className = "Profile";
    Method method;


    /**
     * tests the class
    */
    @Test
    public void testProfile() {
        //check if Profile class exists or not
        try {
            Object object1 = Class.forName("Profile");
        } catch (ClassNotFoundException e) {
            System.out.println("The Profile class doesn't exist.");
            Assert.fail();
        }
        //check if Profile class inherits Object class or not
        if (object.isAssignableFrom(profile)) {
            //success!
        } else {
            System.out.println("The Profile class is inheriting wrong super class");
            Assert.fail();
        }
    }

    /**
     * tests the fields
    */
    @Test
    public void testFields() {
        //fields in Profile class
        Field accountField;
        Field emailField;
        Field aboutMeField;
        Field nameField;
        Field likesAndInterestsField;
        Field friendUserNamesField;
        Field sentFriendRequestsField;
        Field receivedFriendRequestsField;

        //check if account field exists or not
        try {
            accountField = Profile.class.getDeclaredField("account");
        } catch (NoSuchFieldException e) {
            System.out.println("account field does not exist.");
            Assert.fail();
            return;
        }
        //check if account field has correct type or not
        if (accountField.getType().equals(Account.class)) {
            //success
        } else {
            System.out.println("account field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int accountModifier = accountField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "account" + "`" +
                "field is `private`!", Modifier.isPrivate(accountModifier));

        //check if email field exists or not
        try {
            emailField = Profile.class.getDeclaredField("email");
        } catch (NoSuchFieldException e) {
            System.out.println("email field does not exist.");
            Assert.fail();
            return;
        }
        //check if email field has correct type or not
        if (emailField.getType().equals(String.class)) {
            //success
        } else {
            System.out.println("email field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int emailModifier = emailField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "email" +
                "` field is `private`!", Modifier.isPrivate(emailModifier));

        //check if aboutMe field exists or not
        try {
            aboutMeField = Profile.class.getDeclaredField("aboutMe");
        } catch (NoSuchFieldException e) {
            System.out.println("aboutMe field does not exist.");
            Assert.fail();
            return;
        }
        //check if aboutMe field has correct type or not
        if (aboutMeField.getType().equals(String.class)) {
            //success
        } else {
            System.out.println("aboutMe field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int aboutMeModifier = aboutMeField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "aboutMe" +
                "` field is `private`!", Modifier.isPrivate(aboutMeModifier));

        //check if name field exists or not
        try {
            nameField = Profile.class.getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            System.out.println("name field does not exist.");
            Assert.fail();
            return;
        }
        //check if name field has correct type or not
        if (nameField.getType().equals(String.class)) {
            //success
        } else {
            System.out.println("name field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int nameModifier = nameField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "name" +
                "` field is `private`!", Modifier.isPrivate(nameModifier));

        //check if likesAndInterests field exists or not
        try {
            likesAndInterestsField = Profile.class.getDeclaredField("likesAndInterests");

        } catch (NoSuchFieldException e) {
            System.out.println("likesAndInterests field does not exist.");
            Assert.fail();
            return;
        }
        //check if likesAndInterests field has correct type or not
        if (likesAndInterestsField.getType().equals(String.class)) {
            //success
        } else {
            System.out.println("likesAndInterests field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int likesAndInterestsModifier = likesAndInterestsField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "likesAndInterests" +
                "` field is `private`!", Modifier.isPrivate(likesAndInterestsModifier));

        //check if friendUserNames field exists or not
        try {
            friendUserNamesField = Profile.class.getDeclaredField("friendUserNames");

        } catch (NoSuchFieldException e) {
            System.out.println("friendUserNames field does not exist.");
            Assert.fail();
            return;
        }
        //check if friendUserNames field has correct type or not
        if (friendUserNamesField.getType().equals(String[].class)) {
            //success
        } else {
            System.out.println("friendUserNames field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int friendUserNamesModifier = friendUserNamesField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "friendUserNames" +
                "` field is `private`!", Modifier.isPrivate(friendUserNamesModifier));

        //check if sentFriendRequests field exists or not
        try {
            sentFriendRequestsField = Profile.class.getDeclaredField("sentFriendRequests");
        } catch (NoSuchFieldException e) {
            System.out.println("sentFriendRequests field does not exist.");
            Assert.fail();
            return;
        }
        //check if sentFriendRequests field has correct type or not
        if (sentFriendRequestsField.getType().equals(FriendRequest[].class)) {
            //success
        } else {
            System.out.println("sentFriendRequests field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int sentFriendRequestsModifier = sentFriendRequestsField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "sentFriendRequests" +
                "` field is `private`!", Modifier.isPrivate(sentFriendRequestsModifier));

        //check if receivedFriendRequests field exists or not
        try {
            receivedFriendRequestsField = Profile.class.getDeclaredField("receivedFriendRequests");
        } catch (NoSuchFieldException e) {
            System.out.println("receivedFriendRequests field does not exist.");

            Assert.fail();
            return;
        }
        //check if receivedFriendRequests field has correct type or not
        if (receivedFriendRequestsField.getType().equals(FriendRequest[].class)) {
            //success
        } else {
            System.out.println("receivedFriendRequests field has wrong type");
            Assert.fail();
        }
        //check if the field is private
        int receivedFriendRequestsModifier = receivedFriendRequestsField.getModifiers();
        Assert.assertTrue("Ensure that `" + className + "`'s `" + "receivedFriendRequests" +
                "` field is `private`!", Modifier.isPrivate(receivedFriendRequestsModifier));

    }

    
    /** 
     * tests getters and setters
     * tests getAccount()
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void getAccount() throws NoSuchFieldException, IllegalAccessException {

        String methodName = "getAccount";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Account.class;

        // Perform tests
        int modifiers = method.getModifiers();

        Account result = newProfile.getAccount();



        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Field wasn't retrieved properly", result, newAccount);
    }

    
    /** 
     * tests getReceivedFriendRequests()
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void getReceivedFriendRequests() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("receivedFriendRequests");
        field.setAccessible(true);
        field.set(newProfile, newFriendRequestArray);
        FriendRequest[] result = newProfile.getReceivedFriendRequests();

        String methodName = "getReceivedFriendRequests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = FriendRequest[].class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertArrayEquals(result, new FriendRequest[]{newFriendRequest}, "Field wasn't retrieved properly");

    }

    
    /** 
     * tests getSentFiendRequests()
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void getSentFriendRequests() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("sentFriendRequests");
        field.setAccessible(true);
        field.set(newProfile, newFriendRequestArray);
        FriendRequest[] result = newProfile.getSentFriendRequests();

        String methodName = "getSentFriendRequests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = FriendRequest[].class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertArrayEquals(result, newFriendRequestArray, "Field wasn't retrieved properly");

    }

    /**
     * tests getFriendUserNames()
    */
    @Test
    public void getFriendUserNames() {

        String[] result = newProfile.getFriendUserNames();

        String methodName = "getFriendUserNames";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = String[].class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertArrayEquals(result, new String[]{"a"}, "Field wasn't retrieved properly");

    }

    /**
     * tests getLikesAndInterests()
    */
    @Test
    public void getLikesAndInterests() {

        String result = newProfile.getLikesAndInterests();

        String methodName = "getLikesAndInterests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = String.class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Field wasn't retrieved properly", result, "likesAndInterests");

    }

    /**
     * tests getAboutMe()
    */
    @Test
    public void getAboutMe() {

        String result = newProfile.getAboutMe();

        String methodName = "getAboutMe";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = String.class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Field wasn't retrieved properly", result, "aboutMe");

    }

    /**
     * tests getEmail()
    */
    @Test
    public void getEmail() {

        String result = newProfile.getEmail();

        String methodName = "getEmail";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = String.class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Field wasn't retrieved properly", result, "email@email.com");

    }

    /**
     * tests getName()
    */
    @Test
    public void getName() {

        String result = newProfile.getName();

        String methodName = "getName";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has no parameters or does not exist!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = String.class;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Field wasn't retrieved properly", result, "Kyochul Jang");

    }

    
    /** 
     * tests setName()
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setName() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("name");
        field.setAccessible(true);

        newProfile.setName("Yeju Kim");

        String methodName = "setName";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), "Yeju Kim");

    }

    
    /** 
     * tests setAboutMe()
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setAboutMe() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("aboutMe");
        field.setAccessible(true);

        newProfile.setAboutMe("myGirlFriend");

        String methodName = "setAboutMe";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), "myGirlFriend");

    }

    
    /**
     * tests setAccount()
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setAccount() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("account");
        field.setAccessible(true);

        Account yeju = new Account("Yeju", "girlFriend");
        newProfile.setAccount(yeju);

        String methodName = "setAccount";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, Account.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        Assert.assertEquals("Fields didn't match", field.get(newProfile), yeju);

    }

    
    /** 
     * tests setEmail()
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setEmail() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("email");
        field.setAccessible(true);

        newProfile.setEmail("Yeju_Kim@email.com");

        String methodName = "setEmail";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), "Yeju_Kim@email.com");

    }

    
    /** 
     * tests setFriendUserNames()
     * 
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setFriendUserNames() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("friendUserNames");
        field.setAccessible(true);

        newProfile.setFriendUserNames(new String[]{"myGirlLOL"});

        String methodName = "setFriendUserNames";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, String[].class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertArrayEquals((Object[]) field.get(newProfile), new String[]{"myGirlLOL"}, "Fields didn't match");


    }

    
    /**
     * tests setLikesAndInterests()
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setLikesAndInterests() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("likesAndInterests");
        field.setAccessible(true);

        newProfile.setLikesAndInterests("ILikeHer");

        String methodName = "setLikesAndInterests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, String.class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), "ILikeHer");

    }

    
    /** 
     * tests setReceivedFriendRequests()
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setReceivedFriendRequests() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("receivedFriendRequests");
        field.setAccessible(true);

        newProfile.setReceivedFriendRequests(newFriendRequestArray);

        String methodName = "setReceivedFriendRequests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, FriendRequest[].class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" + methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), newFriendRequestArray);

    }

    
    /** 
     * tests setSentFriendRequests() 
     *
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Test
    public void setSentFriendRequests() throws NoSuchFieldException, IllegalAccessException {

        Field field = newProfile.getClass().getDeclaredField("sentFriendRequests");
        field.setAccessible(true);

        newProfile.setSentFriendRequests(newFriendRequestArray);

        String methodName = "setSentFriendRequests";

        // Attempt to access the class method
        try {
            method = profile.getDeclaredMethod(methodName, FriendRequest[].class);
        } catch (NoSuchMethodException e) {
            Assert.fail("Ensure that `" + className + "` declares a method named `" +
                    methodName + "` that" +
                    " has one parameter!");
            return;
        } //end try catch

        Class<?> expectedReturnType = method.getReturnType();
        Class<?> actualReturnType = Void.TYPE;

        // Perform tests
        int modifiers = method.getModifiers();

        Assert.assertTrue("Ensure that `" + className + "`'s `" + methodName +
                "` method is `public`!", Modifier.isPublic(modifiers));

        Assert.assertEquals("Ensure that `" + className + "`'s `" + methodName +
                "` method has the correct return type!", expectedReturnType, actualReturnType);

        assertEquals("Fields didn't match", field.get(newProfile), newFriendRequestArray);

    }
    
    /**
     * The test below are testing whether the methods are workable
     */
    Profile profileEx = new Profile("", new Account("", "")
        , "", "", "", new String[]{""});

    /**
     * tests addToFriendUsernames()
     */
    @Test
    public void addToFriendUsernames() {
        profileEx.addToFriendUsernames("usernameAdd");
        String expected = "usernameAdd";
        String actual = profileEx.getFriendUserNames()[profileEx.getFriendUserNames().length - 1];
        assertEquals(expected, actual);
    }

}
