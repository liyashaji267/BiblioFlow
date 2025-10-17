import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javax.swing.JOptionPane;

public class TwilioSMS {
    public static void sendSms(String to, String messageBody) {
        try {
            System.out.println("=== TWILIO SMS DEBUG ===");
            System.out.println("To: " + to);
            System.out.println("Body: " + messageBody);
            System.out.println("SID: " + TwilioConfig.TWILIO_SID);
            System.out.println("From: " + TwilioConfig.TWILIO_NUMBER);

            if (to == null || to.isEmpty()) {
                throw new Exception("Recipient number is null or empty!");
            }
            if (messageBody == null || messageBody.isEmpty()) {
                throw new Exception("Message body is empty!");
            }

            Twilio.init(TwilioConfig.TWILIO_SID, TwilioConfig.TWILIO_TOKEN);

            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(TwilioConfig.TWILIO_NUMBER),
                    messageBody
            ).create();

            System.out.println("SMS sent! SID: " + message.getSid());
            JOptionPane.showMessageDialog(null, "OTP sent successfully to " + to);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "SMS sending failed: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
