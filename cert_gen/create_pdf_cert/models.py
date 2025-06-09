from django.db import models
from django.core.exceptions import ValidationError
import random
import string
from datetime import datetime

def generate_cert_number():
    """
    Generates a unique certificate number in the format 'CERT-YYYY-XXXXXXXXX'.
    
    The number consists of:
    - A 'CERT-' prefix
    - The current year (YYYY)
    - A random 9-character string of uppercase letters and numbers
    
    Returns:
        str: The generated certificate number
    """
    year = datetime.now().strftime('%Y')
    random_chars = ''.join(random.choices(string.ascii_uppercase + string.digits, k=9))
    return f'CERT-{year}-{random_chars}'

class Cert(models.Model):
    """
    Model representing a certificate.
    
    Stores information about issued certificates including recipient name,
    course name, issue date, certificate file, and unique certificate number.
    
    Attributes:
        name (str): Name of the certificate recipient
        course (str): Name of the completed course
        date (Date): Date when certificate was issued
        cert (File): PDF file of the certificate
        created (DateTime): Timestamp of certificate creation
        cert_number (str): Unique certificate identification number
    """
    name = models.CharField(max_length=50)
    course = models.CharField(max_length=50)
    date = models.DateField(auto_now=True)
    cert = models.FileField(upload_to=('create_pdf_cert/certificates/graduation_certificates'))
    created = models.DateTimeField(auto_now=True)
    cert_number = models.CharField(
        max_length=20, 
        unique=True,
        default=generate_cert_number,
        editable=False
    )

    class Meta:
        ordering = ('-created',)
        verbose_name_plural = 'Certificates'

    def save(self, *args, **kwargs):
        """
        Override of the save method to ensure unique certificate numbers.
        
        Generates a new certificate number if one doesn't exist,
        and keeps generating until a unique number is found.
        """
        if not self.cert_number:
            self.cert_number = generate_cert_number()
        while Cert.objects.filter(cert_number=self.cert_number).exists():
            self.cert_number = generate_cert_number()
        super().save(*args, **kwargs)

    def __str__(self):
        return f'{self.id}. {self.name}, {self.date}, {self.cert_number}'

class GiftCert(models.Model):
    """
    Store information about gift certificates.
    """
    course = models.CharField(max_length=100)
    cert = models.FileField(upload_to='create_pdf_cert/certificates/gift_certificates')
    created = models.DateTimeField(auto_now_add=True)
    expiry_date = models.DateField()
    cert_number = models.CharField(max_length=11, unique=True, editable=False)
    
    def save(self, *args, **kwargs):
        """
        Generate a unique 11-digit cert number before saving if one doesn't exist
        """
        if not self.cert_number:
            self.cert_number = ''.join(random.choices(string.digits, k=11))
        super().save(*args, **kwargs)
    
    def __str__(self):
        return f"{self.name} - {self.course} - {self.cert_number}"

class MailSettings(models.Model):
    """
    Model for storing email configuration settings.
    
    Stores email credentials and recipient information for sending
    certificate-related notifications.
    
    Attributes:
        email (str): Sender's email address
        recipient_email (str): Default recipient's email address
        password (str): Password for the sender's email account
    """
    email = models.EmailField()
    recipient_email = models.EmailField(default='')
    password = models.CharField(max_length=100)

    class Meta:
        verbose_name_plural = 'Mail Settings'

    def __str__(self):
        return self.email
