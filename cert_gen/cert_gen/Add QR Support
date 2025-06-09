import uuid
import qrcode
from PIL import Image, ImageDraw, ImageFont


def generate_unique_url():

    """Генерирует уникальный URL-адрес для сертификата."""

    unique_id = uuid.uuid4()
    return f"https://example.com/certificate/{unique_id}"


def create_qr_code(url, filename):

    """Создает QR-код для указанного URL-адреса и сохраняет его в файл."""

    qr = qrcode.QRCode(version=1, error_correction=qrcode.constants.ERROR_CORRECT_L, box_size=10, border=4)
    qr.add_data(url)
    qr.make(fit=True)

    img = qr.make_image(fill_color="black", back_color="white")
    img.save(filename)


def generate_certificate_with_qr():

    """Создает сертификат с QR-кодом и сохраняет его в файл."""

    width, height = 800, 600  # Создаем пустое изображение сертификата, Примерные размеры сертификата
    certificate_img = Image.new('RGB', (width, height), color='white')
    draw = ImageDraw.Draw(certificate_img)

    # Добавляем текст или другие элементы на сертификат, если необходимо
    title_font = ImageFont.truetype("arial.ttf", 24)
    draw.text((10, 10), "Certificate of Achievement", font=title_font, fill='black')

    # Генерируем уникальный URL
    url = generate_unique_url()

    # Создаем уникальное имя файла для QR-кода
    qr_code_filename = f"qr_code_{uuid.uuid4()}.png"

    # Создаем QR-код с сгенерированным URL
    create_qr_code(url, qr_code_filename)

    # Определяем расположение QR-кода на сертификате
    x_offset, y_offset = 50, 50  # Примерные координаты для размещения QR-кода
    qr_code_img = Image.open(qr_code_filename)

    # Вставляем QR-код на сертификат с указанными смещениями
    certificate_img.paste(qr_code_img, (x_offset, y_offset))

    # Сохраняем измененное изображение сертификата с QR-кодом
    certificate_with_qr_filename = "certificate_with_qr.png"
    certificate_img.save(certificate_with_qr_filename)

    # Возвращаем имя файла с сертификатом и QR-кодом
    return certificate_with_qr_filename

# Генерируем сертификат с QR-кодом
generated_certificate = generate_certificate_with_qr()
print(f"Сгенерирован сертификат с QR-кодом: {generated_certificate}")




