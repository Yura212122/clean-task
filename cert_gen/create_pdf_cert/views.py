from django.core.files.base import ContentFile
from django.shortcuts import render, get_object_or_404
from django.http import HttpResponse
import json

from .management.commands.upload_to_drive import upload_to_google_drive, authenticate_google_drive
from .models import Cert, GiftCert
from fpdf import FPDF
from django.conf import settings
import schedule
import time
from .email import send_zip_email
import qrcode
import threading


def index(request):
    """
    Render the index page with lists of both completion and gift certificates
    """
    certificates = Cert.objects.all()
    gift_certificates = GiftCert.objects.all()
    data = {
        'certificates': certificates,
        'gift_certificates': gift_certificates,
    }
    return render(request, 'index.html', context=data)


def download_file(request, pk):
    instance = get_object_or_404(Cert, pk=pk)
    file_path = instance.cert.path
    with open(file_path, 'rb') as file:
        response = HttpResponse(file.read(), content_type='application/pdf')
        response[
            'Content-Disposition'] = f'attachment; filename="{instance.name}_{instance.course}_{instance.date}.pdf"'
    return response


class PDF(FPDF):
    """
    Custom PDF class for generating certificates with specific templates and fonts.
    """
    def __init__(self, template_name, orientation='L', unit='mm', format='A4'):
        """
        Initializes the PDF object with a specified template name and default PDF settings.
        Registers custom font that is used on the certificate.

        Args:
            template_name (str): The name of the template to use for the certificate.
            orientation (str, optional): The orientation of the PDF. Defaults to 'L'.
            unit (str, optional): The unit of measurement for the PDF. Defaults to 'mm'.
            format (str, optional): The format of the PDF. Defaults to 'A4'.
        """
        super().__init__(orientation, unit, format)
        self.template_name = template_name
        self.add_font('Geometria', '', 'static/fonts/Geometria-Regular.ttf', uni=True)
        self.add_font('Geometria', 'B', 'static/fonts/Geometria-Bold.ttf', uni=True)
        self.add_font('Geometria', 'M', 'static/fonts/Geometria-Medium.ttf', uni=True)

    def header(self):
        """
        Adds the template image to the PDF header.
        """
        template_path = f'static/img/{self.template_name}.png'
        self.image(template_path, 0, 0, self.w, self.h)

    def add_name(self, text):
        """
        Adds the name to the PDF.

        Args:
            text (str): The name to be added to the PDF.
        """
        self.set_font('Geometria', 'B', 34)
        self.set_xy(93.6, 87)
        self.multi_cell(150, 10, text)

    def add_course(self, text):
        """
        Adds the course name to the course completion certificate PDF.

        Args:
            text (str): The course name to be added to the PDF.
        """
        self.set_font('Geometria', 'B', 26)
        self.set_xy(93.5, 125)
        self.multi_cell(150, 10, text, align='L')

    def add_date(self, text):
        """
        Adds the date to the PDF.

        Args:
            text (str): The date to be added to the PDF.
        """
        self.set_font('Geometria', '', 18)
        self.set_xy(98, 163)
        self.multi_cell(150, 10, text)

    def add_qr_code(self, qr_image):
        """
        Adds a QR code to the PDF.

        Args:
            qr_image (str): The path to the QR code image.
        """
        self.image(qr_image, x=self.w - 70, y=10, w=42, h=42)

    def add_cert_number(self, text):
        """
        Adds the certificate number to the PDF.

        Args:
            text (str): The certificate number to be added to the PDF.
        """
        self.set_font('Geometria', 'M', 8.2)
        text_width = self.get_string_width(text)
        x_coordinate = 247 - text_width / 2      
        self.set_xy(x_coordinate, 44.9)
        self.cell(0, 10, text)

    def add_gift_course(self, text):
        """
        Adds the course name to the gift certificate PDF.

        Args:
            text (str): The course name to be added to the PDF.
        """
        self.set_font('Geometria', 'B', 26)
        self.set_xy(93.5, 95)
        self.multi_cell(150, 10, text, align='L')

    def add_gift_date(self, text):
        """
        Adds the date to the PDF.

        Args:
            text (str): The date to be added to the PDF.
        """
        self.set_font('Geometria', '', 18)
        self.set_xy(94, 163)
        self.multi_cell(150, 10, text)




import logging
import uuid

logger = logging.getLogger(__name__)


def create_qr_code_image(url):
    """Generates a QR code for the specified URL and returns it as an image."""
    qr = qrcode.QRCode(version=1, error_correction=qrcode.constants.ERROR_CORRECT_L, box_size=10, border=4)
    qr.add_data(url)
    qr.make(fit=True)

    img = qr.make_image(fill_color="black", back_color="white")
    return img


def generate_unique_url():
    """
    Generates a unique URL for certificate verification.

    Returns:
        str: A URL containing a unique UUID.
    """
    unique_id = uuid.uuid4()  # Generate a version 4 UUID
    return f"https://example.com/certificate/{unique_id}"

def generating_file(request):
    if request.method == 'POST':
        json_data = json.loads(request.body)
        new_cert = Cert()
        new_cert.name = json_data['name']
        new_cert.course = json_data['course']
        selected_template = json_data.get('template', 'template1')

        file_path = f'{new_cert.name}_{new_cert.cert_number}_{new_cert.course}.pdf'
        new_cert.cert.save(file_path, ContentFile(''), save=False)
        new_cert.save()

        qr_code_url = f"https://t.me/cert_validity_test_bot"
        qr_code_image = create_qr_code_image(qr_code_url)

        qr_code_image_path = "temp_qr_code.png"
        qr_code_image.save(qr_code_image_path)

        pdf = PDF(template_name=selected_template)
        pdf.add_page()
        pdf.add_name(json_data['name'])
        pdf.add_course(json_data['course'])
        pdf.add_date(f'{new_cert.date.strftime("%d.%m.%Y")}')
        pdf.add_qr_code(qr_code_image_path)
        pdf.add_cert_number(new_cert.cert_number)

        pdf.output(new_cert.cert.path, 'F')

        with open(new_cert.cert.path, 'rb') as file:
            response = HttpResponse(file.read(), content_type='application/pdf')
            response[
                'Content-Disposition'] = f'attachment; filename="{new_cert.name}_{new_cert.course}_{new_cert.date}.pdf"'

        try:
            service = authenticate_google_drive()
            folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
            file_id = upload_to_google_drive(service, new_cert.cert.path, folder_id)
            print(f"file successfully uploaded into Google Drive. file_id: {file_id}")
        except Exception as e:
            # Logging of mistake or other
            print(f"Mistake while uploading into Google Drive: {e}")
        return response

    else:
        return HttpResponse(status=405)


def generate_gift_cert(request):
    """
    Generate a gift certificate PDF for a specific course
    """
    if request.method == 'POST':
        json_data = json.loads(request.body)
        new_cert = GiftCert()
        new_cert.course = json_data['course']
        
        from datetime import datetime
        expiry_date_str = json_data['expiry_date']
        expiry_date = datetime.strptime(expiry_date_str, '%Y-%m-%d').date()
        new_cert.expiry_date = expiry_date
        
        file_path = f'gift_{new_cert.cert_number}_{new_cert.course}.pdf'
        new_cert.cert.save(file_path, ContentFile(''), save=False)
        new_cert.save()
        
        qr_code_url = f"https://t.me/cert_validity_test_bot"
        qr_code_image = create_qr_code_image(qr_code_url)
        
        qr_code_image_path = "temp_qr_code.png"
        qr_code_image.save(qr_code_image_path)
        
        pdf = PDF(template_name='gift_template1')
        pdf.add_page()
        pdf.add_gift_course(json_data['course'])
        pdf.add_gift_date(f"{new_cert.expiry_date.strftime("%d.%m.%Y")}")
        pdf.add_qr_code(qr_code_image_path)
        pdf.add_cert_number(new_cert.cert_number)
        
        pdf.output(new_cert.cert.path, 'F')
        
        with open(new_cert.cert.path, 'rb') as file:
            response = HttpResponse(file.read(), content_type='application/pdf')
            response['Content-Disposition'] = f'attachment; filename="gift_{new_cert.cert_number}_{new_cert.course}.pdf"'
        
        try:
            service = authenticate_google_drive()
            folder_id = '1A9lMALAVr0yafcycmMX_4GLqju2sqs5l'
            file_id = upload_to_google_drive(service, new_cert.cert.path, folder_id)
            print(f"Gift certificate successfully uploaded to Google Drive. file_id: {file_id}")
        except Exception as e:
            print(f"Error while uploading gift certificate to Google Drive: {e}")
            
        return response
    else:
        return HttpResponse(status=405)

def download_gift_cert(request, pk):
    """
    Download a gift certificate file
    """
    instance = get_object_or_404(GiftCert, pk=pk)
    file_path = instance.cert.path
    with open(file_path, 'rb') as file:
        response = HttpResponse(file.read(), content_type='application/pdf')
        response['Content-Disposition'] = f'attachment; filename="gift_{instance.cert_number}_{instance.course}.pdf"'
    return response

def clear_gift_history(request):
    """
    Delete all gift certificates
    """
    GiftCert.objects.filter(~Q(cert__isnull=True)).delete()
    response = HttpResponse('Gift certificate history cleared successfully.')
    response['Location'] = '/'
    return response


from django.db.models import Q


def clear_history(request):
    Cert.objects.filter(~Q(cert__isnull=True)).delete()
    response = HttpResponse('History cleared successfully.')
    response['Location'] = '/'
    return response


def run_task():
    send_zip_email()


def schedule_task():
    scheduled_time = settings.TASK_SCHEDULE.get('time')
    # print(f"Task scheduled to run at {scheduled_time}")

    schedule.every().day.at(scheduled_time).do(run_task)

    while True:
        schedule.run_pending()
        time.sleep(60)


def start_scheduler():
    scheduler_thread = threading.Thread(target=schedule_task, daemon=True)
    scheduler_thread.start()


def email_tracker(request):
    """
    View to track email opens via a tracking pixel.

    This Django view is designed to be triggered when an email recipient's client loads
    the tracking pixel embedded in the email. When the pixel is loaded, the view logs the
    event and returns a 1x1 transparent GIF image as the response. This enables you to
    track when the email has been opened (provided the email client allows image loading).

    Parameters:
        request (HttpRequest): The HTTP request object.

    Returns:
        HttpResponse: A response containing a 1x1 transparent GIF image with the content type 'image/gif'.
    """
    logging.info("Tracking pixel loaded. Email was opened.")

    transparent_pixel = (
        b'GIF89a\x01\x00\x01\x00\x80\x00\x00\xff\xff\xff'
        b'\x00\x00\x00!\xf9\x04\x01\x00\x00\x00\x00,\x00'
        b'\x00\x00\x00\x01\x00\x01\x00\x00\x02\x02D\x01\x00;'
    )
    return HttpResponse(transparent_pixel, content_type="image/gif")

