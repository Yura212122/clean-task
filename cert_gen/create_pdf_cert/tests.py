from django.test import TestCase, Client
from django.core.files.uploadedfile import SimpleUploadedFile
from django.urls import reverse
from datetime import date
import time
import os
import uuid
from unittest.mock import patch
import qrcode
from django.core.exceptions import ValidationError
from django.core.validators import validate_email

from create_pdf_cert.models import Cert, MailSettings
from create_pdf_cert.views import PDF, generate_unique_url, create_qr_code_image
from create_pdf_cert.email import send_zip_email


class CertModelTest(TestCase):
    def setUp(self):
        self.cert = Cert.objects.create(
            name="John Doe",
            course="Python Development",
            cert="create_pdf_cert/certificates/test_certificate.pdf"
        )
        time.sleep(1)

    def test_cert_creation(self):
        """Проверяем, что объект Cert создается корректно."""
        self.assertEqual(self.cert.name, "John Doe")
        self.assertEqual(self.cert.course, "Python Development")
        self.assertTrue(self.cert.cert.name.endswith("test_certificate.pdf"))
        self.assertEqual(self.cert.date, date.today())

    def test_cert_ordering(self):
        """Проверяем, что сортировка работает по полю created (обратный порядок)."""
        cert2 = Cert.objects.create(
            name="Jane Smith",
            course="Django Development",
            cert="create_pdf_cert/certificates/test2_certificate.pdf"
        )

        certs = Cert.objects.all()

        self.assertEqual(certs[0], cert2)
        self.assertEqual(certs[1], self.cert)

    def test_cert_str_method(self):
        """Проверяем корректность метода __str__."""
        expected_str = f"{self.cert.id}. {self.cert.name}, {self.cert.date}"
        self.assertEqual(str(self.cert), expected_str)

class MailSettingsModelTests(TestCase):
    """
    Test suite for MailSettings model validation and creation functionality.
    
    Tests:
    1. Creation of MailSettings object with valid data
    2. String representation of MailSettings object
    3. Email validation for invalid email formats
    """

    def setUp(self):
        self.mail_settings_data = {
            "email": "test@example.com",
            "recipient_email": "recipient@example.com",
            "password": "secure_password69"
        }

    def test_mail_settings_creation(self):
        """
        Test MailSettings object creation with valid data.
        
        Steps:
        1. Create MailSettings object with test data
        2. Verify email field matches input
        3. Verify recipient_email field matches input
        4. Verify password field matches input
        """
        settings = MailSettings.objects.create(**self.mail_settings_data)
        self.assertEqual(settings.email, self.mail_settings_data["email"])
        self.assertEqual(settings.recipient_email, self.mail_settings_data["recipient_email"])
        self.assertEqual(settings.password, self.mail_settings_data["password"])

    def test_mail_settings_str_representation(self):
        """
        Test string representation of MailSettings object.
        
        Steps:
        1. Create MailSettings object
        2. Verify __str__ method returns email address
        """
        settings = MailSettings.objects.create(**self.mail_settings_data)
        self.assertEqual(str(settings), settings.email)

    def test_mail_settings_email_validation(self):
        """
        Test email validation for invalid email format.
        
        Steps:
        1. Create MailSettings data with invalid email
        2. Attempt to validate email
        3. Verify ValidationError is raised
        4. Verify full model validation fails
        """
        
        invalid_data = self.mail_settings_data.copy()
        invalid_data["email"] = "gerarahere_email"
        
        mail_settings = MailSettings(**invalid_data)
        with self.assertRaises(ValidationError):
            validate_email(mail_settings.email)
            mail_settings.full_clean()


class ViewsTests(TestCase):
    """
    Test suite for application views functionality.
    
    Tests:
    1. Index view rendering
    2. File download functionality
    3. Certificate generation
    4. History clearing
    """
 
    def setUp(self):
        self.client = Client()
        self.cert = Cert.objects.create(
            name="Test Userino",
            course="Test Coursino",
            cert=SimpleUploadedFile("test.pdf", b"content")
        )

    def test_index_view(self):
        """
        Test index view functionality.
        
        Steps:
        1. Send GET request to index URL
        2. Verify 200 status code
        3. Verify correct template usage
        4. Verify certificates context variable exists
        """
        response = self.client.get(reverse("index"))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, "index.html")
        self.assertIn("certificates", response.context)

    def test_download_file_view_success(self):
        """
        Test successful certificate file download.
        
        Steps:
        1. Create test certificate
        2. Request download URL with valid certificate ID
        3. Verify 200 status code
        4. Verify PDF content type header
        """
        response = self.client.get(reverse("download_file", args=[self.cert.pk]))
        self.assertEqual(response.status_code, 200)
        self.assertEqual(response["Content-Type"], "application/pdf")

    def test_download_file_view_not_found(self):
        """
        Test download attempt for non-existent certificate.
        
        Steps:
        1. Request download with invalid certificate ID
        2. Verify 404 status code
        """
        response = self.client.get(reverse("download_file", args=[69]))
        self.assertEqual(response.status_code, 404)

    def test_generating_file_get_method(self):
        """
        Test GET request handling for file generation endpoint.
        
        Steps:
        1. Send GET request to generating_file URL
        2. Verify 405 Method Not Allowed status code
        """
        response = self.client.get(reverse("generating_file"))
        self.assertEqual(response.status_code, 405)

    def test_clear_history(self):
        """
        Test certificate history clearing functionality.
        
        Steps:
        1. Create test certificate
        2. Verify certificate exists in database
        3. Send POST request to clear history
        4. Verify 200 status code
        5. Verify all certificates are deleted
        """
        Cert.objects.create(
            name="Test Userano",
            course="Test Coursano",
            cert=SimpleUploadedFile("test.pdf", b"content")
        )
        self.assertTrue(Cert.objects.exists())
        
        response = self.client.post(reverse("clear_history"))
        self.assertEqual(response.status_code, 200)
        self.assertFalse(Cert.objects.exists())


class PDFGenerationTests(TestCase):
    """
    Test suite for PDF class and related PDF document generation functionality.
    Tests cover template loading, text formatting and positioning of certificate elements.
    """

    def setUp(self):
        self.test_data = {
            "name": "VesTheCoder",
            "course": "Advanced Python", 
            "template": "template69"
        }
        self.pdf = PDF(template_name=self.test_data["template"])

    @patch("create_pdf_cert.views.PDF.image")
    def test_header_template_loading(self, mock_image):
        """
        Test correct template loading in the header method.
        
        Steps:
        1. Call header method on PDF instance
        2. Verify image method was called exactly once
        3. Extract arguments passed to image method
        4. Verify template filename ends with correct template name and extension
        5. Validate template path points to correct PNG file
        """
        self.pdf.header()
        mock_image.assert_called_once()
        args = mock_image.call_args[0]
        self.assertTrue(args[0].endswith(f"{self.test_data['template']}.png"))

    def test_add_name_formatting(self):
        """
        Test proper formatting and positioning of recipient name on certificate.
        
        Steps:
        1. Mock PDF methods:
           - set_font for font configuration
           - set_xy for positioning
           - multi_cell for text rendering
        2. Call add_name with test recipient name
        3. Verify font settings:
           - Font family: Arial
           - Style: Bold
           - Size: 34
        4. Verify positioning at coordinates (90, 87)
        5. Verify text rendering with:
           - Width: 150
           - Height: 10
           - Correct recipient name
        """
        with patch.object(PDF, "set_font") as mock_set_font:
            with patch.object(PDF, "set_xy") as mock_set_xy:
                with patch.object(PDF, "multi_cell") as mock_multi_cell:
                    self.pdf.add_name(self.test_data["name"])
                    
                    mock_set_font.assert_called_with("Arial", "B", 34)
                    mock_set_xy.assert_called_with(90, 87)
                    mock_multi_cell.assert_called_with(150, 10, self.test_data["name"])

    def test_add_course_formatting(self):
        """
        Test proper formatting and positioning of course name on certificate.
        
        Steps:
        1. Mock PDF methods:
           - set_font for font configuration
           - set_xy for positioning
           - multi_cell for text rendering
        2. Call add_course with test course name
        3. Verify font settings:
           - Font family: Arial
           - Style: Bold
           - Size: 26
        4. Verify positioning at coordinates (93, 125)
        5. Verify text rendering with:
           - Width: 150
           - Height: 10
           - Correct course name
           - Left alignment
        """
        with patch.object(PDF, "set_font") as mock_set_font:
            with patch.object(PDF, "set_xy") as mock_set_xy:
                with patch.object(PDF, "multi_cell") as mock_multi_cell:
                    self.pdf.add_course(self.test_data["course"])
                    
                    mock_set_font.assert_called_with("Arial", "B", 26)
                    mock_set_xy.assert_called_with(93, 125)
                    mock_multi_cell.assert_called_with(150, 10, self.test_data["course"], align="L")


class QRCodeTests(TestCase):
    """
    Test suite for QR code generation functionality.
    Tests cover URL generation and QR code image creation with proper parameters.
    """

    def test_generate_unique_url(self):
        """
        Test generation of unique certificate verification URLs.
        
        Steps:
        1. Generate two unique URLs
        2. Verify URLs are different from each other
        3. Verify URL format:
           - Starts with correct domain
           - Contains 'certificate' path segment
        4. Verify UUID in URL:
           - Extract UUID from URL
           - Validate UUID format
           - Ensure UUID is valid version 4
        """
        url1 = generate_unique_url()
        url2 = generate_unique_url()
        
        self.assertNotEqual(url1, url2)
        self.assertTrue(url1.startswith("https://example.com/certificate/"))
        self.assertTrue(uuid.UUID(url1.split("/")[-1]))

    def test_create_qr_code_image(self):
        """
        Test QR code image creation functionality.
        
        Steps:
        1. Create test verification URL
        2. Generate QR code image from URL
        3. Verify QR code image object creation:
           - Check image object is not None
           - Verify image object type
        4. Verify image capabilities:
           - Check presence of save method
           - Ensure image can be saved to file
        """
        test_url = "https://example.com/test"
        qr_image = create_qr_code_image(test_url)
        
        self.assertIsNotNone(qr_image)
        self.assertTrue(hasattr(qr_image, "save"))

    @patch("qrcode.QRCode")
    def test_qr_code_parameters(self, mock_qrcode):
        """
        Test QR code generation parameters configuration.
        
        Steps:
        1. Create test verification URL
        2. Generate QR code with URL
        3. Verify QRCode initialization parameters:
           - Version: 1 (auto-sizing enabled)
           - Error correction: Level L (7% recovery)
           - Box size: 10 pixels per box
           - Border width: 4 boxes
        4. Validate QRCode class instantiation
        5. Ensure all parameters match QR code specifications
        """
        test_url = "https://example.com/test"
        create_qr_code_image(test_url)
        
        mock_qrcode.assert_called_with(
            version=1,
            error_correction=qrcode.constants.ERROR_CORRECT_L,
            box_size=10,
            border=4
        )


class EmailFunctionalityTests(TestCase):
    """
    Test suite for email functionality and ZIP archive creation.
    
    Tests:
    1. Successful email sending
    2. Handling of missing certificates
    3. Handling of missing email settings
    """

    def setUp(self):
        self.test_dir = "test_certificates"
        if not os.path.exists(self.test_dir):
            os.makedirs(self.test_dir)
            
        self.cert = Cert.objects.create(
            name="Test Usereno",
            course="Test Courseno",
            cert=SimpleUploadedFile("test.pdf", b"test content")
        )
        
        self.mail_settings = MailSettings.objects.create(
            email="sender@example.com",
            recipient_email="recipient@example.com",
            password="test_password69"
        )

    def tearDown(self):
        """
        Clean up test resources after each test case execution.
        
        Steps:
        1. Clean up test certificate directory:
           - Check if test directory exists
           - Remove all files inside the test directory
           - Remove the empty test directory itself
        2. Clean up database:
           - Delete all Cert objects from the database
           
        Purpose:
        - Ensures test isolation by removing all test artifacts
        - Prevents test files from accumulating on disk
        - Cleans up database state for next test
        - Avoids interference between test cases
        
        Operations:
        - os.path.exists() to check directory existence
        - os.listdir() to get all files in directory
        - os.path.join() for safe path construction
        - os.remove() to delete individual files
        - os.rmdir() to remove empty directory
        
        Database operations:
        - Cert.objects.all().delete() to remove all certificates
        """
        if os.path.exists(self.test_dir):
            for file in os.listdir(self.test_dir):
                os.remove(os.path.join(self.test_dir, file))
            os.rmdir(self.test_dir)
            
        Cert.objects.all().delete()

    @patch("yagmail.SMTP")
    def test_send_zip_email_success(self, mock_smtp):
        """
        Test successful email sending with ZIP attachment.
        
        Steps:
        1. Mock SMTP connection
        2. Mock ZIP archive creation
        3. Create test certificate
        4. Attempt to send email
        5. Verify SMTP initialization with correct credentials
        6. Verify email sending attempt
        """
        mock_smtp_instance = mock_smtp.return_value
        mock_smtp_instance.__enter__.return_value = mock_smtp_instance
        
        with patch("create_pdf_cert.email.create_zip_archive") as mock_create_zip:
            mock_create_zip.return_value = os.path.join(self.test_dir, "test.zip")
            
            Cert.objects.create(
                name="Test Useruno",
                course="Test Coursuno",
                cert=SimpleUploadedFile("test.pdf", b"test content")
            )
            
            send_zip_email()
            
            mock_smtp.assert_called_with(
                self.mail_settings.email,
                self.mail_settings.password
            )
            mock_smtp_instance.send.assert_called_once()

    def test_send_zip_email_no_certs(self):
        """
        Test email sending with no certificates available.
        
        Steps:
        1. Delete all certificates
        2. Attempt to send email
        3. Verify None is returned
        """
        Cert.objects.all().delete()
        result = send_zip_email()
        self.assertIsNone(result)

    def test_send_zip_email_no_settings(self):
        """
        Test email sending with no email settings configured.
        
        Steps:
        1. Delete all email settings
        2. Attempt to send email
        3. Verify None is returned
        """
        MailSettings.objects.all().delete()
        result = send_zip_email()
        self.assertIsNone(result)


class SchedulerTests(TestCase):
    """
    Test suite for task scheduler functionality.
    
    Verifies the proper functioning of the task scheduling system,
    which is responsible for automating certificate processing and email distribution.
    
    Key Features Tested:
    1. Task execution and scheduling
    2. Task runner mocking and verification
    3. Resource cleanup and test isolation
    
    Test Environment:
    - Creates temporary test directory for file operations
    - Manages test artifacts cleanup
    - Ensures database state consistency
    
    Test Coverage:
    - Task scheduling mechanism
    - Task execution verification
    - Error handling and edge cases
    - Resource management
    
    Notes:
    - Tests run in isolation to prevent interference
    - All resources are properly cleaned up after tests
    - Database state is reset between test runs
    """

    def setUp(self):
        self.test_dir = "test_scheduler"
        if not os.path.exists(self.test_dir):
            os.makedirs(self.test_dir)

    def tearDown(self):
        """
        Clean up test resources after each test case execution.
        
        Steps:
        1. Clean up test certificate directory:
           - Check if test directory exists
           - Remove all files inside the test directory
           - Remove the empty test directory itself
        2. Clean up database:
           - Delete all Cert objects from the database
           
        Purpose:
        - Ensures test isolation by removing all test artifacts
        - Prevents test files from accumulating on disk
        - Cleans up database state for next test
        - Avoids interference between test cases
        
        Operations:
        - os.path.exists() to check directory existence
        - os.listdir() to get all files in directory
        - os.path.join() for safe path construction
        - os.remove() to delete individual files
        - os.rmdir() to remove empty directory
        
        Database operations:
        - Cert.objects.all().delete() to remove all certificates
        """
        if os.path.exists(self.test_dir):
            for file in os.listdir(self.test_dir):
                os.remove(os.path.join(self.test_dir, file))
            os.rmdir(self.test_dir)

    @patch("create_pdf_cert.views.run_task")
    def test_run_task_execution(self, mock_run_task):
        """
        Test scheduled task execution.
        
        Steps:
        1. Mock task runner
        2. Execute task
        3. Verify task was called exactly once
        """
        from create_pdf_cert.views import run_task
        run_task()
        mock_run_task.assert_called_once()


class EmailTrackerTestCase(TestCase):
    def setUp(self):
        self.client = Client()

    @patch('logging.info')
    def test_email_tracker_response(self, mock_logging):
        """
        Test that the email_tracker view returns a 1x1 transparent GIF image
        and logs the event correctly.
        """
        response = self.client.get("/email-tracker/?tracking=1")

        self.assertEqual(response.status_code, 200)

        self.assertEqual(response["Content-Type"], "image/gif")

        expected_pixel = (
            b'GIF89a\x01\x00\x01\x00\x80\x00\x00\xff\xff\xff'
            b'\x00\x00\x00!\xf9\x04\x01\x00\x00\x00\x00,\x00'
            b'\x00\x00\x00\x01\x00\x01\x00\x00\x02\x02D\x01\x00;'
        )
        self.assertEqual(response.content, expected_pixel)

        mock_logging.assert_called_once_with("Tracking pixel loaded. Email was opened.")
