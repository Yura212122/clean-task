import os
from zipfile import ZipFile
import yagmail
from .models import MailSettings, Cert
from datetime import datetime, timedelta
import logging

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')

def create_zip_archive(source_dir, zip_file_path):
    today = datetime.now().date()
    yesterday = today - timedelta(days=1)

    logging.info("Starting zip archive creation.")
    logging.info(f"Current date: {today}, searching for certificates created since: {yesterday}")

    recent_certs = Cert.objects.filter(created__date__gte=yesterday)

    if not recent_certs.exists():
        logging.warning("No new certificates to add to the archive.")
        #print("There are no new certificates to send.")
        return None

    logging.info(f"Found {recent_certs.count()} new certificates. Creating archive at {zip_file_path}")

    with ZipFile(zip_file_path, 'w') as zipf:
        for cert in recent_certs:
            cert_file_path = cert.cert.path
            zipf.write(cert_file_path, os.path.relpath(cert_file_path, source_dir))
            logging.info(f"Added {cert_file_path} to archive")

    logging.info("Zip archive created successfully.")
    return zip_file_path


def send_zip_email():
    print("The function send_zip_email has been started")

    source_directory = 'create_pdf_cert/certificates/'
    zip_file_path = os.path.join(source_directory, 'certs.zip')
    logging.info(f"Source directory for certificates set to: {source_directory}")
    logging.info(f"ZIP file path set to: {zip_file_path}")

    zip_file_path = create_zip_archive(source_directory, zip_file_path)

    if not zip_file_path:
        #print("There are no new certificates for sending.")
        logging.warning("No new certificates found for sending")
        return

    logging.info("Retrieving email settings from the database.")
    mail_settings = MailSettings.objects.last()

    if not mail_settings or not mail_settings.email or not mail_settings.password:
        #print("Email settings not found.")
        logging.error("Email settings not found in the database, orIncomplete email settings (email or password missing)")
        return

    logging.info(f"Email settings retrieved successfully. Email: {mail_settings.email}")


    yag = yagmail.SMTP(mail_settings.email, mail_settings.password)
    # try:
    #     yag = yagmail.SMTP(mail_settings.email, mail_settings.password)
    #     logging.info("SMTP connection established with Gmail.")
    # except Exception as e:
    #     logging.error(f"Failed to establish SMTP connection: {e}")
    #     return

    subject = 'Certificates'
    message_text = 'A ZIP file with certificates is attached to you.'

    tracking_pixel_url = "https://example.com/email-tracker/?tracking=1"

    message_html = f"""
       <html>
         <body>
           <p>A ZIP file with certificates is attached to you.</p>
           <img src="{tracking_pixel_url}" width="1" height="1" style="display:none;" alt="tracker" />
         </body>
       </html>
       """
    recipient_email = mail_settings.recipient_email
    logging.info(f"Email prepared for sending. Recipient: {recipient_email}, Subject: '{subject}'")

    try:
        yag.send(
            to=recipient_email,
            subject=subject,
            contents=[message_text, message_html],
            attachments=zip_file_path
        )
        print("Email sent successfully")
    except Exception as e:
        print("Error while sending email:", e)