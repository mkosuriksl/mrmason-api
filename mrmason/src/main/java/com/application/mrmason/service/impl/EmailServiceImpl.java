package com.application.mrmason.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.application.mrmason.enums.RegSource;
import com.application.mrmason.service.EmailService;
import com.itextpdf.io.IOException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Autowired
	private JavaMailSender mailsender;

	public void sendEmail(String toEmail, String subject, String body) {
		try {
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("no_reply@kosuriers.com"); // Update this if needed
			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body, true); // Set to true for HTML content

			mailsender.send(message);
			log.info("Email sent successfully to {}", toEmail);
		} catch (MessagingException e) {
			log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
		}
	}
	
	public void sendEmailWithAttachment(String to, String subject, String bodyText, byte[] pdfBytes, String filename) {
	    try {
	        MimeMessage message = mailsender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	        helper.setFrom("no_reply@kosuriers.com");
	        helper.setTo(to);
	        helper.setSubject(subject);
	        helper.setText(bodyText, true); // send as HTML

	        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");
	        helper.addAttachment(filename, dataSource);

	        mailsender.send(message);
	    } catch (MessagingException | IOException e) {
	        throw new RuntimeException("Failed to send email with attachment", e);
	    }
	}
	
	  public void sendEmailWithPdfAttachment(String to, String subject, String body, byte[] attachmentData, String fileName) {
	        try {
	            MimeMessage message = mailsender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);

	            helper.setTo(to);
	            helper.setSubject(subject);
	            helper.setText(body);

	            helper.addAttachment(fileName, new ByteArrayResource(attachmentData));

	            mailsender.send(message);
	        } catch (MessagingException e) {
	            throw new RuntimeException("Failed to send email", e);
	        }
	    }


	@Override
	public void sendEmail(String toMail, String otp) {
		try {
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("no_reply@kosuriers.com"); // Update this if needed
			helper.setTo(toMail);
			helper.setSubject("YOUR OTP FOR VERIFICATION.");
			String body =null;
			body = "Thanks for registering with us. Your OTP to verify your email is " + otp + " - www.mrmason.in";	
			helper.setText(body, true); // Set to true for HTML content
			mailsender.send(message);
			log.info("Email sent successfully to {}", toMail);
		} catch (MessagingException e) {
			log.error("Failed to send email to {}: {}", toMail, e.getMessage());
		}
	}
	@Override
	public void sendEmail(String toMail, String otp, RegSource regSource) {
		try {
			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setFrom("no_reply@kosuriers.com"); // Update this if needed
			helper.setTo(toMail);
			helper.setSubject("YOUR OTP FOR VERIFICATION.");
			String body =null;
			if (regSource == RegSource.MRMASON) {
				 body = "Thanks for registering with us. Your OTP to verify your email is " + otp + " - www.mrmason.in";
			}else if(regSource == RegSource.MEKANIK) {
				 body = "Thanks for registering with us. Your OTP to verify your email is " + otp + " - www.mekanik.in";
			}else if(regSource==RegSource.BHATSR) {
				 body = "Thanks for registering with us. Your OTP to verify your email is " + otp + " - www.bhatsr.in";
			}
			
			helper.setText(body, true); // Set to true for HTML content

			mailsender.send(message);
			log.info("Email sent successfully to {}", toMail);
		} catch (MessagingException e) {
			log.error("Failed to send email to {}: {}", toMail, e.getMessage());
		}
	}
	
	@Override
	public void sendEmail(String toMail, RegSource regSource) {
	    try {
	        MimeMessage message = mailsender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setFrom("no_reply@kosuriers.com");
	        helper.setTo(toMail);

	        String subject = null;
	        String body = null;

	        if (regSource == RegSource.MRMASON) {
	            subject = "Welcome to MrMason!";
	            body = "Thanks for registering with us. Visit us at <a href='https://www.mrmason.in'>www.mrmason.in</a>";
	        } else if (regSource == RegSource.MEKANIK) {
	            subject = "Welcome to Mekanik!";
	            body = "Thanks for registering with us. Visit us at <a href='https://www.mekanik.in'>www.mekanik.in</a>";
	        }
	        else if (regSource == RegSource.BHATSR) {
	            subject = "Welcome to Bhatsr!";
	            body = "Thanks for registering with us. Visit us at <a href='https://www.bhatsr.in'>www.bhatsr.in</a>";
	        }else {
	            subject = "Welcome!";
	            body = "Thanks for joining us.";
	        }

	        helper.setSubject(subject);
	        helper.setText(body, true); // HTML enabled

	        mailsender.send(message);
	        log.info("Email sent successfully to {}", toMail);
	    } catch (MessagingException e) {
	        log.error("Failed to send email to {}: {}", toMail, e.getMessage());
	    }
	}

	
	public void sendWebMail(String toMail, String body) {

		MimeMessage message = mailsender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
		try {
			helper.setTo(toMail);
			helper.setSubject("OTP LOGIN SUCCESSFUL");
			helper.setText(body, true);
			mailsender.send(message);
		} catch (MessagingException e) {
			// Handle exception
		}
	}
	
	@Override
	public void sendEmailPromotion(String toMail, String subject, String body, RegSource regSource) {
	    try {
	        MimeMessage message = mailsender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);

	        helper.setFrom("no_reply@kosuriers.com");
	        helper.setTo(toMail);

	        // You pass subject & body directly now
	        helper.setSubject(subject);
	        helper.setText(body, true); // true = allow HTML

	        mailsender.send(message);
	        log.info("Promotional Email sent successfully to {} via {}", toMail, regSource);
	    } catch (MessagingException e) {
	        log.error("Failed to send promotional email to {}: {}", toMail, e.getMessage());
	    }
	}


}
