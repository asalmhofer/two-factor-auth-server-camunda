package net.salmhofer.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class SendPinDelegate implements JavaDelegate{

	private final static Logger LOGGER = Logger.getLogger("LOAN-REQUESTS");
	
	private Properties prop = new Properties();

	
	public void execute(DelegateExecution execution) throws Exception {
		LOGGER.info("Send Email with PIN...");
		
		
		String generatedPin = execution.getVariable("generatedPin").toString();
		
		sendMail(execution.getVariable("mailSubject").toString(), execution.getVariable("mailContent").toString().replaceAll("XXXX", generatedPin.toString()));
	}
	
	private void sendMail(String subject, String content) {
		readMailProperties();
		
		final String username = prop.getProperty("mail.sender.address");
        final String password = prop.getProperty("mail.sender.password");
        
        final String recipientAddress = prop.getProperty("mail.recipient.address");

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username,password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientAddress));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Mail sent succesfully!");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
	
	public void readMailProperties() {
		
		InputStream input = null;

		try {

			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			input = classLoader.getResourceAsStream("mail.properties");

			// load a properties file
			prop.load(input);

			LOGGER.info("Mail sender address for sending: " + prop.getProperty("mail.sender.address"));
			LOGGER.info("Mail sender password: " + prop.getProperty("mail.sender.password"));
			LOGGER.info("Mail recipient address: " + prop.getProperty("mail.recipient.address"));

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
