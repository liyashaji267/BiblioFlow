import io.github.cdimascio.dotenv.Dotenv;

public class TwilioConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String TWILIO_SID = dotenv.get("TWILIO_SID"); 
    public static final String TWILIO_TOKEN = dotenv.get("TWILIO_TOKEN"); 
    public static final String TWILIO_NUMBER = dotenv.get("TWILIO_NUMBER"); 
}
