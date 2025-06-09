import telebot
from telebot import types
from cert_validation_bot.bot import bot
from . import bot_replies

@bot.message_handler(commands=['start'])
def start_command(message):
    """
    Handles the /start command. Sends a welcome message and displays a keyboard with options:
    'Check certificate', 'Help' and 'About'.

    Args:
        message: The message object containing chat information and user input.
    """
    keyboard = types.ReplyKeyboardMarkup(resize_keyboard=True)
    keyboard.row("Check certificate")
    keyboard.row("Help")
    bot.send_message(message.chat.id, bot_replies.start_reply, reply_markup=keyboard)

@bot.message_handler(func=lambda message: message.text == "Help")
def help_command(message):
    """
    Handles the Help button press and sends a simple help message to the user.

    Args:
        message: The message object containing chat information and user input.
    """
    bot.send_message(message.chat.id, bot_replies.help_reply)

@bot.message_handler(func=lambda message: message.text == "Check certificate")
def check_cert_button(message):
    """
    Handles the 'Check certificate' button press. Prompts the user to enter a certificate number
    and sets up the handler for processing the certificate number in the next step.

    Args:
        message: The message object containing chat information and user input.
    """
    msg = bot.send_message(message.chat.id, bot_replies.check_certificate_reply)
    bot.register_next_step_handler(msg, process_cert_number)

def process_cert_number(message):
    """
    Processes the certificate number entered by the user. Checks if the certificate exists in the database
    and sends an appropriate response message.

    If the certificate is found, sends a confirmation message.
    If the certificate is not found, sends a message indicating the certificate is not valid.

    Args:
        message: The message object containing chat information and the certificate number.
    """
    cert_number = message.text.strip().upper()
    from create_pdf_cert.models import Cert, GiftCert
    try:
        cert_obj = Cert.objects.get(cert_number=cert_number)
        bot.send_message(message.chat.id, bot_replies.check_positive)
    except Cert.DoesNotExist:
        try:
            gift_cert = GiftCert.objects.get(cert_number=cert_number)
            from django.utils import timezone
            
            if gift_cert.expiry_date >= timezone.now().date():
                bot.send_message(message.chat.id, bot_replies.check_positive_gift)
            else:
                bot.send_message(message.chat.id, bot_replies.check_negative_gift)
            return
        except GiftCert.DoesNotExist:
            bot.send_message(message.chat.id, bot_replies.check_negative)