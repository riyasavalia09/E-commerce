package ECommerce.Model;

import java.util.*;

// Class to generate and verify OTP
class OTP
{
      String code;
       boolean expired = false;

    public OTP()
    {
        generateCode();        // Generate new OTP
        startExpiryTimer();    // Start 30s expiry timer
    }

    static Scanner sc = new Scanner(System.in);

    // Generate random 6-digit OTP
       void generateCode()
    {
        code = String.valueOf((int)(Math.random() * 900000) + 100000);
    }

    // Get OTP value
    public String getCode()
    {
        return code;
    }

    // Check OTP expiry status
    public boolean isExpired()
    {
        return expired;
    }

    // Verify user-entered OTP
    public boolean verify(String input)
    {
        if (expired)   // OTP already expired
        {
            return false;
        }
        else if (!code.equals(input))   // Wrong OTP entered
        {
            System.out.println("\n[System] Incorrect OTP.");
            return false;
        }
        return true;   // Correct OTP
    }

    // Start expiry timer (30 seconds)
       void startExpiryTimer()
    {
        Thread timer = new Thread()
        {
            public void run()
            {
                try
                {
                    Thread.sleep(30000); // Wait 30 sec
                    expired = true;      // Mark OTP as expired
                }
                catch (InterruptedException e)
                {
                    // Timer interrupted
                }
            }
        };
        timer.start();
    }
}
