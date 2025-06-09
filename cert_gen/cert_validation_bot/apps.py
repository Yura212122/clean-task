from django.apps import AppConfig
import threading
from .bot import run_bot
import os

class CertValidationBotConfig(AppConfig):
    """
    Configuration class for the certificate validation bot application.
    
    This class handles the configuration and initialization of the Telegram bot
    that validates certificates.
    """
    default_auto_field = 'django.db.models.BigAutoField'
    name = 'cert_validation_bot'

    def ready(self):
        """
        Starts the bot in a separate daemon thread during application initialization.
        
        The bot is only started in the main process to avoid duplication and 
        conflicts in the polling method. This is achieved by checking the RUN_MAIN
        environment variable.
        """
        if os.environ.get('RUN_MAIN') != 'true':
            return
        bot_thread = threading.Thread(target=run_bot, daemon=True)
        bot_thread.start()