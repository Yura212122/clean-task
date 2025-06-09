from django.core.management.base import BaseCommand
from create_pdf_cert.views import schedule_task

class Command(BaseCommand):
    def handle(self, *args, **options):
        schedule_task()  #To run, enter into the console python manage.py scheduler