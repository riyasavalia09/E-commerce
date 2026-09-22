package ECommerce.Model;

public class validator {

    // ----------- EMAIL VALIDATION -----------
    public static boolean isValidEmail(String email) {
        try {
            if (email == null || email.isEmpty()) {
                throw new Exception("Email cannot be empty");
            }

            if (email.length() < 5 || email.length() > 100) {
                throw new Exception("Email must be between 5 and 100 characters");
            }

            int atIndex = email.indexOf('@');
            if (atIndex <= 0) {
                throw new Exception("Email must contain '@' not at the beginning");
            }

            int dotIndex = email.lastIndexOf('.');
            if (dotIndex <= atIndex + 1 || dotIndex >= email.length() - 1) {
                throw new Exception("Email must contain '.' after '@' and before domain");
            }

            int secondAtIndex = email.indexOf('@', atIndex + 1);
            if (secondAtIndex != -1) {
                throw new Exception("Email can contain only one '@' symbol");
            }

            for (int i = 0; i < email.length(); i++) {
                char c = email.charAt(i);
                if (!(Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '-' || c == '@')) {
                    throw new Exception("Email contains invalid character: " + c);
                }
            }

            if (email.contains("..")) {
                throw new Exception("Email cannot contain consecutive dots");
            }

            String domain = email.substring(dotIndex + 1);
            if (domain.length() < 2) {
                throw new Exception("Domain must have at least 2 characters");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Email error: " + e.getMessage());
            return false;
        }
    }

    // ----------- PHONE VALIDATION -----------
    public static boolean isValidPhoneNumber(String phone) {
        try {
            if (phone == null || phone.isEmpty()) {
                throw new Exception("Phone number cannot be empty");
            }

            String cleanedPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
            if (cleanedPhone.length() != 10) {
                throw new Exception("Phone number must be exactly 10 digits");
            }

            for (char c : cleanedPhone.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new Exception("Phone number can only contain digits");
                }
            }

            char firstDigit = cleanedPhone.charAt(0);
            if (firstDigit < '6' || firstDigit > '9') {
                throw new Exception("Phone number must start with a digit between 6-9");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Phone error: " + e.getMessage());
            return false;
        }
    }

    // ----------- PASSWORD VALIDATION -----------
    public static boolean isValidPassword(String password, int minLength) {
        try {
            if (password == null) {
                throw new Exception("Password cannot be null");
            }
            if (password.isEmpty()) {
                throw new Exception("Password cannot be empty");
            }
            if (password.length() < minLength) {
                throw new Exception("Password must be at least " + minLength + " characters long");
            }

            boolean hasUpperCase = false, hasDigit = false, hasSpecialChar = false;
            String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";

            for (char c : password.toCharArray()) {
                if (Character.isUpperCase(c)) hasUpperCase = true;
                if (Character.isDigit(c)) hasDigit = true;
                if (specialChars.indexOf(c) != -1) hasSpecialChar = true;
            }

            if (!hasUpperCase) throw new Exception("Password must contain at least one uppercase letter");
            if (!hasDigit) throw new Exception("Password must contain at least one digit");
            if (!hasSpecialChar) throw new Exception("Password must contain at least one special character");
            if (password.contains(" ")) throw new Exception("Password cannot contain spaces");

            return true;
        } catch (Exception e) {
            System.out.println("Password error: " + e.getMessage());
            return false;
        }
    }

    // ----------- PAN VALIDATION -----------
    public static boolean isValidPan(String pan) {
        try {
            if (pan == null || pan.isEmpty()) {
                throw new Exception("PAN cannot be empty");
            }

            String upperPan = pan.toUpperCase();
            if (upperPan.length() != 10) {
                throw new Exception("PAN must be exactly 10 characters long");
            }

            for (int i = 0; i < 5; i++) {
                if (!Character.isUpperCase(upperPan.charAt(i))) {
                    throw new Exception("First 5 characters of PAN must be uppercase letters");
                }
            }

            for (int i = 5; i < 9; i++) {
                if (!Character.isDigit(upperPan.charAt(i))) {
                    throw new Exception("Characters 6-9 of PAN must be digits");
                }
            }

            if (!Character.isUpperCase(upperPan.charAt(9))) {
                throw new Exception("Last character of PAN must be an uppercase letter");
            }

            String firstThree = upperPan.substring(0, 3);
            if ("ABC".equals(firstThree) || "XYZ".equals(firstThree) || "PAN".equals(firstThree)) {
                throw new Exception("Invalid PAN pattern");
            }

            return true;
        } catch (Exception e) {
            System.out.println("PAN error: " + e.getMessage());
            return false;
        }
    }

    // ----------- PIN VALIDATION -----------
    public static boolean isValidPin(String pin) {
        try {
            if (pin == null || pin.isEmpty()) {
                throw new Exception("PIN cannot be empty");
            }

            if (pin.length() != 4) {
                throw new Exception("PIN must be exactly 4 digits");
            }

            for (char c : pin.toCharArray()) {
                if (!Character.isDigit(c)) {
                    throw new Exception("PIN must contain only digits");
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("PIN error: " + e.getMessage());
            return false;
        }
    }

    // ----------- ADDRESS VALIDATION -----------
    public static boolean isValidAddress(String address) {
        try {
            if (address == null || address.isEmpty()) {
                throw new Exception("Address cannot be empty");
            }

            if (address.length() < 5 || address.length() > 200) {
                throw new Exception("Address must be between 5 and 200 characters");
            }

            boolean hasLetter = false;
            for (int i = 0; i < address.length(); i++) {
                char c = address.charAt(i);
                if (Character.isLetter(c)) hasLetter = true;

                if (!(Character.isLetterOrDigit(c) ||
                        c == ' ' || c == ',' || c == '.' ||
                        c == '-' || c == '/' || c == '#')) {
                    throw new Exception("Address contains invalid character: " + c);
                }
            }

            if (!hasLetter) {
                throw new Exception("Address must contain at least one letter");
            }

            if (address.contains(",,") || address.contains("..") || address.contains("--") || address.contains("//")) {
                throw new Exception("Address cannot contain consecutive special characters like .., --, ,,");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Address error: " + e.getMessage());
            return false;
        }
    }

    // ----------- NAME VALIDATION -----------
    public static boolean isValidName(String name) {
        try {
            if (name == null || name.isEmpty()) {
                throw new Exception("Name cannot be empty");
            }

            if (name.length() < 2 || name.length() > 50) {
                throw new Exception("Name must be between 2 and 50 characters");
            }

            boolean hasLetter = false;
            for (int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isLetter(c)) hasLetter = true;

                if (!(Character.isLetter(c) || c == ' ' || c == '.' || c == '-')) {
                    throw new Exception("Name contains invalid character: " + c);
                }
            }

            if (!hasLetter) {
                throw new Exception("Name must contain at least one letter");
            }

            // No consecutive spaces, dots, or hyphens
            if (name.contains("  ") || name.contains("..") || name.contains("--")) {
                throw new Exception("Name cannot contain consecutive spaces or special characters");
            }

            // Cannot start or end with space, dot, or hyphen
            char first = name.charAt(0), last = name.charAt(name.length() - 1);
            if (first == ' ' || first == '.' || first == '-') {
                throw new Exception("Name cannot start with space, dot, or hyphen");
            }
            if (last == ' ' || last == '.' || last == '-') {
                throw new Exception("Name cannot end with space, dot, or hyphen");
            }

            return true;
        } catch (Exception e) {
            System.out.println("Name error: " + e.getMessage());
            return false;
        }
    }

}
