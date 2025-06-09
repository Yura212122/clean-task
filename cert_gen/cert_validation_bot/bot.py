import telebot
from django.conf import settings
if not hasattr(settings, 'TELEGRAM_VALIDITY_BOT_TOKEN') or not settings.TELEGRAM_VALIDITY_BOT_TOKEN:
    raise Exception("TELEGRAM_VALIDITY_BOT_TOKEN must be defined in Django settings")

import logging
logging.getLogger('requests').setLevel(logging.WARNING)
logging.getLogger('urllib3').setLevel(logging.WARNING)

BOT_TOKEN = settings.TELEGRAM_VALIDITY_BOT_TOKEN
bot = telebot.TeleBot(BOT_TOKEN)

import cert_validation_bot.handlers

def run_bot():
    """
    Starts the Telegram bot in synchronous polling mode.
    
    This function initializes and runs the Telegram bot by:
    1. Removing any existing webhook
    2. Starting the bot in polling mode with continuous operation
    
    The bot will run indefinitely until the program is terminated.
    """
    bot.remove_webhook()
    bot.polling(none_stop=True)