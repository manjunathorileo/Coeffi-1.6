package com.dfq.coeffi.servicesimpl.communication;

import com.dfq.coeffi.entity.communication.CommunicationConfiguration;
import com.dfq.coeffi.entity.communication.CommunicationStatus;
import com.dfq.coeffi.entity.communication.EmailLog;
import com.dfq.coeffi.entity.communication.SMSLog;
import com.dfq.coeffi.repository.communication.EmailLogRepository;
import com.dfq.coeffi.repository.communication.MailConfigurationRepository;
import com.dfq.coeffi.repository.communication.SMSLogRepository;
import com.dfq.coeffi.service.communication.CommunicationService;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

@Service
public class CommunicationServiceImpl implements CommunicationService {

	@Autowired
	private MailConfigurationRepository configurationRepository;
	
	@Autowired
	private EmailLogRepository emailLogRepository;
	
	@Autowired
	private SMSLogRepository smsLogRepository;
	
	@Override
	public Boolean sendEmail(List<EmailLog> emailLogs) {
		// TODO Auto-generated method stub
		CommunicationConfiguration communicationConfiguration = configurationRepository.findOne(new Long(1));
		if(communicationConfiguration!=null)
		{
			for(EmailLog emailLog : emailLogs)
			{
				try {
					Email email = new SimpleEmail();
					if(emailLog.getCommunicationStatus() == CommunicationStatus.FAILED)
					{
						email.setHostName(communicationConfiguration.getHostName());
						email.setSmtpPort(communicationConfiguration.getSmtpPort());
						email.setAuthenticator(new DefaultAuthenticator(communicationConfiguration.getEmail(), communicationConfiguration.getPassword()));
						email.setSSL(true);
						email.setFrom(communicationConfiguration.getEmail());
						email.setSubject(emailLog.getSubject());
						email.setMsg(emailLog.getMessage());
						email.addTo(emailLog.getRecipient());
						email.send();
						emailLog.setCommunicationStatus(CommunicationStatus.SENT);
						emailLogRepository.save(emailLog);
					}
				}
				catch(Exception ex)
				{
					return false;
				}
				
			}
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public Boolean sendSMS(List<SMSLog> smsLogs) {
		// TODO Auto-generated method stub
		CommunicationConfiguration communicationConfiguration = configurationRepository.findOne(new Long(1));
		if(communicationConfiguration!=null)
		{
			for(SMSLog smsLog : smsLogs)
			{
				try {
					
					if(smsLog.getCommunicationStatus() == CommunicationStatus.FAILED)
					{
						//Your authentication key
						String authkey = communicationConfiguration.getSmsAuthKey();
						
						long mobiles = smsLog.getRecipient();
						//Sender ID,While using route4 sender id should be 6 characters long.
						String senderId = communicationConfiguration.getSenderId();
						//Your message to send, Add URL encoding here.
						String message = smsLog.getMessage();
						//define route
						String route="default";

						//Prepare Url
						URLConnection myURLConnection=null;
						URL myURL=null;
						BufferedReader reader=null;

						//encoding message
						@SuppressWarnings("deprecation")
						String encoded_message=URLEncoder.encode(message);

						//Send SMS API
						String mainUrl=communicationConfiguration.getApiUrl();

						//Prepare parameter string
						StringBuilder sbPostData= new StringBuilder(mainUrl);
						sbPostData.append("authkey="+authkey);
						sbPostData.append("&mobiles="+mobiles);
						sbPostData.append("&message="+encoded_message);
						sbPostData.append("&route="+route);
						sbPostData.append("&sender="+senderId);
						
						
						mainUrl = sbPostData.toString();
						try
						{
							//prepare connection
							myURL = new URL(mainUrl);
							myURLConnection = myURL.openConnection();
							myURLConnection.connect();
							reader= new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
							//reading response
							String response;
							while ((response = reader.readLine()) != null)
								//print response
								System.out.println(response);

							//finally close connection
							reader.close();

							smsLog.setCommunicationStatus(CommunicationStatus.SENT);
							smsLogRepository.save(smsLog);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					
				}
				catch(Exception ex)
				{
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}

}
