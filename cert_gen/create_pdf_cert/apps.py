from django.apps import AppConfig


class CreatePdfCertConfig(AppConfig):
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'create_pdf_cert'

    def ready(self):
        from .views import start_scheduler
        start_scheduler()
