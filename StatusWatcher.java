package my.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.orsyp.api.execution.ExecutionItem;
import com.orsyp.api.execution.ExecutionStatus;

public class StatusWatcher {

	 private static String USER_NAME ="jda@rexall.ca" ; //"rexalldollaru@gmail.com";//"jda@rexall.ca" ;//"rexalldollaru@gmail.com";////  // GMail user name (just the part before "@gmail.com")
	 private static String PASSWORD ="Rex@ll12345";// GMail password
	 private static String RECIPIENT = "aab@orsyp.com";
	
	public static void main(String[] args) {

		String configFile = args[0];
		String rec = args[1];
		RECIPIENT = rec;
		
		int flag=0;
		try {
			
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MMddyy_hhmm");
			String formattedDate = sdf.format(date);
			

			
			String status_watcher = "sw_"+formattedDate+".log";
			String filenamux = status_watcher;
			FileOutputStream fout = new FileOutputStream (filenamux);
			PrintStream prtstm = new PrintStream(fout);	
			
			Connector myconnection = new Connector(prtstm,configFile,false,"",false,"",false,"");
			
			
			for(int i=0;i<myconnection.getConnectionList().size();i++)
			{
				try {
					ExecutionStatus [] array = new ExecutionStatus[]{ExecutionStatus.Completed,ExecutionStatus.Aborted};//,ExecutionStatus.TimeOverrun};
					
					ArrayList<ExecutionItem> list=myconnection.getConnectionList().get(i).getExecutionList(array);
					flag+=list.size();
					myconnection.getConnectionList().get(i).printListExecution(list,prtstm);
		
				} catch (Exception e) {
					
					e.printStackTrace();
				}

			}
			
			File f = new File(status_watcher);
			if(f.exists() && !f.isDirectory())
			{
				if(flag!=0)
				{
				
					        String from = USER_NAME;
					        String pass = PASSWORD;
					        //String[] to = { RECIPIENT }; // list of recipient email addresses
					        String to = RECIPIENT;
					        String subject = "STATUS_WATCHER";
					        String body = "List of ABORTED runs attached\n#incidents = "+flag;

					        //sendFromGMail(from, pass, to, subject, body,filenamux,flag);
					        sendFromOffice365(from, pass, to, subject, body,filenamux,flag);
				}
				else
				{
					System.out.println("Nothing to report ! ");
				}
			
			}
			

				
			
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		
		
	}
	  @SuppressWarnings("unused")
	private static void sendFromGMail(String from, String pass, String[] to, String subject, String body,String attach,int flag) {
	        Properties props = System.getProperties();
	        String host = "smtp.gmail.com";
	        props.put("mail.smtp.starttls.enable", "true");
	        props.put("mail.smtp.host", host);
	        props.put("mail.smtp.user", from);
	        props.put("mail.smtp.password", pass);
	        props.put("mail.smtp.port", "587");
	        props.put("mail.smtp.auth", "true");

	        Session session = Session.getDefaultInstance(props);
	        MimeMessage message = new MimeMessage(session);
	        try {
	        	
	            message.setFrom(new InternetAddress(from));
	            InternetAddress[] toAddress = new InternetAddress[to.length];

	            // To get the array of addresses
	            for( int i = 0; i < to.length; i++ ) {
	                toAddress[i] = new InternetAddress(to[i]);
	            }

	            for( int i = 0; i < toAddress.length; i++) {
	                message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	            }

	            message.setSubject(subject);
	            //message.setText(body);
	            
	            MimeBodyPart messageBodyPart = new MimeBodyPart();
	            Multipart multipart = new MimeMultipart();
	            
	            messageBodyPart.setText(body);
	            multipart.addBodyPart(messageBodyPart);
	            
	            String filename = attach;
	            messageBodyPart = new MimeBodyPart();
	            DataSource source = new FileDataSource(filename);
	            messageBodyPart.setDataHandler(new DataHandler(source));
	            messageBodyPart.setFileName(filename);
	            multipart.addBodyPart(messageBodyPart);
	            message.setContent(multipart);
	            //message.setText(body);
	            
	            
	            Transport transport = session.getTransport("smtp");
	            transport.connect(host, from, pass);
	            transport.sendMessage(message, message.getAllRecipients());
	            transport.close();
	            System.out.println("#incidents = "+flag);

	            System.out.println("Message sent successfully to : ");
	            for(int t=0;t<to.length;t++)
	            {
	            	System.out.println("- " +to[t]);
	            }
	            System.out.println();
	        }
	        catch (AddressException ae) {
	            ae.printStackTrace();
	        }
	        catch (MessagingException me) {
	            me.printStackTrace();
	        }
	    }
	  @SuppressWarnings("unused")
	private static void sendFromOffice365(String from, String pass, String to, String subject, String body,String attach,int flag) {
	        
	
	        
	        
	        // Recipient's email ID needs to be mentioned.
	      
	        // Sender's email ID needs to be mentioned
	        String from_ = "jda@rexall.ca";
	   
	        // Assuming you are sending email from localhost
	        //String host_ = "smtp.office365.com";
	   String host_="rexall-ca.mail.protection.outlook.com";
	        // Get system properties
	        Properties properties = System.getProperties();
	   
	        // Setup mail server
	        properties.setProperty("mail.smtp.host", host_);
	   
	        // Get the default Session object.
	        Session session = Session.getDefaultInstance(properties);
	   
	        try{
	           // Create a default MimeMessage object.
	           MimeMessage message = new MimeMessage(session);
	   
	           // Set From: header field of the header.
	           message.setFrom(new InternetAddress(from));
	   
	           // Set To: header field of the header.
	           message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
	   
	           // Set Subject: header field
	           message.setSubject(subject);
	   
	           // Now set the actual message
	           message.setText(body);
	   
	           MimeBodyPart messageBodyPart = new MimeBodyPart();
	            Multipart multipart = new MimeMultipart();
	            
	            messageBodyPart.setText(body);
	            multipart.addBodyPart(messageBodyPart);
	            
	            String filename = attach;
	            messageBodyPart = new MimeBodyPart();
	            DataSource source = new FileDataSource(filename);
	            messageBodyPart.setDataHandler(new DataHandler(source));
	            messageBodyPart.setFileName(filename);
	            multipart.addBodyPart(messageBodyPart);
	            message.setContent(multipart);
	            //message.setText(body);
	            
	            
	            Transport transport = session.getTransport("smtp");
	            transport.connect(host_, from, pass);
	            transport.sendMessage(message, message.getAllRecipients());
	            transport.close();
	            System.out.println("#incidents = "+flag);

	            System.out.println("Message sent successfully to : "+to);
	           
	            System.out.println();   
	           
	           
	          
	        }catch (MessagingException mex) {
	           mex.printStackTrace();
	        }
	     } 
	        
	            
	  


	
}
